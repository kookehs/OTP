package com.mrcornman.otp.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.mrcornman.otp.R;
import com.mrcornman.otp.adapters.CardAdapter;
import com.mrcornman.otp.adapters.CardAdapter_;
import com.mrcornman.otp.models.MatchItem;
import com.mrcornman.otp.models.UserItem;
import com.mrcornman.otp.utils.DatabaseHelper;
import com.mrcornman.otp.views.CardStackLayout;
import com.mrcornman.otp.views.CardView;

import java.util.ArrayList;
import java.util.List;


public class GameFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match

    private static final String SAMPLE_FILE_NAME = "men-accessories-jewellery-file";
    private static final String SAMPLE_START_FROM_KEY = "men-accessories-jewellery-start-key";
    private static final String SAMPLE_MAX_PRODUCTS_KEY = "men-accessories-jewellery-max-products-key";
    private static final String SAMPLE_POST_DATA_HEAD = "[{\"query\":\"(global_attr_age_group:(\\\"Adults-Men\\\" OR \\\"Adults-Unisex\\\") AND global_attr_article_type_facet:(\\\"Anklet\\\" OR \\\"Bangle\\\" OR \\\"Bracelet\\\" OR \\\"Earring & Pendant Set\\\" OR \\\"Earrings\\\" OR \\\"Jewellery\\\" OR \\\"Jewellery Set\\\" OR \\\"Key chain\\\" OR \\\"Necklace\\\" OR \\\"Pendant\\\" OR \\\"Ring\\\"))\",\"start\":";
    private static final String SAMPLE_POST_DATA_TAIL = ",\"rows\":96,\"facetField\":[],\"fq\":[\"count_options_availbale:[1 TO *]\"],\"sort\":[{\"sort_field\":\"count_options_availbale\",\"order_by\":\"desc\"},{\"sort_field\":\"style_store21_male_sort_field\",\"order_by\":\"desc\"},{\"sort_field\":\"potential_revenue_male_sort_field\",\"order_by\":\"desc\"},{\"sort_field\":\"global_attr_catalog_add_date\",\"order_by\":\"desc\"}],\"return_docs\":true,\"colour_grouping\":true,\"facet\":true}]";

    private static final String SAMPLE_FILE_NAME_ = "men-accessories-sunglasses-file";
    private static final String SAMPLE_START_FROM_KEY_ = "men-accessories-sunglasses-start-key";
    private static final String SAMPLE_MAX_PRODUCTS_KEY_ = "men-accessories-sunglasses-max-products-key";
    private static final String SAMPLE_POST_DATA_HEAD_ = "[{\"query\":\"(global_attr_age_group:(\\\"Adults-Men\\\" OR \\\"Adults-Unisex\\\") AND global_attr_sub_category:(\\\"Eyewear\\\"))\",\"start\":";
    private static final String SAMPLE_POST_DATA_TAIL_ = ",\"rows\":96,\"facetField\":[],\"fq\":[\"count_options_availbale:[1 TO *]\"],\"sort\":[{\"sort_field\":\"count_options_availbale\",\"order_by\":\"desc\"},{\"sort_field\":\"style_store21_male_sort_field\",\"order_by\":\"desc\"},{\"sort_field\":\"potential_revenue_male_sort_field\",\"order_by\":\"desc\"},{\"sort_field\":\"global_attr_catalog_add_date\",\"order_by\":\"desc\"}],\"return_docs\":true,\"colour_grouping\":true,\"facet\":true}]";

    private CardStackLayout mCardStackLayoutFirst;
    private CardStackLayout mCardStackLayoutSecond;
    private CardAdapter mCardAdapterFirst;
    private CardAdapter mCardAdapterSecond;
    private DatabaseHelper db;
    private int startFromFirst;
    private int startFromSecond;
    private String maxCardsFirst;
    private String maxCardsSecond;

    private SharedPreferences sharedPreferences;

    private MatchItem potentialMatch;

    public String url = "http://www.myntra.com/searchws/search/styleids2";

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
        mCardStackLayoutFirst = (CardStackLayout) view.findViewById(R.id.cardstack_first);
        mCardStackLayoutSecond = (CardStackLayout) view.findViewById(R.id.cardstack_second);

        db = new DatabaseHelper(getActivity().getApplicationContext());

        sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        // resetStoredValues();
        startFromFirst = sharedPreferences.getInt(SAMPLE_START_FROM_KEY, 0);
        startFromSecond = sharedPreferences.getInt(SAMPLE_START_FROM_KEY_, 0);
        maxCardsFirst = sharedPreferences.getString(SAMPLE_MAX_PRODUCTS_KEY, "1000");
        maxCardsSecond = sharedPreferences.getString(SAMPLE_MAX_PRODUCTS_KEY_, "1000");

        potentialMatch = new MatchItem();

        refreshFirst();
        refreshSecond();

        Button refreshButtonFirst = (Button) view.findViewById(R.id.btn_refresh_first);
        refreshButtonFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refreshFirst();
            }
        });

        Button refreshButtonSecond = (Button) view.findViewById(R.id.btn_refresh_second);
        refreshButtonSecond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refreshSecond();
            }
        });

        mCardStackLayoutFirst.setmCardStackListener(new CardStackLayout.CardStackListener() {
            @Override
            public void onBeginProgress(View view) {
                buildPotentialMatch(getFirstFocused().getId(), getSecondFocused().getId());
            }

            @Override
            public void onUpdateProgress(boolean positif, float percent, View view) {
                CardView item = (CardView) view;
                item.onUpdateProgress(positif, percent, view);
            }

            @Override
            public void onCancelled(View beingDragged) {
                CardView item = (CardView) beingDragged;
                item.onCancelled(beingDragged);
                clearPotentialMatch();
            }

            @Override
            public void onChoiceMade(boolean choice, View beingDragged) {
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
            }
        });

        mCardStackLayoutSecond.setmCardStackListener(new CardStackLayout.CardStackListener() {
            @Override
            public void onBeginProgress(View view) {
                buildPotentialMatch(getFirstFocused().getId(), getSecondFocused().getId());
            }

            @Override
            public void onUpdateProgress(boolean positif, float percent, View view) {
                CardView item = (CardView) view;
                item.onUpdateProgress(positif, percent, view);
            }

            @Override
            public void onCancelled(View beingDragged) {
                CardView item = (CardView) beingDragged;
                item.onCancelled(beingDragged);
                clearPotentialMatch();
            }

            @Override
            public void onChoiceMade(boolean choice, View beingDragged) {
                onCreateMatch();
            }
        });

        return view;
    }

    private void buildPotentialMatch(String firstId, String secondId) {
        potentialMatch = new MatchItem();
        potentialMatch.setFirstId(firstId);
        potentialMatch.setSecondId(secondId);
        potentialMatch.setMatchmakerId("");
        potentialMatch.setNumLikes(1);
    }

    private void clearPotentialMatch() {
        potentialMatch = null;
    }

    public void onCreateMatch() {
        if(potentialMatch == null) return;

        // TODO: Possibly make it update on insert match instead of doing a costly check beforehand
        MatchItem filterMatch = db.getMatchByPairIds(potentialMatch.getFirstId(), potentialMatch.getSecondId());
        if(filterMatch == null) {
            db.insertNewMatch(potentialMatch);
        } else {
            db.updateNumMatchLikes(filterMatch, filterMatch.getNumLikes() + 1);
        }

        clearPotentialMatch();
    }

    private void resetStoredValues() {
        db.onResetTables();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(SAMPLE_START_FROM_KEY, 0);
        editor.putString(SAMPLE_MAX_PRODUCTS_KEY, "1000");
        editor.commit();
    }
    //fixme: figure out how to eliminate the ui lag when the database gets new products from the internet..
    // already tried initializing the product stack inside the overridden method onViewCreated, and it didn't improve anything..

    private void refreshFirst() {
        Log.i("First: start from", String.valueOf(startFromFirst));
        Log.i("First: max products", maxCardsFirst);

        mCardAdapterFirst = CardAdapter_.getInstance_(getActivity());
        if (startFromFirst > Integer.parseInt(maxCardsFirst)){
            startFromFirst = 0;
            SharedPreferences.Editor editor = sharedPreferences.edit();
            //editor.putInt(getString(R.string.men_shoes_start_from_key), 0);
            editor.commit();
        }
        String postData = SAMPLE_POST_DATA_HEAD + String.valueOf(startFromFirst) + SAMPLE_POST_DATA_TAIL;
        mCardAdapterFirst.initFromDatabaseFromOtherUserUsingSharedPref(getSecondList(), url, postData, SAMPLE_FILE_NAME, db, sharedPreferences, SAMPLE_MAX_PRODUCTS_KEY, SAMPLE_START_FROM_KEY);
        if (!mCardAdapterFirst.isEmpty()) {
            mCardStackLayoutFirst.setAdapter(mCardAdapterFirst);
        }
    }

    private void refreshSecond() {
        Log.i("Second: start from", String.valueOf(startFromSecond));
        Log.i("Second: max products", maxCardsSecond);

        mCardAdapterSecond = CardAdapter_.getInstance_(getActivity());
        if (startFromSecond > Integer.parseInt(maxCardsSecond)){
            startFromSecond = 0;
            SharedPreferences.Editor editor = sharedPreferences.edit();
            //editor.putInt(getString(R.string.men_shoes_start_from_key), 0);
            editor.commit();
        }
        String postData = SAMPLE_POST_DATA_HEAD_ + String.valueOf(startFromSecond) + SAMPLE_POST_DATA_TAIL_;
        mCardAdapterSecond.initFromDatabaseFromOtherUserUsingSharedPref(getFirstList(), url, postData, SAMPLE_FILE_NAME_, db, sharedPreferences, SAMPLE_MAX_PRODUCTS_KEY_, SAMPLE_START_FROM_KEY_);
        if (!mCardAdapterSecond.isEmpty()){
            mCardStackLayoutSecond.setAdapter(mCardAdapterSecond);
        }
    }

    public UserItem getFirstFocused() {
        CardView view = mCardStackLayoutFirst.getmBeingDragged() != null ? (CardView)mCardStackLayoutFirst.getmBeingDragged() : (CardView)mCardStackLayoutFirst.getTopCard();
        return view != null ? view.userItem : null;
    }

    public UserItem getSecondFocused() {
        CardView view = mCardStackLayoutSecond.getmBeingDragged() != null ? (CardView)mCardStackLayoutSecond.getmBeingDragged() : (CardView)mCardStackLayoutSecond.getTopCard();
        return view != null ? view.userItem : null;
    }

    public List<UserItem> getFirstList() {
        List<UserItem> firstList = new ArrayList<UserItem>();
        if(mCardStackLayoutFirst.getmBeingDragged() != null)
            firstList.add(((CardView)mCardStackLayoutFirst.getmBeingDragged()).userItem);

        for(CardView view : mCardStackLayoutFirst.getCards()) {
            firstList.add(view.userItem);
        }

        return firstList;
    }

    public List<UserItem> getSecondList() {
        List<UserItem> secondList = new ArrayList<UserItem>();
        if(mCardStackLayoutSecond.getmBeingDragged() != null)
            secondList.add(((CardView)mCardStackLayoutSecond.getmBeingDragged()).userItem);

        for(CardView view : mCardStackLayoutSecond.getCards()) {
            secondList.add(view.userItem);
        }

        return secondList;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }
}
