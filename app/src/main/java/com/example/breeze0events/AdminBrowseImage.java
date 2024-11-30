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
    OverallStorageController overallStorageController= new OverallStorageController();;

    protected void onCreate(Bundle savedInstanceState){
        String eventId=getIntent().getStringExtra("event1Id");
        System.out.println(eventId);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_poster_photo);
        ImageView poster_photo= findViewById(R.id.posterPhoto);
        String posterPhotoValue= getIntent().getStringExtra("poster");
        Button delete_button=findViewById(R.id.delete_button);
        Button back_button=findViewById(R.id.back_button);
        back_button.setOnClickListener(v->{
            finish();
        });
        delete_button.setOnClickListener(v->{
            overallStorageController.getEvent(eventId,new EventCallback(){
                public void onSuccess(Event event){
                    event.setPosterPhoto(null);
                    overallStorageController.updateEvent(event);
                    finish();
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
