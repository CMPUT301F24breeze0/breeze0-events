package com.example.breeze0events;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class EditFacilityActivity extends AppCompatActivity {
    private EditText facilityNameInput;
    private String facilityId;
    private String facilityName;
    private Button updateButton;
    private Button backButton;
    private OverallStorageController overallStorageController;
    private TextView facilityIdTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_facility_activity);

        facilityNameInput = findViewById(R.id.facility_name_input);
        updateButton = findViewById(R.id.edit_facility_activity_update_button);
        backButton = findViewById(R.id.edit_facility_activity_back_button);
        overallStorageController = new OverallStorageController();
        facilityIdTextView = findViewById(R.id.edit_facility_activity_facility_id_text);

        facilityId = getIntent().getStringExtra("facility_id");
        facilityName = getIntent().getStringExtra("facility_name");

        // Retrieve the facility ID and name from the intent
        facilityId = getIntent().getStringExtra("facility_id");
        facilityName = getIntent().getStringExtra("facility_name");


        // Set the facility ID in the TextView
        if (facilityId != null) {
            facilityIdTextView.setText(facilityId);
        } else {
            Toast.makeText(this, "Error loading facility ID", Toast.LENGTH_SHORT).show();
        }
        // Set the facility name in the input field
        if (facilityName != null) {
            if (facilityName.startsWith("Facility: ")) {
                facilityName = facilityName.substring("Facility: ".length());
            }
            facilityNameInput.setText(facilityName);
        } else {
            Toast.makeText(this, "Error loading facility name", Toast.LENGTH_SHORT).show();
        }


        // by clicking back button
        backButton.setOnClickListener(v -> finish());

        // by clicking update button
        updateButton.setOnClickListener(v -> {
            String updatedName = facilityNameInput.getText().toString().trim();

            if (updatedName.isEmpty()) {
                Toast.makeText(this, "Facility name cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            Facility updatedFacility = new Facility(facilityId, updatedName, facilityId);

            overallStorageController.updateFacility(updatedFacility);

            Toast.makeText(this, "Facility updated successfully", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}
