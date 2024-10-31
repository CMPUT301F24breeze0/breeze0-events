package com.example.breeze0events;

import static android.app.PendingIntent.getActivity;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.TextView;
import android.widget.Toast;


public class OrganizerEventActivity extends AppCompatActivity implements AddFacilityActivity.FacilitySelectListener {
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
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri selectedPosterUri = null;
    Button facilityButton = findViewById(R.id.organizer_event_activity_facility_button);
    private String eventFacility;



    public interface OnFragmentInteractionListener{
        void onOkPressed(Event newEvent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_event_activity);

        Button back_button = findViewById(R.id.organizer_event_activity_back_button);
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
            // 显示 AddFacilityActivity 对话框
            AddFacilityActivity addFacilityDialog = new AddFacilityActivity();
            addFacilityDialog.show(getSupportFragmentManager(), "AddFacilityDialog");
        });




        // by clicking "Add" button
        addButton.setOnClickListener(v->{
            String eventName = name.getText().toString().trim();
            String startDate = start_date.getText().toString().trim();
            String endDate = end_date.getText().toString().trim();
            String entrantsList = entrants.getText().toString().trim();
            String eventId = idTextView.getText().toString();
            String qrCodePath = "android.resource://" + getPackageName() + "/drawable/example_qr";
            String posterUri = "android.resource://" + getPackageName() + "/drawable/default_poster";
            String organizerId = android.os.Build.SERIAL; // device id as organizer id

            // Check if required fields are empty
            if (eventName.isEmpty() || startDate.isEmpty() || endDate.isEmpty() || entrantsList.isEmpty()) {
                Toast.makeText(OrganizerEventActivity.this, "Please fill in all required fields", Toast.LENGTH_SHORT).show();
                return;
            }

            List<String> organizers = new ArrayList<>();
            organizers.add(organizerId);
            List<String> newEntrants = Arrays.asList(entrantsList.split("\\s*,\\s*"));

            Event newEvent = new Event(eventId, eventName, qrCodePath, posterUri, eventFacility, startDate, endDate, newEntrants, organizers);
            overallStorageController.addEvent(newEvent);


            Toast.makeText(OrganizerEventActivity.this, "Event added successfully", Toast.LENGTH_SHORT).show();

            finish(); // Close activity
        });

        // by clicking "Back" button
        back_button.setOnClickListener(v-> finish());
    }

    @Override
    public void onFacilitySelected(String selectedFacility) {
        eventFacility = selectedFacility;
        facilityButton.setText(eventFacility);
    }
    /*

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener){
            listener = (OnFragmentInteractionListener) context;
        }
        else{
            throw new RuntimeException(context + "need to implement OnFragmentInteractionListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@NonNull Bundle savedInstanceState){
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.organizer_event_activity, null);
        event_name_bar = view.findViewById(R.id.event_name_bar);
        event_start_date_bar = view.findViewById(R.id.event_start_date_bar);
        event_end_date_bar = view.findViewById(R.id.event_end_date_bar);
        entrants_bar = view.findViewById(R.id.entrants_bar);
        // event_facility_bar = view.findViewById(R.id.event_facility_bar);

        final OrganizerMyListActivity current = (OrganizerMyListActivity) getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        return builder.setView(view).setTitle("Create New Event")
                .setCancelable(true)
                .setNegativeButton("Cancel",null)
                .setNeutralButton("Generate QR",(dialoginterface,i)->{})
                .setNeutralButton("Upload Poster",(dialoginterface,i)->{
                    // openGallery();
                })
                .setPositiveButton("Save",(dialoginterface,i) ->{
                    String name = event_name_bar.getText().toString();
                    String facility = event_facility_bar.getText().toString();
                    String start_date = event_start_date_bar.getText().toString();
                    String end_date = event_end_date_bar.getText().toString();
                    String entrants = entrants_bar.getText().toString();
                    List<String> entrantsList = Arrays.asList(entrants.split(",\\s*")); // when entering multiple entrants, use ',' to split each other


                    Event newEvent = new Event(null, name, null, null, facility,start_date, end_date, entrantsList, null);
                    listener.onOkPressed(newEvent);

                }).create();


    }

    private static final int PICK_IMAGE = 1;
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE);
    }
*/
}


