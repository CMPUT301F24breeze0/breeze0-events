package com.example.breeze0events;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.OutputStream;

import android.util.Base64;
import android.widget.Toast;

public class EntrantEventDetail extends AppCompatActivity {
    private TextView event_title;
    private TextView event_information;
    private ImageView QRcode;
    private Button event_join;
    private Button event_cancel;
    private ImageView event_poster;
    private OverallStorageController overallStorageController;
    private String eventID;
    private String eventLocation;
    private Event eventLocal;
    private int Mutex=1;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entrant_event_show);

        // initialize
        event_title = findViewById(R.id.entrant_event_title);
        event_information = findViewById(R.id.Event_information);
        QRcode = findViewById(R.id.Entrent_event_QRcode);
        event_join = findViewById(R.id.entrant_event_join);
        event_cancel = findViewById(R.id.entrant_event_cancel);
        event_poster = findViewById(R.id.Entrant_event_poster);
        overallStorageController = new OverallStorageController();
        Intent intent = getIntent();
        String id = intent.getStringExtra("eventID");

        // Receive data from firebase
        overallStorageController.getEvent(String.valueOf(id), new EventCallback() {
            // FIXME: 2024/10/27 Add functionality to show Max number of entrants
            @Override
            public void onSuccess(Event event) {
                eventLocal = event;
                eventID = event.getEventId();
                eventLocation = event.getFacility();
                event_title.setText(event.getName());
                String information = "Event Name: "+event.getName()
                        +"\nEvent Date: "+event.getStartDate()
                        +"\nSign up Due Date: "+event.getEndDate()
                        +"\nEvent Organizers: "+event.getOrganizers();
                event_information.setText(information);
                try {
                    event_poster.setImageBitmap(ImageHashGenerator.decryptImage(event.getPosterPhoto()));
                    QRcode.setImageBitmap(QRHashGenerator.generateQRCode(event.getQrCode()));
                } catch (Exception e) {
                }
//                event_poster.setImageBitmap(decodeBase64Image(event.getPosterPhoto()));
//                QRcode.setImageBitmap(decodeBase64Image(event.getQrCode()));
                Mutex = 0;
            }

            @Override
            public void onFailure(String errorMessage) {

            }
        });
        QRcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(EntrantEventDetail.this, "Downloading", Toast.LENGTH_SHORT).show();
                QRcode.setDrawingCacheEnabled(true);
                QRcode.buildDrawingCache();
                Bitmap SavedBitmap = ((BitmapDrawable) QRcode.getDrawable()).getBitmap();

                saveImageToStorage(EntrantEventDetail.this, SavedBitmap);
                QRcode.setDrawingCacheEnabled(false); // 关闭缓存
            }
        });

        event_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        event_join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Mutex != 0){
                    Toast.makeText(EntrantEventDetail.this,"Loading data", Toast.LENGTH_SHORT).show();
                    return;
                }
                String deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
                overallStorageController.getEntrant(deviceId, new EntrantCallback() {
                    @Override
                    public void onSuccess(Entrant entrant) {
                        if(entrant.checkEventId(eventID)){
                            Toast.makeText(EntrantEventDetail.this,"You have joined this event", Toast.LENGTH_SHORT ).show();
                        }else{
                            entrant.set_add_Event(eventID, eventLocal.getName(), "Joined");
                            overallStorageController.updateEntrant(entrant);
                            Toast.makeText(EntrantEventDetail.this,"Join successfully", Toast.LENGTH_SHORT ).show();
                            backToMyList();
                        }
                        eventLocal.addEntrants(deviceId);
                        overallStorageController.updateEvent(eventLocal);
                        Mutex = 1;

                    }
                    @Override
                    public void onFailure(String errorMessage) {
                        Toast.makeText(EntrantEventDetail.this,"Failed due to network or database failed", Toast.LENGTH_SHORT ).show();
                    }
                });
            }
        });
    }
    public static Bitmap decodeBase64Image(String base64ImageString) {
        byte[] imageBytes = Base64.decode(base64ImageString, Base64.DEFAULT);
        // Decode Base64 string to byte array
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);

    }
    private void backToMyList(){
        Intent go_back = new Intent(EntrantEventDetail.this, EntrantMylistActivity.class);
        go_back.putExtra("update", eventID);
        go_back.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(go_back);
    }
//    private void saveImageToStorage(Bitmap bitmap) {
//        String filename = System.currentTimeMillis() + ".jpg";
//        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), filename);
//        try (FileOutputStream out = new FileOutputStream(file)) {
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        MediaScannerConnection.scanFile(EntrantEventDetail.this, new String[] { file.getAbsolutePath() }, null, null);
//    }
    private void saveImageToStorage(EntrantEventDetail context, Bitmap bitmap) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, "downloaded_image.jpg");
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/MyAppImages");

        Uri imageUri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        if (imageUri != null) {
            try (OutputStream outputStream = context.getContentResolver().openOutputStream(imageUri)) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream);
                Toast.makeText(context, "Image saved successfully", Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(context, "Failed to save image", Toast.LENGTH_SHORT).show();
            }
        }
    }


}
