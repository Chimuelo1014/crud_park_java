package com.crudpark.controller;

import com.crudpark.model.Operador;
import com.crudpark.model.Ticket;
import com.crudpark.service.ParkingService;
import com.crudpark.view.SalidaView;
import java.math.BigDecimal;

public class SalidaController {

    private ParkingService parkingService;
    private SalidaView view;
    private Ticket ticketActual;
    private BigDecimal montoActual;

    public SalidaController(SalidaView view) {
        this.view = view;
        this.parkingService = new ParkingService();
        this.montoActual = BigDecimal.ZERO;
    }

    public void handleBuscar(String placa) {
        if (placa.isEmpty()) {
            view.showMessage("Debe ingresar una placa.", true);
            return;
        }

        ticketActual = parkingService.getOpenTicket(placa);

        if (ticketActual != null) {
            montoActual = parkingService.calcularMonto(ticketActual);
            view.displayTicketInfo(ticketActual, montoActual);
        } else {
            view.displayTicketInfo(null, BigDecimal.ZERO);
        }
    }

    public void handlePagar(Operador operador) {
        if (ticketActual == null || montoActual == null) {
            view.showMessage("Busque un ticket antes de pagar.", true);
            return;
        }

        // Lógica de validación final y cierre
        boolean exito = parkingService.finalizarSalida(ticketActual, operador, montoActual);

        if (exito) {
            view.showMessage("Salida registrada con éxito. Total cobrado: $" + montoActual.toString(), false);
            // Reiniciar la vista después del éxito
            view.dispose();
        } else {
            view.showMessage("Error al cerrar el ticket en la base de datos.", true);
        }
    }
}