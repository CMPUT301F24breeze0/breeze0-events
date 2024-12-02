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


/**
 * DialogFragment that allows users to filter events based on a keyword and location.
 * This class provides an interface for entering keyword and location filters
 */

public class EntrantKeywordFilter extends DialogFragment {
    private EditText LocalFilter;
    private EditText KeywordFilter;
    private EntrantKeywordFilter.OnFragmentInteractionListener listener;

    /**
     * Interface to be implemented by the host activity to handle keyword and location filter updates.
     */
    public interface OnFragmentInteractionListener{
        /**
         * Callback method to pass the keyword and location filters back to the host activity.
         * @param keyword  The keyword entered by the user.
         * @param location The location entered by the user.
         */
        void UpdateKeyword(String keyword, String location);
    }


    /**
     * Attaches the fragment to the host activity, ensuring that the host implements the
     * OnFragmentInteractionListener interface.
     *
     * @param context the context of the host activity
     * @throws RuntimeException if the host activity does not implement OnFragmentInteractionListener
     */

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


    /**
     * Creates the dialog with input fields for keyword and location filters.
     * Sets up the dialog with "Cancel" and "Confirm" buttons to handle user input.
     *
     * @param savedInstanceState the saved state
     * @return a configured Dialog with keyword and location inputs
     */

    @NonNull
    @Override
    public Dialog onCreateDialog(@NonNull Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.entrant_filter, null);
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
