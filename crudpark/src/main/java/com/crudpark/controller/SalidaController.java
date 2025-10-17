package com.crudpark.controller;

import com.crudpark.model.Operador;
import com.crudpark.model.Ticket;
import com.crudpark.service.ParkingService;
import com.crudpark.view.SalidaView;
import java.math.BigDecimal;

public class SalidaController {

    private final ParkingService parkingService;
    private final SalidaView view;
    private Ticket ticketActual;
    private BigDecimal montoActual;

    // Constante para reconocer el formato QR/Payload del ticket
    private static final String QR_PAYLOAD_PREFIX = "FOLIO:";

    public SalidaController(SalidaView view) {
        this.view = view;
        this.parkingService = new ParkingService();
        this.montoActual = BigDecimal.ZERO;
    }

    /**
     * Maneja la búsqueda de un ticket por placa o por el payload del QR (Folio).
     */
    public void handleBuscar(String input) {
        if (input.isEmpty()) {
            view.showMessage("Debe ingresar la Placa o escanear el Folio/QR.", true);
            view.displayTicketInfo(null, BigDecimal.ZERO);
            return;
        }

        // Limpia el estado anterior
        ticketActual = null;
        montoActual = BigDecimal.ZERO;

        Ticket ticket;
        String placaBusqueda;

        // 1. Lógica para manejar el Payload del QR (Ej: FOLIO:12345|PLACA:ABC123)
        if (input.startsWith(QR_PAYLOAD_PREFIX)) {
            try {
                // Parseamos el payload (asumiendo que el separador es '|PLACA:')
                String[] parts = input.split("\\|PLACA:");

                // Extraemos el Folio y lo buscamos (usando búsqueda por placa, ya que no tienes findById)
                placaBusqueda = parts[1].trim();
                ticket = parkingService.getOpenTicket(placaBusqueda);

            } catch (Exception e) {
                view.showMessage("Error al procesar el código QR/Folio. Intente con la Placa.", true);
                view.displayTicketInfo(null, BigDecimal.ZERO);
                return;
            }
        } else {
            // 2. Búsqueda normal por Placa
            placaBusqueda = input;
            ticket = parkingService.getOpenTicket(placaBusqueda);
        }

        // 3. Resultado de la búsqueda
        if (ticket != null) {
            ticketActual = ticket;
            montoActual = parkingService.calcularMonto(ticketActual);

            if (montoActual.compareTo(new BigDecimal("-1.00")) == 0) {
                view.showMessage("ERROR: No se pudo calcular la tarifa. Verifique si hay una Tarifa activa.", true);
                view.displayTicketInfo(null, BigDecimal.ZERO);
                return;
            }

            view.displayTicketInfo(ticketActual, montoActual);
        } else {
            view.displayTicketInfo(null, BigDecimal.ZERO);
        }
    }

    /**
     * Maneja el cierre del ticket y el cobro.
     */
    public void handlePagar(Operador operador) {
        if (ticketActual == null || montoActual == null) {
            view.showMessage("Busque un ticket válido antes de pagar.", true);
            return;
        }

        // 1. Lógica de validación final y cierre
        boolean exito = parkingService.finalizarSalida(ticketActual, operador, montoActual);

        if (exito) {
            view.showMessage("Salida registrada con éxito. Total cobrado: $" + String.format("%.2f", montoActual), false);
            // 2. Reiniciar la vista después del éxito
            view.resetView();
            ticketActual = null;
            montoActual = BigDecimal.ZERO;
        } else {
            view.showMessage("Error al cerrar el ticket en la base de datos.", true);
        }
    }

    public Ticket getTicketActual() { return ticketActual; }
    public BigDecimal getMontoActual() { return montoActual; }
}