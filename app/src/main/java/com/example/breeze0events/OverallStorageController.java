package com.example.breeze0events;

import android.util.Log;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class OverallStorageController {

    private static final String TAG = "OverallStorageController";
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    // Fetch Entrant data from Firestore
    public void getEntrant(String entrantId, final EntrantCallback callback) {
        DocumentReference docRef = db.collection("EntrantDB").document(entrantId);
        // Retrieve entrant data
        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                // Retrieve and build Entrant object
                String name = documentSnapshot.getString("name");
                String email = documentSnapshot.getString("email");
                String phoneNumber = documentSnapshot.getString("phoneNumber");
                String profilePhoto = documentSnapshot.getString("profilePhoto");
                String device = documentSnapshot.getString("device");

                // Retrieve events list
                List<Pair<String, String>> events = new ArrayList<>();
                List<DocumentSnapshot> eventSnapshots = (List<DocumentSnapshot>) documentSnapshot.get("events");

                if (eventSnapshots != null) {
                    for (DocumentSnapshot eventSnapshot : eventSnapshots) {
                        String eventId = eventSnapshot.getString("eventId");
                        String location = eventSnapshot.getString("location");
                        if (eventId != null && location != null) {
                            events.add(new Pair<>(eventId, location));
                        }
                    }
                } else {
                    Log.d(TAG, "No events found for this entrant.");
                }

                // Create Entrant object
                Entrant entrant = new Entrant(entrantId, name, email, phoneNumber, profilePhoto, device, events);

                // Use the callback to pass the Entrant object
                callback.onSuccess(entrant);
            } else {
                Log.d(TAG, "Entrant not found!");
                callback.onFailure("Entrant not found");
            }
        }).addOnFailureListener(e -> {
            Log.w(TAG, "Failed to read entrant data.", e);
            callback.onFailure(e.getMessage());
        });
    }

    // Fetch Organizer data from Firestore
    public void getOrganizer(String organizerId, final OrganizerCallback callback) {
        DocumentReference docRef = db.collection("OrganizerDB").document(organizerId);

        // Retrieve organizer data
        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String device = documentSnapshot.getString("device");

                // Retrieve the list of events managed by the organizer
                List<String> events = (List<String>) documentSnapshot.get("events");

                // Check if events list is not null
                if (events == null) {
                    events = new ArrayList<>();
                }

                // Create Organizer object
                Organizer organizer = new Organizer(organizerId, device, events);
                callback.onSuccess(organizer);
            } else {
                Log.d(TAG, "Organizer not found!");
                callback.onFailure("Organizer not found");
            }
        }).addOnFailureListener(e -> {
            Log.w(TAG, "Failed to read organizer data.", e);
            callback.onFailure(e.getMessage());
        });
    }

    // Fetch Event data from Firestore
    public void getEvent(String eventId, final EventCallback callback) {
        DocumentReference docRef = db.collection("OverallDB").document(eventId);

        // Retrieve event data
        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String name = documentSnapshot.getString("name");
                String qrCode = documentSnapshot.getString("qrCode");
                String posterPhoto = documentSnapshot.getString("posterPhoto");
                String facility = documentSnapshot.getString("facility");
                String startDate = documentSnapshot.getString("startDate");
                String endDate = documentSnapshot.getString("endDate");

                // Retrieve entrants and organizers list
                List<String> entrants = (List<String>) documentSnapshot.get("entrants");
                List<String> organizers = (List<String>) documentSnapshot.get("organizers");

                // Check if entrants and organizers lists are not null
                if (entrants == null) {
                    entrants = new ArrayList<>();
                }
                if (organizers == null) {
                    organizers = new ArrayList<>();
                }

                // Create Event object
                Event event = new Event(eventId, name, qrCode, posterPhoto, facility, startDate, endDate, entrants, organizers);
                callback.onSuccess(event);
            } else {
                Log.d(TAG, "Event not found!");
                callback.onFailure("Event not found");
            }
        }).addOnFailureListener(e -> {
            Log.w(TAG, "Failed to read event data.", e);
            callback.onFailure(e.getMessage());
        });
    }

    // Fetch Facility data from Firestore
    public void getFacility(String facilityId, final FacilityCallback callback) {
        DocumentReference docRef = db.collection("FacilityDB").document(facilityId);

        // Retrieve facility data
        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String location = documentSnapshot.getString("location");
                String device = documentSnapshot.getString("device");

                // Create Facility object
                Facility facility = new Facility(facilityId, location, device);
                callback.onSuccess(facility);
            } else {
                Log.d(TAG, "Facility not found!");
                callback.onFailure("Facility not found");
            }
        }).addOnFailureListener(e -> {
            Log.w(TAG, "Failed to read facility data.", e);
            callback.onFailure(e.getMessage());
        });
    }

    // Fetch Admin data from Firestore
    public void getAdmin(String adminId, final AdminCallback callback) {
        DocumentReference docRef = db.collection("AdminDB").document(adminId);

        // Retrieve admin data
        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String device = documentSnapshot.getString("device");

                // Create Admin object
                Admin admin = new Admin(adminId, device);
                callback.onSuccess(admin);
            } else {
                Log.d(TAG, "Admin not found!");
                callback.onFailure("Admin not found");
            }
        }).addOnFailureListener(e -> {
            Log.w(TAG, "Failed to read admin data.", e);
            callback.onFailure(e.getMessage());
        });
    }
}
