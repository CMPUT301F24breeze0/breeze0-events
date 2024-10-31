package com.example.breeze0events;

import android.util.Log;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

                // Retrieve events and status lists
                List<String> events = (List<String>) documentSnapshot.get("events");
                List<String> status = (List<String>) documentSnapshot.get("status");

                if (events == null) events = new ArrayList<>();
                if (status == null) status = new ArrayList<>();

                // Create Entrant object
                Entrant entrant = new Entrant(entrantId, name, email, phoneNumber, profilePhoto, device, events, status);

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

    // Add an Entrant to Firestore
    public void addEntrant(Entrant entrant) {
        // Creating a map for the Entrant data
        Map<String, Object> entrantData = new HashMap<>();
        entrantData.put("entrantId", entrant.getEntrantId());
        entrantData.put("name", entrant.getName());
        entrantData.put("email", entrant.getEmail());
        entrantData.put("phoneNumber", entrant.getPhoneNumber());
        entrantData.put("profilePhoto", entrant.getProfilePhoto());
        entrantData.put("device", entrant.getDevice());
        entrantData.put("events", new ArrayList<>(entrant.getEvents()));
        entrantData.put("status", new ArrayList<>(entrant.getStatus()));

        // Add the entrant data to Firestore
        db.collection("EntrantDB").document(entrant.getEntrantId()).set(entrantData)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Entrant successfully added!"))
                .addOnFailureListener(e -> Log.w(TAG, "Error adding entrant", e));
    }

    // Update an Entrant in Firestore
    public void updateEntrant(Entrant entrant) {
        // Creating a map for the Entrant data
        Map<String, Object> entrantData = new HashMap<>();
        entrantData.put("entrantId", entrant.getEntrantId());
        entrantData.put("name", entrant.getName());
        entrantData.put("email", entrant.getEmail());
        entrantData.put("phoneNumber", entrant.getPhoneNumber());
        entrantData.put("profilePhoto", entrant.getProfilePhoto());
        entrantData.put("device", entrant.getDevice());
        entrantData.put("events", new ArrayList<>(entrant.getEvents()));
        entrantData.put("status", new ArrayList<>(entrant.getStatus()));

        // Update the entrant data in Firestore
        db.collection("EntrantDB").document(entrant.getEntrantId()).update(entrantData)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Entrant successfully updated!"))
                .addOnFailureListener(e -> Log.w(TAG, "Error updating entrant", e));
    }

    // Other parts of the OverallStorageController remain unchanged
    // Fetch Organizer, Event, Facility, and Admin data
    // Add Organizer, Event, Facility, and Admin to Firestore
    // Update Organizer, Event, Facility, and Admin in Firestore

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

    // Add an Organizer to Firestore
    public void addOrganizer(Organizer organizer) {
        // Creating a map for the Organizer data
        Map<String, Object> organizerData = new HashMap<>();
        organizerData.put("organizerId", organizer.getOrganizerId());
        organizerData.put("device", organizer.getDevice());

        // Prepare event data: Convert List<String> to a List<Map<String, String>>
        ArrayList<String> eventData = new ArrayList<>(organizer.getEvents());

        // Add the event data to the organizer data
        organizerData.put("events", eventData);

        // Add the organizer data to Firestore
        db.collection("OrganizerDB").document(organizer.getOrganizerId()).set(organizerData)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Organizer successfully added!"))
                .addOnFailureListener(e -> Log.w(TAG, "Error adding organizer", e));
    }

    // Add an Event to Firestore
    public void addEvent(Event event) {
        // Creating a map for the Event data
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("eventId", event.getEventId());
        eventData.put("name", event.getName());
        eventData.put("qrCode", event.getQrCode());
        eventData.put("posterPhoto", event.getPosterPhoto());
        eventData.put("facility", event.getFacility());
        eventData.put("startDate", event.getStartDate());
        eventData.put("endDate", event.getEndDate());

        // Add entrant and organizer data
        eventData.put("entrants", new ArrayList<>(event.getEntrants()));
        eventData.put("organizers", new ArrayList<>(event.getOrganizers()));

        // Add the event data to Firestore
        db.collection("OverallDB").document(event.getEventId()).set(eventData)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Event successfully added!"))
                .addOnFailureListener(e -> Log.w(TAG, "Error adding event", e));

        Log.d("OverallStorageController","Attempting to add event: " + event.toString());
    }

    // Add a Facility to Firestore
    public void addFacility(Facility facility) {
        // Creating a map for the Facility data
        Map<String, Object> facilityData = new HashMap<>();
        facilityData.put("facilityId", facility.getFacilityId());
        facilityData.put("location", facility.getLocation());
        facilityData.put("device", facility.getDevice());

        // Add the facility data to Firestore
        db.collection("FacilityDB").document(facility.getFacilityId()).set(facilityData)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Facility successfully added!"))
                .addOnFailureListener(e -> Log.w(TAG, "Error adding facility", e));
    }

    // Add an Admin to Firestore
    public void addAdmin(Admin admin) {
        // Creating a map for the Admin data
        Map<String, Object> adminData = new HashMap<>();
        adminData.put("adminId", admin.getAdminId());
        adminData.put("device", admin.getDevice());

        // Add the admin data to Firestore
        db.collection("AdminDB").document(admin.getAdminId()).set(adminData)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Admin successfully added!"))
                .addOnFailureListener(e -> Log.w(TAG, "Error adding admin", e));
    }
}
