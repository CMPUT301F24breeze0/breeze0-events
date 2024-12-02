package com.example.breeze0events;
import android.graphics.Bitmap;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
/**
 * The QRHashGenerator class provides utility methods to:
 * 1. Generate a SHA-256 hash from an input string.
 * 2. Generate a QR code bitmap from a hashed string.
 */
public class QRHashGenerator {

    /**
     * Generates a SHA-256 hash from the provided input string.
     *
     * @param input The input string to hash.
     * @return The SHA-256 hash of the input string, or null if hashing fails.
     */
    public static String generateHash(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(input.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
    /**
     * Generates a QR code bitmap from the provided hashed string.
     *
     * @param qrHashCode The hashed string to encode in the QR code.
     * @return A Bitmap representing the QR code, or null if generation fails.
     */
    public static Bitmap generateQRCode(String qrHashCode) {
        QRCodeWriter writer = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = writer.encode(qrHashCode, BarcodeFormat.QR_CODE, 512, 512); // QR code size 512x512
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bmp.setPixel(x, y, bitMatrix.get(x, y) ? android.graphics.Color.BLACK : android.graphics.Color.WHITE);
                }
            }
            return bmp;
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }
}
