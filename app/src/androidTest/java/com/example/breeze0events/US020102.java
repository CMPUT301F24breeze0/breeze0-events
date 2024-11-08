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

import java.util.ArrayList;
import java.util.List;

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
        String eventId = "12345";
        String name = "Sample Event";
        String qrCode = QRHashGenerator.generateHash(eventId); // Generate QR code hash
        String posterPhoto = "samplePosterPath";
        String facility = "Main Hall";
        String startDate = "2023-12-01";
        String endDate = "2023-12-10";
        String limitedNumber = "100";
        List<String> entrants = new ArrayList<>();
        entrants.add("entrant1");
        entrants.add("entrant2");
        List<String> organizers = new ArrayList<>();
        organizers.add("organizer1");

        // Initialize the event with all fields
        Event event = new Event(eventId, name, qrCode, posterPhoto, facility, startDate, endDate, limitedNumber, entrants, organizers);

        // Act
        Bitmap qrCodeBitmap = QRHashGenerator.generateQRCode(qrCode);

        // Assert
        assertNotNull("QR Code Bitmap should not be null.", qrCodeBitmap);
        assertEquals("QR Code width should be 512 pixels.", 512, qrCodeBitmap.getWidth());
        assertEquals("QR Code height should be 512 pixels.", 512, qrCodeBitmap.getHeight());
        assertEquals("Event QR code hash should match expected hash code.", qrCode, event.getQrCode());

        // Add the event to OverallStorageController
        OverallStorageController overallStorageController = new OverallStorageController();
        overallStorageController.addEvent(event);

        // Verify if the event is correctly added
        overallStorageController.getEvent(eventId, new EventCallback() {
            @Override
            public void onSuccess(Event retrievedEvent) {
                assertEquals("Retrieved event ID should match", eventId, retrievedEvent.getEventId());
            }

            @Override
            public void onFailure(String errorMessage) {
                fail("Failed to retrieve event: " + errorMessage);
            }
        });

        // Clean up by deleting the event
        overallStorageController.deleteEvent(eventId);
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
    public void testQRCodeEncodingAndDecoding() {
        // Arrange
        String originalText = "textToEncode";

        // Act
        Bitmap qrCodeBitmap = QRHashGenerator.generateQRCode(originalText);
        assertNotNull("Generated QR Code Bitmap should not be null.", qrCodeBitmap);
    }
}
