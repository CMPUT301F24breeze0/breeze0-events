package com.example.breeze0events;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import androidx.appcompat.app.AlertDialog;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * EntrantPreLoginActivity provides options for users to either sign up or log in
 * as entrants in the application. It checks for an existing entrant account based on device ID,
 * guiding users to the appropriate login or sign-up activity.
 */
public class EntrantPreLoginActivity extends AppCompatActivity {
    private Button buttonFirstTimeUse, buttonAlreadyHaveAccount, buttonReturn;

    /**
     * Initializes the activity and sets up button click listeners for first-time use, login, and return.
     *
     * @param savedInstanceState The saved instance state containing previous data (if any).
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entrant_select);
        buttonFirstTimeUse = findViewById(R.id.buttonFirstTimeUse);
        buttonAlreadyHaveAccount = findViewById(R.id.buttonAlreadyHaveAccount);
        buttonReturn = findViewById(R.id.buttonReturn);

        // Handle first-time use button click
        buttonFirstTimeUse.setOnClickListener(v -> {
            String deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

            // Check if an entrant with this deviceId already exists
            new OverallStorageController().getEntrant(deviceId, new EntrantCallback() {
                @Override
                public void onSuccess(Entrant entrant) {
                    new AlertDialog.Builder(EntrantPreLoginActivity.this)
                            .setTitle("Account Already Exists")
                            .setMessage("It looks like you already have an account. Please use the 'I have an account already' option to log in.")
                            .setPositiveButton("OK", null)
                            .setCancelable(true)
                            .show();
                }

                @Override
                public void onFailure(String message) {
                    // If no entrant is found, proceed to sign-up
                    Intent intent = new Intent(EntrantPreLoginActivity.this, EntrantLoginActivity.class);
                    startActivity(intent);
                }
            });
        });

        // Handle already have an account button click
        buttonAlreadyHaveAccount.setOnClickListener(v -> {
            String deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
            new OverallStorageController().getEntrant(deviceId, new EntrantCallback() {
                @Override
                public void onSuccess(Entrant entrant) {
                    // Show success dialog and navigate to EntrantMylistActivity
                    new AlertDialog.Builder(EntrantPreLoginActivity.this)
                            .setTitle("Login Successful")
                            .setMessage("Welcome back, " + entrant.getName() + "!")
                            .setPositiveButton("Continue", (dialog, which) -> {
                                // If the entrant exists, navigate to EntrantMylistActivity
                                Intent intent = new Intent(EntrantPreLoginActivity.this, EntrantMylistActivity.class);
                                startActivity(intent);
                            })
                            .setCancelable(false)
                            .show();
                }

                @Override
                public void onFailure(String message) {
                    // Show failure dialog, suggesting the user sign up
                    new AlertDialog.Builder(EntrantPreLoginActivity.this)
                            .setTitle("Login Failed")
                            .setMessage("No account found. You might need to sign up as a first-time user.")
                            .setPositiveButton("Sign Up", (dialog, which) -> {
                                Intent intent = new Intent(EntrantPreLoginActivity.this, EntrantLoginActivity.class);
                                startActivity(intent);
                            })
                            .setNegativeButton("Cancel", null)
                            .setCancelable(true)
                            .show();
                }
            });
        });

        // Handle return button click
        buttonReturn.setOnClickListener(v -> {
            Intent intent = new Intent(EntrantPreLoginActivity.this, OverallLoginPage.class);
            startActivity(intent);
        });

    }
}
