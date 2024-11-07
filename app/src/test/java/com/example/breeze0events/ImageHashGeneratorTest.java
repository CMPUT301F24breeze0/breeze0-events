package com.example.breeze0events;

import static org.junit.jupiter.api.Assertions.*;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import androidx.test.core.app.ApplicationProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageHashGeneratorTest {

    private Context context;
    private Uri imageUri;

    @BeforeEach
    public void setUp() throws Exception {
        context = ApplicationProvider.getApplicationContext();

        // Create a simple 100x100 blank Bitmap
        Bitmap bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);

        // Save the Bitmap to a file and get its URI
        File file = new File(context.getCacheDir(), "test_image.png");
        try (FileOutputStream out = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
        }
        imageUri = Uri.fromFile(file);
    }

    @Test
    public void testGenerateHashCodeAndDecryptImage() throws Exception {
        // Test the generateHashCode method
        String encryptedImage = ImageHashGenerator.generateHashCode(context, imageUri);
        assertNotNull(encryptedImage, "The encrypted image string should not be null.");

        // Test the decryptImage method
        Bitmap decryptedBitmap = ImageHashGenerator.decryptImage(encryptedImage);
        assertNotNull(decryptedBitmap, "The decrypted Bitmap should not be null.");
        assertEquals(100, decryptedBitmap.getWidth(), "The width of the decrypted image should be 100 pixels.");
        assertEquals(100, decryptedBitmap.getHeight(), "The height of the decrypted image should be 100 pixels.");
    }

    @Test
    public void testEncryptionDecryptionConsistency() throws Exception {
        // Generate hash code for the image
        String encryptedImage = ImageHashGenerator.generateHashCode(context, imageUri);
        assertNotNull(encryptedImage, "The encrypted image string should not be null.");

        // Decrypt and re-encrypt to ensure consistency
        Bitmap decryptedBitmap = ImageHashGenerator.decryptImage(encryptedImage);
        assertNotNull(decryptedBitmap, "The decrypted Bitmap should not be null.");

        // Convert the decrypted Bitmap to a byte array for comparison
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        decryptedBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] decryptedBytes = byteArrayOutputStream.toByteArray();

        // Retrieve the original image bytes with a try-catch block
        byte[] originalBytes;
        try {
            originalBytes = ImageHashGenerator.getImageBytes(context, imageUri);
            assertNotNull(originalBytes, "Original image bytes should not be null.");
        } catch (IOException e) {
            fail("Failed to retrieve original image bytes: " + e.getMessage());
            return; // Exit test if we cannot retrieve original bytes
        }

        // Compare the decrypted bytes to the original image bytes
        assertArrayEquals(originalBytes, decryptedBytes, "The decrypted image bytes should match the original image bytes.");
    }
}
