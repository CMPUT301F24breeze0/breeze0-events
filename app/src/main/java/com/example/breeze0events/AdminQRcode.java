package com.example.breeze0events;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
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
    private String eventId;


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

        backButton = findViewById(R.id.back_in_main);
        deleteButton = findViewById(R.id.delete_button);
        qrCodeImageView = findViewById(R.id.QRcode);

        qrCodeData = getIntent().getStringExtra("qrcode");
        eventId = getIntent().getStringExtra("eventId");

        //Log.d("AdminQRcode", "Received eventId: " + eventId);
        Log.d("AdminQRcode", "Received qrCodeData: " + qrCodeData);
        if (qrCodeData != null && !qrCodeData.isEmpty()) {
            qrCodeHash = QRHashGenerator.generateHash(qrCodeData);
            Log.d("AdminQRcode", "Generated QR Code Hash: " + qrCodeHash);
        } else {
            qrCodeHash = null;
        }

        loadQRCode();

        backButton.setOnClickListener(v -> {
            finish();
        });

        deleteButton.setOnClickListener(v -> showDeleteConfirmationDialog());
    }

    /**
     * Load and display the QR code from FireStore.
     */
    private void loadQRCode() {
        if (qrCodeData == null || qrCodeData.isEmpty()) {
            QRCodeMissing();
            return;
        }

        Bitmap qrCodeBitmap = QRHashGenerator.generateQRCode(qrCodeHash);
        if (qrCodeBitmap != null) {
            qrCodeImageView.setImageBitmap(qrCodeBitmap);
            deleteButton.setEnabled(true);
        }
    }


    /**
     * The case where QR code data is missing.
     */
    private void QRCodeMissing() {
        qrCodeImageView.setImageBitmap(null);
        deleteButton.setEnabled(false);
        Toast.makeText(this, "QR code data not available.", Toast.LENGTH_SHORT).show();
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
     * */
    private void deleteQRCodeData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("OverallDB").document(eventId)
                .update("qrCode", null)
                .addOnSuccessListener(aVoid -> {
                    Log.d("AdminQRcode", "QR code field successfully cleared in Firestore");
                    qrCodeImageView.setImageBitmap(null);
                    deleteButton.setEnabled(false);
                    Toast.makeText(this, "QR code deleted successfully", Toast.LENGTH_SHORT).show();
                    BackToEventList(); // in case user click the qr code again, but it is not updated
                })
                .addOnFailureListener(e -> {
                    Log.e("AdminQRcode", "Failed to delete QR code field", e);
                });
    }

    /**
     * In in case user click the qr code again, but it is not updated
     */
    private void BackToEventList() {
        Intent intent = new Intent(AdminQRcode.this, AdminEventActivity.class);
        startActivity(intent);
        finish();
    }


}


//old version
//    private void loadQRCode() {
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//
//        db.collection("OverallDB").document(eventId)
//                .get()
//                .addOnSuccessListener(documentSnapshot -> {
//                    if (documentSnapshot.exists()) {
//                        String qrCodeValue = documentSnapshot.getString("qrCode");
//
//                        if (qrCodeValue != null) {
//                            Bitmap qrCodeBitmap = QRHashGenerator.generateQRCode(qrCodeValue);
//                            if (qrCodeBitmap != null) {
//                                qrCodeImageView.setImageBitmap(qrCodeBitmap);
//                                deleteButton.setEnabled(true);
//                            }
//                        } else {
//                            QRCodeMissing();
//                        }
//                    } else {
//                        QRCodeMissing();
//                    }
//                })
//    }

//    /**
//     * Old version of Delete QR code data.
//     */
//    private void deleteQRCodeData() {
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//
//        if (qrCodeHash != null) {
//            db.collection("QRCodes").document(qrCodeHash)
//                    .delete()
//                    .addOnSuccessListener(aVoid -> {
//                        qrCodeImageView.setImageBitmap(null);
//                        Toast.makeText(this, "QR code deleted successfully", Toast.LENGTH_SHORT).show();
//                        deleteButton.setEnabled(false);
//                        deleteButton.setVisibility(View.GONE);
//                        //finish();
//                    })
//                    .addOnFailureListener(e -> {
//                        Toast.makeText(this, "Failed to delete QR code: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                    });
//        } else {
//            Toast.makeText(this, "Error generating hash for QR code", Toast.LENGTH_SHORT).show();
//        }
//    }
//}
