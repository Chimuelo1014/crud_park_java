package com.crudpark.util;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.print.*;

public class PrintUtil {

    // El método printPlainText debe recibir el contenido del ticket Y la imagen del QR
    public static boolean printTicketWithQR(String ticketContent, BufferedImage qrImage) {
        try {
            PrintService defaultPrintService = PrintServiceLookup.lookupDefaultPrintService();

            if (defaultPrintService == null) {
                System.err.println("Error de Impresión: No se encontró la impresora predeterminada.");
                return false;
            }

            PrinterJob job = PrinterJob.getPrinterJob();
            job.setPrintService(defaultPrintService);

            // Usar la nueva clase interna que dibuja el texto Y la imagen
            job.setPrintable(new TicketPrintJobWithQR(ticketContent, qrImage));

            job.print();
            return true;

        } catch (Exception e) {
            System.err.println("Error al imprimir el ticket con QR: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Clase interna que implementa Printable para dibujar el texto y la imagen QR.
     */
    private static class TicketPrintJobWithQR implements Printable {
        private final String content;
        private final BufferedImage qrImage;
        private final String[] lines;

        public TicketPrintJobWithQR(String content, BufferedImage qrImage) {
            this.content = content;
            this.qrImage = qrImage;
            this.lines = content.split("\n");
        }

        @Override
        public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
            if (pageIndex > 0) {
                return NO_SUCH_PAGE;
            }

            Graphics2D g2d = (Graphics2D) graphics;
            g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

            // --- 1. Dibujar Texto ---
            Font font = new Font("Monospaced", Font.PLAIN, 10);
            g2d.setFont(font);

            int y = 0; // Coordenada Y actual
            int lineHeight = g2d.getFontMetrics().getHeight();

            for (String line : lines) {
                g2d.drawString(line, 0, y);
                y += lineHeight;
            }

            // Agregar un espacio entre el texto y el QR
            y += lineHeight * 2;

            // --- 2. Dibujar QR si existe ---
            if (qrImage != null) {
                // Dibujar la imagen centrada si el recibo lo permite
                int xCenter = (int) ((pageFormat.getImageableWidth() - qrImage.getWidth()) / 2);

                g2d.drawString("ESCANEA PARA SALIDA:", 0, y);
                y += lineHeight;

                // Dibujar la imagen del QR
                g2d.drawImage(qrImage, xCenter, y, null);
            }

            return PAGE_EXISTS;
        }
    }
}