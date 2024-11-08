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
import com.google.zxing.MultiFormatReader;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;

public class EntrantQRScanActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 2;
    private OverallStorageController overallStorageController;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_scan);

        overallStorageController = new OverallStorageController();
        showScanOptionsDialog();
    }

    private void showScanOptionsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose QR Code Scan Method")
                .setMessage("Would you like to scan the QR code with your camera or upload an image?")
                .setPositiveButton("Scan with Camera", (dialog, which) -> startCameraScan())
                .setNegativeButton("Upload Image", (dialog, which) -> selectImageFromGallery())
                .setCancelable(true)
                .show();
    }

    private void startCameraScan() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setPrompt("Scan the Event QR Code");
        integrator.setOrientationLocked(false);
        integrator.initiateScan();
    }

    private void selectImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IntentIntegrator.REQUEST_CODE) {
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (result != null && result.getContents() != null) {
                findEventByQRCodeHash(result.getContents());
            } else {
                Toast.makeText(this, "No QR code found", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                String qrCodeData = decodeQRCodeFromBitmap(bitmap);
                if (qrCodeData != null) {
                    findEventByQRCodeHash(qrCodeData);
                } else {
                    Toast.makeText(this, "No QR code found in image", Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String decodeQRCodeFromBitmap(Bitmap bitmap) {
        int[] intArray = new int[bitmap.getWidth() * bitmap.getHeight()];
        bitmap.getPixels(intArray, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

        RGBLuminanceSource source = new RGBLuminanceSource(bitmap.getWidth(), bitmap.getHeight(), intArray);
        BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(source));

        MultiFormatReader reader = new MultiFormatReader();
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

    private void findEventByQRCodeHash(String qrCodeData) {
        overallStorageController.findEventByQRCodeHash(qrCodeData, new EventCallback() {
            @Override
            public void onSuccess(Event event) {
                Intent intent = new Intent(EntrantQRScanActivity.this, EntrantEventDetail.class);
                intent.putExtra("eventID", event.getEventId());
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(EntrantQRScanActivity.this, "Event not found: " + errorMessage, Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}
