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

        // Prepare event data: Convert List<Pair<String, String>> to List<Map<String, String>>
        ArrayList<Map<String, String>> eventData = new ArrayList<>();
        for (Pair<String, String> event : entrant.getEvents()) {
            Map<String, String> eventMap = new HashMap<>();
            eventMap.put("eventId", event.getLeft());  // event.first is the eventId
            eventMap.put("location", event.getRight());  // event.second is the location
            eventData.add(eventMap);
        }

        // Add the event data to the entrant data
        entrantData.put("events", eventData);

        // Add the entrant data to Firestore
        db.collection("EntrantDB").document(entrant.getEntrantId()).set(entrantData)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Entrant successfully added!"))
                .addOnFailureListener(e -> Log.w(TAG, "Error adding entrant", e));
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
    // Update an Entrant in Firestore
    public void updateEntrant(String entrantId, Entrant entrant) {
        // Creating a map for the Entrant data
        Map<String, Object> entrantData = new HashMap<>();
        entrantData.put("entrantId", entrant.getEntrantId());
        entrantData.put("name", entrant.getName());
        entrantData.put("email", entrant.getEmail());
        entrantData.put("phoneNumber", entrant.getPhoneNumber());
        entrantData.put("profilePhoto", entrant.getProfilePhoto());
        entrantData.put("device", entrant.getDevice());

        // Prepare event data: Convert List<Pair<String, String>> to List<Map<String, String>>
        ArrayList<Map<String, String>> eventData = new ArrayList<>();
        for (Pair<String, String> event : entrant.getEvents()) {
            Map<String, String> eventMap = new HashMap<>();
            eventMap.put("eventId", event.getLeft());
            eventMap.put("location", event.getRight());
            eventData.add(eventMap);
        }
        entrantData.put("events", eventData);

        // Update the entrant data in Firestore
        db.collection("EntrantDB").document(entrantId).update(entrantData)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Entrant successfully updated!"))
                .addOnFailureListener(e -> Log.w(TAG, "Error updating entrant", e));
    }

    // Update an Organizer in Firestore
    public void updateOrganizer(String organizerId, Organizer organizer) {
        // Creating a map for the Organizer data
        Map<String, Object> organizerData = new HashMap<>();
        organizerData.put("organizerId", organizer.getOrganizerId());
        organizerData.put("device", organizer.getDevice());

        // Convert event list
        ArrayList<String> eventData = new ArrayList<>(organizer.getEvents());
        organizerData.put("events", eventData);

        // Update the organizer data in Firestore
        db.collection("OrganizerDB").document(organizerId).update(organizerData)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Organizer successfully updated!"))
                .addOnFailureListener(e -> Log.w(TAG, "Error updating organizer", e));
    }

    // Update an Event in Firestore
    public void updateEvent(String eventId, Event event) {
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

        // Update the event data in Firestore
        db.collection("OverallDB").document(eventId).update(eventData)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Event successfully updated!"))
                .addOnFailureListener(e -> Log.w(TAG, "Error updating event", e));
    }

    // Update a Facility in Firestore
    public void updateFacility(String facilityId, Facility facility) {
        // Creating a map for the Facility data
        Map<String, Object> facilityData = new HashMap<>();
        facilityData.put("facilityId", facility.getFacilityId());
        facilityData.put("location", facility.getLocation());
        facilityData.put("device", facility.getDevice());

        // Update the facility data in Firestore
        db.collection("FacilityDB").document(facilityId).update(facilityData)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Facility successfully updated!"))
                .addOnFailureListener(e -> Log.w(TAG, "Error updating facility", e));
    }

    // Update an Admin in Firestore
    public void updateAdmin(String adminId, Admin admin) {
        // Creating a map for the Admin data
        Map<String, Object> adminData = new HashMap<>();
        adminData.put("adminId", admin.getAdminId());
        adminData.put("device", admin.getDevice());

        // Update the admin data in Firestore
        db.collection("AdminDB").document(adminId).update(adminData)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Admin successfully updated!"))
                .addOnFailureListener(e -> Log.w(TAG, "Error updating admin", e));
    }
}
