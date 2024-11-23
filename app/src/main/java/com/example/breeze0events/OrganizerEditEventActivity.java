package com.example.breeze0events;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;


/**
 * The OrganizerEditEventActivity class allows organizers to edit event details,
 * such as the name, date, facility, poster, and entrants limit. This class provides
 * functionality to load existing event data, modify it, and save updates to the database.
 */
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
    private Switch geolocationButton;

    /**
     * Called when the activity is first created. Initializes UI components, sets up
     * button click listeners, and loads event data if an event ID is provided.
     *
     * @param savedInstanceState The saved instance state bundle, if available.
     */
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
        geolocationButton = findViewById(R.id.permission_button);
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

        startDateEditText.setFocusable(false);
        endDateEditText.setFocusable(false);

        startDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CalendarView calendarView = new CalendarView(OrganizerEditEventActivity.this);
                Calendar calendar = Calendar.getInstance();
                calendarView.setDate(calendar.getTimeInMillis());

                int Year = calendar.get(Calendar.YEAR);
                int Month = calendar.get(Calendar.MONTH) + 1;
                int Day = calendar.get(Calendar.DAY_OF_MONTH);
                startDateEditText.setText(Year+"-"+Month+"-"+Day);

                AlertDialog.Builder alert = new AlertDialog.Builder(OrganizerEditEventActivity.this);
                alert.setTitle("Select a Date");
                alert.setView(calendarView);
                calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
                    String selectedDate = year + "-" + (month + 1) + "-" + dayOfMonth;
                    startDateEditText.setText(selectedDate);
                    Toast.makeText(OrganizerEditEventActivity.this, "Selected Date: " + selectedDate, Toast.LENGTH_SHORT).show();
                });
                alert.setPositiveButton("Confirm", (dialog, which) -> {
                    Toast.makeText(OrganizerEditEventActivity.this, "Selected Date: " + startDateEditText.getText().toString(), Toast.LENGTH_SHORT).show();
                });
                alert.show();
            }
        });

        endDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CalendarView calendarView = new CalendarView(OrganizerEditEventActivity.this);
                Calendar calendar = Calendar.getInstance();
                calendarView.setDate(calendar.getTimeInMillis());

                int Year = calendar.get(Calendar.YEAR);
                int Month = calendar.get(Calendar.MONTH) + 1;
                int Day = calendar.get(Calendar.DAY_OF_MONTH);
                startDateEditText.setText(Year+"-"+Month+"-"+Day);

                AlertDialog.Builder alert = new AlertDialog.Builder(OrganizerEditEventActivity.this);
                alert.setTitle("Select a Date");
                alert.setView(calendarView);
                calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
                    String selectedDate = year + "-" + (month + 1) + "-" + dayOfMonth;
                    endDateEditText.setText(selectedDate);
                });
                alert.setPositiveButton("Confirm", (dialog, which) -> {
                    Toast.makeText(OrganizerEditEventActivity.this, "Selected Date: " +  endDateEditText.getText().toString(), Toast.LENGTH_SHORT).show();
                });
                alert.show();
            }
        });
    }

    /**
     * Loads event data from the database based on the given event ID.
     *
     * @param eventId The ID of the event to be loaded.
     */
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
                geolocationButton.setChecked((event.getGeolocation().equals("true"))?true:false);
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

    /**
     * Saves the updated event details to the database. If any required fields are empty,
     * a toast message will prompt the user to fill them in before saving.
     */
    private void saveEvent() {
        String eventName = eventNameEditText.getText().toString().trim();
        String startDate = startDateEditText.getText().toString().trim();
        String endDate = endDateEditText.getText().toString().trim();
        String entrants = entrantsEditText.getText().toString().trim();
        String organizerId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        String geolocation = (geolocationButton.isChecked()==true)? "true":"false";
        if (eventName.isEmpty() || startDate.isEmpty() || endDate.isEmpty() || entrants.isEmpty()) {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Update event details
        Event updatedEvent = new Event(eventId, eventName, qrHashCode, ImageHashCode, eventFacility, startDate, endDate, entrants, geolocation, currentEvent.getEntrants(), Arrays.asList(organizerId));
        overallStorageController.updateEvent(updatedEvent);

        Toast.makeText(this, "Event updated successfully", Toast.LENGTH_SHORT).show();
        finish();
    }

    /**
     * Opens the device's gallery for the user to select an image as the event poster.
     */
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    /**
     * Opens a dialog to allow the user to select a facility for the event.
     */
    private void openFacilitySelectionDialog() {
        SelectFacilityForEventActivity dialog = new SelectFacilityForEventActivity();
        dialog.show(getSupportFragmentManager(), "SelectFacilityDialog");
    }

    /**
     * Callback method that receives the selected facility name from the dialog.
     *
     * @param selectedFacilityName The name of the selected facility.
     */
    @Override
    public void onFacilitySelected(String selectedFacilityName) {
        eventFacility = selectedFacilityName;
        selectedFacilityTextView.setText(selectedFacilityName);
        Toast.makeText(this, "Facility selected: " + selectedFacilityName, Toast.LENGTH_SHORT).show();
    }

    /**
     * Handles the result from the gallery intent and updates the event's poster image.
     *
     * @param requestCode The request code originally supplied to startActivityForResult().
     * @param resultCode  The result code returned by the child activity.
     * @param data        An Intent that carries the result data.
     */
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