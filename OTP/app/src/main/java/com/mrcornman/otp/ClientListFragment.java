package com.mrcornman.otp;

import android.app.Activity;
import android.app.Fragment;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.mrcornman.otp.adapters.ClientListCursorAdapter;
import com.mrcornman.otp.utils.DatabaseHelper;

public class ClientListFragment extends Fragment {
    private static final String ARG_SECTION_NUMBER = "section_number";

    private String sectionNumber;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static ClientListFragment newInstance(int sectionNumber) {
        ClientListFragment fragment = new ClientListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public ClientListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_client_list, container, false);
        ListView listView = (ListView) rootView.findViewById(R.id.client_list);
        DatabaseHelper db = new DatabaseHelper(getActivity().getApplicationContext());
        Cursor cursor = db.getTopMatches(20);
        ListAdapter adapter = new ClientListCursorAdapter(getActivity(), cursor, false);
        listView.setAdapter(adapter);
        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null){
            sectionNumber = getArguments().getString(ARG_SECTION_NUMBER);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
    }
}