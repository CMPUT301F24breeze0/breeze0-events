package com.example.breeze0events;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;
/**
 * The SearchingAdapter class is a custom adapter used for displaying a list of Event objects in a ListView.
 * It inflates a custom layout for each item and populates it with event data (ID and name).
 */
public class SearchingAdapter extends ArrayAdapter<Event> {
    /**
     * Constructor for the SearchingAdapter.
     *
     * @param context The context where the adapter is being used.
     * @param events  The list of Event objects to be displayed.
     */
    public SearchingAdapter(Context context, List<Event> events) {
        super(context, 0, events);
    }

    /**
     * Provides a view for an adapter view (ListView, GridView, etc.).
     *
     * @param position    The position of the item in the adapter's data set.
     * @param convertView The old view to reuse, if possible.
     * @param parent      The parent view that this view will eventually be attached to.
     * @return A View corresponding to the data at the specified position.
     */
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
        idTextView.setText(String.valueOf(event.getEventId()) + ". ");
        nameTextView.setText(event.getName());

        return convertView;
    }
}
