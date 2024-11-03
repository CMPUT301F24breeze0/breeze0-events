package com.example.breeze0events;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class NewFacilityActivity extends AppCompatActivity {
    private String facilityId;
    private OverallStorageController overallStorageController;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_facility_activity);

        Intent intent = getIntent();
        facilityId = intent.getStringExtra("new_facility_id");

        EditText facilityInput = findViewById(R.id.facility_bar);
        Button addButton = findViewById(R.id.Edit_facility_activity_add_button);
        Button backButton = findViewById(R.id.edit_facility_activity_back_button);
        TextView facilityIdText = findViewById(R.id.facility_id_text);
        overallStorageController = new OverallStorageController();

        // 显示传递过来的设施ID
        facilityIdText.setText(facilityId);

        // 点击添加按钮
        addButton.setOnClickListener(v -> {
            String facilityName = facilityInput.getText().toString().trim();
            if (!facilityName.isEmpty()) {
                // String facilityId = facilityId;
                String deviceInfo = Build.ID;
                Facility newFacility = new Facility(facilityId, facilityName, deviceInfo);

                overallStorageController.addFacility(newFacility); // 添加设施到数据库
                Toast.makeText(this, "Facility added successfully!", Toast.LENGTH_SHORT).show();
                finish(); // 关闭当前活动
            } else {
                Toast.makeText(NewFacilityActivity.this, "Please enter a facility name", Toast.LENGTH_SHORT).show();
            }
        });

        // 点击返回按钮
        backButton.setOnClickListener(v -> finish());
    }

}