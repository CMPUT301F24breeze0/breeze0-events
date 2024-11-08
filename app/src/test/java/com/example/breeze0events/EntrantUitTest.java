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
}


