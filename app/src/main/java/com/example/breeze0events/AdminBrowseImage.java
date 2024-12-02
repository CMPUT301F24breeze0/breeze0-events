package com.example.breeze0events;

import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.appcompat.app.AppCompatActivity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import com.squareup.picasso.Picasso;
/**
 * The {@code AdminBrowseImage} class represents the activity for browsing and managing an event's poster image.
 * It provides functionalities to view the poster, delete it, and navigate back to the event list.
 * <p>
 * This activity retrieves event details through intents and interacts with the storage controller
 * to perform CRUD operations on the event's poster image.
 * </p>
 */
public class AdminBrowseImage extends AppCompatActivity {
    /**
     * Controller for managing overall storage operations, such as retrieving and updating events.
     */

    OverallStorageController overallStorageController= new OverallStorageController();
    /**
     * Called when the activity is created. Sets up the layout and initializes the view components.
     *
     * @param savedInstanceState A {@link Bundle} object containing the activity's previously saved state.
     */

    protected void onCreate(Bundle savedInstanceState){
        String eventId=getIntent().getStringExtra("event1Id");
        System.out.println(eventId);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_poster_photo);
        ImageView poster_photo= findViewById(R.id.posterPhoto);
        String posterPhotoValue= getIntent().getStringExtra("poster");
        Button delete_button=findViewById(R.id.delete_button);
        Button back_button=findViewById(R.id.back_button);

        if (posterPhotoValue == null) {
            poster_photo.setVisibility(View.GONE); // US030302 testing needed
        }

        back_button.setOnClickListener(v->{
            finish();
        });
        delete_button.setOnClickListener(v->{
            overallStorageController.getEvent(eventId,new EventCallback(){
                /**
                 * Handles successful retrieval of the event.
                 *
                 * @param event The retrieved {@link Event} object.
                 */
                public void onSuccess(Event event){
                    if (event.getPosterPhoto()!=null) {


                        event.setPosterPhoto(null);
                        overallStorageController.updateEvent(event);
                        BackToEventList();
                    }
                    else{
                        BackToEventList();
                    }
                }
                /**
                 * Handles failure in retrieving the event.
                 *
                 * @param errorMessage A {@link String} describing the error that occurred.
                 */
                public void onFailure(String errorMessage){

                }

            });

        });

        System.out.println(posterPhotoValue);
        try {
            Bitmap decryptedBitmap = ImageHashGenerator.decryptImage(posterPhotoValue);

            poster_photo.setImageBitmap(decryptedBitmap);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("ImageError", "Error decrypting and setting image: " + e.getMessage());
        }

    }
    /**
     * Navigates back to the admin event list activity.
     * This method starts the {@link AdminEventActivity} and finishes the current activity.
     */
    private void BackToEventList() {
        Intent intent = new Intent(AdminBrowseImage.this, AdminEventActivity.class);
        startActivity(intent);
        finish();
    }
}

