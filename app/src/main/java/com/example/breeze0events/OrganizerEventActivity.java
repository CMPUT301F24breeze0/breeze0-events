package com.example.breeze0events;

import static android.app.PendingIntent.getActivity;

import static java.security.AccessController.getContext;

import android.app.Dialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.view.View;
import androidx.annotation.NonNull;
import java.util.ArrayList;
import com.google.firebase.firestore.FirebaseFirestore;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;


public class OrganizerEventActivity exteds DialogFragment{
    private FirebaseFirestore db;
    private OverallStorageController overallStorageController;
    private ListView eventListView;
    private ArrayAdapter<String> eventListAdapter;
    private ArrayList<String> eventList;
    private EditText event_name_bar;
    private EditText event_date_bar;
    private EditText event_time_bar;
    private EditText max_entrants_bar;
    private EditText sighup_due_date_bar;
    private EditText event_description_bar;
    private EditText no_of_attendees_bar;
    private OnFragmentInteractionListener listener;
    public interface OnFragmentInteractionListener{
        void onOkPressed(Event newEvent);
    }
/*
    @Override
    public void onAttach(@NonNull Context context) {
        super.wait(context);
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
        event_date_bar = view.findViewById(R.id.event_date_bar);
        event_time_bar = view.findViewById(R.id.event_time_bar);
        max_entrants_bar = view.findViewById(R.id.max_entrants_bar);
        sighup_due_date_bar = view.findViewById(R.id.sighup_due_date_bar);
        event_description_bar = view.findViewById(R.id.event_description_bar);
        no_of_attendees_bar = view.findViewById(R.id.no_of_attendees_bar);

        final OrganizerMyListActivity current = (OrganizerMyListActivity) getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        return builder.setView(view).setTitle("Create New Event")
                .setNegativeButton("Cancel",null)
                //.setNeutralButton("Generate QR")
                //.setNeutralButton("Upload Poster")
                .setPositiveButton("Add and Save",(dialoginterface,i) ->{
                    String name = event_name_bar.getText().toString();
                    String date = event_date_bar.getText().toString();
                    String time = event_time_bar.getText().toString();
                    String max_entrants = max_entrants_bar.getText().toString();
                    String sighup_due_date = sighup_due_date_bar.getText().toString();
                    String description = event_description_bar.getText().toString();
                    String no_of_attendees = no_of_attendees_bar.getText().toString();

                    // check if all fields are filled properly

                }).create();


    }

}
*/

