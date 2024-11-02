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
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;

public class ImageHashGenerator {

    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final byte[] FIXED_KEY_BYTES = "1234567890123456".getBytes();
    private static final SecretKey DEFAULT_SECRET_KEY = new SecretKeySpec(FIXED_KEY_BYTES, "AES");

    // Encrypts the image and returns it as a Base64 string, with IV included
    public static String generateHashCode(Context context, Uri imageUri) throws Exception {
        byte[] imageBytes = getImageBytes(context, imageUri);
        if (imageBytes == null) return null;

        Cipher cipher = Cipher.getInstance(ALGORITHM);

        // 生成随机 IV 并将其与密文一起存储
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        // Initialize the cipher for encryption
        cipher.init(Cipher.ENCRYPT_MODE, DEFAULT_SECRET_KEY, ivSpec);

        // Encrypt the byte array
        byte[] encryptedBytes = cipher.doFinal(imageBytes);

        // 将 IV 和密文组合
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(iv); // 先写 IV
        outputStream.write(encryptedBytes); // 然后写加密内容

        // Encode to Base64 string for easy storage or transfer
        return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);
    }

    // Decrypts the Base64 string back into a Bitmap image
    public static Bitmap decryptImage(String encryptedImage) throws Exception {
        byte[] encryptedData = Base64.decode(encryptedImage, Base64.DEFAULT);

        // 从加密数据中提取 IV 和密文
        byte[] iv = new byte[16];
        System.arraycopy(encryptedData, 0, iv, 0, iv.length);
        byte[] encryptedBytes = new byte[encryptedData.length - iv.length];
        System.arraycopy(encryptedData, iv.length, encryptedBytes, 0, encryptedBytes.length);

        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, DEFAULT_SECRET_KEY, ivSpec);

        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        return BitmapFactory.decodeByteArray(decryptedBytes, 0, decryptedBytes.length);
    }

    // Helper method: Converts the image at the given Uri into a byte array
    private static byte[] getImageBytes(Context context, Uri imageUri) throws IOException {
        ContentResolver contentResolver = context.getContentResolver();
        InputStream inputStream = contentResolver.openInputStream(imageUri);

        if (inputStream == null) {
            return null;
        }

        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
        inputStream.close();

        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteStream);
        return byteStream.toByteArray();
    }
}
