package com.mrcornman.otp.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.mrcornman.otp.MainActivity;
import com.mrcornman.otp.R;
import com.mrcornman.otp.adapters.MatchMakerListCursorAdapter;
import com.mrcornman.otp.models.MatchItem;
import com.mrcornman.otp.models.UserItem;
import com.mrcornman.otp.utils.DatabaseHelper;

public class PrefileFragment extends Fragment {


    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static PrefileFragment newInstance() {
        PrefileFragment fragment = new PrefileFragment();

        return fragment;
    }

    public PrefileFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_prefile, container, false);

        TextView nameText = (TextView) rootView.findViewById(R.id.name_text);
        TextView ageText = (TextView) rootView.findViewById(R.id.age_text);

        DatabaseHelper db = new DatabaseHelper(getActivity().getApplicationContext());
        //UserItem user = db.getUserById("0");

        UserItem user = new UserItem("0");
        user.setName("MJ");
        user.setAge(18);
        nameText.setText(user.getName());
        ageText.setText("" + user.getAge());

        return rootView;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }
}