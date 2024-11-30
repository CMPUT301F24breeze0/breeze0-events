package com.example.breeze0events;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;


/**
 * The AdminSelectedEvents class displays the details of a selected event, allowing the admin
 * to view additional event information, navigate to more detailed views, or view the QR code
 * associated with the selected event.
 */

public class AdminSelectedEvents extends AppCompatActivity {
    TextView EventName;
    Button backButton, DetailButton, QRCodeButton, imageButton;
    Event selected_event;
    OverallStorageController overallStorageController;
    String facilityName;

    /**
     * Initializes the activity, sets up UI components, and retrieves the selected event details from
     * the intent. Allows the admin to view event details, navigate to event detail view, or view the QR code.
     *
     * @param savedInstanceState The saved instance state of the activity.
     */

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_guide_fragment);

        ImageView posterImageView = findViewById(R.id.posterImageView);
        String posterPhoto = getIntent().getStringExtra("poster_photo");
        String Id = (String) getIntent().getSerializableExtra("selectedID");
        ArrayList<String> eventListDisplay = getIntent().getStringArrayListExtra("eventListDisplay");

        backButton = findViewById(R.id.back_in_main);
        DetailButton = findViewById(R.id.DetailButton);
        QRCodeButton = findViewById(R.id.QRCodeButton);
        EventName = findViewById(R.id.EventName);
        imageButton = findViewById(R.id.imageButton);


        if (posterPhoto != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(posterPhoto);
            posterImageView.setImageBitmap(bitmap);
        }


        overallStorageController = new OverallStorageController();
        overallStorageController.getEvent(Id, new EventCallback() {
            @Override
            public void onSuccess(Event event) {
                selected_event = event;
                EventName.setText(selected_event.getName());
            }

            @Override
            public void onFailure(String errorMessage) {
            }
        });


        backButton.setOnClickListener(v -> {
            finish();
        });

        DetailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminSelectedEvents.this, AdminEventDetail.class);
                intent.putExtra("selectedID", selected_event.getEventId());
                intent.putExtra("start_date", selected_event.getStartDate());
                intent.putExtra("end_date", selected_event.getEndDate());
                intent.putStringArrayListExtra("eventListDisplay", eventListDisplay);
                startActivity(intent);
            }
        });

        QRCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminSelectedEvents.this, AdminQRcode.class);
                intent.putExtra("eventId", selected_event.getEventId());
                intent.putExtra("qrcode", selected_event.getQrCode());
                startActivity(intent);
            }
        });
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminSelectedEvents.this, AdminBrowseImage.class);
                intent.putExtra("event1Id", selected_event.getEventId());
                System.out.println(selected_event.getEventId());
                intent.putExtra("poster", selected_event.getPosterPhoto());
                startActivity(intent);
            }
        });


//        //11.17 POSTER PHOTO UPDATE but TransactionTooLargeException
//        if (posterPhoto != null && !posterPhoto.isEmpty()) {
//            if (posterPhoto.startsWith("http")) {
//                // Load image from URL using a background thread
//                loadImageFromUrl(posterPhoto, posterImageView);
//            } else {
//                // Decode Base64 string and set to ImageView
//                try {
//                    byte[] decodedString = Base64.decode(posterPhoto, Base64.DEFAULT);
//                    Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
//                    posterImageView.setImageBitmap(decodedBitmap);
//                } catch (IllegalArgumentException e) {
//                    Log.e("AdminSelectedEvents", "Invalid Base64 string for poster photo", e);
//                    Toast.makeText(this, "Failed to load poster photo", Toast.LENGTH_SHORT).show();
//                }
//            }
//        }
//    }
//    //11.17 POSTER PHOTO UPDATE
///**
// * Loads an image from a URL into an ImageView.
// *
// * @param imageUrl The URL of the image to load.
// * @param imageView The ImageView where the image will be displayed.
// */
//        private void loadImageFromUrl (String imageUrl, ImageView imageView){
//            new Thread(() -> {
//                try {
//                    java.net.URL url = new java.net.URL(imageUrl);
//                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//                    connection.setDoInput(true);
//                    connection.connect();
//                    InputStream input = connection.getInputStream();
//                    Bitmap bitmap = BitmapFactory.decodeStream(input);
//
//                    // Update the ImageView on the main thread
//                    runOnUiThread(() -> imageView.setImageBitmap(bitmap));
//                } catch (Exception e) {
//                    Log.e("AdminSelectedEvents", "Failed to load image from URL", e);
//                    runOnUiThread(() -> Toast.makeText(this, "Failed to load poster photo.", Toast.LENGTH_SHORT).show());
//                }
//            }).start();
//        }

    }
}
