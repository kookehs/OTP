package com.mrcornman.otp.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.mrcornman.otp.R;
import com.mrcornman.otp.adapters.UserCardAdapter;
import com.mrcornman.otp.utils.DatabaseHelper;
import com.mrcornman.otp.views.CardStackLayout;
import com.mrcornman.otp.views.CardView;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;


public class GameFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match

    private CardStackLayout mCardStackLayoutFirst;
    private CardStackLayout mCardStackLayoutSecond;
    private ProgressBar firstProgress;
    private ProgressBar secondProgress;
    private UserCardAdapter mUserCardAdapterFirst;
    private UserCardAdapter mUserCardAdapterSecond;

    private GameInteractionListener gameInteractionListener;

    private SharedPreferences sharedPreferences;

    private String potentialFirstId = "";
    private String potentialSecondId = "";

    public static GameFragment newInstance() {
        GameFragment fragment = new GameFragment();
        return fragment;
    }

    public GameFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_game, container, false);

        // init views
        mCardStackLayoutFirst = (CardStackLayout) view.findViewById(R.id.cardstack_first);
        mCardStackLayoutSecond = (CardStackLayout) view.findViewById(R.id.cardstack_second);

        firstProgress = (ProgressBar) view.findViewById(R.id.first_progress);
        secondProgress = (ProgressBar) view.findViewById(R.id.second_progress);

        mCardStackLayoutFirst.setCardStackListener(new CardStackLayout.CardStackListener() {
            @Override
            public void onBeginProgress() {
                buildPotentialMatch(getCurrentFirstId(), getCurrentSecondId());
            }

            @Override
            public void onUpdateProgress(float percent) {
            }

            @Override
            public void onCancelled() {
                clearPotentialMatch();
            }

            @Override
            public void onChoiceAccepted() {
                /*
                SingleUserView item = (SingleUserView) beingDragged;
                item.onChoiceMade(choice, beingDragged);
                //todo: handle what to do after the choice is made.
                if (choice) {
                    db.updateNumLikes(item.userItem, db.VALUE_LIKED);
                } else {
                    db.updateNumLikes(item.userItem, db.VALUE_DISLIKED);
                }
                Log.d("game fragment", "updated the choice made " + String.valueOf(choice) + " " + item.userItem.getName());
                */
                onCreateMatch();

                if(!mCardStackLayoutFirst.hasMoreItems()) {
                    firstProgress.setVisibility(View.VISIBLE);
                    refreshFirst();
                }
            }
        });

        mCardStackLayoutSecond.setCardStackListener(new CardStackLayout.CardStackListener() {
            @Override
            public void onBeginProgress() {
                buildPotentialMatch(getCurrentFirstId(), getCurrentSecondId());
            }

            @Override
            public void onUpdateProgress(float percent) {
            }

            @Override
            public void onCancelled() {
                clearPotentialMatch();
            }

            @Override
            public void onChoiceAccepted() {
                onCreateMatch();

                if(!mCardStackLayoutSecond.hasMoreItems()) {
                    secondProgress.setVisibility(View.VISIBLE);
                    refreshSecond();
                }
            }
        });

        // init data
        sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);

        mUserCardAdapterFirst = new UserCardAdapter(getActivity().getApplicationContext());
        mCardStackLayoutFirst.setAdapter(mUserCardAdapterFirst);

        mUserCardAdapterSecond = new UserCardAdapter(getActivity().getApplicationContext());
        mCardStackLayoutSecond.setAdapter(mUserCardAdapterSecond);

        refreshFirst();
        refreshSecond();
        firstProgress.setVisibility(View.VISIBLE);
        secondProgress.setVisibility(View.VISIBLE);

        return view;
    }

    private void buildPotentialMatch(String firstId, String secondId) {
        potentialFirstId = firstId != null ? firstId : "";
        potentialSecondId = secondId != null ? secondId : "";
        Log.i("DatabaseHelper", "ShamalamaDingDong " + potentialFirstId + " : " + potentialSecondId);
    }

    private void clearPotentialMatch() {
        potentialFirstId = "";
        potentialSecondId = "";
    }

    public void onCreateMatch() {
        if(potentialFirstId.equals("") || potentialSecondId.equals("")) return;

        if(potentialFirstId != potentialSecondId) {
            DatabaseHelper.insertMatchByPair(ParseUser.getCurrentUser().getObjectId(), potentialFirstId, potentialSecondId);

            gameInteractionListener.onCreateMatch();
        }

        clearPotentialMatch();
    }

    private void refreshFirst() {
        ParseQuery<ParseUser> query = ParseUser.getQuery();

        // exclude self
        //query.whereNotEqualTo("objectId", ParseUser.getCurrentUser().getObjectId());
        query.setLimit(20);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> list, ParseException e) {
                if (e == null) {
                    mUserCardAdapterFirst.fillUsers(list);
                    mCardStackLayoutFirst.refreshStack();
                    firstProgress.setVisibility(View.INVISIBLE);
                } else {
                    Toast.makeText(getActivity().getApplicationContext(),
                            "Sorry, there was a problem loading users",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void refreshSecond() {
        ParseQuery<ParseUser> query = ParseUser.getQuery();

        // exclude self
        query.whereNotEqualTo("objectId", ParseUser.getCurrentUser().getObjectId());
        query.setLimit(20);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> list, ParseException e) {
                if (e == null) {
                    mUserCardAdapterSecond.fillUsers(list);
                    mCardStackLayoutSecond.refreshStack();
                    secondProgress.setVisibility(View.INVISIBLE);
                } else {
                    Toast.makeText(getActivity().getApplicationContext(),
                            "Sorry, there was a problem loading users",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public String getCurrentFirstId() {
        CardView view = mCardStackLayoutFirst.getDraggedCard() != null ? (CardView)mCardStackLayoutFirst.getDraggedCard() : mCardStackLayoutFirst.getTopCard();
        return view != null ? view.getTag().toString() : null;
    }

    public String getCurrentSecondId() {
        CardView view = mCardStackLayoutSecond.getDraggedCard() != null ? (CardView)mCardStackLayoutSecond.getDraggedCard() : mCardStackLayoutSecond.getTopCard();
        return view != null ? view.getTag().toString() : null;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            gameInteractionListener= (GameInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnClientListInteractionListener");
        }
    }

    public interface GameInteractionListener {
        void onCreateMatch();
    }
}
