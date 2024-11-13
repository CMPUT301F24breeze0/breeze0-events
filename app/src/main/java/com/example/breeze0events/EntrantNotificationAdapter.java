package com.example.breeze0events;

import android.app.AlertDialog;
import android.content.Context;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.Objects;

// The adapter receives a array of a pair of
public class EntrantNotificationAdapter extends ArrayAdapter<Pair<String, String>> {
    private OnNotificationListener onNotificationListener;
    private Entrant entrant;
    private OverallStorageController overallStorageController;

    public interface OnNotificationListener {
        void OnNotification(String eventId, int id);
    }
    public EntrantNotificationAdapter(@NonNull Context context, List<Pair<String, String>> notificationList, Entrant entrant) {
        super(context, 0, notificationList);
        this.entrant = entrant;
        if (context instanceof EntrantNotificationAdapter.OnNotificationListener){
            this.onNotificationListener = (EntrantNotificationAdapter.OnNotificationListener) context;
        }else{
            throw new RuntimeException(context + "need to implement eventNameProvider");
        }
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.entrant_notification_content, parent, false);
        }

        // Set UI text from array
        String eventId = getItem(position).getLeft();
        String message = getItem(position).getRight();

        TextView UI_PostName = convertView.findViewById(R.id.publisher);
        Button UI_message = convertView.findViewById(R.id.notification_message);
        String postName = entrant.getName(eventId);
        if(postName == null){
            overallStorageController.getEvent(eventId, new EventCallback() {
                @Override
                public void onSuccess(Event event) {
                    UI_message.setText(message);
                    UI_PostName.setText(event.getName());
                }

                @Override
                public void onFailure(String errorMessage) {

                }
            });
        }else {
            UI_message.setText(message);
            UI_PostName.setText(postName);
        }
        // Set button click listener
        UI_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(postName)
                        .setMessage(message)
                        .setPositiveButton("View", (dialog, which)->{
                            if(!Objects.equals(postName, "Admin")){
                                onNotificationListener.OnNotification(eventId, position);
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
        return convertView;
    }
}
