package com.example.breeze0events;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;

public class AddFacilityActivity extends DialogFragment {
    private ListView facilityListView;
    private ArrayAdapter<String> facilityListAdapter;
    private ArrayList<String> facilityList;
    private FacilitySelectListener listener;

    private static final String ARG_FACILITY_LIST = "facility_list";

    public interface FacilitySelectListener {
        void onFacilitySelected(String selectedFacility);
    }

    public static AddFacilityActivity newInstance(ArrayList<String> facilityList) {
        AddFacilityActivity fragment = new AddFacilityActivity();
        Bundle args = new Bundle();
        args.putStringArrayList(ARG_FACILITY_LIST, facilityList);
        fragment.setArguments(args);
        return fragment;
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
        View view = inflater.inflate(R.layout.add_facility_activity, container, false);

        if (getArguments() != null) {
            facilityList = getArguments().getStringArrayList(ARG_FACILITY_LIST);
        } else {
            facilityList = new ArrayList<>();
        }

        facilityListView = view.findViewById(R.id.facility_list_view);
        facilityListAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_single_choice, facilityList);
        facilityListView.setAdapter(facilityListAdapter);

        // select facility
        facilityListView.setOnItemClickListener((parent, view1, position, id) -> {
            String selectedFacility = facilityList.get(position);
            listener.onFacilitySelected(selectedFacility);
            dismiss();
        });

        return view;
    }
}