package com.example.breeze0events;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;


/**
 * Activity class for displaying a QR code of certain event to the administrator.
 * The activity retrieves QR code data from the intent, generates a QR code bitmap from the data,
 * and displays it in an ImageView.
 */

public class AdminQRcode extends AppCompatActivity {
    private Button backButton;
    private Button deleteButton;
    private ImageView qrCodeImageView;
    private String qrCodeData;
    private String qrCodeHash;


    /**
     * Initializes the activity, setting up the QR code display and the back button functionality.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     *                           this Bundle contains the data it most recently supplied.
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qr_code_fragment);

        backButton = findViewById(R.id.backButton);
        deleteButton = findViewById(R.id.delete_button);
        qrCodeImageView = findViewById(R.id.QRcode);
        qrCodeData = getIntent().getStringExtra("qrcode");

        qrCodeHash = QRHashGenerator.generateHash(qrCodeData);
        Log.d("AdminQRcode", "Generated QR Code Hash: " + qrCodeHash);



        if (qrCodeHash != null) {
            Bitmap qrCodeBitmap = QRHashGenerator.generateQRCode(qrCodeHash);

            if (qrCodeBitmap != null) {
                qrCodeImageView.setImageBitmap(qrCodeBitmap);
            }
        } else {
            Toast.makeText(this, "No QR code data provided", Toast.LENGTH_SHORT).show();
        }


        backButton.setOnClickListener(v -> {
            finish();
        });

        deleteButton.setOnClickListener(v -> showDeleteConfirmationDialog());
    }

    /**
     * Show a confirmation dialog before deleting QR code data.
     */
    private void showDeleteConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Delete QR Code")
                .setMessage("Are you sure to delete this QR code?")
                .setPositiveButton("Confirm", (dialog, which) -> deleteQRCodeData())
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();

    }

    /**
     * Delete QR code data.
     */
    private void deleteQRCodeData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();


        if (qrCodeHash != null) {
            db.collection("QRCodes").document(qrCodeHash)
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        qrCodeImageView.setImageBitmap(null);
                        Toast.makeText(this, "QR code deleted successfully", Toast.LENGTH_SHORT).show();
                        deleteButton.setEnabled(false);
                        deleteButton.setVisibility(View.GONE);
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed to delete QR code: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(this, "Error generating hash for QR code", Toast.LENGTH_SHORT).show();
        }

    }
}
