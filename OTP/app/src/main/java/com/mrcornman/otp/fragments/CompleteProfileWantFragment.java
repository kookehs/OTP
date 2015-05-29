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

public class CompleteProfileWantFragment extends Fragment {

    private OnWantInteractionListener onWantInteractionListener;

    private EditText wantEditText;
    private Button submitButton;

    private ClientMatchAdapter clientMatchAdapter;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static CompleteProfileWantFragment newInstance() {
        CompleteProfileWantFragment fragment = new CompleteProfileWantFragment();
        return fragment;
    }

    public CompleteProfileWantFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // init views
        View rootView = inflater.inflate(R.layout.fragment_complete_profile_want, container, false);

        wantEditText = (EditText) rootView.findViewById(R.id.want_text_value);

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
            onWantInteractionListener = (OnWantInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnWantInteractionListener");
        }
    }

    private void onSubmit() {
        String wantStr = wantEditText.getText().toString();
        if(wantStr.length() > 0) {
            onWantInteractionListener.onWantSubmit(wantStr);
        } else {
            Toast.makeText(getActivity(), "You need to write something!", Toast.LENGTH_LONG).show();
        }
    }

    public interface OnWantInteractionListener {
        void onWantSubmit(String wantStr);
    }
}