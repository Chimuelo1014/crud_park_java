package com.crudpark.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class QRUtil {

    // Reducir el tamaño de 150 a un valor bajo (ej. 80) para asegurar que quepa en el papel de 58mm,
    // ya que el driver reporta un área de impresión muy pequeña.
    private static final int QR_SIZE = 80; // <-- VALOR AJUSTADO

    /**
     * Genera la imagen del Código QR como un BufferedImage.
     * @param text El contenido (Payload) a codificar.
     * @return BufferedImage de la imagen QR, o null si falla.
     */
    public static BufferedImage generateQRCodeImage(String text) {
        try {
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
            hints.put(EncodeHintType.MARGIN, 0); // Establecer el margen del QR a 0

            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(
                    text,
                    BarcodeFormat.QR_CODE,
                    QR_SIZE,
                    QR_SIZE,
                    hints
            );

            // Crea la imagen
            return MatrixToImageWriter.toBufferedImage(bitMatrix);

        } catch (Exception e) {
            System.err.println("Error al generar el Código QR: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
