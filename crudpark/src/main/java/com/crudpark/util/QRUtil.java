package com.crudpark.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import java.io.ByteArrayOutputStream;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class QRUtil {

    private static final int QR_SIZE = 150; // Tamaño en píxeles (para recibo)

    /**
     * Genera la imagen del Código QR como un array de bytes.
     * @param text El contenido (Payload) a codificar (Ej: FOLIO:123|PLACA:ABC).
     * @return Array de bytes de la imagen PNG, o null si falla.
     */
    public static byte[] generateQRCodeImage(String text) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, QR_SIZE, QR_SIZE);

            // Convertir BitMatrix a BufferedImage
            BufferedImage image = MatrixToImageWriter.toBufferedImage(bitMatrix);

            // Convertir BufferedImage a ByteArrayOutputStream (PNG)
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "PNG", baos);

            return baos.toByteArray();

        } catch (Exception e) {
            System.err.println("Error al generar el Código QR: " + e.getMessage());
            return null;
        }
    }
}