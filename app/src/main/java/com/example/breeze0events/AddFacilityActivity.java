package com.example.breeze0events;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;

public class AddFacilityActivity extends DialogFragment{
    private ListView facilityListView;
    private ArrayAdapter<String> facilityListAdapter;
    private ArrayList<String> facilityList;
    private FirebaseFirestore db;
    private FacilitySelectListener listener;

    public interface FacilitySelectListener {
        void onFacilitySelected(String selectedFacility);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (FacilitySelectListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement FacilitySelectListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_add_facility, container, false);

        facilityListView = view.findViewById(R.id.facility_list_view);
        facilityList = new ArrayList<>();
        facilityListAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_single_choice, facilityList);
        facilityListView.setAdapter(facilityListAdapter);

        db = FirebaseFirestore.getInstance();
        CollectionReference collectionRef = db.collection("FacilityDB");

        // Load facilities from Firestore
        collectionRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String facilityName = document.getString("location");
                    facilityList.add(facilityName);
                    facilityListAdapter.notifyDataSetChanged();
                }
            } else {
                Toast.makeText(getContext(), "Error loading facilities", Toast.LENGTH_SHORT).show();
            }
        });

        // Set item click listener to handle facility selection
        facilityListView.setOnItemClickListener((parent, view1, position, id) -> {
            String selectedFacility = facilityList.get(position);
            listener.onFacilitySelected(selectedFacility); // Pass selected facility back to OrganizerEventActivity
            dismiss();
        });

        return view;
    }
}
