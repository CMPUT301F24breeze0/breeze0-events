package com.example.breeze0events;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.datatransport.backend.cct.BuildConfig;

import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.util.ArrayList;
import java.util.List;

/**
 * OrganizerMapActivity provides a map view for organizers using OSMDroid to load OpenStreetMap with multiple markers.
 */
public class OrganizerMapActivity extends AppCompatActivity {

    private MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set OSMDroid cache directory
        Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID);

        setContentView(R.layout.map_activity);

        // Set up the back button to return to the previous screen
        Button backButton = findViewById(R.id.map_activity_back_button);
        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(OrganizerMapActivity.this, OrganizerMyListActivity.class);
            startActivity(intent);
            finish();
        });

        // Initialize MapView and set basic map settings
        mapView = findViewById(R.id.mapView);
        mapView.setMultiTouchControls(true); // Enable multi-touch gestures (e.g., pinch to zoom)

        // Set initial map center and zoom level
        mapView.getController().setZoom(10.0);
        mapView.getController().setCenter(new GeoPoint(53.5461, -113.4938)); // Center on Edmonton

        // Add multiple markers to the map
        List<GeoPoint> locations = new ArrayList<>();
        locations.add(new GeoPoint(53.5461, -113.4938)); // Edmonton
        locations.add(new GeoPoint(53.5444, -113.4900)); // Marker 2
        locations.add(new GeoPoint(53.5500, -113.5000)); // Marker 3

        // Loop through the locations list and add markers
        for (GeoPoint location : locations) {
            addMarker(location, "Marker at " + location.getLatitude() + ", " + location.getLongitude());
        }
    }

    // Helper method to add a marker to the map
    private void addMarker(GeoPoint location, String title) {
        Marker marker = new Marker(mapView);
        marker.setPosition(location);
        marker.setTitle(title);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM); // Set anchor point
        mapView.getOverlays().add(marker); // Add the marker to the map
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume(); // Resume MapView when activity is resumed
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause(); // Pause MapView to save resources
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDetach(); // Properly release MapView resources
    }
}
