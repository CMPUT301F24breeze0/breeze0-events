package com.example.breeze0events;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


/**
 * Activity class for displaying a QR code of certain event to the administrator.
 * The activity retrieves QR code data from the intent, generates a QR code bitmap from the data,
 * and displays it in an ImageView.
 */

public class AdminQRcode extends AppCompatActivity {
    Button backButton;
    ImageView qrCodeImageView;

    /**
     * Initializes the activity, setting up the QR code display and the back button functionality.
     *
     * @param savedInstanceState
     * If the activity is being re-initialized after previously being shut down,
     * this Bundle contains the data it most recently supplied.
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qr_code_fragment);

        backButton = findViewById(R.id.backButton);
        qrCodeImageView = findViewById(R.id.QRcode);
        String qrCodeData = getIntent().getStringExtra("qrcode");

        backButton.setOnClickListener(v -> {
            finish();
        });

        if (qrCodeData != null) {
            Bitmap qrCodeBitmap = QRHashGenerator.generateQRCode(qrCodeData);

            if (qrCodeBitmap != null) {
                qrCodeImageView.setImageBitmap(qrCodeBitmap);
            } else {
                Toast.makeText(this, "Failed to generate QR code", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "No QR code data provided", Toast.LENGTH_SHORT).show();
        }

    }
}
