package com.example.breeze0events;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class EntrantLoginActivity extends AppCompatActivity {
    private EditText editTextName, editTextEmail, editTextPhone;
    private ImageView profileImage;
    private Button buttonSignUp, buttonReturn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entrant_login_activity);  // Use the XML layout you provided

        editTextName = findViewById(R.id.editTextName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPhone = findViewById(R.id.editTextPhone);
        profileImage = findViewById(R.id.profileImage);
        buttonSignUp = findViewById(R.id.buttonSignUp);
        buttonReturn = findViewById(R.id.buttonReturn);

        buttonSignUp.setOnClickListener(v -> {
            String name = editTextName.getText().toString().trim();
            String email = editTextEmail.getText().toString().trim();
            String phone = editTextPhone.getText().toString().trim();
            String deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

            // Create a new Entrant object
            Entrant newEntrant = new Entrant(deviceId, name, email, phone, "profilePhotoUrl", deviceId, new ArrayList<>());

            // Save the entrant to the database
            new OverallStorageController().addEntrant(newEntrant);

            // Navigate to EntrantMylistActivity
            Intent intent = new Intent(EntrantLoginActivity.this, EntrantMylistActivity.class);
            startActivity(intent);
        });

        buttonReturn.setOnClickListener(v -> {
            Intent intent = new Intent(EntrantLoginActivity.this, EntrantEventActivity.class);
            startActivity(intent);
        });
    }
}
