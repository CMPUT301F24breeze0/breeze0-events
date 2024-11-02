package com.example.breeze0events;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class SearchingAdapter extends ArrayAdapter<Event> {
    public SearchingAdapter(Context context, List<Event> events) {
        super(context, 0, events);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the current event object
        Event event = getItem(position);

        // If there is no reusable view, load a new view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.entrant_event_search_content, parent, false);
        }

        // Find TextViews
        TextView idTextView = convertView.findViewById(R.id.Event_id);
        TextView nameTextView = convertView.findViewById(R.id.Event_title);

        // Set ID and event name
        idTextView.setText(String.valueOf(event.getEventId()));
        nameTextView.setText(event.getName());

        return convertView;
    }
}
