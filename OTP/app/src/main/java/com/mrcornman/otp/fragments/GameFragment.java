package com.mrcornman.otp.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.mrcornman.otp.R;
import com.mrcornman.otp.adapters.UserCardAdapter;
import com.mrcornman.otp.utils.DatabaseHelper;
import com.mrcornman.otp.utils.ProfileBuilder;
import com.mrcornman.otp.views.CardStackLayout;
import com.mrcornman.otp.views.CardView;
import com.parse.FunctionCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;


public class GameFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match

    private CardStackLayout mCardStackLayoutFirst;
    private CardStackLayout mCardStackLayoutSecond;
    private ProgressBar firstProgress;
    private ProgressBar secondProgress;
    private UserCardAdapter mUserCardAdapterFirst;
    private UserCardAdapter mUserCardAdapterSecond;
    private ImageView progressIndicatorImage;

    private OnGameInteractionListener onGameInteractionListener;

    private String potentialFirstId = "";
    private String potentialSecondId = "";

    private float firstRatio;
    private float secondRatio;

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

        mCardStackLayoutFirst.setOrientation(CardStackLayout.ORIENTATION_DOWN);
        mCardStackLayoutSecond.setOrientation(CardStackLayout.ORIENTATION_UP);

        firstProgress = (ProgressBar) view.findViewById(R.id.first_progress);
        secondProgress = (ProgressBar) view.findViewById(R.id.second_progress);

        progressIndicatorImage = (ImageView) view.findViewById(R.id.progress_indicator_image);

        mCardStackLayoutFirst.setCardStackListener(new CardStackLayout.CardStackListener() {
            @Override
            public void onBeginProgress() {
                buildPotentialMatch(getCurrentFirstId(), getCurrentSecondId());
            }

            @Override
            public void onUpdateProgress(float ratio) {
                firstRatio = ratio;
                progressIndicatorImage.setAlpha(Math.max(firstRatio, secondRatio));
            }

            @Override
            public void onCancelled() {
                clearPotentialMatch();
            }

            @Override
            public void onChoiceAccepted() {
                if(getStackChoice(mCardStackLayoutFirst)) onCreateMatch();
            }

            @Override
            public void onRefreshRequest() {
                refreshFirstStack();
            }
        });

        mCardStackLayoutSecond.setCardStackListener(new CardStackLayout.CardStackListener() {
            @Override
            public void onBeginProgress() {
                buildPotentialMatch(getCurrentFirstId(), getCurrentSecondId());
            }

            @Override
            public void onUpdateProgress(float ratio) {
                secondRatio = ratio;
                progressIndicatorImage.setAlpha(Math.max(secondRatio, firstRatio));
                Log.i("GameFragment", "Ratio: " + ratio);
            }

            @Override
            public void onCancelled() {
                clearPotentialMatch();
            }

            @Override
            public void onChoiceAccepted() {
                if(getStackChoice(mCardStackLayoutSecond)) onCreateMatch();
            }

            @Override
            public void onRefreshRequest() {
                refreshSecondStack();
            }
        });

        mUserCardAdapterFirst = new UserCardAdapter(getActivity().getApplicationContext());
        mCardStackLayoutFirst.setAdapter(mUserCardAdapterFirst);

        mUserCardAdapterSecond = new UserCardAdapter(getActivity().getApplicationContext());
        mCardStackLayoutSecond.setAdapter(mUserCardAdapterSecond);

        initStacks();

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            onGameInteractionListener = (OnGameInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnGameInteractionListener");
        }
    }

    private boolean getStackChoice(CardStackLayout cardStack) {
        if(cardStack != null) return cardStack.getChoice();
        return false;
    }

    private void buildPotentialMatch(String firstId, String secondId) {
        potentialFirstId = firstId != null ? firstId : "";
        potentialSecondId = secondId != null ? secondId : "";
    }

    private void clearPotentialMatch() {
        potentialFirstId = "";
        potentialSecondId = "";

        firstRatio = 0;
        secondRatio = 0;
        progressIndicatorImage.setAlpha(0);

        Log.i("GameFragment", "Clearing");
    }

    private void onCreateMatch() {
        if(potentialFirstId.equals("") || potentialSecondId.equals("")) return;

        if(!potentialFirstId.equals(potentialSecondId)) {
            DatabaseHelper.insertMatchByPair(ParseUser.getCurrentUser().getObjectId(), potentialFirstId, potentialSecondId);
            onGameInteractionListener.onCreateMatch();
        }

        clearPotentialMatch();
    }

    private void initStacks() {
        firstProgress.setVisibility(View.VISIBLE);
        secondProgress.setVisibility(View.VISIBLE);

        ParseUser user = ParseUser.getCurrentUser();
        ParseGeoPoint location = user.getParseGeoPoint(ProfileBuilder.PROFILE_KEY_LOCATION);
        List<String> excludedIds = new ArrayList<>();
        excludedIds.add(user.getObjectId());

        DatabaseHelper.findPotentialUsers(null, excludedIds, location, 10, 50, new FunctionCallback<List<ParseUser>>() {
            @Override
            public void done(List<ParseUser> parseUsers, ParseException e) {
                if (e == null) {
                    // must be at last 2 parse users for a potential match
                    // we split the list in half and give each stack an equal share of users
                    if (parseUsers != null && parseUsers.size() > 1) {
                        int listSize = parseUsers.size();
                        int halfSize = listSize / 2;

                        List<ParseUser> firstList = parseUsers.subList(0, halfSize);
                        mUserCardAdapterFirst.setUsers(firstList);
                        mCardStackLayoutFirst.refreshStack();

                        List<ParseUser> secondList = parseUsers.subList(halfSize, listSize);
                        mUserCardAdapterSecond.setUsers(secondList);
                        mCardStackLayoutSecond.refreshStack();
                    } else {
                        Toast.makeText(getActivity(), "We're out of users to show you for now. Try again soon!", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Sorry, there was a problem loading users: " + e.toString(), Toast.LENGTH_LONG).show();
                }

                firstProgress.setVisibility(View.INVISIBLE);
                secondProgress.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void refreshFirstStack() {
        firstProgress.setVisibility(View.VISIBLE);

        ParseUser user = ParseUser.getCurrentUser();
        ParseGeoPoint location = user.getParseGeoPoint(ProfileBuilder.PROFILE_KEY_LOCATION);
        List<String> excludedIds = getSecondIds();
        excludedIds.add(user.getObjectId());

        DatabaseHelper.findPotentialUsers(getCurrentSecondId(), excludedIds, location, 10, 50, new FunctionCallback<List<ParseUser>>() {
            @Override
            public void done(List<ParseUser> parseUsers, ParseException e) {
                if (e == null) {
                    if (parseUsers != null && parseUsers.size() > 0) {
                        mUserCardAdapterFirst.setUsers(parseUsers);
                        mCardStackLayoutFirst.refreshStack();
                    } else {
                        Toast.makeText(getActivity(), "We're out of users to show you for now. Try again soon!", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Sorry, there was a problem loading users: " + e.toString(), Toast.LENGTH_LONG).show();
                }

                firstProgress.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void refreshSecondStack() {
        secondProgress.setVisibility(View.VISIBLE);

        ParseUser user = ParseUser.getCurrentUser();
        ParseGeoPoint location = user.getParseGeoPoint(ProfileBuilder.PROFILE_KEY_LOCATION);
        List<String> excludedIds = getFirstIds();
        excludedIds.add(user.getObjectId());

        DatabaseHelper.findPotentialUsers(getCurrentFirstId(), excludedIds, location, 10, 50, new FunctionCallback<List<ParseUser>>() {
            @Override
            public void done(List<ParseUser> parseUsers, ParseException e) {
                if (e == null) {
                    if (parseUsers != null && parseUsers.size() > 0) {
                        mUserCardAdapterSecond.setUsers(parseUsers);
                        mCardStackLayoutSecond.refreshStack();
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
}
