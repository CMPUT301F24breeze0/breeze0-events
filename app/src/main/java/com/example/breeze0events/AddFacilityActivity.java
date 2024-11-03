package com.example.breeze0events;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class AddFacilityActivity extends DialogFragment {

    private ListView facilityListView;
    private ArrayAdapter<String> facilityListAdapter;
    private ArrayList<String> facilityList,facilityIdList;
    private FacilitySelectListener listener;
    private FirebaseFirestore db; // Firebase instance

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

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        facilityList = new ArrayList<>(); // Initialize facility list
        facilityIdList = new ArrayList<>();
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.add_facility_activity, null);
        builder.setView(view);

        // Initialize ListView and adapter
        facilityListView = view.findViewById(R.id.facility_list_view);
        facilityListAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_single_choice, facilityList);
        facilityListView.setAdapter(facilityListAdapter);

        // Setup Firebase Firestore instance and retrieve data
        db = FirebaseFirestore.getInstance();
        CollectionReference collectionRef = db.collection("FacilityDB");

        collectionRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        // Assuming Facility object has a method to get location or other details
                        String facilityInfo =  document.getString("location"); // Adjust field name as needed
                        String facilityIdInfo = document.getId();
                        facilityList.add(facilityInfo);
                        facilityIdList.add(facilityIdInfo);
                    }
                    facilityListAdapter.notifyDataSetChanged();
                } else {
                    Log.e("FirestoreError", "Error getting documents: ", task.getException());
                }
            }
        });

        facilityListView.setOnItemClickListener((parent, view1, position, id) -> {
            String selectedFacilityName = facilityIdList.get(position);
            listener.onFacilitySelected(selectedFacilityName);
            dismiss();
        });

        builder.setTitle("Select Facility");
        builder.setNegativeButton("Cancel", null);
        return builder.create();
    }
}