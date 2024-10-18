package com.example.breeze0_events;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import com.google.firebase.auth.FirebaseAuth;


public class OrganizerMyListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.organizer_mylist_activity);
        Button button_mylist= findViewById(R.id.button_mylist);
        Button button_profile= findViewById(R.id.button_profile);
        Button button_events= findViewById(R.id.button_events);
        button_events.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OrganizerMyListActivity.this, OrganizerEventActivity.class);
                startActivity(intent);
            }
        });
        button_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OrganizerMyListActivity.this, OrganizerProfileActivity.class);
                startActivity(intent);
            }
        });
    }
}
