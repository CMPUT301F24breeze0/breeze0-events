package com.example.breeze0events;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * AdminLoginActivity class provides the initial interface for administrators in the Breeze0Events
 * application. This activity presents the main administrative options for managing profiles, events,
 * and facilities, allowing navigation to other parts of the admin interface.
 */

public class AdminLoginActivity extends AppCompatActivity {
    private OverallStorageController overallStorageController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_page);
        Button profile_remove_button=findViewById(R.id.removeButton1);
        Button event_remove_button=findViewById(R.id.removeButton2);
        Button facility_button=findViewById(R.id.facility);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference collectionRef = db.collection("OverallDB");
        overallStorageController = new OverallStorageController();

        String androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        overallStorageController.getAdmin(androidId, new AdminCallback() {
            @Override
            public void onSuccess(Admin admin) {

            }

            @Override
            public void onFailure(String errorMessage) {
                collectionRef.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        int maxAdminId = 0;
                        for (DocumentSnapshot doc : task.getResult()) {
                            String adminIdStr = doc.getString("adminId");
                            if (adminIdStr != null) {
                                try {
                                    int adminId = Integer.parseInt(adminIdStr);
                                    if (adminId > maxAdminId) {
                                        maxAdminId = adminId;
                                    }
                                } catch (NumberFormatException ignored) {
                                }
                            }
                        }
                        String newAdminId = String.valueOf(maxAdminId + 1);
                        Admin admin = new Admin(newAdminId, androidId);
                        overallStorageController.addAdmin(admin);
                    }
                });
            }
        });

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
