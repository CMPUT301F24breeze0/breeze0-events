package com.example.breeze0events;

import static android.app.PendingIntent.getActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.firebase.firestore.FirebaseFirestore;

import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

/**
 * The OrganizerEventActivity class allows organizers to create and edit events,
 * including setting event details, selecting facilities, uploading poster images,
 * and generating QR codes for event access. The activity integrates with a shared
 * storage controller for database operations.
 */
public class OrganizerEventActivity extends AppCompatActivity implements SelectFacilityForEventActivity.FacilitySelectListener {
    private static final int PICK_IMAGE_REQUEST = 2;
    private FirebaseFirestore db;
    private OverallStorageController overallStorageController;
    private ListView eventListView;
    private ArrayAdapter<String> eventListAdapter;
    private ArrayList<String> eventList;
    private EditText event_name_bar;
    private EditText event_start_date_bar;
    private EditText event_end_date_bar;
    private EditText entrants_bar;
    private EditText sighup_due_date_bar;
    private EditText event_facility_bar;
    private EditText no_of_attendees_bar;
    private OnFragmentInteractionListener listener;
    private Uri selectedPosterUri = null;

    private ImageView posterImageView;
    String eventFacility;
    private String qrHashCode;
    private String ImageHashCode;

    ArrayList<String> facilityList;
    // ImageView posterImageView;

    /**
     * Interface for fragment interaction, allowing communication with other fragments.
     */
    public interface OnFragmentInteractionListener{
        void onOkPressed(Event newEvent);
    }

