package com.mrcornman.otp.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mrcornman.otp.R;
import com.mrcornman.otp.adapters.ClientMatchAdapter;

public class CompleteProfileAboutFragment extends Fragment {

    private OnAboutInteractionListener onAboutInteractionListener;

    private EditText aboutEditText;
    private Button submitButton;

    private ClientMatchAdapter clientMatchAdapter;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static CompleteProfileAboutFragment newInstance() {
        CompleteProfileAboutFragment fragment = new CompleteProfileAboutFragment();
        return fragment;
    }

    public CompleteProfileAboutFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // init views
        View rootView = inflater.inflate(R.layout.fragment_complete_profile_about, container, false);

        aboutEditText = (EditText) rootView.findViewById(R.id.about_text_value);

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
            onAboutInteractionListener = (OnAboutInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnAboutInteractionListener");
        }
    }

    private void onSubmit() {
        String aboutStr = aboutEditText.getText().toString();
        if(aboutStr.length() > 0) {
            onAboutInteractionListener.onAboutSubmit(aboutStr);
        } else {
            Toast.makeText(getActivity(), "You need to write something!", Toast.LENGTH_LONG).show();
        }
    }

    public interface OnAboutInteractionListener {
        void onAboutSubmit(String aboutStr);
    }
}