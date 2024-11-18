package com.example.breeze0events;

import static org.junit.Assert.*;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.core.app.ApplicationProvider;

import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@RunWith(AndroidJUnit4.class)
public class OverallStorageControllerTest {

    private OverallStorageController controller;
    private FirebaseFirestore db;

    @Before
    public void setUp() {
        // Initialize Firebase Firestore instance and OverallStorageController
        db = FirebaseFirestore.getInstance();
        controller = new OverallStorageController();
        controller.db = db;
    }

    @After
    public void tearDown() {
        // Clean up test data
        db.collection("EntrantDB").document("testEntrantId").delete();
        db.collection("OrganizerDB").document("testOrganizerId").delete();
        db.collection("OverallDB").document("testEventId").delete();
    }

    // Test adding an Entrant
    @Test
    public void testAddEntrant_Success() {
        Entrant entrant = new Entrant("testEntrantId", "Test User", "test@example.com", "1234567890", "photoUrl", "device1", new HashMap<>(), new HashMap<>(),new ArrayList<>());
        controller.addEntrant(entrant);

        // Verify the Entrant is added by retrieving it
        db.collection("EntrantDB").document("testEntrantId").get()
                .addOnSuccessListener(documentSnapshot -> {
                    assertTrue("Entrant document should exist", documentSnapshot.exists());
                    assertEquals("Test User", documentSnapshot.getString("name"));
                    assertEquals("test@example.com", documentSnapshot.getString("email"));
                })
                .addOnFailureListener(e -> fail("Failed to retrieve entrant: " + e.getMessage()));
    }

    // Test updating an Entrant
    @Test
    public void testUpdateEntrant_Success() {
        Entrant entrant = new Entrant("testEntrantId", "Updated User", "updated@example.com", "0987654321", "newPhotoUrl", "device1", new HashMap<>(), new HashMap<>(),new ArrayList<>());
        controller.updateEntrant(entrant);

        // Verify the Entrant is updated by retrieving it
        db.collection("EntrantDB").document("testEntrantId").get()
                .addOnSuccessListener(documentSnapshot -> {
                    assertTrue("Entrant document should exist", documentSnapshot.exists());
                    assertEquals("Updated User", documentSnapshot.getString("name"));
                    assertEquals("updated@example.com", documentSnapshot.getString("email"));
                })
                .addOnFailureListener(e -> fail("Failed to retrieve updated entrant: " + e.getMessage()));
    }

    // Test deleting an Entrant
    @Test
    public void testDeleteEntrant_Success() {
        controller.deleteEntrant("testEntrantId");

        // Verify the Entrant is deleted
        db.collection("EntrantDB").document("testEntrantId").get()
                .addOnSuccessListener(documentSnapshot -> {
                    assertFalse("Entrant document should not exist after deletion", documentSnapshot.exists());
                })
                .addOnFailureListener(e -> fail("Failed to retrieve entrant after deletion: " + e.getMessage()));
    }

    // Additional tests for Organizer, Event, Facility, and Admin methods
    @Test
    public void testAddOrganizer_Success() {
        Organizer organizer = new Organizer("testOrganizerId", "device1", new ArrayList<>());
        controller.addOrganizer(organizer);

        db.collection("OrganizerDB").document("testOrganizerId").get()
                .addOnSuccessListener(documentSnapshot -> {
                    assertTrue("Organizer document should exist", documentSnapshot.exists());
                    assertEquals("device1", documentSnapshot.getString("device"));
                })
                .addOnFailureListener(e -> fail("Failed to retrieve organizer: " + e.getMessage()));
    }
}
