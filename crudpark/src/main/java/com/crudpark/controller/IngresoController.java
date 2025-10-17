package com.crudpark.controller;

import com.crudpark.model.Operador;
import com.crudpark.model.Ticket;
import com.crudpark.service.ParkingService;
import com.crudpark.util.PrintUtil;
import com.crudpark.view.IngresoView;

public class IngresoController {

    private ParkingService parkingService;
    private IngresoView view;

    public IngresoController(IngresoView view) {
        this.view = view;
        this.parkingService = new ParkingService();
    }

    public void handleRegistro(String placa, Operador operador) {

        // 1. Validar placa
        if (placa == null || placa.trim().isEmpty()) {
            view.showMessage("La placa no puede estar vacía.", true);
            return;
        }

        // 2. Registrar el ingreso y obtener el mensaje (el servicio lo registra en DB)
        String resultadoMensaje = parkingService.registrarIngreso(placa.toUpperCase(), operador);

        if (resultadoMensaje.startsWith("ERROR")) {
            view.showMessage(resultadoMensaje, true);
            return;
        }

        // 3. Obtener el ticket recién creado para imprimir (método que debe estar en ParkingService)
        Ticket ticketCreado = parkingService.getLatestOpenTicketByPlaca(placa.toUpperCase());

        if (ticketCreado != null) {
            // Lógica de Impresión
            String ticketContent = parkingService.generarTicketImpresion(ticketCreado);
            boolean impresionExitosa = PrintUtil.printPlainText(ticketContent);

            String mensajeFinal = resultadoMensaje;
            if (impresionExitosa) {
                mensajeFinal += "\nTicket impreso con éxito.";
            } else {
                mensajeFinal += "\nERROR: No se pudo imprimir el ticket (Verifique la impresora).";
            }

            view.showMessage(mensajeFinal, false);
            view.clearPlaca(); // Limpia el campo de texto si el registro fue exitoso
        } else {
            // Esto solo ocurre si la inserción en el DAO funciona pero la búsqueda falla.
            view.showMessage("ÉXITO en el registro, pero el sistema no encontró el ticket para imprimir.", true);
        }
    }
}