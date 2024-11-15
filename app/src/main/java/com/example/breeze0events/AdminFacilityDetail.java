package com.example.breeze0events;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;


/**
 * AdminFacilityDetail class provides the UI and backend functionality for displaying detailed information
 * about a specific facility within the Admin section of the Breeze0Events application. This class primarily
 * interacts with the OverallStorageController to retrieve facility data and displays it within the UI elements.
 */
public class AdminFacilityDetail extends AppCompatActivity {
   private OverallStorageController overallStorageController;
   private TextView facilityName;
   private TextView facilityID;
   private TextView violatedPolices;
   private TextView evidence;
   private String facilityId;
   private ArrayList<Event> facilityList;
   private ArrayList<String> facilityListDisplay;


   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.admin_facility_detail);

      Button return_button = findViewById(R.id.cancelButton);
      Button delete_button = findViewById(R.id.Deletebutton);
      facilityName = findViewById(R.id.facilityName);
      facilityID = findViewById(R.id.facilityID);


      overallStorageController = new OverallStorageController();
      facilityId = getIntent().getStringExtra("selectedID");
      facilityListDisplay = getIntent().getStringArrayListExtra("facilityListDisplay");


      overallStorageController.getFacility(String.valueOf(facilityId), new FacilityCallback() {
         @Override
         public void onSuccess(Facility facility) {
            facilityName.setText("Facility Name:"+ facility.getLocation());
            facilityID.setText("Facility ID:" + facility.getFacilityId());
         }

         @Override
         public void onFailure(String errorMessage) {
            Log.e("AdminFacilityDetail", "Failed to fetch admins: " + errorMessage);
         }
      });

      return_button.setOnClickListener(v -> {
         finish();
      });
   }
}
