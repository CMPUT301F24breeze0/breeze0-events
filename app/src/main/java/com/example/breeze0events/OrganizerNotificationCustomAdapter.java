package com.example.breeze0events;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.util.Pair;
import java.util.ArrayList;
import java.util.HashSet;
/**
 * OrganizerNotificationCustomAdapter is a custom adapter for displaying a list of entrants.
 * Each item in the list represents an entrant, showing their ID and name.
 * The adapter supports highlighting selected items and customizing the appearance of the list items.
 */
public class OrganizerNotificationCustomAdapter extends ArrayAdapter<Pair<String, String>> {
    private Context context;
    private ArrayList<Pair<String, String>> data;
    private HashSet<Integer> selectedPositions;
    /**
     * OrganizerNotificationCustomAdapter is a custom adapter for displaying a list of entrants.
     * It supports highlighting selected items and customizing the appearance of list items.
     */
    public OrganizerNotificationCustomAdapter(Context context, ArrayList<Pair<String, String>> data, HashSet<Integer> selectedPositions) {
        super(context, 0, data);
        this.context = context;
        this.data = data;
        this.selectedPositions = selectedPositions;
    }

    @Override
    public int getCount() {
        return data.size();
    }
    /**
     * Returns the data item at the specified position.
     *
     * @param position The position of the item in the data set.
     * @return The entrant data as a Pair (ID and name).
     */
    @Override
    public Pair<String, String> getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    /**
     * Returns the unique ID for the item at the specified position.
     * Since the position is unique in the list, it is returned as the ID.
     *
     * @param position The position of the item in the data set.
     * @return The position of the item.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.organizer_contact_layout, parent, false);
        }

        TextView nameTextView = convertView.findViewById(R.id.entrant_name);
        TextView idTextView = convertView.findViewById(R.id.entrant_id);

        Pair<String, String> currentEntrant = getItem(position);

        if (currentEntrant != null) {
            nameTextView.setText(currentEntrant.second);
            idTextView.setText(currentEntrant.first);
        }

        nameTextView.setTextColor(context.getResources().getColor(R.color.text_color));
        idTextView.setTextColor(context.getResources().getColor(R.color.text_color));

        if (selectedPositions.contains(position)) {
            convertView.setBackgroundColor(context.getResources().getColor(R.color.listview_divider_color));
        } else {
            convertView.setBackgroundColor(context.getResources().getColor(R.color.button_color));
        }

        return convertView;
    }
}