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

import com.mrcornman.otp.adapters.ProductCardAdapter;
import com.mrcornman.otp.adapters.ProductCardAdapter_;
import com.mrcornman.otp.utils.DatabaseHelper;
import com.mrcornman.otp.views.ProductStackView;
import com.mrcornman.otp.views.SingleProductView;


public class GameFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_GROUP_LABEL = "groupLabel";
    private static final String ARG_UNIQUE_GROUP_LABEL = "uniqueGroupLabel";
    private static final String ARG_GROUP_FILE_NAME = "fileName";
    private static final String ARG_GROUP_START_FROM_KEY = "startFromKey";
    private static final String ARG_GROUP_MAX_PRODUCTS_KEY = "maxProductsKey";
    private static final String ARG_GROUP_POST_DATA_HEAD = "postDataHead";
    private static final String ARG_GROUP_POST_DATA_TAIL = "postDataTail";

    // TODO: Rename and change types of parameters
    private String mGroupLabel;
    private String mUniqueGroupLabel;
    private String mFileName;
    private String mStartFromKey;
    private String mMaxProductsKey;
    private String mPostDataHead;
    private String mPostDataTail;

    ProductStackView mProductStackView;
    DatabaseHelper db;
    int startFrom;
    String maxProducts;
    SharedPreferences sharedPreferences;

    public String url = "http://www.myntra.com/searchws/search/styleids2";

    public static GameFragment newInstance() {
        GameFragment fragment = new GameFragment();
        Bundle args = new Bundle();

        args.putString(ARG_GROUP_LABEL, "Game");
        args.putString(ARG_UNIQUE_GROUP_LABEL, "men-accessories-jewellery");
        args.putString(ARG_GROUP_FILE_NAME, "men-accessories-jewellery-file");
        args.putString(ARG_GROUP_START_FROM_KEY, "men-accessories-jewellery-start-key");
        args.putString(ARG_GROUP_MAX_PRODUCTS_KEY, "men-accessories-jewellery-max-products-key");
        args.putString(ARG_GROUP_POST_DATA_HEAD, "[{\"query\":\"(global_attr_age_group:(\\\"Adults-Men\\\" OR \\\"Adults-Unisex\\\") AND global_attr_article_type_facet:(\\\"Anklet\\\" OR \\\"Bangle\\\" OR \\\"Bracelet\\\" OR \\\"Earring & Pendant Set\\\" OR \\\"Earrings\\\" OR \\\"Jewellery\\\" OR \\\"Jewellery Set\\\" OR \\\"Key chain\\\" OR \\\"Necklace\\\" OR \\\"Pendant\\\" OR \\\"Ring\\\"))\",\"start\":");
        args.putString(ARG_GROUP_POST_DATA_TAIL, ",\"rows\":96,\"facetField\":[],\"fq\":[\"count_options_availbale:[1 TO *]\"],\"sort\":[{\"sort_field\":\"count_options_availbale\",\"order_by\":\"desc\"},{\"sort_field\":\"style_store21_male_sort_field\",\"order_by\":\"desc\"},{\"sort_field\":\"potential_revenue_male_sort_field\",\"order_by\":\"desc\"},{\"sort_field\":\"global_attr_catalog_add_date\",\"order_by\":\"desc\"}],\"return_docs\":true,\"colour_grouping\":true,\"facet\":true}]");
        fragment.setArguments(args);
        return fragment;
    }
    public GameFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mGroupLabel = getArguments().getString(ARG_GROUP_LABEL);
            mUniqueGroupLabel = getArguments().getString(ARG_UNIQUE_GROUP_LABEL);
            mFileName = getArguments().getString(ARG_GROUP_FILE_NAME);
            mStartFromKey = getArguments().getString(ARG_GROUP_START_FROM_KEY);
            mMaxProductsKey = getArguments().getString(ARG_GROUP_MAX_PRODUCTS_KEY);
            mPostDataHead = getArguments().getString(ARG_GROUP_POST_DATA_HEAD);
            mPostDataTail = getArguments().getString(ARG_GROUP_POST_DATA_TAIL);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_game, container, false);
        mProductStackView = (ProductStackView) view.findViewById(R.id.tinder_mProductStack);
        db = new DatabaseHelper(getActivity().getApplicationContext());
        // List<Product> products = db.getUnseenProductsFromGroup(getString(R.string.men_shoes_group_label), 5);
        // Log.e("from tinder fragment, check if its the same database", products.toString());
        sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        // resetStoredValues();
        startFrom = sharedPreferences.getInt(mStartFromKey, 0);
        maxProducts = sharedPreferences.getString(mMaxProductsKey, "1000");
        doInitialize();

        Button refreshButton = (Button) view.findViewById(R.id.refreshButton);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doInitialize();
            }
        });
        mProductStackView.setmProductStackListener(new ProductStackView.ProductStackListener() {
            @Override
            public void onUpdateProgress(boolean positif, float percent, View view) {
                SingleProductView item = (SingleProductView)view;
                item.onUpdateProgress(positif, percent, view);
            }

            @Override
            public void onCancelled(View beingDragged) {
                SingleProductView item = (SingleProductView)beingDragged;
                item.onCancelled(beingDragged);
            }

            @Override
            public void onChoiceMade(boolean choice, View beingDragged) {
                SingleProductView item = (SingleProductView)beingDragged;
                item.onChoiceMade(choice, beingDragged);
                //todo: handle what to do after the choice is made.
                if (choice) {
                    db.updateLikeStatus(item.product, db.VALUE_LIKED);
                } else {
                    db.updateLikeStatus(item.product, db.VALUE_DISLIKED);
                }
                Log.d("tinder fragment", "updated the choice made " + String.valueOf(choice) + " " + item.product.getStyleName());
            }
        });

        return view;
    }

    private void resetStoredValues() {
        db.deleteTable(db.TABLE_NAME);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(mStartFromKey, 0);
        editor.putString(mMaxProductsKey, "1000");
        editor.commit();
    }
    //fixme: figure out how to eliminate the ui lag when the database gets new products from the internet..
    // already tried initializing the product stack inside the overridden method onViewCreated, and it didn't improve anything..

    private void doInitialize() {
        Log.e("Tinder Fragment: start from", String.valueOf(startFrom));
        Log.e("Tinder Fragment: max products", maxProducts);
        ProductCardAdapter mAdapter = ProductCardAdapter_.getInstance_(getActivity());
        String postData = getUpdatedPostData(startFrom);
        // todo: have a helper function that directly takes ProductGroup instead of all its parameters individually
        // mAdapter.initForTinderFragment(url, ProductGroup, db, sharedPreferences); something like this
        mAdapter.initForTinderFragment(url,postData,mFileName,mUniqueGroupLabel, mGroupLabel, db, sharedPreferences, mMaxProductsKey, mStartFromKey);
        if (!mAdapter.isEmpty()){
            mProductStackView.setAdapter(mAdapter);
        }
    }

    private String getUpdatedPostData(int startFrom) {
        if (startFrom > Integer.parseInt(maxProducts)){
            startFrom = 0;
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(getString(R.string.men_shoes_start_from_key), 0);
            editor.commit();
        }
        return mPostDataHead + String.valueOf(startFrom) + mPostDataTail;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onTinderFragmentAttached(
                getArguments().getString(ARG_GROUP_LABEL)
        );
    }
}
