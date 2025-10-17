package com.crudpark.util;

import javax.print.*;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import java.awt.*;
import java.awt.print.*;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class PrintUtil {

    /**
     * Envía una cadena de texto a la impresora, usando la interfaz Printable
     * para asegurar la compatibilidad con impresoras gráficas y PDF.
     * @param ticketContent El contenido del ticket generado.
     * @return True si el trabajo de impresión se envía con éxito.
     */
    public static boolean printPlainText(String ticketContent) {
        try {
            PrintService defaultPrintService = PrintServiceLookup.lookupDefaultPrintService();

            if (defaultPrintService == null) {
                System.err.println("Error de Impresión: No se encontró la impresora predeterminada.");
                return false;
            }

            // Crear un trabajo que usará la clase TicketPrintJob (definida abajo)
            PrinterJob job = PrinterJob.getPrinterJob();
            job.setPrintService(defaultPrintService);
            job.setPrintable(new TicketPrintJob(ticketContent));

            // Configurar el tamaño de página (específico para recibos si es necesario)
            PageFormat pageFormat = job.defaultPage();
            // Si es una impresora de recibos, puedes intentar reducir el ancho, pero generalmente no es necesario para texto plano.
            // pageFormat.setOrientation(PageFormat.PORTRAIT);

            job.setPrintable(new TicketPrintJob(ticketContent), pageFormat);

            // job.printDialog(); // Opcional: Para que el usuario elija la impresora

            job.print();
            return true;

        } catch (Exception e) {
            System.err.println("Error al imprimir el ticket: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Clase interna que implementa la interfaz Printable para dibujar el texto.
     */
    private static class TicketPrintJob implements Printable {
        private final String content;
        private final String[] lines;

        public TicketPrintJob(String content) {
            this.content = content;
            // Dividir el contenido en líneas para dibujar cada una
            this.lines = content.split("\n");
        }

        @Override
        public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
            if (pageIndex > 0) {
                return NO_SUCH_PAGE;
            }

            Graphics2D g2d = (Graphics2D) graphics;
            g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

            // --- Estilos ---
            // Usar una fuente monoespaciada para que las columnas se alineen
            Font font = new Font("Monospaced", Font.PLAIN, 10);
            g2d.setFont(font);

            int y = 0; // Coordenada Y
            int lineHeight = g2d.getFontMetrics().getHeight();

            // Dibujar cada línea
            for (String line : lines) {
                g2d.drawString(line, 0, y);
                y += lineHeight;
            }

            return PAGE_EXISTS;
        }
    }
}