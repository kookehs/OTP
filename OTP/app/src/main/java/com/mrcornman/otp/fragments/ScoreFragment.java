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

public class ScoreFragment extends Fragment {

    private OnScoreListInteractionListener onScoreListInteractionListener;
    private MakerMatchAdapter makerMatchAdapter;
    private static String KEY_USER_SCORE = "score";

    public static ScoreFragment newInstance(String userScore) {
        ScoreFragment fragment = new ScoreFragment();

        // arguments
        Bundle arguments = new Bundle();
        arguments.putString(KEY_USER_SCORE, userScore);
        fragment.setArguments(arguments);

        return fragment;
    }

    public ScoreFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_score, container, false);
        ListView listView = (ListView) rootView.findViewById(R.id.score_list);

        // set up list view input
        // set up on click
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String matchId = view.getTag().toString();
                onScoreListInteractionListener.onRequestOpenScore(matchId);
            }
        });

        makerMatchAdapter = new MakerMatchAdapter(getActivity());
        listView.setAdapter(makerMatchAdapter);

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
            onScoreListInteractionListener = (OnScoreListInteractionListener) activity;
        } catch (ClassCastException e) {
            //throw new ClassCastException(activity.toString()
            //       + " must implement OnScoreListInteractionListener");
        }
    }

    public interface OnScoreListInteractionListener {
        void onRequestOpenScore(String matchId);
    }
}