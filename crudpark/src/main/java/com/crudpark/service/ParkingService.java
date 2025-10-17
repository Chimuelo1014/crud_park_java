package com.crudpark.service;

import com.crudpark.dao.MensualidadDAO;
import com.crudpark.dao.TicketDAO;
import com.crudpark.model.Mensualidad;
import com.crudpark.model.Operador;
import com.crudpark.model.Ticket;
import java.time.LocalDateTime;
import com.crudpark.dao.TarifaDAO;
import com.crudpark.model.Tarifa;
import java.math.BigDecimal;
import java.time.Duration;

public class ParkingService {
    private static final String QR_PAYLOAD_PREFIX = "FOLIO:";
    private static final String QR_PAYLOAD_SEPARATOR = "|PLACA:";

    private TarifaDAO tarifaDAO;
    private TicketDAO ticketDAO;
    private MensualidadDAO mensualidadDAO;

    public ParkingService() {
        this.ticketDAO = new TicketDAO();
        this.mensualidadDAO = new MensualidadDAO();
        this.tarifaDAO = new TarifaDAO();
    }

    /**
     * Registra un nuevo ingreso de vehículo, aplicando las reglas de negocio.
     * @param placa La placa del vehículo.
     * @param operador El operador logueado.
     * @return Mensaje de éxito o error.
     */
    public String registrarIngreso(String placa, Operador operador) {

        // 1. REGLA DE NEGOCIO: No se puede registrar un ingreso si ya hay un ticket abierto.
        Ticket ticketAbierto = ticketDAO.findOpenTicketByPlaca(placa);
        if (ticketAbierto != null) {
            return "ERROR: Ya existe un ticket abierto (Folio #" + ticketAbierto.getId() + ") para la placa " + placa;
        }

        // 2. VERIFICACIÓN: ¿Tiene Mensualidad activa? (Solo informativo por ahora)
        Mensualidad mensualidad = mensualidadDAO.findByPlaca(placa);
        boolean esAbonado = false;
        if (mensualidad != null && mensualidad.esMensualidadValidaHoy()) {
            esAbonado = true;
        }

        // 3. CREAR EL NUEVO TICKET
        LocalDateTime now = LocalDateTime.now();
        Ticket nuevoTicket = new Ticket(placa, now, operador);

        int folioGenerado = ticketDAO.create(nuevoTicket);

        if (folioGenerado > 0) {
            // AJUSTE CLAVE: Asignar el ID generado para que el objeto Ticket esté completo
            nuevoTicket.setId(folioGenerado);

            String tipo = esAbonado ? "ABONADO" : "NORMAL";
            return "ÉXITO: Ingreso de " + placa + " registrado. Folio: #" + folioGenerado + " (" + tipo + ")";
        } else {
            return "ERROR CRÍTICO: Falló la inserción en la base de datos.";
        }
    }


    /**
     * Genera el contenido de texto plano para un ticket de ingreso, incluyendo el payload del QR.
     * @param ticket El ticket que acaba de ser creado (debe tener el ID).
     * @return String con el formato de impresión.
     */
    public String generarTicketImpresion(Ticket ticket) {
        if (ticket == null || ticket.getId() == 0) return "ERROR: TICKET NULO O SIN FOLIO ASIGNADO";

        StringBuilder ticketText = new StringBuilder();
        ticketText.append("------------------------------------------\n");
        ticketText.append("           CRUD PARK - BIENVENIDO         \n");
        ticketText.append("------------------------------------------\n");
        ticketText.append("FOLIO:      #").append(ticket.getId()).append("\n");
        ticketText.append("PLACA:      ").append(ticket.getPlaca()).append("\n");
        ticketText.append("FECHA ING.: ").append(ticket.getFechaIngreso().toLocalDate()).append("\n");
        ticketText.append("HORA ING.:  ").append(ticket.getFechaIngreso().toLocalTime()).append("\n");
        ticketText.append("OPERADOR:   ").append(ticket.getOperadorIngreso().getUsuario()).append("\n");
        ticketText.append("------------------------------------------\n");

        // Generar el payload del QR usando constantes
        String qrPayload = QR_PAYLOAD_PREFIX + ticket.getId() + QR_PAYLOAD_SEPARATOR + ticket.getPlaca();

        ticketText.append("     CODIGO DE LECTURA RAPIDA (ESCANEAR)  \n");
        ticketText.append("     ").append(qrPayload).append("\n");
        ticketText.append("------------------------------------------\n");

        ticketText.append("    CONSERVE SU TICKET, GRACIAS POR USAR  \n");
        ticketText.append("            NUESTROS SERVICIOS.           \n");
        ticketText.append("------------------------------------------\n");

        return ticketText.toString();
    }


