package com.crudpark.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import java.awt.image.BufferedImage;

public class QRUtil {

    private static final int QR_SIZE = 150; // Tamaño en píxeles (adecuado para recibos)

    /**
     * Genera la imagen del Código QR como un BufferedImage.
     * @param text El contenido (Payload) a codificar (Ej: FOLIO:123|PLACA:ABC).
     * @return BufferedImage de la imagen QR, o null si falla.
     */
    public static BufferedImage generateQRCodeImage(String text) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, QR_SIZE, QR_SIZE);

            // Convertir BitMatrix a BufferedImage
            return MatrixToImageWriter.toBufferedImage(bitMatrix);

        } catch (Exception e) {
            System.err.println("Error al generar el Código QR: " + e.getMessage());
            return null;
        }
    }
}