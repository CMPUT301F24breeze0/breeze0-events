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

// Adapter for handling notifications
public class EntrantNotificationAdapter extends ArrayAdapter<NewPair<String, String>> {
    private final OnNotificationListener onNotificationListener;
    private final OverallStorageController overallStorageController;
    private final String deviceId;

    public interface OnNotificationListener {
        void OnNotification();
    }

    public EntrantNotificationAdapter(@NonNull Context context, List<NewPair<String, String>> notificationList) {
        super(context, 0, notificationList);
        if (context instanceof EntrantNotificationAdapter.OnNotificationListener) {
            this.onNotificationListener = (EntrantNotificationAdapter.OnNotificationListener) context;
        } else {
            throw new RuntimeException(context + " must implement OnNotificationListener");
        }

        overallStorageController = new OverallStorageController();
        deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.entrant_notification_content, parent, false);
        }

        // Get notification data
        NewPair<String, String> notification = getItem(position);
        String notificationId = notification.getLeft();
        String message = notification.getRight();

        // Setup UI
        TextView uiPostName = convertView.findViewById(R.id.publisher);
        Button uiMessage = convertView.findViewById(R.id.notification_message);

        uiPostName.setText(notificationId);
        uiMessage.setText(message);

        // Set button click listener
        uiMessage.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle(notificationId)
                    .setMessage(message)
                    .setNegativeButton("OK", null)
                    .setNeutralButton("Delete", (dialog, which) -> {
                        deleteNotification(notificationId);
                    })
                    .setPositiveButton("View MyList", (dialog, which) -> {
                        onNotificationListener.OnNotification();
                    });
            builder.create().show();
        });

        return convertView;
    }

    /**
     * Deletes a notification from the database and the adapter list.
     *
     * @param notificationId The unique ID of the notification to delete.
     */
    private void deleteNotification(String notificationId) {
        overallStorageController.getEntrant(deviceId, new EntrantCallback() {
            @Override
            public void onSuccess(Entrant entrant) {
                List<NewPair<String, String>> updatedNotifications = entrant.getNotifications();

                // Find and remove the notification with the matching ID
                for (int i = 0; i < updatedNotifications.size(); i++) {
                    if (updatedNotifications.get(i).getLeft().equals(notificationId)) {
                        updatedNotifications.remove(i);
                        break;
                    }
                }

                // Update the entrant's notifications in the database
                entrant.setNotifications(updatedNotifications);
                overallStorageController.updateEntrant(entrant);

                // Update the adapter's list and notify the changes
                removeIfExists(notificationId);
                notifyDataSetChanged();
            }

            @Override
            public void onFailure(String errorMessage) {
                // Handle failure (e.g., log an error message)
            }
        });
    }

    /**
     * Removes the notification from the adapter's list if it exists.
     *
     * @param notificationId The unique ID of the notification to remove.
     */
    private void removeIfExists(String notificationId) {
        for (int i = 0; i < getCount(); i++) {
            if (getItem(i).getLeft().equals(notificationId)) {
                remove(getItem(i));
                break;
            }
        }
    }
}
