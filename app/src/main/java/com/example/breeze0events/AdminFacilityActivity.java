package com.example.breeze0events;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class AdminFacilityActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_facility_page);
        Button return_button = findViewById(R.id.backButton);

        return_button.setOnClickListener(v -> {
            Intent intent = new Intent(AdminFacilityActivity.this, AdminLoginActivity.class);
            startActivity(intent);
        });
    }
}
