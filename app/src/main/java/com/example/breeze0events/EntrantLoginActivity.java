package com.example.breeze0events;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Base64;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class EntrantLoginActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private EditText editTextName, editTextEmail, editTextPhone;
    private ImageView profileImage;
    private Button buttonSignUp, buttonReturn;
    private String profileImageString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entrant_login_activity);

        editTextName = findViewById(R.id.editTextName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPhone = findViewById(R.id.editTextPhone);
        profileImage = findViewById(R.id.profileImage);
        buttonSignUp = findViewById(R.id.buttonSignUp);
        buttonReturn = findViewById(R.id.buttonReturn);

        profileImage.setOnClickListener(v -> openImageSelector());

        // Handle sign-up button click
        buttonSignUp.setOnClickListener(v -> {
            String name = editTextName.getText().toString().trim();
            String email = editTextEmail.getText().toString().trim();
            String phone = editTextPhone.getText().toString().trim();
            String deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

            // Check if a profile image is uploaded; if not, generate default image
            if (profileImageString == null || profileImageString.isEmpty()) {
                profileImageString = generateDefaultProfileImage(name);
            }

            Entrant newEntrant = new Entrant(deviceId, name, email, phone, profileImageString, deviceId, new ArrayList<>());
            new OverallStorageController().addEntrant(newEntrant);

            Intent intent = new Intent(EntrantLoginActivity.this, EntrantMylistActivity.class);
            startActivity(intent);
        });

        buttonReturn.setOnClickListener(v -> {
            Intent intent = new Intent(EntrantLoginActivity.this, EntrantEventActivity.class);
            startActivity(intent);
        });
    }

    private void openImageSelector() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    // Handle result from image picker
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                profileImage.setImageBitmap(bitmap);
                profileImageString = convertBitmapToBase64(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Convert a Bitmap to Base64 string for storage
    private String convertBitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    // Generate a default profile image as a colored circle with the first letter of the user's name
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
