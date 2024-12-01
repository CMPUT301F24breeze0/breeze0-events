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


public class AdminEventListAdapter extends ArrayAdapter<Event> {
    private Context context;
    private List<Event> events;

    public AdminEventListAdapter(Context context, List<Event> events) {
        super(context, 0, events);
        this.context = context;
        this.events = events;
    }

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
