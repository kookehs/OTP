package com.mrcornman.otp.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mrcornman.otp.R;
import com.mrcornman.otp.models.MatchItem;
import com.mrcornman.otp.utils.DatabaseHelper;
import com.mrcornman.otp.utils.Downloader;
import com.mrcornman.otp.utils.ProductsJSONPullParser;
import com.mrcornman.otp.views.SingleProductView;
import com.mrcornman.otp.views.SingleProductView_;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.Background;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

@EBean
public class CardAdapter extends BaseAdapter {

    List<MatchItem> mItems;
    //fixme: should I add @RootContext annotation for mContext below?
    Context mContext;
    ImageLoader imageLoader;
    DisplayImageOptions options;

    public CardAdapter(Context context) {
        // fixme: should we download the json file here?
        mContext = context;


        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(mContext).build();
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(config);

        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();

    }

    public void initFromDatabaseUsingSharedPref(String url, String postData, String fileName, DatabaseHelper db, SharedPreferences sharedPreferences, String maxProductsKey, String startFromKey){
        List<MatchItem> productsFromDb = db.getUnseenProductsFromGroup(20);
        if (productsFromDb.isEmpty()){
            if (isNetworkAvailable()){
                // fixme: downloadJsonToFileAndUpdateDb is a background task that downloads new products and updates the db, perhaps it should also query products from the db and return
                downloadJsonToFileAndUpdateDbWithGivenKeys(url, postData, fileName, db, sharedPreferences, maxProductsKey, startFromKey);
                SystemClock.sleep(2000);
                mItems = db.getUnseenProductsFromGroup(20);
            } else {
                Log.d("productCardAdapter", "network is not available");
                mItems = new ArrayList<MatchItem>();
            }
        } else {
            mItems = productsFromDb;
        }
    }

    @Background
    public void downloadJsonToFileAndUpdateDbWithGivenKeys(String url, String postData, String fileName, DatabaseHelper db, SharedPreferences sharedPreferences, String maxProductsKey, String startFromKey) {
        try {
            Downloader.downloadFromUrl(url, postData, mContext.openFileOutput(fileName, Context.MODE_PRIVATE));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        List<MatchItem> productsFromFile = ProductsJSONPullParser.getMatchesFromFileAndUpdateMaxMatches(mContext, fileName, maxProductsKey, sharedPreferences);
        db.insertOrIgnoreProducts(productsFromFile, db.TABLE_NAME);
        int startFrom = sharedPreferences.getInt(startFromKey, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(startFromKey, startFrom + 96);
        editor.commit();
    }

    @Background
    public void downloadJsonToFile(String url, String postdata, String filename) {
        try {
            Downloader.downloadFromUrl(url, postdata, mContext.openFileOutput(filename, Context.MODE_PRIVATE));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public MatchItem getItem(int i) {
        return mItems.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        SingleProductView singleProductView;
        if (convertView == null) {
            singleProductView = SingleProductView_.build(mContext);
        } else {
            singleProductView = (SingleProductView) convertView;
        }
        MatchItem matchItem = getItem(position);
        singleProductView.bind(matchItem);

        ImageView productImage = (ImageView)singleProductView.findViewById(R.id.picture);
        // fixme: maybe we need a progressbar when the image is loading?
        TextView styleName = (TextView)singleProductView.findViewById(R.id.styleName);
        TextView discountedPrice = (TextView)singleProductView.findViewById(R.id.discountedPrice);
        TextView actualPrice = (TextView)singleProductView.findViewById(R.id.actualPrice);

        ImageLoadingListener listener = new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String s, View view) {

            }

            @Override
            public void onLoadingFailed(String s, View view, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String s, View view, Bitmap bitmap) {

            }

            @Override
            public void onLoadingCancelled(String s, View view) {

            }
        };
        imageLoader.displayImage(matchItem.getImageUrl(), productImage, options, listener);
        styleName.setText(matchItem.getStyleName());
        //todo: update Product class to add additional properties like discounted price, discount, actual price
        //fixme: here we used product.getPrice() for everything, fix this.
        discountedPrice.setText(matchItem.getDiscountedPrice());
        actualPrice.setText(matchItem.getPrice());
        actualPrice.setPaintFlags(actualPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG); // http://stackoverflow.com/questions/8033316/to-draw-an-underline-below-the-textview-in-android

        return singleProductView;
    }
}
