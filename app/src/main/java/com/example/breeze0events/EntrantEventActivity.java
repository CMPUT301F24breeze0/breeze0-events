package com.example.breeze0events;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class EntrantEventActivity extends AppCompatActivity {
    private Button buttonFirstTimeUse, buttonAlreadyHaveAccount, buttonReturn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entrant_select);  // Use the XML layout you provided

        buttonFirstTimeUse = findViewById(R.id.buttonFirstTimeUse);
        buttonAlreadyHaveAccount = findViewById(R.id.buttonAlreadyHaveAccount);
        buttonReturn = findViewById(R.id.buttonReturn);

        // Handle first-time use button click
        buttonFirstTimeUse.setOnClickListener(v -> {
            Intent intent = new Intent(EntrantEventActivity.this, EntrantLoginActivity.class);
            startActivity(intent);
        });

        // Handle already have an account button click
        buttonAlreadyHaveAccount.setOnClickListener(v -> {
            // FIXME: 2024/10/26 The following is for testing UI
            Intent test = new Intent(EntrantEventActivity.this, EntrantMylistActivity.class);
            startActivity(test);
            // FIXME: 2024/10/26 
            
            String deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
            new OverallStorageController().getEntrant(deviceId, new EntrantCallback() {
                @Override
                public void onSuccess(Entrant entrant) {
                    // If the entrant exists, navigate to EntrantMylistActivity
                    Intent intent = new Intent(EntrantEventActivity.this, EntrantMylistActivity.class);
                    startActivity(intent);
                }

                @Override
                public void onFailure(String message) {
                    // Handle failure (e.g., show a message to the user)
                    Log.d("EntrantEventActivity", "Failed to retrieve entrant: " + message);
                }
            });
        });

        // Handle return button click
        buttonReturn.setOnClickListener(v -> {
            Intent intent = new Intent(EntrantEventActivity.this, OverallLoginPage.class);
            startActivity(intent);
        });
    }
}
