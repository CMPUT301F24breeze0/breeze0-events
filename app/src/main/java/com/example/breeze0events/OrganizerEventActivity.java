package com.example.breeze0events;

import static android.app.PendingIntent.getActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.firebase.firestore.FirebaseFirestore;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;


public class OrganizerEventActivity extends DialogFragment {
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
    public interface OnFragmentInteractionListener{
        void onOkPressed(Event newEvent);
    }

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
        event_facility_bar = view.findViewById(R.id.event_facility_bar);

        final OrganizerMyListActivity current = (OrganizerMyListActivity) getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        return builder.setView(view).setTitle("Create New Event")
                .setCancelable(true)
                .setNegativeButton("Cancel",null)
                .setNeutralButton("Generate QR",(dialoginterface,i)->{})
                .setNeutralButton("Upload Poster",(dialoginterface,i)->{
                    // openGallery();
                })
                .setPositiveButton("Add and Save",(dialoginterface,i) ->{
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

}

