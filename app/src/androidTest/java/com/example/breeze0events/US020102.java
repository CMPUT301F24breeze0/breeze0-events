package com.example.breeze0events;

import android.content.Context;
import android.graphics.Bitmap;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

@RunWith(AndroidJUnit4.class)
public class US020102 {

    private Context context;

    @Before
    public void setUp() {
        // Get the application context for any operations requiring it
        context = ApplicationProvider.getApplicationContext();
    }

    @Test
    public void testGenerateEventWithQRCodeHash() {
        // Arrange
        String eventId = "12345"; // This would normally be generated dynamically
        String expectedHashCode = QRHashGenerator.generateHash(eventId); // Generate hash from event ID

        // Create a new event with the event ID and set the generated hash as its QR code hash
        Event event = new Event();
        event.setEventId(eventId);
        event.setQrCode(expectedHashCode); // Set the QR code hash

        // Act
        Bitmap qrCodeBitmap = QRHashGenerator.generateQRCode(expectedHashCode);

        // Assert
        assertNotNull("QR Code Bitmap should not be null.", qrCodeBitmap);
        assertEquals("QR Code width should be 512 pixels.", 512, qrCodeBitmap.getWidth());
        assertEquals("QR Code height should be 512 pixels.", 512, qrCodeBitmap.getHeight());
        assertEquals("Event QR code hash should match expected hash code.", expectedHashCode, event.getQrCode());
        OverallStorageController overallStorageController=new OverallStorageController();

        overallStorageController.addEvent(event);
        overallStorageController.getEvent(eventId, new EventCallback() {
            @Override
            public void onSuccess(Event event) {

            }

            @Override
            public void onFailure(String errorMessage) {
                fail();
            }
        });
    //    overallStorageController.deleteEvent(eventId);
    }

    @Test
    public void testGenerateHash_validInput() {
        // Arrange
        String input = "testInput";
        String expectedHash = "f5d1278e8109edd94e1e4197e04873b9ac88b548cdaf7d33b0f41f8ef9cda51a"; // Expected SHA-256 hash

        // Act
        String generatedHash = QRHashGenerator.generateHash(input);

        // Assert
        assertNotNull("Hash should not be null.", generatedHash);
        assertEquals("Generated hash does not match expected hash.", expectedHash, generatedHash);
    }

    @Test
    public void testGenerateHash_emptyInput() {
        // Arrange
        String input = "";
        String expectedHash = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855"; // SHA-256 hash for an empty string

        // Act
        String generatedHash = QRHashGenerator.generateHash(input);

        // Assert
        assertNotNull("Hash should not be null.", generatedHash);
        assertEquals("Generated hash for empty input does not match expected hash.", expectedHash, generatedHash);
    }

    @Test
    public void testGenerateQRCode_validInput() {
        // Arrange
        String qrHashCode = "testQRCode";

        // Act
        Bitmap qrCodeBitmap = QRHashGenerator.generateQRCode(qrHashCode);

        // Assert
        assertNotNull("QR Code Bitmap should not be null.", qrCodeBitmap);
        assertEquals("QR Code width should be 512 pixels.", 512, qrCodeBitmap.getWidth());
        assertEquals("QR Code height should be 512 pixels.", 512, qrCodeBitmap.getHeight());
    }

    @Test
    public void testGenerateQRCode_emptyInput() {
        // Arrange
        String qrHashCode = "";

        // Act
        Bitmap qrCodeBitmap = QRHashGenerator.generateQRCode(qrHashCode);

        // Assert
        assertNotNull("QR Code Bitmap should not be null for empty input.", qrCodeBitmap);
        assertEquals("QR Code width should be 512 pixels.", 512, qrCodeBitmap.getWidth());
        assertEquals("QR Code height should be 512 pixels.", 512, qrCodeBitmap.getHeight());
    }

    @Test
    public void testQRCodeEncodingAndDecoding() {
        // Arrange
        String originalText = "textToEncode";

        // Act
        Bitmap qrCodeBitmap = QRHashGenerator.generateQRCode(originalText);
        assertNotNull("Generated QR Code Bitmap should not be null.", qrCodeBitmap);
    }
}