    /**
     * Called when the activity is first created. Initializes the UI components, sets up button
     * click listeners, and prepares event details if editing an existing event.
     *
     * @param savedInstanceState The saved instance state bundle, if available.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_event_activity);

        String headerText = getIntent().getStringExtra("header_text");
        String newEventId = getIntent().getStringExtra("new_event_id");
        TextView idTextView = findViewById(R.id.organizer_edit_event_activity_id);
        Button addButton = findViewById(R.id.organizer_edit_event_activity_add_button);
        Button backButton = findViewById(R.id.organizer_edit_event_activity_back_button);
        Button uploadPosterButton = findViewById(R.id.organizer_edit_event_activity_poster_upload_button);
        Button generateQRButton = findViewById(R.id.organizer_event_activity_generate_qr_button);
        EditText name = findViewById(R.id.event_name_bar);
        EditText start_date = findViewById(R.id.event_start_date_bar);
        EditText end_date = findViewById(R.id.event_end_date_bar);
        EditText entrants = findViewById(R.id.entrants_bar);
        Button facilityButton = findViewById(R.id.organizer_event_activity_facility_button);
        Switch geolocationButton = findViewById(R.id.permission_button);
        overallStorageController = new OverallStorageController();
        posterImageView = findViewById(R.id.organizer_edit_event_activity_poster_image);

        start_date.setFocusable(false);
        end_date.setFocusable(false);

        // set header
        TextView headerTextView = findViewById(R.id.organizer_edit_event_activity_header);
        if (headerText != null) {
            headerTextView.setText(headerText);
        }

        // display new id
        if (newEventId != null) {
            idTextView.setText(newEventId);
        }

        start_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CalendarView calendarView = new CalendarView(OrganizerEventActivity.this);
                Calendar calendar = Calendar.getInstance();
                calendarView.setDate(calendar.getTimeInMillis());

                int Year = calendar.get(Calendar.YEAR);
                int Month = calendar.get(Calendar.MONTH) + 1;
                int Day = calendar.get(Calendar.DAY_OF_MONTH);
                start_date.setText(String.format("%d-%02d-%02d", Year, Month, Day));

                AlertDialog.Builder alert = new AlertDialog.Builder(OrganizerEventActivity.this);
                alert.setTitle("Select a Date");
                alert.setView(calendarView);
                calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
                    String selectedDate = String.format("%d-%02d-%02d", year, month, dayOfMonth);
                    start_date.setText(selectedDate);
                    Toast.makeText(OrganizerEventActivity.this, "Selected Date: " + selectedDate, Toast.LENGTH_SHORT).show();
                });
                alert.setPositiveButton("Confirm", (dialog, which) -> {
                    Toast.makeText(OrganizerEventActivity.this, "Selected Date: " + start_date.getText().toString(), Toast.LENGTH_SHORT).show();
                });
                alert.show();
            }
        });

        end_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CalendarView calendarView = new CalendarView(OrganizerEventActivity.this);
                Calendar calendar = Calendar.getInstance();
                calendarView.setDate(calendar.getTimeInMillis());

                int Year = calendar.get(Calendar.YEAR);
                int Month = calendar.get(Calendar.MONTH) + 1;
                int Day = calendar.get(Calendar.DAY_OF_MONTH);
                end_date.setText(String.format("%d-%02d-%02d", Year, Month, Day));

                AlertDialog.Builder alert = new AlertDialog.Builder(OrganizerEventActivity.this);
                alert.setTitle("Select a Date");
                alert.setView(calendarView);
                calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
                    String selectedDate = String.format("%d-%02d-%02d", year, month, dayOfMonth);
                    end_date.setText(selectedDate);
                });
                alert.setPositiveButton("Confirm", (dialog, which) -> {
                    Toast.makeText(OrganizerEventActivity.this, "Selected Date: " + end_date.getText().toString(), Toast.LENGTH_SHORT).show();
                });
                alert.show();
            }
        });

        //  by clicking "Select Facility" button
        facilityButton.setOnClickListener(v -> {
            SelectFacilityForEventActivity dialog = new SelectFacilityForEventActivity();
            dialog.show(getSupportFragmentManager(), "AddFacilityActivity");
        });

        // by clicking "Upload Poster" button
        uploadPosterButton.setOnClickListener(v->openGallery());

        //by clicking "uploading QR" Button
        // Request code for image picker


        uploadPosterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to open the image gallery
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*"); // Filter only for images
                startActivityForResult(intent, PICK_IMAGE_REQUEST);
            }
        });


        //by clicking "Generate QR Code" Button
        generateQRButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String eventId = idTextView.getText().toString();
                qrHashCode = QRHashGenerator.generateHash(eventId);
                Log.d("OrganizerEventActivity", "Generated QR HashCode: " + qrHashCode);

                ImageView qrImageView = findViewById(R.id.selected_qr_image_view);
                Bitmap qrBitmap = QRHashGenerator.generateQRCode(qrHashCode);
                qrImageView.setImageBitmap(qrBitmap);
            }
        });


        // by clicking "Add" button
        addButton.setOnClickListener(v->{
            String eventName = name.getText().toString().trim();
            String startDate = start_date.getText().toString().trim();
            String endDate = end_date.getText().toString().trim();
            String entrantsList = entrants.getText().toString().trim();
            String eventId = idTextView.getText().toString();
            String limitedNumber = entrants.getText().toString();
            String qrCodePath = qrHashCode;
            String posterUri = ImageHashCode;
            String geolocation = (geolocationButton.isChecked()==true)? "true":"false";
            String organizerId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID); // Device ID as organizer ID

            // Check if required fields are empty
            if (eventName.isEmpty() || startDate.isEmpty() || endDate.isEmpty() || entrantsList.isEmpty()) {
                Toast.makeText(OrganizerEventActivity.this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
                return;
            }
            if (start_date.getText().toString().compareTo(end_date.getText().toString())>0){
                Toast.makeText(OrganizerEventActivity.this, "start date cannot be after end date", Toast.LENGTH_SHORT).show();
                return;
            }
            List<String> organizers = new ArrayList<>();
            organizers.add(organizerId);
            List<String> newEntrants = Arrays.asList(entrantsList.split("\\s*,\\s*"));

            // Use facility name instead of an ID
            Event newEvent = new Event(eventId, eventName, qrCodePath, posterUri, eventFacility, startDate, endDate, limitedNumber, geolocation, new ArrayList<>(), organizers);
            Log.d("OrganizerEventActivity", "Calling addEvent with Event ID: " + eventId + " and Facility: " + eventFacility);

            overallStorageController.addEvent(newEvent);
            overallStorageController.addEventWithOrganizerCheck(newEvent, organizerId);


            Toast.makeText(OrganizerEventActivity.this, "Event added successfully", Toast.LENGTH_SHORT).show();
            finish(); // Close activity
        });


        // by clicking "Back" button
        backButton.setOnClickListener(v-> finish());
    }

    /**
     * Opens the device gallery for image selection to upload a poster image.
     */
    private void openGallery(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    /**
     * Handles the facility selection, updating the selected facility name.
     *
     * @param selectedFacilityName The name of the selected facility.
     */
    @Override
    public void onFacilitySelected(String selectedFacilityName) {
        eventFacility = selectedFacilityName;
        Toast.makeText(this, "Selected Facility: " + selectedFacilityName, Toast.LENGTH_SHORT).show();
        TextView selectedFacilityTextView = findViewById(R.id.selected_facility_text_view);
        selectedFacilityTextView.setText(selectedFacilityName);
    }

    /**
     * Retrieves the facility list from SharedPreferences.
     *
     * @return ArrayList of facility names saved in SharedPreferences.
     */
    private ArrayList<String> getFacilityListFromSharedPreferences() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Set<String> facilitySet = prefs.getStringSet("facilityList", new HashSet<>());
        return new ArrayList<>(facilitySet);
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
            // Get the selected image Uri
            selectedPosterUri = data.getData();

            // Display a message or update UI
            Toast.makeText(this, "Poster selected successfully", Toast.LENGTH_SHORT).show();
            Log.d("OrganizerEventActivity", "Selected poster URI: " + selectedPosterUri.toString());

            // Optional: If you have an ImageView to show the poster, set it here
            // ImageView posterImageView = findViewById(R.id.poster_image_view);
             posterImageView.setImageURI(selectedPosterUri);
            try {
                // Generate the encrypted Base64 hash code for the image
                ImageHashCode = ImageHashGenerator.generateHashCode(this, selectedPosterUri);
                Log.d("ImageHash", "Generated Hash Code: " + ImageHashCode);

            } catch (Exception e) {
                // Catch any exception, including IOException and GeneralSecurityException
                e.printStackTrace();
                Log.e("ImageHash", "Error generating hash code: " + e.getMessage());
            }

        }
    }

}


