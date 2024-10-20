package com.example.breeze0events;
import java.util.ArrayList;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Event {
    String event_id;
    String name;
    String QRcode;
    String start_date;
    String end_date;
    String poster_photo;
    ArrayList<String> entrants = new ArrayList<>();
    ArrayList<String> organizers= new ArrayList<>();
    Event(String event_id,String name,String QRcode,String start_date,String end_date,String poster_photo,
          ArrayList<String> entrants,ArrayList<String> organizers){
        this.event_id=event_id;
        this.name=name;
        this.QRcode=QRcode;
        this.start_date=start_date;
        this.end_date=end_date;
        this.poster_photo=poster_photo;
        this.entrants=entrants;
        this.organizers=organizers;
    }
}
