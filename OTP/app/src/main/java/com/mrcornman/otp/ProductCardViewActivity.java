package com.mrcornman.otp;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.mrcornman.otp.adapters.ProductCardAdapter;
import com.mrcornman.otp.adapters.ProductCardAdapter_;
import com.mrcornman.otp.utils.DatabaseHelper;
import com.mrcornman.otp.views.ProductStackView;
import com.mrcornman.otp.views.SingleProductView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_product_card_view)
public class ProductCardViewActivity extends Activity {

    @ViewById
    ProductStackView mProductStack;

    private Handler handler;

    public String url = "http://www.myntra.com/searchws/search/styleids2";
    public String postData = "[{\"query\":\"(global_attr_age_group:(\\\"Adults-Men\\\" OR \\\"Adults-Unisex\\\") AND global_attr_article_type_facet:(\\\"Casual Shoes\\\") AND global_attr_master_category:(\\\"Footwear\\\" OR \\\"Free Items\\\"))\",\"start\":0,\"rows\":96,\"facet\":true,\"facetField\":[\"Casual_Shoe_Type_article_attr\",\"Upper_Material_article_attr\",\"Fastening_article_attr\",\"Ankle_Height_article_attr\",\"Width_article_attr\"],\"fq\":[\"discounted_price:[499 TO 8199]\",\"count_options_availbale:[1 TO *]\"],\"sort\":[{\"sort_field\":\"count_options_availbale\",\"order_by\":\"desc\"},{\"sort_field\":\"style_store3_male_sort_field\",\"order_by\":\"desc\"},{\"sort_field\":\"potential_revenue_male_sort_field\",\"order_by\":\"desc\"},{\"sort_field\":\"global_attr_catalog_add_date\",\"order_by\":\"desc\"}],\"return_docs\":true,\"colour_grouping\":true}]";
    public String postDataHead = "[{\"query\":\"(global_attr_age_group:(\\\"Adults-Men\\\" OR \\\"Adults-Unisex\\\") AND global_attr_article_type_facet:(\\\"Casual Shoes\\\") AND global_attr_master_category:(\\\"Footwear\\\" OR \\\"Free Items\\\"))\",\"start\":";
    public String postDataTail = ",\"rows\":96,\"facet\":true,\"facetField\":[\"Casual_Shoe_Type_article_attr\",\"Upper_Material_article_attr\",\"Fastening_article_attr\",\"Ankle_Height_article_attr\",\"Width_article_attr\"],\"fq\":[\"discounted_price:[499 TO 8199]\",\"count_options_availbale:[1 TO *]\"],\"sort\":[{\"sort_field\":\"count_options_availbale\",\"order_by\":\"desc\"},{\"sort_field\":\"style_store3_male_sort_field\",\"order_by\":\"desc\"},{\"sort_field\":\"potential_revenue_male_sort_field\",\"order_by\":\"desc\"},{\"sort_field\":\"global_attr_catalog_add_date\",\"order_by\":\"desc\"}],\"return_docs\":true,\"colour_grouping\":true}]";
    public String fileName;

    public SharedPreferences sharedPreferences;
    public int startFrom;
    public String maxProducts;

    DatabaseHelper db;

    @AfterViews
    public void initialize(){
//        handler = new Handler();
//
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                final View splash = findViewById(R.id.splash);
//                splash.animate().alpha(0.0f).setListener(new AnimatorListenerAdapter() {
//                    @Override
//                    public void onAnimationEnd(Animator animation) {
//                        splash.setVisibility(View.GONE);
//                        doInitialize();
//                    }
//                }).setDuration(2000).start();
//            }
//        }, 0);
        final View splash = findViewById(R.id.splash);
        final TextView logText = (TextView) findViewById(R.id.logText);
        splash.setVisibility(View.GONE);
        db = new DatabaseHelper(getApplicationContext());
        fileName = getString(R.string.men_shoes_filename);
        sharedPreferences = getSharedPreferences(getString(R.string.shared_preference_file_name_card_activity), MODE_PRIVATE);
        // resetStoredValues();
        startFrom = sharedPreferences.getInt(getString(R.string.men_shoes_start_from_key), 0);
        maxProducts = sharedPreferences.getString(getString(R.string.men_shoes_max_products_key), "1000");
        doInitializeSharedPref();

        mProductStack.setmProductStackListener(new ProductStackView.ProductStackListener() {
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
                //todo: here is where you have to handle what happens after you select yes or no to a particular card
                if (choice) {
                    db.updateLikeStatus(item.product, db.VALUE_LIKED);
                } else {
                    db.updateLikeStatus(item.product, db.VALUE_DISLIKED);
                }
                Log.d("product card view activity", "updated the choice made " + String.valueOf(choice) + " " + item.product.getStyleName());
                logText.setText(logText.getText() + String.valueOf(item.product));
            }
        });

        return;
    }

    private void resetStoredValues() {
        db.deleteTable(db.TABLE_NAME);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(getString(R.string.men_shoes_start_from_key), 0);
        editor.putString(getString(R.string.men_shoes_max_products_key), "1000");
        editor.commit();
        startFrom = 0;
        maxProducts = "1000";
    }

    private void doInitializeSharedPref() {
        Log.e("start from ", String.valueOf(startFrom));
        Log.e("max_products", maxProducts);
        ProductCardAdapter mAdapter = ProductCardAdapter_.getInstance_(this);
        String updatedPostData = getUpdatedPostData(startFrom);
        mAdapter.initFromDatabaseUsingSharedPref(url, updatedPostData, db, fileName, sharedPreferences);
        if (!mAdapter.isEmpty()){
            mProductStack.setAdapter(mAdapter);
        }
    }

    private String getUpdatedPostData(int startFrom) {
        if (startFrom > Integer.parseInt(maxProducts)){
            startFrom = 0;
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(getString(R.string.men_shoes_start_from_key), 0);
            editor.commit();
        }
        return postDataHead + String.valueOf(startFrom) + postDataTail;
    }

    private void doInitialize() {
        ProductCardAdapter mAdapter = ProductCardAdapter_.getInstance_(this);
        mAdapter.initFromDatabase(url, postData, db, fileName);
        mProductStack.setAdapter(mAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.product_card_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
