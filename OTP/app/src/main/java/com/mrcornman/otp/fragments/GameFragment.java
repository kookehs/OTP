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
import com.mrcornman.otp.adapters.RecommendationAdapter;
import com.mrcornman.otp.models.gson.Recommendation;
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
    private RecommendationAdapter mRecommendationAdapterFirst;
    private RecommendationAdapter mRecommendationAdapterSecond;
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

        mRecommendationAdapterFirst = new RecommendationAdapter(getActivity().getApplicationContext());
        mCardStackLayoutFirst.setAdapter(mRecommendationAdapterFirst);

        mRecommendationAdapterSecond = new RecommendationAdapter(getActivity().getApplicationContext());
        mCardStackLayoutSecond.setAdapter(mRecommendationAdapterSecond);

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

        DatabaseHelper.findRecommendations(null, excludedIds, location, 10, 50, new FunctionCallback<List<Recommendation>>() {
            @Override
            public void done(List<Recommendation> recommendations, ParseException e) {

                boolean resultErr = false;

                firstProgress.setVisibility(View.INVISIBLE);
                secondProgress.setVisibility(View.INVISIBLE);

                if (e == null && recommendations != null) {
                    // must be at last 2 parse recommendations for a potential match
                    // we split the list in half and give each stack an equal share of recommendations
                    if (recommendations.size() > 1) {
                        int listSize = recommendations.size();
                        int halfSize = listSize / 2;

                        List<Recommendation> firstList = recommendations.subList(0, halfSize);
                        mRecommendationAdapterFirst.setRecommendations(firstList);
                        mCardStackLayoutFirst.refreshStack();

                        List<Recommendation> secondList = recommendations.subList(halfSize, listSize);
                        mRecommendationAdapterSecond.setRecommendations(secondList);
                        mCardStackLayoutSecond.refreshStack();
                    } else {
                        Toast.makeText(getActivity(), "We're out of people to show you for now. Try again soon!", Toast.LENGTH_LONG).show();
                    }
                } else {
                    resultErr = true;
                    Log.e("GameFragment", "Problem loading recommendations: " + e.toString());
                }

                if(resultErr) {
                    Toast.makeText(getActivity(), "Sorry, there was a problem. Please contact us for assistance!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void refreshFirstStack() {
        firstProgress.setVisibility(View.VISIBLE);

        ParseUser user = ParseUser.getCurrentUser();
        ParseGeoPoint location = user.getParseGeoPoint(ProfileBuilder.PROFILE_KEY_LOCATION);
        List<String> excludedIds = getSecondIds();
        excludedIds.add(user.getObjectId());

        DatabaseHelper.findRecommendations(getCurrentSecondId(), excludedIds, location, 10, 50, new FunctionCallback<List<Recommendation>>() {
            @Override
            public void done(List<Recommendation> recommendations, ParseException e) {
                if (e == null && recommendations != null) {
                    // must be at last 2 parse recommendations for a potential match
                    // we split the list in half and give each stack an equal share of recommendations
                    if (recommendations.size() > 1) {
                        int listSize = recommendations.size();
                        int halfSize = listSize / 2;

                        List<Recommendation> firstList = recommendations.subList(0, halfSize);
                        mRecommendationAdapterFirst.setRecommendations(firstList);
                        mCardStackLayoutFirst.refreshStack();

                        List<Recommendation> secondList = recommendations.subList(halfSize, listSize);
                        mRecommendationAdapterSecond.setRecommendations(secondList);
                        mCardStackLayoutSecond.refreshStack();
                    } else {
                        Toast.makeText(getActivity(), "We're out of people to show you for now. Try again soon!", Toast.LENGTH_LONG).show();
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

        DatabaseHelper.findRecommendations(getCurrentFirstId(), excludedIds, location, 10, 50, new FunctionCallback<List<Recommendation>>() {
            @Override
            public void done(List<Recommendation> recommendations, ParseException e) {
                if (e == null && recommendations != null) {
                    if (recommendations.size() > 0) {
                        mRecommendationAdapterSecond.setRecommendations(recommendations);
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
        List<Recommendation> recommendations = mRecommendationAdapterFirst.getRecommendations();
        List<String> result = new ArrayList<>();
        for(Recommendation recommendation : recommendations) {
            result.add(recommendation.userId);
        }
        return result;
    }

    public List<String> getSecondIds() {
        List<Recommendation> recommendations = mRecommendationAdapterSecond.getRecommendations();
        List<String> result = new ArrayList<>();
        for(Recommendation recommendation : recommendations) {
            result.add(recommendation.userId);
        }
        return result;
    }

    public interface OnGameInteractionListener {
        void onCreateMatch();
    }
}
