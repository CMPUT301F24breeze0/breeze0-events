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

public class EntrantMyListAdapter extends ArrayAdapter<Pair<String, String>> {

    public EntrantMyListAdapter(Context context, List<Pair<String, String>> events) {
        super(context, 0, events);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Reuse the view if possible
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.entrant_mylist_content, parent, false);
        }

        // Get the data item for this position
        String name = getItem(position).getLeft();
        String Status = getItem(position).getRight();

        // Lookup views in layout
        TextView eventName = convertView.findViewById(R.id.eventName);
        Button EventStatus = convertView.findViewById(R.id.buttonEventStatus);

        // Populate the data into the template view
        eventName.setText(name);
        EventStatus.setText(Status);

        // Set button click listener
        EventStatus.setOnClickListener(v -> {
            // Define action when the button is clicked
            Toast.makeText(getContext(), "Button clicked for " + name, Toast.LENGTH_SHORT).show();
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Unjoin?")
                    .setMessage("您确定要退出 " + name + " Do you wish to unjoin this event?")
                    .setPositiveButton("Yes", (dialog, which) -> {

                    })
                    .setNegativeButton("Cancel", null);
            AlertDialog dialog = builder.create();
            dialog.show();
        });

        return convertView;
    }
}
