package com.example.breeze0events;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class EntrantMyListAdapter extends ArrayAdapter<Event> {

    public EntrantMyListAdapter(Context context, List<Event> events) {
        super(context, 0, events);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Reuse the view if possible
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.entrant_mylist_content, parent, false);
        }

        // Get the data item for this position
        Event event = getItem(position);

        // Lookup views in layout
        TextView eventName = convertView.findViewById(R.id.eventName);
        Button actionButton = convertView.findViewById(R.id.buttonEventStatus);

        // Populate the data into the template view
        eventName.setText(event.getName());

        // Set button click listener
        actionButton.setOnClickListener(v -> {
            // Define action when the button is clicked
            Toast.makeText(getContext(), "Button clicked for " + event.getName(), Toast.LENGTH_SHORT).show();
        });

        return convertView;
    }
}
