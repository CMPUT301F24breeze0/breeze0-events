package com.example.breeze0events;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;
import java.util.List;

public class OrganizerEditEventActivity extends AppCompatActivity implements SelectFacilityForEventActivity.FacilitySelectListener {

    private static final int PICK_IMAGE_REQUEST = 2;

    private OverallStorageController overallStorageController;
    private EditText eventNameEditText, startDateEditText, endDateEditText, entrantsEditText;
    private TextView eventIdTextView, selectedFacilityTextView;
    private ImageView posterImageView;
    private Button uploadPosterButton, saveButton, backButton, selectFacilityButton;

    private String eventFacility, qrHashCode, ImageHashCode, eventId;
    private Uri selectedPosterUri;
    private Event currentEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_edit_event_activity);

        overallStorageController = new OverallStorageController();

        // Initialize views
        eventIdTextView = findViewById(R.id.organizer_edit_event_activity_id);
        eventNameEditText = findViewById(R.id.event_name_bar);
        startDateEditText = findViewById(R.id.event_start_date_bar);
        endDateEditText = findViewById(R.id.event_end_date_bar);
        entrantsEditText = findViewById(R.id.entrants_bar);
        selectedFacilityTextView = findViewById(R.id.selected_facility_text_view);
        posterImageView = findViewById(R.id.organizer_edit_event_activity_poster_image);
        uploadPosterButton = findViewById(R.id.organizer_edit_event_activity_poster_upload_button);
        saveButton = findViewById(R.id.organizer_edit_event_activity_add_button);
        backButton = findViewById(R.id.organizer_edit_event_activity_back_button);
        selectFacilityButton = findViewById(R.id.organizer_event_activity_facility_button);

        // Get event ID from intent to determine if in edit mode
        eventId = getIntent().getStringExtra("event_id");
        if (eventId != null) {
            loadEventData(eventId);
        }

        // Set up button actions
        uploadPosterButton.setOnClickListener(v -> openGallery());
        saveButton.setOnClickListener(v -> saveEvent());
        backButton.setOnClickListener(v -> finish());
        selectFacilityButton.setOnClickListener(v -> openFacilitySelectionDialog());
    }

    private void loadEventData(String eventId) {
        overallStorageController.getEvent(eventId, new EventCallback() {
            @Override
            public void onSuccess(Event event) {
                currentEvent = event;
                eventIdTextView.setText(event.getEventId());
                eventNameEditText.setText(event.getName());
                startDateEditText.setText(event.getStartDate());
                endDateEditText.setText(event.getEndDate());
                entrantsEditText.setText(event.getLimitedNumber());
                selectedFacilityTextView.setText(event.getFacility());
                eventFacility = event.getFacility();
                qrHashCode = event.getQrCode();
                ImageHashCode = event.getPosterPhoto();

                if (ImageHashCode != null) {
                    posterImageView.setImageURI(Uri.parse(ImageHashCode));
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(OrganizerEditEventActivity.this, "Failed to load event", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveEvent() {
        String eventName = eventNameEditText.getText().toString().trim();
        String startDate = startDateEditText.getText().toString().trim();
        String endDate = endDateEditText.getText().toString().trim();
        String entrants = entrantsEditText.getText().toString().trim();
        String organizerId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        if (eventName.isEmpty() || startDate.isEmpty() || endDate.isEmpty() || entrants.isEmpty()) {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Update event details
        Event updatedEvent = new Event(eventId, eventName, qrHashCode, ImageHashCode, eventFacility, startDate, endDate, entrants, currentEvent.getEntrants(), Arrays.asList(organizerId));
        overallStorageController.updateEvent(updatedEvent);

        Toast.makeText(this, "Event updated successfully", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void openFacilitySelectionDialog() {
        SelectFacilityForEventActivity dialog = new SelectFacilityForEventActivity();
        dialog.show(getSupportFragmentManager(), "SelectFacilityDialog");
    }

    @Override
    public void onFacilitySelected(String selectedFacilityName) {
        eventFacility = selectedFacilityName;
        selectedFacilityTextView.setText(selectedFacilityName);
        Toast.makeText(this, "Facility selected: " + selectedFacilityName, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedPosterUri = data.getData();
            posterImageView.setImageURI(selectedPosterUri);

            // Generate and store hash code for the image
            try {
                ImageHashCode = ImageHashGenerator.generateHashCode(this, selectedPosterUri);
            } catch (Exception e) {
                Log.e("ImageHashError", "Error generating image hash", e);
            }
        }
    }
}