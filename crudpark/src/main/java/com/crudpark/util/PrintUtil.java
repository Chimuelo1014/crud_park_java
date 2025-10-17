package com.crudpark.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import javax.print.*;
import javax.print.attribute.*;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.MediaPrintableArea;
import javax.print.attribute.standard.MediaSize;
import javax.print.attribute.standard.MediaSizeName;
import javax.print.attribute.standard.PrintQuality;

public class PrintUtil {

    /**
     * Envía una cadena de texto plano a la impresora predeterminada.
     * @param ticketContent El contenido del ticket generado.
     * @return True si el trabajo de impresión se envía con éxito.
     */
    public static boolean printPlainText(String ticketContent) {
        try {
            // 1. Obtener el servicio de impresión predeterminado
            PrintService defaultPrintService = PrintServiceLookup.lookupDefaultPrintService();

            if (defaultPrintService == null) {
                System.err.println("Error de Impresión: No se encontró la impresora predeterminada.");
                return false;
            }

            // 2. Definir los atributos del trabajo de impresión
            PrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();
            aset.add(new Copies(1));
            // aset.add(MediaSizeName.ISO_A7); // Opcional: para impresoras de recibos pequeñas

            // 3. Crear el documento (InputStream)
            InputStream stream = new ByteArrayInputStream(ticketContent.getBytes());
            DocFlavor flavor = DocFlavor.INPUT_STREAM.AUTOSENSE; // Auto-detectar tipo de datos (texto plano)
            Doc doc = new SimpleDoc(stream, flavor, null);

            // 4. Crear el trabajo de impresión y enviarlo
            DocPrintJob job = defaultPrintService.createPrintJob();
            job.print(doc, aset);

            stream.close();
            return true;

        } catch (Exception e) {
            System.err.println("Error al imprimir el ticket: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}