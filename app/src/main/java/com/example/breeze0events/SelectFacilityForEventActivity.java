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
/**
 * SelectFacilityForEventActivity is a DialogFragment that allows the user to select a facility from a list.
 * It fetches facility data from Firebase Firestore and displays the facilities in a single-choice list view.
 */
public class SelectFacilityForEventActivity extends DialogFragment {

    private ListView facilityListView;
    private ArrayAdapter<String> facilityListAdapter;
    private ArrayList<String> facilityList,facilityIdList;
    private FacilitySelectListener listener;
    private FirebaseFirestore db; // Firebase instance
    /**
     * Interface for the activity to receive the selected facility.
     */
    public interface FacilitySelectListener {
        /**
         * Called when a facility is selected.
         *
         * @param selectedFacility The ID of the selected facility.
         */
        void onFacilitySelected(String selectedFacility);
    }
    /**
     * Ensures that the activity implements the FacilitySelectListener interface.
     *
     * @param context The context of the activity hosting this fragment.
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (FacilitySelectListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement FacilitySelectListener");
        }
    }
    /**
     * Creates the dialog for selecting a facility.
     *
     * @param savedInstanceState The previously saved state, if available.
     * @return The Dialog instance for selecting a facility.
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        facilityList = new ArrayList<>(); // Initialize facility list
        facilityIdList = new ArrayList<>();
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.select_facility_activity, null);
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