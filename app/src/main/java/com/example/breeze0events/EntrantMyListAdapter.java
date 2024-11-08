package com.example.breeze0events;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * EntrantMyListAdapter is a custom adapter for displaying a list of events that an entrant has joined.
 * It provides functionality for viewing details or unjoining an event through dialog options.
 */
public class EntrantMyListAdapter extends ArrayAdapter<Pair<String, String>> {
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
    public EntrantMyListAdapter(Context context, List<Pair<String, String>> events) {
        super(context, 0, events);
        if (context instanceof EntrantMyListAdapter.OnUnjoinListener){
            this.unjoinListener = (EntrantMyListAdapter.OnUnjoinListener) context;
        }else{
            throw new RuntimeException(context + "need to implement OnUnjoinListener");
        }
        if (context instanceof EntrantMyListAdapter.EventNameProvider){
            this.eventNameProvider = (EntrantMyListAdapter.EventNameProvider) context;
        }else{
            throw new RuntimeException(context + "need to implement eventNameProvider");
        }
        if (context instanceof EntrantMyListAdapter.ViewListener){
            this.viewListener = (EntrantMyListAdapter.ViewListener) context;
        }else{
            throw new RuntimeException(context + "need to implement eventNameProvider");
        }
    }

    /**
     * Provides a view for each event in the list. Displays the event name and status, and includes
     * a button for viewing or unjoining the event.
     *
     * @param position    the position of the item within the adapter's data set
     * @param convertView the old view to reuse, if possible
     * @param parent      the parent view that this view will eventually be attached to
     * @return
     * the view for the specified position in the adapter
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
        String Status = getItem(position).getRight();

        // Lookup views in layout
        TextView eventName = convertView.findViewById(R.id.eventName);
        Button EventStatus = convertView.findViewById(R.id.buttonEventStatus);

        // Populate the data into the template view
        eventName.setText(name);
        EventStatus.setText(Status);

        // Set button click listener
        EventStatus.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Unjoin?")
                    .setMessage("Do you wish to unjoin this event?" + name)
                    .setNeutralButton("View", (dialog, which)->{
                        viewListener.onView(eventId);
                    })
                    .setPositiveButton("Yes", (dialog, which) -> {
                        unjoinListener.onUnjoin(eventId, position);
                    })
                    .setNegativeButton("Cancel", null);
            AlertDialog dialog = builder.create();
            dialog.show();
        });

        return convertView;
    }
}
