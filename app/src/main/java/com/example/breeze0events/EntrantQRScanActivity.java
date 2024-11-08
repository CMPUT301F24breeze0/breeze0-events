package com.example.breeze0events;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.google.zxing.qrcode.QRCodeReader;
import com.google.zxing.RGBLuminanceSource;

import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;

public class EntrantQRScanActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_scan);

        // Show dialog to choose between camera or image upload
        showScanOptionsDialog();
    }

    // Show options dialog
    private void showScanOptionsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose QR Code Scan Method")
                .setMessage("Would you like to scan the QR code with your camera or upload an image?")
                .setPositiveButton("Scan with Camera", (dialog, which) -> startCameraScan())
                .setNegativeButton("Upload Image", (dialog, which) -> selectImageFromGallery())
                .setCancelable(true)
                .show();
    }

    // Start QR scan with camera
    private void startCameraScan() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setPrompt("Scan the Event QR Code");
        integrator.setOrientationLocked(false);
        integrator.initiateScan(); // Start camera scanning
    }

    // Select image from gallery for QR code scanning
    private void selectImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    // Handle activity result for camera scan and image upload
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Handle camera QR code scan result
        if (requestCode == IntentIntegrator.REQUEST_CODE) {
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (result != null && result.getContents() != null) {
                handleQRCodeResult(result.getContents());
            } else {
                Toast.makeText(this, "No QR code found", Toast.LENGTH_SHORT).show();
                finish();
            }
        }

        // Handle image selection for QR code scanning
        else if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                String qrCodeData = decodeQRCodeFromBitmap(bitmap);
                if (qrCodeData != null) {
                    handleQRCodeResult(qrCodeData);
                } else {
                    Toast.makeText(this, "No QR code found in image", Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Decode QR code from Bitmap
    private String decodeQRCodeFromBitmap(Bitmap bitmap) {
        int[] intArray = new int[bitmap.getWidth() * bitmap.getHeight()];
        bitmap.getPixels(intArray, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

        RGBLuminanceSource source = new RGBLuminanceSource(bitmap.getWidth(), bitmap.getHeight(), intArray);
        BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(source));

        QRCodeReader reader = new QRCodeReader();
        try {
            Map<DecodeHintType, Object> hints = new EnumMap<>(DecodeHintType.class);
            hints.put(DecodeHintType.CHARACTER_SET, "UTF-8");
            Result result = reader.decode(binaryBitmap, hints);
            return result.getText();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Handle decoded QR code data
    private void handleQRCodeResult(String qrCodeData) {
        // Assume qrCodeData is the eventID
        Intent intent = new Intent(EntrantQRScanActivity.this, EntrantEventDetail.class);
        intent.putExtra("eventID", qrCodeData);
        startActivity(intent);
        finish();
    }
}
