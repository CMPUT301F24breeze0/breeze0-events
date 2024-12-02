package com.example.breeze0events;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Button;
/**
 * OverallLoginPage is the main login page of the application, where users
 * can select their roles (Entrant, Organizer, Admin) to navigate to the respective
 * login or activity pages.
 */
public class OverallLoginPage extends AppCompatActivity {
    /**
     * Called when the activity is starting. This is where most initialization
     * should go, such as setting up the UI elements and their interactions.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down,
     *                           this contains the most recent data. Otherwise, it is null.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set the layout for the activity
        setContentView(R.layout.overall_login_page);

        // Initialize UI elements by finding them in the layout
        Button entrantButton = findViewById(R.id.entrant_button);
        Button organizerButton = findViewById(R.id.organizer_button);
        Button adminButton = findViewById(R.id.admin_button);

        // Set click listener for Entrant Button to navigate to EntrantEventActivity
        /**
         * Set a click listener for the Entrant button.
         * Navigates the user to the EntrantPreLoginActivity.
         */
        entrantButton.setOnClickListener(v -> {
            Intent intent = new Intent(OverallLoginPage.this, EntrantPreLoginActivity.class);
            startActivity(intent);
        });
        /**
         * Set a click listener for the Organizer button.
         * Navigates the user to the OrganizerMyListActivity.
         */
        // Set click listener for Organizer Button to navigate to OrganizerEventActivity
        organizerButton.setOnClickListener(v -> {
            Intent intent = new Intent(OverallLoginPage.this, OrganizerMyListActivity.class);
            startActivity(intent);
        });

        // Set click listener for Admin Button to navigate to AdminOperateActivity
        /**
         * Set a click listener for the Admin button.
         * Navigates the user to the AdminLoginActivity.
         */
        adminButton.setOnClickListener(v -> {
            Intent intent = new Intent(OverallLoginPage.this, AdminLoginActivity.class);
            startActivity(intent);
        });
    }
}