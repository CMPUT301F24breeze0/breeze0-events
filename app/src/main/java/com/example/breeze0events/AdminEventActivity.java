package com.example.breeze0events;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class AdminEventActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.remove_events_page);
        Button return_button = findViewById(R.id.backButton);

        return_button.setOnClickListener(v -> {
            Intent intent = new Intent(AdminEventActivity.this, AdminLoginActivity.class);
            startActivity(intent);
        });
    }
}
