package com.mrcornman.otp.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.mrcornman.otp.R;
import com.mrcornman.otp.adapters.ClientMatchAdapter;

public class CompleteProfileArrivalFragment extends Fragment {

    private OnArrivalInteractionListener onArrivalInteractionListener;

    private Button submitButton;

    private ClientMatchAdapter clientMatchAdapter;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static CompleteProfileArrivalFragment newInstance() {
        CompleteProfileArrivalFragment fragment = new CompleteProfileArrivalFragment();
        return fragment;
    }

    public CompleteProfileArrivalFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // init views
        View rootView = inflater.inflate(R.layout.fragment_complete_profile_arrival, container, false);

        submitButton = (Button) rootView.findViewById(R.id.submit_button);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSubmit();
            }
        });

        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            onArrivalInteractionListener = (OnArrivalInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnArrivalInteractionListener");
        }
    }

    private void onSubmit() {
        onArrivalInteractionListener.onArrivalSubmit();
    }

    public interface OnArrivalInteractionListener {
        void onArrivalSubmit();
    }
}