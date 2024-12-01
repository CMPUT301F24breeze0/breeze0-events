package com.example.breeze0events;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

import android.util.Base64;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.GeoPoint;


/**
 * The EntrantEventDetail class provides a detailed view of an event for an entrant,
 * displaying info such as event title, description, poster, location and QR code.
 * Entrants can join the event, save the event's QR code as an image.
 * This class able to retrieve and update event and entrant data from FireStore.
 */

public class EntrantEventDetail extends AppCompatActivity {
    private TextView event_title, event_information;
    private ImageView QRcode, event_poster;
    private Button event_join, event_cancel;
    private OverallStorageController overallStorageController;
    private String eventID, eventLocation,deviceId;
    private Event eventLocal;
    private int Mutex=1, mutext2 = 0;
    private FusedLocationProviderClient fusedLocationClient;
    private GeoPoint entrantGeoPoint;
    private boolean geoRequest;

    /**
     * Called when the activity is created. Sets up the UI components, fetches event data,
     * and initializes listeners for interaction with UI elements.
     *
     * @param savedInstanceState The saved instance state bundle
     */

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
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        Intent intent = getIntent();
        String id = intent.getStringExtra("eventID");
        deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

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
                        +"\n\nEvent Date: "+event.getStartDate()
                        +"\n\nSign up Due Date: "+event.getEndDate()
                        +"\n\nEvent Organizers: "+event.getOrganizers();
                event_information.setText(information);
                try {
                    QRcode.setImageBitmap(QRHashGenerator.generateQRCode(event.getQrCode()));
                } catch (Exception e) {
                }
                try{
                    event_poster.setImageBitmap(ImageHashGenerator.decryptImage(event.getPosterPhoto()));
                }catch(Exception e) {

                }
                if(Objects.equals(event.getGeolocation(), "true")){
                    geoRequest = true;
                }
                // initialize geolocation
                if (geoRequest){
                    AlertDialog.Builder builder = new AlertDialog.Builder(EntrantEventDetail.this);
                    builder.setTitle("This Event requires your geolocation ")
                            .setMessage("Do you want to continue or return?")
                            .setCancelable(false)
                            .setNeutralButton("Return", (dialog, which) -> {
                                finish();
                            })
                            .setPositiveButton("OK", (dialog, which) -> {
                                requestLocation();
                            });
                    builder.create().show();
                }
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
                if(Mutex != 0 || mutext2 != 0){
                    Toast.makeText(EntrantEventDetail.this,"Loading data", Toast.LENGTH_SHORT).show();
                    return;
                }
                overallStorageController.getEntrant(deviceId, new EntrantCallback() {
                    @Override
                    public void onSuccess(Entrant entrant) {
                        if(entrant.checkEventId(eventID)){
                            Toast.makeText(EntrantEventDetail.this,"You have joined this event", Toast.LENGTH_SHORT ).show();
                        }else{
                            entrant.set_add_Event(eventID, eventLocal.getName(), "Joined");
                            if ((entrantGeoPoint== null || entrantGeoPoint.getLatitude() == -1 || entrantGeoPoint.getLongitude() == -1) && geoRequest){
                                Toast.makeText(EntrantEventDetail.this,"Geolocation access failed", Toast.LENGTH_SHORT ).show();
                            }else{
                                entrant.addGeoPoint(eventID,entrantGeoPoint);
                            }
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
    private void requestLocation(){
        mutext2++;
        Toast.makeText(EntrantEventDetail.this,"Geolocation updating", Toast.LENGTH_SHORT ).show();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            entrantGeoPoint = new GeoPoint(-1,-1);
            Toast.makeText(EntrantEventDetail.this,"Geolocation updating failed", Toast.LENGTH_SHORT ).show();
            return;
        }

        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        String provider = LocationManager.GPS_PROVIDER;
        manager.requestSingleUpdate(provider, new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                entrantGeoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
                Toast.makeText(EntrantEventDetail.this,"Geolocation updated success", Toast.LENGTH_SHORT ).show();
                mutext2--;
            }
            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {}

            @Override
            public void onProviderEnabled(String provider) {}

            @Override
            public void onProviderDisabled(String provider) {}
        }, null);
    }

    /**
     * Decodes a Base64-encoded string to a Bitmap.
     *
     * @param base64ImageString the Base64-encoded image string
     * @return the decoded Bitmap image
     */
    private static Bitmap decodeBase64Image(String base64ImageString) {
        byte[] imageBytes = Base64.decode(base64ImageString, Base64.DEFAULT);
        // Decode Base64 string to byte array
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);

    }

    /**
     * Returns to the user's event list.
     * Called after successfully joining an event.
     */
    private void backToMyList(){
        Intent go_back = new Intent(EntrantEventDetail.this, EntrantMylistActivity.class);
        go_back.putExtra("update", eventID);
        go_back.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(go_back);
    }

    /**
     * Saves a Bitmap image to external storage in a specified directory.
     *
     * @param context the activity context
     * @param bitmap the image to save
     */
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
