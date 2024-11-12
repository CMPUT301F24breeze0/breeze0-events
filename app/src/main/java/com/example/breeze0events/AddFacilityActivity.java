package com.example.breeze0events;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


/**
 * Activity for adding a new facility to the system.
 * This activity allows users to enter a facility name and store it
 * with a unique facility ID and device information.
 */

public class AddFacilityActivity extends AppCompatActivity {
    private String facilityId;
    private OverallStorageController overallStorageController;

    /**
     * Initializes the activity and sets up the UI components and event listeners.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down, this Bundle contains the saved state data.
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_facility_activity);

        Intent intent = getIntent();
        facilityId = intent.getStringExtra("new_facility_id");

        EditText facilityInput = findViewById(R.id.facility_name_input);
        Button addButton = findViewById(R.id.edit_facility_activity_update_button);
        Button backButton = findViewById(R.id.edit_facility_activity_back_button);
        TextView facilityIdText = findViewById(R.id.edit_facility_activity_facility_id_text);
        overallStorageController = new OverallStorageController();

        facilityIdText.setText(facilityId);

        // by clicking add button
        addButton.setOnClickListener(v -> {
            String facilityName = facilityInput.getText().toString().trim();
            if (!facilityName.isEmpty()) {
                // String facilityId = facilityId;
                String deviceInfo = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
                Facility newFacility = new Facility(facilityId, facilityName, deviceInfo);

                overallStorageController.addFacility(newFacility);
                Toast.makeText(this, "Facility added successfully!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(AddFacilityActivity.this, "Please enter a facility name", Toast.LENGTH_SHORT).show();
            }
        });

        // by clicking back button
        backButton.setOnClickListener(v -> finish());
    }

}