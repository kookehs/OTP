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

public class LeaderBoardFragment extends Fragment implements OnRefreshListener {

    private OnLeaderBoardListInteractionListener onLeaderBoardListInteractionListener;
    private MakerMatchAdapter makerMatchAdapter;
    private static String KEY_USER_SCORE = "score";

    public static LeaderBoardFragment newInstance(String userScore) {
        LeaderBoardFragment fragment = new LeaderBoardFragment();

        // arguments
        Bundle arguments = new Bundle();
        arguments.putString(KEY_USER_SCORE, userScore);
        fragment.setArguments(arguments);

        return fragment;
    }

    public LeaderBoardFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_leader_board, container, false);
        ListView listView = (ListView) rootView.findViewById(R.id.leader_board_list);

        // set up list view input
        // set up on click
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String matchId = view.getTag().toString();
                onLeaderBoardListInteractionListener.onRequestOpenLeaderBoard(matchId);
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
            onLeaderBoardListInteractionListener = (OnLeaderBoardListInteractionListener) activity;
        } catch (ClassCastException e) {
            //throw new ClassCastException(activity.toString()
            //        + " must implement OnLeaderBoardListInteractionListener");
        }
    }

    @Override
    public void onRefresh() {
        refreshList();
    }

    public void refreshList() {
        /*DatabaseHelper.findMakerMatches(ParseUser.getCurrentUser().getObjectId(), 20, new FunctionCallback<List<MatchItem>>() {
            @Override
            public void done(List<MatchItem> matchItems, ParseException e) {
                if(e == null) {
                    makerMatchAdapter.clearMatches();

                    for (MatchItem match : matchItems) {
                        makerMatchAdapter.addMatch(match);
                    }
                }
            }
        });*/
    }

    public interface OnLeaderBoardListInteractionListener {
        void onRequestOpenLeaderBoard(String matchId);
    }
}