    /**
     * Busca un ticket abierto para una placa.
     */
    public Ticket getOpenTicket(String placa) {
        if (placa == null || placa.isEmpty()) return null;
        return ticketDAO.findOpenTicketByPlaca(placa.toUpperCase());
    }

    /**
     * Calcula el monto a pagar por un ticket.
     * @param ticket El ticket abierto.
     * @return Monto total o 0 si es mensualidad/gracia.
     */
    public BigDecimal calcularMonto(Ticket ticket) {
        if (ticket == null || ticket.getFechaIngreso() == null) {
            return BigDecimal.ZERO;
        }

        Tarifa tarifa = tarifaDAO.findActiveTarifa();
        if (tarifa == null) {
            // Manejar error: No hay tarifa activa
            return new BigDecimal("-1.00"); // Devolver un valor que indique error de tarifa
        }

        LocalDateTime salida = LocalDateTime.now();
        Duration duracion = Duration.between(ticket.getFechaIngreso(), salida);
        long minutosTotales = duracion.toMinutes();

        // 1. Verificar si es Mensualidad (no cobramos)
        Mensualidad mensualidad = mensualidadDAO.findByPlaca(ticket.getPlaca());
        if (mensualidad != null && mensualidad.esMensualidadValidaHoy()) {
            return BigDecimal.ZERO;
        }

        // 2. Aplicar Minutos de Gracia
        if (minutosTotales <= tarifa.getMinutosGracia()) {
            return BigDecimal.ZERO;
        }

        // 3. Cálculo basado en fracción de minutos
        long minutosCobro = minutosTotales - tarifa.getMinutosGracia();

        BigDecimal totalMinutos = new BigDecimal(minutosCobro);

        BigDecimal monto = totalMinutos.multiply(tarifa.getValorFraccionMinuto());

        // Redondeo a 2 decimales y devuelve el monto.
        // Se recomienda usar RoundingMode.HALF_UP o HALF_EVEN, pero mantendremos ROUND_UP como lo tienes:
        return monto.setScale(2, BigDecimal.ROUND_UP);
    }

    /**
     * Cierra el ticket y registra el pago (Lógica de Pago y Cierre).
     */
    public boolean finalizarSalida(Ticket ticket, Operador operador, BigDecimal montoPagado) {

        if (montoPagado == null || montoPagado.compareTo(BigDecimal.ZERO) < 0) {
            return false;
        }

        LocalDateTime fechaSalida = LocalDateTime.now();

        // 1. Llamar al DAO para actualizar el registro del ticket
        boolean ticketCerrado = ticketDAO.closeTicket(
                ticket.getId(),
                operador.getId(),
                fechaSalida
        );

        // 2. Opcional: Registrar el pago en la tabla 'pagos' (se haría con un PagoDAO)
        // Ya que la tabla 'pagos' existe, esta es la línea real que faltaría implementar.

        return ticketCerrado;
    }

    /**
     * Busca el ticket abierto más reciente para una placa.
     * Este método se usa principalmente para la impresión inmediata tras el registro.
     * @param placa La placa del vehículo.
     * @return El objeto Ticket abierto o null.
     */
    public Ticket getLatestOpenTicketByPlaca(String placa) {
        return ticketDAO.findLatestOpenTicketByPlaca(placa);
    }
}