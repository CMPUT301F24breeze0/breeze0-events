package com.example.breeze0events;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;

/**
 * The {@code AdminEventListAdapter} class is a custom {@link ArrayAdapter} for displaying a list of events in an admin interface.
 * It populates each list item with event information, including the event name, start and end dates, and a poster image.
 * <p>
 * This adapter uses a custom layout defined in {@code admin_listview_with_image.xml}.
 * If an event's poster image is unavailable or cannot be decrypted, a default placeholder image is used.
 * </p>
 */
public class AdminEventListAdapter extends ArrayAdapter<Event> {
    private Context context;
    private List<Event> events;
    /**
     * Constructs a new {@code AdminEventListAdapter}.
     *
     * @param context The context in which the adapter will be used.
     * @param events  The list of {@link Event} objects to display in the list.
     */
    public AdminEventListAdapter(Context context, List<Event> events) {
        super(context, 0, events);
        this.context = context;
        this.events = events;
    }
    /**
     * Populates a view for a single item in the list with event details and a poster image.
     *
     * @param position    The position of the item within the adapter's data set.
     * @param convertView The old view to reuse, if possible.
     * @param parent      The parent view that this view will be attached to.
     * @return A {@link View} corresponding to the data at the specified position.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.admin_listview_with_image, parent, false);
        }

        Event event = events.get(position);

        TextView eventInfo = convertView.findViewById(R.id.eventInfo);
        ImageView posterImage = convertView.findViewById(R.id.posterImage);

        eventInfo.setText(" " + event.getName() + "\n From " + event.getStartDate() +
                "\n To " + event.getEndDate());

        String encryptedImage = event.getPosterPhoto();
        if (encryptedImage != null && !encryptedImage.isEmpty()) {
            try {
                Bitmap decryptedPosterImage = ImageHashGenerator.decryptImage(encryptedImage);
                if (decryptedPosterImage != null) {
                    posterImage.setImageBitmap(decryptedPosterImage);
                } else {
                    Log.e("AdminEventAdapter", "Failed to decrypt image. Using placeholder.");
                    posterImage.setImageResource(R.drawable.default_poster);
                }
            } catch (Exception e) {
                Log.e("AdminEventAdapter", "Error decrypting image: " + e.getMessage(), e);
                posterImage.setImageResource(R.drawable.default_poster);
            }
        } else {
            Log.e("AdminEventAdapter", "Encrypted image is null or empty. Using placeholder.");
            posterImage.setImageResource(R.drawable.default_poster);
        }

        return convertView;
    }


}
