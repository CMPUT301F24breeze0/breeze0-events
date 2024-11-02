package com.example.breeze0events;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class ImageHashGenerator {

    private static final String ALGORITHM = "AES/ECB/PKCS5Padding";
    private static final int KEY_SIZE = 128; // AES 128-bit encryption

    // Default SecretKey for encryption and decryption
    private static final SecretKey DEFAULT_SECRET_KEY = generateDefaultKey();

    // Generates a default AES SecretKey for internal use
    private static SecretKey generateDefaultKey() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES"); // AES without mode/padding
            keyGenerator.init(KEY_SIZE);
            return keyGenerator.generateKey();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Encrypts the image and returns it as a Base64 string
    public static String generateHashCode(Context context, Uri imageUri) throws Exception {
        // Convert the image at the given Uri to a byte array
        byte[] imageBytes = getImageBytes(context, imageUri);
        if (imageBytes == null) return null;

        // Initialize the cipher for encryption with specified algorithm and key
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, DEFAULT_SECRET_KEY);

        // Encrypt the byte array
        byte[] encryptedBytes = cipher.doFinal(imageBytes);

        // Encode the encrypted bytes to Base64 string for easy storage or transfer
        return Base64.encodeToString(encryptedBytes, Base64.DEFAULT);
    }

    // Decrypts the Base64 string back into a Bitmap image
    public static Bitmap decryptImage(String encryptedImage) throws Exception {
        // Decode the Base64 string back into encrypted bytes
        byte[] encryptedBytes = Base64.decode(encryptedImage, Base64.DEFAULT);

        // Initialize the cipher for decryption with specified algorithm and key
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, DEFAULT_SECRET_KEY);

        // Decrypt the byte array
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

        // Convert the decrypted bytes back into a Bitmap
        return BitmapFactory.decodeByteArray(decryptedBytes, 0, decryptedBytes.length);
    }

    // Helper method: Converts the image at the given Uri into a byte array
    private static byte[] getImageBytes(Context context, Uri imageUri) throws IOException {
        ContentResolver contentResolver = context.getContentResolver();
        InputStream inputStream = contentResolver.openInputStream(imageUri);

        if (inputStream == null) {
            return null;
        }

        // Decode the image into a Bitmap
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
        inputStream.close();

        // Convert the Bitmap into a byte array
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteStream); // Compress to PNG format
        return byteStream.toByteArray();
    }
}
