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

public class AdminQRcode extends AppCompatActivity {
    Button backButton;
    ImageView qrCodeImageView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qr_code_fragment);

        backButton = findViewById(R.id.backButton);
        qrCodeImageView = findViewById(R.id.QRcode);

        backButton.setOnClickListener(v -> {
            finish();
        });
    }
}
