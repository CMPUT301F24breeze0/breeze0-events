package com.example.breeze0events;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.common.BitMatrix;

/**
 * Activity class to display a QR code for a specific organizer event.
 * The QR code is generated based on a hash code passed as an intent extra.
 */
public class OrganizerEventDisplayQRcode extends AppCompatActivity {

    Button backButton;
    ImageView qrCodeImageView;

    /**
     * Called when the activity is first created. Sets up the layout, back button functionality,
     * and generates the QR code based on the passed-in hash code.
     *
     * @param savedInstanceState
     * If the activity is being re-initialized after previously being shut down,
     * this Bundle contains the data it most recently supplied.
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_display_qrcode); // Set the layout file

        backButton = findViewById(R.id.organizer_facility_activity_back_button);
        qrCodeImageView = findViewById(R.id.qr_code_image);

        // Set up the back button to finish the activity
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Generate the QR code and display it
        String qrHashCode = (String) getIntent().getSerializableExtra("qrcode");// Replace with your hash code
        QRHashGenerator qrhashgenerator=new QRHashGenerator();
        if(qrHashCode!=null) {
            Bitmap qrCodeBitmap = qrhashgenerator.generateQRCode(qrHashCode);

            if (qrCodeBitmap != null) {
                qrCodeImageView.setImageBitmap(qrCodeBitmap);
            } else {
                Toast.makeText(this, "Failed to generate QR code", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Method to generate a QR code bitmap
}
