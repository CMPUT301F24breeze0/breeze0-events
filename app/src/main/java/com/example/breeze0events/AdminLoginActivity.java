package com.example.breeze0events;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
/**
 * AdminLoginActivity class provides the initial interface for administrators in the Breeze0Events
 * application. This activity presents the main administrative options for managing profiles, events,
 * and facilities, allowing navigation to other parts of the admin interface.
 */

public class AdminLoginActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_page);
        Button profile_remove_button=findViewById(R.id.removeButton1);
        Button event_remove_button=findViewById(R.id.removeButton2);
        Button facility_button=findViewById(R.id.facility);
        //by clicking organization profile button
        profile_remove_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view){
                Intent intent = new Intent(AdminLoginActivity.this, AdminOperateActivity.class);
                startActivity(intent);
            }
        });

        event_remove_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminLoginActivity.this, AdminEventActivity.class);
                startActivity(intent);
            }
        });

        facility_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view){
                Intent intent = new Intent(AdminLoginActivity.this, AdminFacilityActivity.class);
                startActivity(intent);
            }
        });

    }
}
