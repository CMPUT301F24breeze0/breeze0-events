package com.example.breeze0events;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

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
      if (facilityId == null || facilityId.isEmpty()) {finish();}
      facilityListDisplay = getIntent().getStringArrayListExtra("facilityListDisplay");


      overallStorageController.getFacility(String.valueOf(facilityId), new FacilityCallback() {
         @Override
         public void onSuccess(Facility facility) {
            facilityName.setText("Facility Name: "+ facility.getLocation());
            facilityID.setText("Facility ID: " + facility.getFacilityId());
         }

         @Override
         public void onFailure(String errorMessage) {
            Log.e("AdminFacilityDetail", "Failed to fetch admins: " + errorMessage);
         }
      });

      return_button.setOnClickListener(v -> {
         finish();
      });

      delete_button.setOnClickListener(v -> {
         new AlertDialog.Builder(this)
                 .setTitle("Delete Facility")
                 .setMessage("Are you sure to delete this facility?")
                 .setPositiveButton("Delete", (dialog, which) -> {
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection("FacilityDB").document(facilityId).delete()
                            .addOnSuccessListener(aVoid -> {
                               Toast.makeText(this, "Facility deleted successfully.", Toast.LENGTH_SHORT).show();
                               //BackToFacilityList(); // in case user click detail again
                               finish();
                            })
                            .addOnFailureListener(e -> {
                               Log.e("DeleteError", "Error deleting facility", e);
                            });
                 })
                 .setNegativeButton("No, MissClick", null)
                 .show();
      });
   }

}
