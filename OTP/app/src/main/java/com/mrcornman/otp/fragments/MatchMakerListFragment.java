package com.mrcornman.otp.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.mrcornman.otp.MainActivity;
import com.mrcornman.otp.R;
import com.mrcornman.otp.adapters.MatchMakerListCursorAdapter;
import com.mrcornman.otp.models.MatchItem;
import com.mrcornman.otp.utils.DatabaseHelper;

public class MatchMakerListFragment extends Fragment {
    private static final String ARG_SECTION_NUMBER = "section_number";

    private String sectionNumber;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static MatchMakerListFragment newInstance(int sectionNumber) {
        MatchMakerListFragment fragment = new MatchMakerListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public MatchMakerListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final DatabaseHelper db = new DatabaseHelper(getActivity().getApplicationContext());

        View rootView = inflater.inflate(R.layout.fragment_matchmaker_list, container, false);
        ListView listView = (ListView) rootView.findViewById(R.id.matchmaker_list);

        // set up list view input
        // set up on click
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String matchId = view.getTag().toString();
                MatchItem matchItem = db.getMatchById(matchId);
                if(matchItem == null) {
                    Log.e("ClientListFragment", "Got a null user from a client match click!");
                    return;
                }

                String numLikesStr = matchItem.getNumLikes() + "";
                Log.i("MatchMakerListFragment", "Checking out - " + numLikesStr);
            }
        });

        Cursor cursor = db.getTopMatches(20);
        ListAdapter adapter = new MatchMakerListCursorAdapter(getActivity(), cursor, false);
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