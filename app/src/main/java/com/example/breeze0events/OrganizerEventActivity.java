package com.example.breeze0events;

import static android.app.PendingIntent.getActivity;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView;


public class OrganizerEventActivity extends AppCompatActivity implements AddFacilityActivity.FacilitySelectListener {
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
    private String eventFacility,qrHashCode,ImageHashCode;

    ArrayList<String> facilityList;
    ImageView posterImageView;
    public interface OnFragmentInteractionListener{
        void onOkPressed(Event newEvent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_event_activity);

        String headerText = getIntent().getStringExtra("header_text");
        String newEventId = getIntent().getStringExtra("new_event_id");
        TextView idTextView = findViewById(R.id.organizer_event_activity_id);
        Button addButton = findViewById(R.id.organizer_event_activity_add_button);
        Button backButton = findViewById(R.id.organizer_event_activity_back_button);
        Button uploadPosterButton = findViewById(R.id.organizer_facility_event_poster_upload_button);
        Button generateQRButton = findViewById(R.id.organizer_event_activity_generate_qr_button);
        EditText name = findViewById(R.id.event_name_bar);
        EditText start_date = findViewById(R.id.event_start_date_bar);
        EditText end_date = findViewById(R.id.event_end_date_bar);
        EditText entrants = findViewById(R.id.entrants_bar);
        Button facilityButton = findViewById(R.id.organizer_event_activity_facility_button);
        overallStorageController = new OverallStorageController();
        posterImageView = findViewById(R.id.organizer_facility_event_poster_image);

        // set header
        TextView headerTextView = findViewById(R.id.organizer_event_activity_header);
        if (headerText != null) {
            headerTextView.setText(headerText);
        }

        // display new id
        if (newEventId != null) {
            idTextView.setText(newEventId);
        }

        //  by clicking "Select Facility" button
        facilityButton.setOnClickListener(v -> {
            AddFacilityActivity dialog = new AddFacilityActivity();
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


        //by clicking "Generate" Button
        generateQRButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String eventId = idTextView.getText().toString();
                qrHashCode = QRHashGenerator.generateHash(eventId);
                Log.d("OrganizerEventActivity", "Generated QR HashCode: " + qrHashCode);

            }
        });


        // by clicking "Add" button
        addButton.setOnClickListener(v->{
            String eventName = name.getText().toString().trim();
            String startDate = start_date.getText().toString().trim();
            String endDate = end_date.getText().toString().trim();
            String entrantsList = entrants.getText().toString().trim();
            String eventId = idTextView.getText().toString();

            String qrCodePath = qrHashCode;
            String posterUri = ImageHashCode;
            String organizerId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID); // device id as organizer id


            // Check if required fields are empty
            if (eventName.isEmpty() || startDate.isEmpty() || endDate.isEmpty() || entrantsList.isEmpty()) {
                Toast.makeText(OrganizerEventActivity.this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
                return;
            }

            List<String> organizers = new ArrayList<>();
            organizers.add(organizerId);
            List<String> newEntrants = Arrays.asList(entrantsList.split("\\s*,\\s*"));

            Event newEvent = new Event(eventId, eventName, qrCodePath, posterUri, eventFacility, startDate, endDate, new ArrayList<>(), organizers);
            Log.d("OrganizerEventActivity", "Calling addEvent with Event ID: " + eventId);
            overallStorageController.addEvent(newEvent);


            Toast.makeText(OrganizerEventActivity.this, "Event added successfully", Toast.LENGTH_SHORT).show();

            finish(); // Close activity
        });

        // by clicking "Back" button
        backButton.setOnClickListener(v-> finish());
    }

    private void openGallery(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedPosterUri = data.getData();
            posterImageView.setImageURI(selectedPosterUri);
        }
    }

    @Override
    public void onFacilitySelected(String selectedFacility) {
        eventFacility = selectedFacility;
        Toast.makeText(this, "Selected Facility: " + selectedFacility, Toast.LENGTH_SHORT).show();
    }

    private ArrayList<String> getFacilityListFromSharedPreferences() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Set<String> facilitySet = prefs.getStringSet("facilityList", new HashSet<>());
        return new ArrayList<>(facilitySet);
    }


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


