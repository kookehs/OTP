package com.mrcornman.otp.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.mrcornman.otp.R;
import com.mrcornman.otp.adapters.UserCardAdapter;
import com.mrcornman.otp.utils.DatabaseHelper;
import com.mrcornman.otp.utils.ProfileBuilder;
import com.mrcornman.otp.views.CardStackLayout;
import com.mrcornman.otp.views.CardView;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class GameFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match

    private CardStackLayout mCardStackLayoutFirst;
    private CardStackLayout mCardStackLayoutSecond;
    private ProgressBar firstProgress;
    private ProgressBar secondProgress;
    private UserCardAdapter mUserCardAdapterFirst;
    private UserCardAdapter mUserCardAdapterSecond;

    private OnGameInteractionListener onGameInteractionListener;

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

        refreshFirst(new RefreshCallback() {
            @Override
            public void done() {
                refreshSecond();
            }
        });

        firstProgress.setVisibility(View.VISIBLE);
        secondProgress.setVisibility(View.VISIBLE);

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            onGameInteractionListener = (OnGameInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnClientListInteractionListener");
        }
    }

    private void buildPotentialMatch(String firstId, String secondId) {
        potentialFirstId = firstId != null ? firstId : "";
        potentialSecondId = secondId != null ? secondId : "";
    }

    private void clearPotentialMatch() {
        potentialFirstId = "";
        potentialSecondId = "";
    }

    private void onCreateMatch() {
        if(potentialFirstId.equals("") || potentialSecondId.equals("")) return;

        if(potentialFirstId != potentialSecondId) {
            DatabaseHelper.insertMatchByPair(ParseUser.getCurrentUser().getObjectId(), potentialFirstId, potentialSecondId);

            onGameInteractionListener.onCreateMatch();
        }

        clearPotentialMatch();
    }

    private void refreshFirst() {
        refreshFirst(null);
    }

    private void refreshFirst(RefreshCallback callback) {
        final RefreshCallback mCallback = callback;

        ParseUser user = ParseUser.getCurrentUser();
        ParseGeoPoint location = user.getParseGeoPoint(ProfileBuilder.PROFILE_KEY_LOCATION);
        List<String> excludedIds = getSecondIds();
        excludedIds.add(user.getObjectId());

        findPotentialUsers(getCurrentSecondId(), excludedIds, location, new FunctionCallback<List<ParseUser>>() {
            @Override
            public void done(List<ParseUser> parseUsers, ParseException e) {
                if (e == null) {
                    if(parseUsers != null && parseUsers.size() > 0) {
                        mUserCardAdapterFirst.setUsers(parseUsers);
                        mCardStackLayoutFirst.refreshStack();
                        if (mCallback != null) mCallback.done();
                    } else {
                        Toast.makeText(getActivity(), "No users detected in the immediate area.", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Sorry, there was a problem loading users: " + e.toString(), Toast.LENGTH_LONG).show();
                }

                firstProgress.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void refreshSecond() {
        refreshSecond(null);
    }

    private void refreshSecond(RefreshCallback callback) {
        final RefreshCallback mCallback = callback;

        ParseUser user = ParseUser.getCurrentUser();
        ParseGeoPoint location = user.getParseGeoPoint(ProfileBuilder.PROFILE_KEY_LOCATION);
        List<String> excludedIds = getFirstIds();
        excludedIds.add(user.getObjectId());

        findPotentialUsers(getCurrentFirstId(), excludedIds, location, new FunctionCallback<List<ParseUser>>() {
            @Override
            public void done(List<ParseUser> parseUsers, ParseException e) {
                if (e == null) {
                    if(parseUsers != null && parseUsers.size() > 0) {
                        mUserCardAdapterSecond.setUsers(parseUsers);
                        mCardStackLayoutSecond.refreshStack();
                        if (mCallback != null) mCallback.done();
                    } else {
                        Toast.makeText(getActivity(), "We're out of users to show you for now. Try again soon!", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Sorry, there was a problem loading users: " + e.toString(), Toast.LENGTH_LONG).show();
                }

                secondProgress.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void findPotentialUsers(String otherId, List<String> excludedIds, ParseGeoPoint location, FunctionCallback<List<ParseUser>> callback) {
        Map<String, Object> params = new HashMap<>();
        params.put("otherId", otherId);
        params.put("excludedIds", excludedIds);
        params.put("location", location);
        params.put("searchDistance", 50 + "");
        params.put("limit", 20 + "");

        ParseCloud.callFunctionInBackground("findPotentialUsers", params, callback);
    }

    public String getCurrentFirstId() {
        CardView view = mCardStackLayoutFirst.getTopCard();
        return view != null ? view.getTag().toString() : null;
    }

    public String getCurrentSecondId() {
        CardView view = mCardStackLayoutSecond.getTopCard();
        return view != null ? view.getTag().toString() : null;
    }

    public List<String> getFirstIds() {
        List<ParseUser> users = mUserCardAdapterFirst.getUsers();
        List<String> result = new ArrayList<>();
        for(ParseUser user : users) {
            result.add(user.getObjectId());
        }
        return result;
    }

    public List<String> getSecondIds() {
        List<ParseUser> users = mUserCardAdapterSecond.getUsers();
        List<String> result = new ArrayList<>();
        for(ParseUser user : users) {
            result.add(user.getObjectId());
        }
        return result;
    }

    public interface OnGameInteractionListener {
        void onCreateMatch();
    }

    private interface RefreshCallback {
        void done();
    }
}
