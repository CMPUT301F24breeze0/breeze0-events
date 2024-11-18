package com.example.breeze0events;

import android.app.AlertDialog;
import android.content.Context;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * EntrantMyListAdapter is a custom adapter for displaying a list of events that an entrant has joined.
 * It provides functionality for viewing details or handling event statuses.
 */
public class EntrantMyListAdapter extends ArrayAdapter<NewPair<String, String>> {
    private OnUnjoinListener unjoinListener;
    private EventNameProvider eventNameProvider;
    private ViewListener viewListener;

    /**
     * Interface to handle unjoin actions.
     */
    public interface OnUnjoinListener {
        void onUnjoin(String eventId, int id);
    }

    /**
     * Interface to retrieve the name of an event based on its ID.
     */
    public interface EventNameProvider {
        String getEventNameById(String eventId);
    }

    /**
     * Interface to handle view actions.
     */
    public interface ViewListener {
        void onView(String eventId);
    }

    /**
     * Constructs a new EntrantMyListAdapter.
     *
     * @param context the current context
     * @param events  the list of events represented by pairs of event IDs and statuses
     * @throws RuntimeException if the context does not implement the required interfaces
     */
    public EntrantMyListAdapter(Context context, List<NewPair<String, String>> events) {
        super(context, 0, events);
        if (context instanceof OnUnjoinListener) {
            this.unjoinListener = (OnUnjoinListener) context;
        } else {
            throw new RuntimeException(context + " must implement OnUnjoinListener");
        }
        if (context instanceof EventNameProvider) {
            this.eventNameProvider = (EventNameProvider) context;
        } else {
            throw new RuntimeException(context + " must implement EventNameProvider");
        }
        if (context instanceof ViewListener) {
            this.viewListener = (ViewListener) context;
        } else {
            throw new RuntimeException(context + " must implement ViewListener");
        }
    }

    /**
     * Provides a view for each event in the list. Displays the event name and status, and includes
     * a button for handling event status changes.
     *
     * @param position    the position of the item within the adapter's data set
     * @param convertView the old view to reuse, if possible
     * @param parent      the parent view that this view will eventually be attached to
     * @return the view for the specified position in the adapter
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Reuse the view if possible
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.entrant_mylist_content, parent, false);
        }

        // Get the data item for this position
        String eventId = getItem(position).getLeft();
        String name = eventNameProvider.getEventNameById(eventId);
        String status = getItem(position).getRight();

        // Lookup views in layout
        TextView eventName = convertView.findViewById(R.id.eventName);
        Button eventStatusButton = convertView.findViewById(R.id.buttonEventStatus);
        LinearLayout eventLayout = (LinearLayout) convertView; // The parent layout

        // Populate the data into the template view
        eventName.setText(name);
        eventStatusButton.setText(status);

        // Add click listener to the entire layout to view event details
        eventLayout.setOnClickListener(v -> viewListener.onView(eventId));

        // Set button click listener for handling status changes
        eventStatusButton.setOnClickListener(v -> {
            switch (status) {
                case "Requested":
                    // Organizer has requested acceptance; ask entrant to accept or reject
                    AlertDialog.Builder requestedBuilder = new AlertDialog.Builder(getContext());
                    requestedBuilder.setTitle("Event Request")
                            .setMessage("The organizer has requested your acceptance. Would you like to accept or reject this event?")
                            .setPositiveButton("Accept", (dialog, which) -> updateEventStatus(eventId, "Accepted", position))
                            .setNegativeButton("Reject", (dialog, which) -> updateEventStatus(eventId, "Rejected", position));
                    requestedBuilder.create().show();
                    break;
                    
                case "Joined":
                    // Organizer has not yet reviewed; only unjoin is possible
                    AlertDialog.Builder joinedBuilder = new AlertDialog.Builder(getContext());
                    joinedBuilder.setTitle("Unjoin Event?")
                            .setMessage("You are currently joined. Do you want to unjoin?")
                            .setPositiveButton("Yes", (dialog, which) -> unjoinListener.onUnjoin(eventId, position))
                            .setNegativeButton("Cancel", null);
                    joinedBuilder.create().show();
                    break;

                case "Accepted":
                    // Entrant has been accepted, decide to accept or reject
                    AlertDialog.Builder acceptedBuilder = new AlertDialog.Builder(getContext());
                    acceptedBuilder.setTitle("Event Decision")
                            .setMessage("You have been accepted. Would you like to accept or reject this event?")
                            .setPositiveButton("Accept", (dialog, which) -> updateEventStatus(eventId, "Accepted", position))
                            .setNegativeButton("Reject", (dialog, which) -> updateEventStatus(eventId, "Rejected", position));
                    acceptedBuilder.create().show();
                    break;

                case "Rejected":
                    // Entrant has rejected but can rejoin
                    AlertDialog.Builder rejectedBuilder = new AlertDialog.Builder(getContext());
                    rejectedBuilder.setTitle("Rejoin Event?")
                            .setMessage("You have rejected this event. Would you like to rejoin?")
                            .setPositiveButton("Rejoin", (dialog, which) -> updateEventStatus(eventId, "Joined", position))
                            .setNegativeButton("Cancel", null);
                    rejectedBuilder.create().show();
                    break;

                default:
                    // Unknown status, provide a generic message
                    Toast.makeText(getContext(), "Unexpected status: " + status, Toast.LENGTH_SHORT).show();
                    break;
            }
        });

        return convertView;
    }

    /**
     * Updates the status of an event for the entrant and refreshes the UI.
     *
     * @param eventId  the ID of the event to update
     * @param newStatus the new status to set
     * @param position the position of the event in the list
     */
    private void updateEventStatus(String eventId, String newStatus, int position) {
        getItem(position).setRight(newStatus);
        notifyDataSetChanged();
        OverallStorageController overallStorageController = new OverallStorageController();
        overallStorageController.getEntrant(Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID), new EntrantCallback() {
            @Override
            public void onSuccess(Entrant entrant) {
                entrant.getEventsStatus().put(eventId, newStatus);
                overallStorageController.updateEntrant(entrant);
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(getContext(), "Failed to update status: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
