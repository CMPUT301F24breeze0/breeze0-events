package com.example.breeze0events;

import android.util.Log;

import com.google.android.gms.common.api.internal.StatusCallback;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OverallStorageController {
    private ListenerRegistration statusListener;
    private ArrayList<String> lastChangedEventId;

    private static final String TAG = "OverallStorageController";
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    // Status listener
    public void startStatusListener(String entrantId, StatusCallback callback) {
        if (statusListener != null) {
            statusListener.remove();
        }

        DocumentReference entrantRef = db.collection("EntrantDB").document(entrantId);

        statusListener = entrantRef.addSnapshotListener((snapshot, error) -> {
            if (error != null) {
                Log.w("StatusListener", "Listen failed.", error);
                return;
            }

            if (snapshot != null && snapshot.exists()) {
                ArrayList<String> newArrayList = (ArrayList<String>) snapshot.get("fixme");
                if (newArrayList != null && newArrayList.size()>lastChangedEventId.size()){
                    callback.onStatusChanged(lastChangedEventId);
                }
                lastChangedEventId = newArrayList;
            }
        });
    }
    public void stopStatusListener() {
        if (statusListener != null) {
            statusListener.remove();
            statusListener = null;
        }
    }

    public ArrayList<String> getLatestStatus() {
        return lastChangedEventId;
    }

    public interface StatusCallback {
        void onStatusChanged(ArrayList<String> status);
    }

    // Get Entrant
    public void getEntrant(String entrantId, final EntrantCallback callback) {
        DocumentReference docRef = db.collection("EntrantDB").document(entrantId);
        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String name = documentSnapshot.getString("name");
                String email = documentSnapshot.getString("email");
                String phoneNumber = documentSnapshot.getString("phoneNumber");
                String profilePhoto = documentSnapshot.getString("profilePhoto");
                String device = documentSnapshot.getString("device");

                // Retrieve events and status lists
                Map<String, String> eventsName = (Map<String, String>) documentSnapshot.get("events");
                Map<String, String> eventsStatus = (Map<String, String>) documentSnapshot.get("status");
                Map<String, GeoPoint> geoPointMap = (Map<String, GeoPoint>)documentSnapshot.get("Geolocation");
                if (eventsName == null) eventsName = new HashMap<>();
                if (eventsStatus == null) eventsStatus = new HashMap<>();
                if (geoPointMap == null) geoPointMap = new HashMap<>();

                // Retrieve notifications as a list of pairs
                List<Map<String, String>> notificationsData = (List<Map<String, String>>) documentSnapshot.get("notifications");
                List<Pair<String, String>> notifications = new ArrayList<>();
                if (notificationsData != null) {
                    for (Map<String, String> map : notificationsData) {
                        if (map.containsKey("left") && map.containsKey("right")) {
                            notifications.add(new Pair<>(map.get("left").toString(), map.get("right").toString()));
                        }
                    }
                }

                // Create Entrant object
                Entrant entrant = new Entrant(entrantId, name, email, phoneNumber, profilePhoto, device, eventsName, eventsStatus, notifications, geoPointMap);
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

    // Add Entrant
    public void addEntrant(Entrant entrant) {
        Map<String, Object> entrantData = new HashMap<>();
        entrantData.put("entrantId", entrant.getEntrantId());
        entrantData.put("name", entrant.getName());
        entrantData.put("email", entrant.getEmail());
        entrantData.put("phoneNumber", entrant.getPhoneNumber());
        entrantData.put("profilePhoto", entrant.getProfilePhoto());
        entrantData.put("device", entrant.getDevice());
        entrantData.put("events", entrant.getEventsName());
        entrantData.put("status", entrant.getEventsStatus());
        entrantData.put("Geolocation", entrant.getGeoPointMap());

        // Convert notifications to a format compatible with Firestore
        List<Map<String, String>> notificationsData = new ArrayList<>();
        for (Pair<String, String> notification : entrant.getNotifications()) {
            Map<String, String> map = new HashMap<>();
            map.put("left", notification.getLeft());
            map.put("right", notification.getRight());
            notificationsData.add(map);
        }
        entrantData.put("notifications", notificationsData);

        db.collection("EntrantDB").document(entrant.getEntrantId()).set(entrantData)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Entrant successfully added!"))
                .addOnFailureListener(e -> Log.w(TAG, "Error adding entrant", e));
    }

    // Update Entrant
    public void updateEntrant(Entrant entrant) {
        Map<String, Object> entrantData = new HashMap<>();
        entrantData.put("entrantId", entrant.getEntrantId());
        entrantData.put("name", entrant.getName());
        entrantData.put("email", entrant.getEmail());
        entrantData.put("phoneNumber", entrant.getPhoneNumber());
        entrantData.put("profilePhoto", entrant.getProfilePhoto());
        entrantData.put("device", entrant.getDevice());
        entrantData.put("events", entrant.getEventsName());
        entrantData.put("status", entrant.getEventsStatus());
        entrantData.put("Geolocation", entrant.getGeoPointMap());

        // Convert notifications to a format compatible with Firestore
        List<Map<String, String>> notificationsData = new ArrayList<>();
        for (Pair<String, String> notification : entrant.getNotifications()) {
            Map<String, String> map = new HashMap<>();
            map.put("left", notification.getLeft());
            map.put("right", notification.getRight());
            notificationsData.add(map);
        }
        entrantData.put("notifications", notificationsData);

        db.collection("EntrantDB").document(entrant.getEntrantId()).update(entrantData)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Entrant successfully updated!"))
                .addOnFailureListener(e -> Log.w(TAG, "Error updating entrant", e));
    }

    // Delete Entrant
    public void deleteEntrant(String entrantId) {
        db.collection("EntrantDB").document(entrantId).delete()
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Entrant successfully deleted!"))
                .addOnFailureListener(e -> Log.w(TAG, "Error deleting entrant", e));
    }

    // **Organizer Functions**

    // Get Organizer
    public void getOrganizer(String organizerId, final OrganizerCallback callback) {
        DocumentReference docRef = db.collection("OrganizerDB").document(organizerId);
        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String device = documentSnapshot.getString("device");
                List<String> events = (List<String>) documentSnapshot.get("events");
                if (events == null) events = new ArrayList<>();

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

    // add event id to corresponding organizer in organizer db
    public void addEventWithOrganizerCheck(Event event, String organizerId) {
        CollectionReference organizersRef = db.collection("OrganizerDB");
        organizersRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                boolean organizerExists = false;
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String device = document.getString("device");
                    List<String> events = (List<String>) document.get("events");

                    if (device != null && device.equals(organizerId)) {
                        organizerExists = true;
                        if (events == null) {
                            events = new ArrayList<>();
                        }
                        events.add(event.getEventId());

                        document.getReference().update("events", events)
                                .addOnSuccessListener(aVoid -> Log.d("Firestore", "Organizer updated with new event!"))
                                .addOnFailureListener(e -> Log.w("Firestore", "Error updating organizer", e));
                        break;
                    }
                }

                if (!organizerExists) {
                    Organizer newOrganizer = new Organizer(organizerId, organizerId, new ArrayList<>(Arrays.asList(event.getEventId())));
                    addOrganizer(newOrganizer);
                    Log.d("Firestore", "New Organizer created!");
                }
            } else {
                Log.w("Firestore", "Error getting organizers: ", task.getException());
            }
        });
    }

    // delete event and event id from corresponding organizer in organizer db
    public void deleteEventWithOrganizerCheck(String eventId, String organizerId) {
        db.collection("OverallDB").document(eventId).delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firestore", "Event successfully deleted from OverallDB!");

                    CollectionReference organizersRef = db.collection("OrganizerDB");
                    organizersRef.get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            boolean organizerExists = false;

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String device = document.getString("device");
                                List<String> events = (List<String>) document.get("events");

                                if (device != null && device.equals(organizerId)) {
                                    organizerExists = true;

                                    if (events != null && events.contains(eventId)) {
                                        events.remove(eventId);  // remove enent id

                                        document.getReference().update("events", events)
                                                .addOnSuccessListener(aVoid1 -> Log.d("Firestore", "Event removed from Organizer's events list!"))
                                                .addOnFailureListener(e -> Log.w("Firestore", "Error updating organizer's events list", e));
                                    }
                                    break;
                                }
                            }

                            if (!organizerExists) {
                                Log.w("Firestore", "Organizer not found for device ID: " + organizerId);
                            }
                        } else {
                            Log.w("Firestore", "Error getting organizers: ", task.getException());
                        }
                    });
                })
                .addOnFailureListener(e -> Log.w("Firestore", "Error deleting event from OverallDB", e));
    }

    // Add Organizer
    public void addOrganizer(Organizer organizer) {
        Map<String, Object> organizerData = new HashMap<>();
        organizerData.put("organizerId", organizer.getOrganizerId());
        organizerData.put("device", organizer.getDevice());
        organizerData.put("events", new ArrayList<>(organizer.getEvents()));

        db.collection("OrganizerDB").document(organizer.getOrganizerId()).set(organizerData)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Organizer successfully added!"))
                .addOnFailureListener(e -> Log.w(TAG, "Error adding organizer", e));
    }

    // Update Organizer
    public void updateOrganizer(Organizer organizer) {
        Map<String, Object> organizerData = new HashMap<>();
        organizerData.put("organizerId", organizer.getOrganizerId());
        organizerData.put("device", organizer.getDevice());
        organizerData.put("events", new ArrayList<>(organizer.getEvents()));

        db.collection("OrganizerDB").document(organizer.getOrganizerId()).update(organizerData)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Organizer successfully updated!"))
                .addOnFailureListener(e -> Log.w(TAG, "Error updating organizer", e));
    }

    // Delete Organizer
    public void deleteOrganizer(String organizerId) {
        db.collection("OrganizerDB").document(organizerId).delete()
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Organizer successfully deleted!"))
                .addOnFailureListener(e -> Log.w(TAG, "Error deleting organizer", e));
    }

    // **Event Functions**

    // Get Event
    public void getEvent(String eventId, final EventCallback callback) {
        DocumentReference docRef = db.collection("OverallDB").document(eventId);
        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String name = documentSnapshot.getString("name");
                String qrCode = documentSnapshot.getString("qrCode");
                String posterPhoto = documentSnapshot.getString("posterPhoto");
                String facility = documentSnapshot.getString("facility");
                String startDate = documentSnapshot.getString("startDate");
                String endDate = documentSnapshot.getString("endDate");
                String limitedNumber =documentSnapshot.getString("limitedNumber");
                List<String> entrants = (List<String>) documentSnapshot.get("entrants");
                List<String> organizers = (List<String>) documentSnapshot.get("organizers");

                if (entrants == null) entrants = new ArrayList<>();
                if (organizers == null) organizers = new ArrayList<>();

                Event event = new Event(eventId, name, qrCode, posterPhoto, facility, startDate, endDate, limitedNumber, entrants, organizers);
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

    // Add Event
    public void addEvent(Event event) {
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("eventId", event.getEventId());
        eventData.put("name", event.getName());
        eventData.put("qrCode", event.getQrCode());
        eventData.put("posterPhoto", event.getPosterPhoto());
        eventData.put("facility", event.getFacility());
        eventData.put("startDate", event.getStartDate());
        eventData.put("endDate", event.getEndDate());
        eventData.put("limitedNumber", event.getLimitedNumber());
        eventData.put("entrants", new ArrayList<>(event.getEntrants()));
        eventData.put("organizers", new ArrayList<>(event.getOrganizers()));

        Log.d(TAG, "Attempting to add event: " + eventData);

        db.collection("OverallDB").document(event.getEventId()).set(eventData)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Event successfully added!"))
                .addOnFailureListener(e -> Log.w(TAG, "Error adding event" + e.getMessage(), e));
    }

    // Update Event
    public void updateEvent(Event event) {
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("eventId", event.getEventId());
        eventData.put("name", event.getName());
        eventData.put("qrCode", event.getQrCode());
        eventData.put("posterPhoto", event.getPosterPhoto());
        eventData.put("facility", event.getFacility());
        eventData.put("startDate", event.getStartDate());
        eventData.put("endDate", event.getEndDate());
        eventData.put("limitedNumber", event.getLimitedNumber());
        eventData.put("entrants", new ArrayList<>(event.getEntrants()));
        eventData.put("organizers", new ArrayList<>(event.getOrganizers()));

        db.collection("OverallDB").document(event.getEventId()).update(eventData)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Event successfully updated!"))
                .addOnFailureListener(e -> Log.w(TAG, "Error updating event", e));
    }

    // Delete Event
    public void deleteEvent(String eventId) {
        db.collection("OverallDB").document(eventId).delete()
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Event successfully deleted!"))
                .addOnFailureListener(e -> Log.w(TAG, "Error deleting event", e));
    }

    // **Facility Functions**

    // Get Facility
    public void getFacility(String facilityId, final FacilityCallback callback) {
        DocumentReference docRef = db.collection("FacilityDB").document(facilityId);
        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String location = documentSnapshot.getString("location");
                String device = documentSnapshot.getString("device");

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

    // Add Facility
    public void addFacility(Facility facility) {
        Map<String, Object> facilityData = new HashMap<>();
        facilityData.put("facilityId", facility.getFacilityId());
        facilityData.put("location", facility.getLocation());
        facilityData.put("device", facility.getDevice());

        db.collection("FacilityDB").document(facility.getFacilityId()).set(facilityData)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Facility successfully added!"))
                .addOnFailureListener(e -> Log.w(TAG, "Error adding facility", e));
    }

    // Update Facility
    public void updateFacility(Facility facility) {
        Map<String, Object> facilityData = new HashMap<>();
        facilityData.put("facilityId", facility.getFacilityId());
        facilityData.put("location", facility.getLocation());
        facilityData.put("device", facility.getDevice());

        db.collection("FacilityDB").document(facility.getFacilityId()).update(facilityData)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Facility successfully updated!"))
                .addOnFailureListener(e -> Log.w(TAG, "Error updating facility", e));
    }

    // Delete Facility
    public void deleteFacility(String facilityId) {
        db.collection("FacilityDB").document(facilityId).delete()
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Facility successfully deleted!"))
                .addOnFailureListener(e -> Log.w(TAG, "Error deleting facility", e));
    }

    // **Admin Functions**

    // Get Admin
    public void getAdmin(String adminId, final AdminCallback callback) {
        DocumentReference docRef = db.collection("AdminDB").document(adminId);
        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String device = documentSnapshot.getString("device");

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

    // Add Admin
    public void addAdmin(Admin admin) {
        Map<String, Object> adminData = new HashMap<>();
        adminData.put("adminId", admin.getAdminId());
        adminData.put("device", admin.getDevice());

        db.collection("AdminDB").document(admin.getAdminId()).set(adminData)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Admin successfully added!"))
                .addOnFailureListener(e -> Log.w(TAG, "Error adding admin", e));
    }

    // Update Admin
    public void updateAdmin(Admin admin) {
        Map<String, Object> adminData = new HashMap<>();
        adminData.put("adminId", admin.getAdminId());
        adminData.put("device", admin.getDevice());

        db.collection("AdminDB").document(admin.getAdminId()).update(adminData)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Admin successfully updated!"))
                .addOnFailureListener(e -> Log.w(TAG, "Error updating admin", e));
    }

    // Delete Admin
    public void deleteAdmin(String adminId) {
        db.collection("AdminDB").document(adminId).delete()
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Admin successfully deleted!"))
                .addOnFailureListener(e -> Log.w(TAG, "Error deleting admin", e));
    }

    public void findEventByQRCodeHash(String qrCodeHash, final EventCallback callback) {
        db.collection("OverallDB").get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String eventId = document.getString("eventId");
                    if (eventId != null && QRHashGenerator.generateHash(eventId).equals(qrCodeHash)) {
                        String name = document.getString("name");
                        String qrCode = document.getString("qrCode");
                        String posterPhoto = document.getString("posterPhoto");
                        String facility = document.getString("facility");
                        String startDate = document.getString("startDate");
                        String endDate = document.getString("endDate");
                        String limitedNumber = document.getString("limitedNumber");
                        List<String> entrants = (List<String>) document.get("entrants");
                        List<String> organizers = (List<String>) document.get("organizers");

                        Event event = new Event(eventId, name, qrCode, posterPhoto, facility, startDate, endDate, limitedNumber, entrants, organizers);
                        callback.onSuccess(event);
                        return;
                    }
                }
                callback.onFailure("No event found with the matching QR code.");
            } else {
                callback.onFailure("Error retrieving events from database.");
            }
        }).addOnFailureListener(e -> {
            Log.w(TAG, "Error finding event by QR code hash", e);
            callback.onFailure("Failed to access the database.");
        });
    }

}
