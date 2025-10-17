package com.crudpark.controller;

import com.crudpark.model.Operador;
import com.crudpark.model.Ticket;
import com.crudpark.service.ParkingService;
import com.crudpark.util.PrintUtil;
import com.crudpark.util.QRUtil; // <-- NUEVA IMPORTACIÓN
import com.crudpark.view.IngresoView;
import java.awt.image.BufferedImage; // <-- NUEVA IMPORTACIÓN

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

        // 3. Obtener el ticket recién creado para imprimir
        Ticket ticketCreado = parkingService.getLatestOpenTicketByPlaca(placa.toUpperCase());

        if (ticketCreado != null) {

            // =========================================================
            // LÓGICA DE GENERACIÓN E IMPRESIÓN DEL QR (IMAGEN)
            // =========================================================

            // A. Generar el Payload (el texto que contendrá el QR)
            String qrPayload = "FOLIO:" + ticketCreado.getId() + "|PLACA:" + ticketCreado.getPlaca();

            // B. Generar la imagen del QR (BufferedImage)
            BufferedImage qrImage = QRUtil.generateQRCodeImage(qrPayload);

            // C. Generar el contenido del ticket (SOLO TEXTO)
            String ticketContent = parkingService.generarTicketImpresion(ticketCreado);

            // D. Imprimir el texto y la imagen del QR
            boolean impresionExitosa = PrintUtil.printTicketWithQR(ticketContent, qrImage);

            // =========================================================

            String mensajeFinal = resultadoMensaje;
            if (impresionExitosa) {
                mensajeFinal += "\nTicket impreso con éxito.";
            } else {
                mensajeFinal += "\nERROR: No se pudo imprimir el ticket con QR (Verifique la impresora y la configuración de Drivers).";
            }

            view.showMessage(mensajeFinal, false);
            view.clearPlaca();
        } else {
            view.showMessage("ÉXITO en el registro, pero el sistema no encontró el ticket para imprimir.", true);
        }
    }
}