package com.example.breeze0events;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.example.breeze0events.AdminOrganizationProfileActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AdminBroseRemoveTest {

    @Mock
    private FirebaseFirestore mockFirestore;

    @Mock
    private CollectionReference mockCollectionReference;

    @Mock
    private Task<QuerySnapshot> mockTask;

    @Mock
    private QuerySnapshot mockQuerySnapshot;

    @Mock
    private QueryDocumentSnapshot mockDocument;

    private AdminOrganizationProfileActivity activity;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);

        // Initialize activity and inject mocks
        ActivityScenario<AdminOrganizationProfileActivity> scenario = ActivityScenario.launch(AdminOrganizationProfileActivity.class);
        scenario.onActivity(act -> {
            activity = act;

            activity.db = mockFirestore;
            activity.organizerList = new ArrayList<>();
            activity.organizerIdMap = new HashMap<>();
        });

        // Set up mock Firestore collection reference
        when(mockFirestore.collection("OrganizerDB")).thenReturn(mockCollectionReference);
    }

    @Test
    public void testFirestoreDataRetrievalInOnCreate() {
        // Mock get() to return the mock task
        when(mockCollectionReference.get()).thenReturn(mockTask);

        // Capture the OnCompleteListener added to the mockTask
        ArgumentCaptor<OnCompleteListener<QuerySnapshot>> captor = ArgumentCaptor.forClass(OnCompleteListener.class);

        // Mock the behavior of addOnCompleteListener to capture the listener
        when(mockTask.addOnCompleteListener(captor.capture())).thenReturn(mockTask);

        // Trigger Firestore logic
        activity.db = mockFirestore;
        activity.organizerList = new ArrayList<>();
        activity.organizerIdMap = new HashMap<>();

        // Verify that get() is called
        verify(mockCollectionReference).get();

        // Simulate successful Firestore response
        when(mockTask.isSuccessful()).thenReturn(true);
        QuerySnapshot mockQuerySnapshot = mock(QuerySnapshot.class);
        when(mockTask.getResult()).thenReturn(mockQuerySnapshot);

        // Mock Firestore document
        QueryDocumentSnapshot mockDocument = mock(QueryDocumentSnapshot.class);
        when(mockQuerySnapshot.iterator()).thenReturn(List.of(mockDocument).iterator());
        when(mockDocument.getId()).thenReturn("1");

        // Trigger the captured OnCompleteListener
        captor.getValue().onComplete(mockTask);

        // Verify that the organizerList and organizerIdMap are updated
        assertEquals(1, activity.organizerList.size());
        assertEquals("Organizer: 1", activity.organizerList.get(0));
        assertEquals("1", activity.organizerIdMap.get("Organizer: 1"));
    }


}
