package com.example.breeze0events;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;


//import org.junit.Before;
import android.content.Context;
import android.graphics.Bitmap;

import androidx.test.core.app.ApplicationProvider;

import org.junit.Before;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;


public class EntrantUitTest {
    private EntrantMylistActivity activity;

    private EntrantMyListAdapter adapter;
    private List<Pair<String, String>> eventsList;
    private Context context;
    public void MockEntrantMyListAdapter(){
        Context context = mock(EntrantMylistActivity.class);
        adapter = new EntrantMyListAdapter(context, eventsList);
    }
        @Test
        public void testDecodeBase64Image() {
            // Example base64 string for a tiny image (modify with a valid base64 image string if needed)
            String base64Image = "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mP8/wcAAgAB/om8lYgAAAAASUVORK5CYII=";

            // Call the method under test
            Bitmap bitmap = EntrantMylistActivity.decodeBase64Image(base64Image);

            // Assert that the bitmap is not null
            assertNotNull(bitmap, "Bitmap should not be null after decoding a valid base64 image");

            // Optionally, check dimensions if known (in this example, we skip as it depends on your sample base64 data)
        }
    @BeforeEach
    public void setUp() {
        // Initialize the context and events list
        context = ApplicationProvider.getApplicationContext();
        eventsList = new ArrayList<>();

        // Initialize the activity and set up eventsList through a mock or real EntrantMyListAdapter
        activity = new EntrantMylistActivity();
    }

    @Test
    public void testAddEventToMyList() {
        // Check initial count of items in the adapter
        assertEquals(0, adapter.getCount(), "Initial adapter item count should be 0");

        // Add a mock event to the list
        Pair<String, String> event1 = new Pair<>("event1", "Joined");
        eventsList.add(event1);

        // Notify adapter of data change
        adapter.notifyDataSetChanged();

        // Check that the adapter's count reflects the new item
        assertEquals(1, adapter.getCount(), "Adapter item count should be 1 after adding an event");

        // Verify the event is correctly added
        Pair<String, String> item = adapter.getItem(0);
        assertEquals("event1", item.getLeft(), "The event ID should be 'event1'");
        assertEquals("Joined", item.getRight(), "The event status should be 'Joined'");
    }

    @Test
    public void testMultipleEventsInMyList() {
        // Add multiple events
        eventsList.add(new Pair<>("event1", "Joined"));
        eventsList.add(new Pair<>("event2", "Pending"));
        eventsList.add(new Pair<>("event3", "Completed"));

        // Notify adapter of data change
        adapter.notifyDataSetChanged();

        // Verify that the adapter count matches the number of added items
        assertEquals(3, adapter.getCount(), "Adapter item count should match the number of added events");

        // Check each item's properties
        assertEquals("event1", adapter.getItem(0).getLeft());
        assertEquals("Joined", adapter.getItem(0).getRight());
        assertEquals("event2", adapter.getItem(1).getLeft());
        assertEquals("Pending", adapter.getItem(1).getRight());
        assertEquals("event3", adapter.getItem(2).getLeft());
        assertEquals("Completed", adapter.getItem(2).getRight());
    }


}


