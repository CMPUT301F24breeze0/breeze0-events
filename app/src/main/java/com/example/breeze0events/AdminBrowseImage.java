package com.example.breeze0events;

import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.appcompat.app.AppCompatActivity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import com.squareup.picasso.Picasso;

public class AdminBrowseImage extends AppCompatActivity {
    String eventId=getIntent().getStringExtra("eventId");
    OverallStorageController overallStorageController;
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_poster_photo);
        ImageView poster_photo= findViewById(R.id.posterPhoto);
        String posterPhotoValue= getIntent().getStringExtra("poster");
        Button delete_button=findViewById(R.id.delete_button);
        delete_button.setOnClickListener(v->{
            overallStorageController.getEvent(eventId,new EventCallback(){
                public void onSuccess(Event event){
                    event.setPosterPhoto(null);
                    overallStorageController.updateEvent(event);
                }
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
}
