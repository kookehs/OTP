package com.mrcornman.otp;

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

import com.mrcornman.otp.adapters.CardAdapter;
import com.mrcornman.otp.adapters.CardAdapter_;
import com.mrcornman.otp.utils.DatabaseHelper;
import com.mrcornman.otp.views.CardStackView;
import com.mrcornman.otp.views.SingleProductView;


public class GameFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match

    private static final String SAMPLE_UNIQUE_LABEL = "men-accessories-jewellery";
    private static final String SAMPLE_FILE_NAME = "men-accessories-jewellery-file";
    private static final String SAMPLE_START_FROM_KEY = "men-accessories-jewellery-start-key";
    private static final String SAMPLE_MAX_PRODUCTS_KEY = "men-accessories-jewellery-max-products-key";
    private static final String SAMPLE_POST_DATA_HEAD = "[{\"query\":\"(global_attr_age_group:(\\\"Adults-Men\\\" OR \\\"Adults-Unisex\\\") AND global_attr_article_type_facet:(\\\"Anklet\\\" OR \\\"Bangle\\\" OR \\\"Bracelet\\\" OR \\\"Earring & Pendant Set\\\" OR \\\"Earrings\\\" OR \\\"Jewellery\\\" OR \\\"Jewellery Set\\\" OR \\\"Key chain\\\" OR \\\"Necklace\\\" OR \\\"Pendant\\\" OR \\\"Ring\\\"))\",\"start\":";
    private static final String SAMPLE_POST_DATA_TAIL = ",\"rows\":96,\"facetField\":[],\"fq\":[\"count_options_availbale:[1 TO *]\"],\"sort\":[{\"sort_field\":\"count_options_availbale\",\"order_by\":\"desc\"},{\"sort_field\":\"style_store21_male_sort_field\",\"order_by\":\"desc\"},{\"sort_field\":\"potential_revenue_male_sort_field\",\"order_by\":\"desc\"},{\"sort_field\":\"global_attr_catalog_add_date\",\"order_by\":\"desc\"}],\"return_docs\":true,\"colour_grouping\":true,\"facet\":true}]";

    private static final String SAMPLE_UNIQUE_LABEL_ = "men-accessories-sunglasses";
    private static final String SAMPLE_FILE_NAME_ = "men-accessories-sunglasses-file";
    private static final String SAMPLE_START_FROM_KEY_ = "men-accessories-sunglasses-start-key";
    private static final String SAMPLE_MAX_PRODUCTS_KEY_ = "men-accessories-sunglasses-max-products-key";
    private static final String SAMPLE_POST_DATA_HEAD_ = "[{\"query\":\"(global_attr_age_group:(\\\"Adults-Men\\\" OR \\\"Adults-Unisex\\\") AND global_attr_sub_category:(\\\"Eyewear\\\"))\",\"start\":";
    private static final String SAMPLE_POST_DATA_TAIL_ = ",\"rows\":96,\"facetField\":[],\"fq\":[\"count_options_availbale:[1 TO *]\"],\"sort\":[{\"sort_field\":\"count_options_availbale\",\"order_by\":\"desc\"},{\"sort_field\":\"style_store21_male_sort_field\",\"order_by\":\"desc\"},{\"sort_field\":\"potential_revenue_male_sort_field\",\"order_by\":\"desc\"},{\"sort_field\":\"global_attr_catalog_add_date\",\"order_by\":\"desc\"}],\"return_docs\":true,\"colour_grouping\":true,\"facet\":true}]";

    CardStackView mCardStackViewFirst;
    CardStackView mCardStackViewSecond;
    CardAdapter mCardAdapterFirst;
    CardAdapter mCardAdapterSecond;
    DatabaseHelper db;
    int startFromFirst;
    int startFromSecond;
    String maxProductsFirst;
    String maxProductsSecond;
    SharedPreferences sharedPreferences;

    public String url = "http://www.myntra.com/searchws/search/styleids2";

    public static GameFragment newInstance() {
        GameFragment fragment = new GameFragment();
        return fragment;
    }
    public GameFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_game, container, false);
        mCardStackViewFirst = (CardStackView) view.findViewById(R.id.cardstack_first);
        mCardStackViewSecond = (CardStackView) view.findViewById(R.id.cardstack_second);

        db = new DatabaseHelper(getActivity().getApplicationContext());
        // List<Product> products = db.getUnseenProductsFromGroup(getString(R.string.men_shoes_group_label), 5);

        sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        // resetStoredValues();
        startFromFirst = sharedPreferences.getInt(SAMPLE_START_FROM_KEY, 0);
        startFromSecond = sharedPreferences.getInt(SAMPLE_START_FROM_KEY_, 0);
        maxProductsFirst = sharedPreferences.getString(SAMPLE_MAX_PRODUCTS_KEY, "1000");
        maxProductsSecond = sharedPreferences.getString(SAMPLE_MAX_PRODUCTS_KEY_, "1000");

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

        mCardStackViewFirst.setmProductStackListener(new CardStackView.CardStackListener() {
            @Override
            public void onUpdateProgress(boolean positif, float percent, View view) {
                SingleProductView item = (SingleProductView) view;
                item.onUpdateProgress(positif, percent, view);
            }

            @Override
            public void onCancelled(View beingDragged) {
                SingleProductView item = (SingleProductView) beingDragged;
                item.onCancelled(beingDragged);
            }

            @Override
            public void onChoiceMade(boolean choice, View beingDragged) {
                SingleProductView item = (SingleProductView) beingDragged;
                item.onChoiceMade(choice, beingDragged);
                //todo: handle what to do after the choice is made.
                if (choice) {
                    db.updateLikeStatus(item.matchItem, db.VALUE_LIKED);
                } else {
                    db.updateLikeStatus(item.matchItem, db.VALUE_DISLIKED);
                }
                Log.d("tinder fragment", "updated the choice made " + String.valueOf(choice) + " " + item.matchItem.getStyleName());
            }
        });

        mCardStackViewSecond.setmProductStackListener(new CardStackView.CardStackListener() {
            @Override
            public void onUpdateProgress(boolean positif, float percent, View view) {
                SingleProductView item = (SingleProductView) view;
                item.onUpdateProgress(positif, percent, view);
            }

            @Override
            public void onCancelled(View beingDragged) {
                SingleProductView item = (SingleProductView) beingDragged;
                item.onCancelled(beingDragged);
            }

            @Override
            public void onChoiceMade(boolean choice, View beingDragged) {
                SingleProductView item = (SingleProductView) beingDragged;
                item.onChoiceMade(choice, beingDragged);
                //todo: handle what to do after the choice is made.
                if (choice) {
                    db.updateLikeStatus(item.matchItem, db.VALUE_LIKED);
                } else {
                    db.updateLikeStatus(item.matchItem, db.VALUE_DISLIKED);
                }
                Log.d("tinder fragment", "updated the choice made " + String.valueOf(choice) + " " + item.matchItem.getStyleName());
            }
        });

        return view;
    }

    private void resetStoredValues() {
        db.deleteTable(db.TABLE_NAME);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(SAMPLE_START_FROM_KEY, 0);
        editor.putString(SAMPLE_MAX_PRODUCTS_KEY, "1000");
        editor.commit();
    }
    //fixme: figure out how to eliminate the ui lag when the database gets new products from the internet..
    // already tried initializing the product stack inside the overridden method onViewCreated, and it didn't improve anything..

    private void refreshFirst() {
        Log.i("First: start from", String.valueOf(startFromFirst));
        Log.i("First: max products", maxProductsFirst);

        mCardAdapterFirst = CardAdapter_.getInstance_(getActivity());
        if (startFromFirst > Integer.parseInt(maxProductsFirst)){
            startFromFirst = 0;
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(getString(R.string.men_shoes_start_from_key), 0);
            editor.commit();
        }
        String postData = SAMPLE_POST_DATA_HEAD + String.valueOf(startFromFirst) + SAMPLE_POST_DATA_TAIL;
        mCardAdapterFirst.initForTinderFragment(url, postData, SAMPLE_FILE_NAME, SAMPLE_UNIQUE_LABEL, "Jewellery", db, sharedPreferences, SAMPLE_MAX_PRODUCTS_KEY, SAMPLE_START_FROM_KEY);
        if (!mCardAdapterFirst.isEmpty()) {
            mCardStackViewFirst.setAdapter(mCardAdapterFirst);
        }
    }

    private void refreshSecond() {
        Log.i("Second: start from", String.valueOf(startFromSecond));
        Log.i("Second: max products", maxProductsSecond);

        mCardAdapterSecond = CardAdapter_.getInstance_(getActivity());
        if (startFromSecond > Integer.parseInt(maxProductsSecond)){
            startFromSecond = 0;
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(getString(R.string.men_shoes_start_from_key), 0);
            editor.commit();
        }
        String postData = SAMPLE_POST_DATA_HEAD_ + String.valueOf(startFromSecond) + SAMPLE_POST_DATA_TAIL_;
        mCardAdapterSecond.initForTinderFragment(url,postData,SAMPLE_FILE_NAME_,SAMPLE_UNIQUE_LABEL_, "Sunglasses", db, sharedPreferences, SAMPLE_MAX_PRODUCTS_KEY_, SAMPLE_START_FROM_KEY_);
        if (!mCardAdapterSecond.isEmpty()){
            mCardStackViewSecond.setAdapter(mCardAdapterSecond);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        /*
        ((MainActivity) activity).onTinderFragmentAttached(
                getArguments().getString(ARG_GROUP_LABEL)
        );
        */
    }
}
