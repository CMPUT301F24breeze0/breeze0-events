package com.example.breeze0events;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class OverallStorageController {

    private static final String TAG = "OverallStorageController";
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    // Fetch Entrant data from the database
    public void getEntrant(String entrantId, final EntrantCallback callback) {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();

        // Retrieve entrant data
        databaseRef.child("EntrantDB").child(entrantId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Retrieve and build Entrant object
                    String name = dataSnapshot.child("name").getValue(String.class);
                    String email = dataSnapshot.child("email").getValue(String.class);
                    String phoneNumber = dataSnapshot.child("phoneNumber").getValue(String.class);
                    String profilePhoto = dataSnapshot.child("profilePhoto").getValue(String.class);
                    String device = dataSnapshot.child("device").getValue(String.class);

                    // Retrieve events list
                    List<Pair<String, String>> events = new ArrayList<>();
                    for (DataSnapshot eventSnapshot : dataSnapshot.child("events").getChildren()) {
                        String eventId = eventSnapshot.child("eventId").getValue(String.class);
                        String location = eventSnapshot.child("location").getValue(String.class);
                        if (eventId != null && location != null) {
                            events.add(new Pair<>(eventId, location));
                        }
                    }

                    // Create Entrant object
                    Entrant entrant = new Entrant(entrantId, name, email, phoneNumber, profilePhoto, device, events);

                    // Use the callback to pass the Entrant object
                    callback.onSuccess(entrant);
                } else {
                    Log.d(TAG, "Entrant not found!");
                    callback.onFailure("Entrant not found");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "Failed to read entrant data.", databaseError.toException());
                callback.onFailure(databaseError.getMessage());
            }
        });
    }

    // Fetch Organizer data from the database
    public void getOrganizer(String organizerId, final OrganizerCallback callback) {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();

        // Retrieve organizer data
        databaseRef.child("OrganizerDB").child(organizerId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String device = dataSnapshot.child("device").getValue(String.class);

                    // Retrieve the list of events managed by the organizer
                    List<String> events = new ArrayList<>();
                    for (DataSnapshot eventSnapshot : dataSnapshot.child("events").getChildren()) {
                        String eventId = eventSnapshot.getValue(String.class);
                        if (eventId != null) {
                            events.add(eventId);
                        }
                    }

                    // Create Organizer object
                    Organizer organizer = new Organizer(organizerId, device, events);
                    callback.onSuccess(organizer);
                } else {
                    Log.d(TAG, "Organizer not found!");
                    callback.onFailure("Organizer not found");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "Failed to read organizer data.", databaseError.toException());
                callback.onFailure(databaseError.getMessage());
            }
        });
    }

    // Fetch Event data from the database
    public void getEvent(String eventId, final EventCallback callback) {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();

        // Retrieve event data
        databaseRef.child("EventDB").child(eventId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String name = dataSnapshot.child("name").getValue(String.class);
                    String qrCode = dataSnapshot.child("qrCode").getValue(String.class);
                    String posterPhoto = dataSnapshot.child("posterPhoto").getValue(String.class);
                    String facility = dataSnapshot.child("facility").getValue(String.class);
                    String startDate = dataSnapshot.child("startDate").getValue(String.class);
                    String endDate = dataSnapshot.child("endDate").getValue(String.class);

                    // Retrieve entrants and organizers list
                    List<String> entrants = new ArrayList<>();
                    List<String> organizers = new ArrayList<>();
                    for (DataSnapshot entrantSnapshot : dataSnapshot.child("entrants").getChildren()) {
                        String entrantId = entrantSnapshot.getValue(String.class);
                        if (entrantId != null) {
                            entrants.add(entrantId);
                        }
                    }
                    for (DataSnapshot organizerSnapshot : dataSnapshot.child("organizers").getChildren()) {
                        String organizerId = organizerSnapshot.getValue(String.class);
                        if (organizerId != null) {
                            organizers.add(organizerId);
                        }
                    }

                    // Create Event object
                    Event event = new Event(eventId, name, qrCode, posterPhoto, facility, startDate, endDate, entrants, organizers);
                    callback.onSuccess(event);
                } else {
                    Log.d(TAG, "Event not found!");
                    callback.onFailure("Event not found");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "Failed to read event data.", databaseError.toException());
                callback.onFailure(databaseError.getMessage());
            }
        });
    }

    // Fetch Facility data from the database
    public void getFacility(String facilityId, final FacilityCallback callback) {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();

        // Retrieve facility data
        databaseRef.child("FacilityDB").child(facilityId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String location = dataSnapshot.child("location").getValue(String.class);
                    String device = dataSnapshot.child("device").getValue(String.class);

                    // Create Facility object
                    Facility facility = new Facility(facilityId, location, device);
                    callback.onSuccess(facility);
                } else {
                    Log.d(TAG, "Facility not found!");
                    callback.onFailure("Facility not found");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "Failed to read facility data.", databaseError.toException());
                callback.onFailure(databaseError.getMessage());
            }
        });
    }

    // Fetch Admin data from the database
    public void getAdmin(String adminId, final AdminCallback callback) {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();

        // Retrieve admin data
        databaseRef.child("AdminDB").child(adminId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String device = dataSnapshot.child("device").getValue(String.class);

                    // Create Admin object
                    Admin admin = new Admin(adminId, device);
                    callback.onSuccess(admin);
                } else {
                    Log.d(TAG, "Admin not found!");
                    callback.onFailure("Admin not found");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "Failed to read admin data.", databaseError.toException());
                callback.onFailure(databaseError.getMessage());
            }
        });
    }
}
