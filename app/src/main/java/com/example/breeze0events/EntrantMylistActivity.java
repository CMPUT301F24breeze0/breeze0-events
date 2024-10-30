package com.example.breeze0events;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;

// This is the main page of the Entrant containing the wishlist, profile of Entrant
public class EntrantMylistActivity extends AppCompatActivity  {
    private ImageView profileImage;
    private TextView entrantName;
    private Button QR_Scan;
    private TextView eventName;
    private Button EventStatus;
    private Button Blacklist;
    private Button QuietMode;
    private Button ProfileModify;
    private OverallStorageController overallStorageController;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entrant_main_activity);

        // Initialize UI
        profileImage = findViewById(R.id.profileImage);
        entrantName = findViewById(R.id.entrantName);
        QR_Scan = findViewById(R.id.buttonQRScan);
        eventName = findViewById(R.id.eventName);
        EventStatus = findViewById(R.id.buttonEventStatus);
        Blacklist = findViewById(R.id.buttonBlacklist);
        QuietMode = findViewById(R.id.buttonQuietMode);
        ProfileModify = findViewById(R.id.buttonProfile);
        overallStorageController = new OverallStorageController();

        // Event Searching Functionality
        FloatingActionButton EventSearch = findViewById(R.id.buttonEventSearch);
        EventSearch.setOnClickListener(v->{
            Intent OnlineSearching = new Intent(EntrantMylistActivity.this, EntrantSearchingActivity.class);
            startActivity(OnlineSearching);
        });
        String deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        overallStorageController.getEntrant(deviceId, new EntrantCallback() {
            @Override
            public void onSuccess(Entrant entrant) {
                entrantName.setText(entrant.getName());

                profileImage.setImageBitmap(decodeBase64Image(entrant.getProfilePhoto()));
            }

            @Override
            public void onFailure(String errorMessage) {

            }
        });

    }
    public static Bitmap decodeBase64Image(String base64ImageString) {
        byte[] imageBytes = Base64.decode(base64ImageString, Base64.DEFAULT);
        // 解码 Base64 字符串为字节数组
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);

    }
}
