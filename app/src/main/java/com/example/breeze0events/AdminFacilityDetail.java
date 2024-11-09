package com.example.breeze0events;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
/**
 * AdminFacilityDetail class provides the UI and backend functionality for displaying detailed information
 * about a specific facility within the Admin section of the Breeze0Events application. This class primarily
 * interacts with the OverallStorageController to retrieve facility data and displays it within the UI elements.
 */

 public class AdminFacilityDetail {
    private OverallStorageController overallStorageController;
    private TextView facilityName;
    private TextView facilityID;
    private TextView violatedPolices;
    private TextView evidence;
    private String FacilityID;
}
