package com.example.breeze0events;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class EntrantProfileActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private EditText editTextName, editTextEmail, editTextPhone;
    private ImageView profileImage;
    private Button buttonUpdateProfile, buttonReturn;
    private String profileImageString;
    private OverallStorageController overallStorageController;
    private String deviceId;
    private Entrant localEntrant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entrant_profile_activity);

        // Initialize UI components
        editTextName = findViewById(R.id.editTextName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPhone = findViewById(R.id.editTextPhone);
        profileImage = findViewById(R.id.profileImage);
        buttonUpdateProfile = findViewById(R.id.buttonUpdateProfile);
        buttonReturn = findViewById(R.id.buttonReturn);

        overallStorageController = new OverallStorageController();

        deviceId = getIntent().getStringExtra("deviceId");

        loadEntrantData();

        profileImage.setOnClickListener(v -> openImageSelector());

        buttonUpdateProfile.setOnClickListener(v -> updateProfile());

        buttonReturn.setOnClickListener(v -> finish());
    }

    // Load Entrant data from Firestore
    private void loadEntrantData() {
        overallStorageController.getEntrant(deviceId, new EntrantCallback() {
            @Override
            public void onSuccess(Entrant entrant) {
                localEntrant = entrant;
                editTextName.setText(entrant.getName());
                editTextEmail.setText(entrant.getEmail());
                editTextPhone.setText(entrant.getPhoneNumber());
                profileImage.setImageBitmap(decodeBase64Image(entrant.getProfilePhoto()));
                profileImageString = entrant.getProfilePhoto(); // Store the current profile image string
            }

            @Override
            public void onFailure(String message) {
                Toast.makeText(EntrantProfileActivity.this, "Failed to load profile: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Open image selector for profile image
    private void openImageSelector() {
        new AlertDialog.Builder(EntrantProfileActivity.this)
                .setTitle("Upload/Remove profile picture")
                .setPositiveButton("Upload", (dialog, which)->{
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, PICK_IMAGE_REQUEST);
                })
                .setNeutralButton("Remove", (dialog, which)->{
                    profileImageString = generateDefaultProfileImage(editTextName.getText().toString());
                })
                .setNegativeButton("Cancel", null)
                .setCancelable(true)
                .show();
    }

    // Handle result from image picker
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                // Compress to JPEG
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                while (outputStream.toByteArray().length / 1024 > 1024) {
                    bitmap = BitmapFactory.decodeByteArray(outputStream.toByteArray(), 0, outputStream.toByteArray().length);
                    outputStream.reset();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 95, outputStream);
                }
                profileImage.setImageBitmap(bitmap);
                profileImageString = convertBitmapToBase64(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Update Entrant profile in Firestore
    private void updateProfile() {
        String name = editTextName.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String phone = editTextPhone.getText().toString().trim();

        localEntrant.setName(name);
        localEntrant.setEmail(email);
        localEntrant.setPhoneNumber(phone);
        localEntrant.setProfilePhoto(profileImageString);
        overallStorageController.updateEntrant(localEntrant);

        // Return the updated profile data to EntrantMylistActivity
        Intent resultIntent = new Intent(EntrantProfileActivity.this, EntrantMylistActivity.class);
        resultIntent.putExtra("update", "SetImage");

        Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(resultIntent);
    }

    private String convertBitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    public static Bitmap decodeBase64Image(String base64ImageString) {
        byte[] imageBytes = Base64.decode(base64ImageString, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
    }
    private String generateDefaultProfileImage(String name) {
        // Get the first letter of the name
        String firstLetter = name.isEmpty() ? "A" : String.valueOf(name.charAt(0)).toUpperCase();

        // Create a Bitmap with a background circle and first letter
        int size = 100;
        Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.rgb((int) (Math.random() * 256), (int) (Math.random() * 256), (int) (Math.random() * 256)));
        canvas.drawCircle(size / 2, size / 2, size / 2, paint);
        paint.setColor(Color.rgb((int) (Math.random() * 256), (int) (Math.random() * 256), (int) (Math.random() * 256)));
        paint.setTextSize(50);
        paint.setTextAlign(Paint.Align.CENTER);
        float yPos = (canvas.getHeight() / 2) - ((paint.descent() + paint.ascent()) / 2);
        canvas.drawText(firstLetter, size / 2, yPos, paint);

        profileImage.setImageBitmap(bitmap);
        return convertBitmapToBase64(bitmap);
    }
}
