package com.mrcornman.otp.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.mrcornman.otp.R;
import com.mrcornman.otp.adapters.MakerMatchAdapter;
import com.mrcornman.otp.listeners.OnRefreshListener;
import com.mrcornman.otp.models.models.MatchItem;
import com.mrcornman.otp.utils.DatabaseHelper;
import com.parse.FunctionCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.List;

public class MakerListFragment extends Fragment implements OnRefreshListener {

    private OnMakerListInteractionListener onMakerListInteractionListener;
    private MakerMatchAdapter makerMatchAdapter;

    public static MakerListFragment newInstance() {
        MakerListFragment fragment = new MakerListFragment();
        return fragment;
    }

    public MakerListFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_maker_list, container, false);
        ListView listView = (ListView) rootView.findViewById(R.id.matchmaker_list);

        // set up list view input
        // set up on click
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String matchId = view.getTag().toString();
                onMakerListInteractionListener.onRequestOpenMakerMatch(matchId);
            }
        });

        makerMatchAdapter = new MakerMatchAdapter(getActivity());
        listView.setAdapter(makerMatchAdapter);

        refreshList();

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
            onMakerListInteractionListener = (OnMakerListInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnMakerListInteractionListener");
        }
    }

    @Override
    public void onRefresh() {
        refreshList();
    }

    public void refreshList() {
        DatabaseHelper.findMakerMatches(ParseUser.getCurrentUser().getObjectId(), 20, new FunctionCallback<List<MatchItem>>() {
            @Override
            public void done(List<MatchItem> matchItems, ParseException e) {
                if(e == null) {
                    makerMatchAdapter.clearMatches();

                    for (MatchItem match : matchItems) {
                        makerMatchAdapter.addMatch(match);

                    }
                }
            }
        });
    }

    public interface OnMakerListInteractionListener {
        void onRequestOpenMakerMatch(String matchId);
    }
}