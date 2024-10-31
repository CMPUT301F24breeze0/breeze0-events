package com.example.breeze0events;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.util.Arrays;
import java.util.List;

public class EntrantKeywordFilter extends DialogFragment {
    private EditText LocalFilter;
    private EditText KeywordFilter;
    private EntrantKeywordFilter.OnFragmentInteractionListener listener;
    public interface OnFragmentInteractionListener{
        void UpdateKeyword(String keyword, String location);
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof EntrantKeywordFilter.OnFragmentInteractionListener){
            listener = (EntrantKeywordFilter.OnFragmentInteractionListener) context;
        }
        else{
            throw new RuntimeException(context + "need to implement OnFragmentInteractionListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@NonNull Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.entrant_filter, null);
        LocalFilter = view.findViewById(R.id.location_filter);
        KeywordFilter = view.findViewById(R.id.keyword_filter);

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        return builder.setView(view).setTitle("PassingFilter")
                .setCancelable(true)
                .setNegativeButton("Cancel",null)
                .setPositiveButton("Confirm",(dialoginterface,i) ->{
                    String keyword = KeywordFilter.getText().toString();
                    String location = LocalFilter.getText().toString();
                    listener.UpdateKeyword(keyword, location);
                }).create();
    }
}
