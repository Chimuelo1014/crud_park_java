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
            // Podrías registrar un tipo de ticket diferente o imprimir un mensaje especial.
        }

        // 3. CREAR EL NUEVO TICKET
        LocalDateTime now = LocalDateTime.now();
        Ticket nuevoTicket = new Ticket(placa, now, operador);

        int folioGenerado = ticketDAO.create(nuevoTicket);

        if (folioGenerado > 0) {
            String tipo = esAbonado ? "ABONADO" : "NORMAL";
            return "ÉXITO: Ingreso de " + placa + " registrado. Folio: #" + folioGenerado + " (" + tipo + ")";
        } else {
            return "ERROR CRÍTICO: Falló la inserción en la base de datos.";
        }
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
            return new BigDecimal(-1);
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
        // Descontamos minutos de gracia para el cobro
        long minutosCobro = minutosTotales - tarifa.getMinutosGracia();

        // Monto = minutos_cobro * valor_fraccion_minuto
        BigDecimal totalMinutos = new BigDecimal(minutosCobro);

        // Nota: La tarifa de PostgreSQL se insertó como 50.00 por hora / 0.84 por minuto.
        BigDecimal monto = totalMinutos.multiply(tarifa.getValorFraccionMinuto());

        // Redondeo a 2 decimales y devuelve el monto.
        return monto.setScale(2, BigDecimal.ROUND_UP);
    }

    /**
     * Cierra el ticket y registra el pago (Lógica de Pago y Cierre).
     */
    public boolean finalizarSalida(Ticket ticket, Operador operador, BigDecimal montoPagado) {
        // En un sistema real, aquí se llamaría a un PagoDAO. Por ahora, solo cerramos el ticket.

        if (montoPagado.compareTo(BigDecimal.ZERO) < 0) {
            // Error de cálculo o intento de pago negativo
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
        // pagoDAO.create(ticket.getId(), montoPagado, "Efectivo", operador.getId());

        return ticketCerrado;
    }
}