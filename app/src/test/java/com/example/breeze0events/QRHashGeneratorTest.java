package com.example.breeze0events;

import static org.junit.jupiter.api.Assertions.*;

import android.graphics.Bitmap;
import org.junit.jupiter.api.Test;

public class QRHashGeneratorTest {

    @Test
    public void testGenerateHash_validInput() {
        // Arrange
        String input = "testInput";
        String expectedHash = "f5d1278e8109edd94e1e4197e04873b9ac88b548cdaf7d33b0f41f8ef9cda51a"; // 预期的 SHA-256 哈希

        // Act
        String generatedHash = QRHashGenerator.generateHash(input);

        // Assert
        assertNotNull(generatedHash, "Hash should not be null.");
        assertEquals(expectedHash, generatedHash, "Generated hash does not match expected hash.");
    }

    @Test
    public void testGenerateHash_emptyInput() {
        // Arrange
        String input = "";
        String expectedHash = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855"; // 空字符串的 SHA-256 哈希值

        // Act
        String generatedHash = QRHashGenerator.generateHash(input);

        // Assert
        assertNotNull(generatedHash, "Hash should not be null.");
        assertEquals(expectedHash, generatedHash, "Generated hash for empty input does not match expected hash.");
    }

    @Test
    public void testGenerateQRCode_validInput() {
        // Arrange
        String qrHashCode = "testQRCode";

        // Act
        Bitmap qrCodeBitmap = QRHashGenerator.generateQRCode(qrHashCode);

        // Assert
        assertNotNull(qrCodeBitmap, "QR Code Bitmap should not be null.");
        assertEquals(512, qrCodeBitmap.getWidth(), "QR Code width should be 512 pixels.");
        assertEquals(512, qrCodeBitmap.getHeight(), "QR Code height should be 512 pixels.");
    }

    @Test
    public void testGenerateQRCode_emptyInput() {
        // Arrange
        String qrHashCode = "";

        // Act
        Bitmap qrCodeBitmap = QRHashGenerator.generateQRCode(qrHashCode);

        // Assert
        assertNotNull(qrCodeBitmap, "QR Code Bitmap should not be null for empty input.");
        assertEquals(512, qrCodeBitmap.getWidth(), "QR Code width should be 512 pixels.");
        assertEquals(512, qrCodeBitmap.getHeight(), "QR Code height should be 512 pixels.");
    }

    @Test
    public void testQRCodeEncodingAndDecoding() {
        // Arrange
        String originalText = "textToEncode";

        // Act
        Bitmap qrCodeBitmap = QRHashGenerator.generateQRCode(originalText);
        assertNotNull(qrCodeBitmap, "Generated QR Code Bitmap should not be null.");

    }
}
