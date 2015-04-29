package com.mrcornman.otp.adapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mrcornman.otp.models.MatchItem;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import com.mrcornman.otp.R;
import com.mrcornman.otp.utils.DatabaseHelper;

/**
 * Created by Anil on 8/29/2014.
 */
public class ProductListAdapterWithACursor extends CursorAdapter {

    ImageLoader imageLoader;
    DisplayImageOptions options;

    public ProductListAdapterWithACursor(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context).build();
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(config);

        options = new DisplayImageOptions.Builder()
                .cacheOnDisk(true)
                .cacheInMemory(true)
                .build();
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        LayoutInflater inflater =(LayoutInflater)parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        RelativeLayout row = (RelativeLayout)inflater.inflate(R.layout.row_product, null);
        return row;

    }

    @Override
    public void bindView(View row, final Context context, Cursor cursor) {

        final ImageView productImage = (ImageView)row.findViewById(R.id.productImage);
        TextView productName = (TextView)row.findViewById(R.id.productName);
        TextView productPrice = (TextView)row.findViewById(R.id.productPrice);
        final ProgressBar progressBar = (ProgressBar)row.findViewById(R.id.listRowProgress);

        progressBar.setVisibility(View.VISIBLE);
        productImage.setVisibility(View.INVISIBLE);

        ImageLoadingListener listener = new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String s, View view) {

            }

            @Override
            public void onLoadingFailed(String s, View view, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                progressBar.setVisibility(View.INVISIBLE);
                productImage.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingCancelled(String s, View view) {

            }
        };
        final MatchItem matchItem = new MatchItem(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.KEY_ID)));
        matchItem.setUniqueProductGroup(cursor.getString(cursor.getColumnIndex(DatabaseHelper.KEY_UNIQUE_PRODUCT_GROUP)));
        matchItem.setDiscountedPrice(cursor.getString(cursor.getColumnIndex(DatabaseHelper.KEY_DISCOUNTED_PRICE)));
        matchItem.setStyleName(cursor.getString(cursor.getColumnIndex(DatabaseHelper.KEY_STYLE_NAME)));
        matchItem.setDiscount(cursor.getString(cursor.getColumnIndex(DatabaseHelper.KEY_DISCOUNT)));
        matchItem.setPrice(cursor.getString(cursor.getColumnIndex(DatabaseHelper.KEY_PRICE)));
        matchItem.setStyleId(cursor.getString(cursor.getColumnIndex(DatabaseHelper.KEY_STYLE_ID)));
        matchItem.setImageUrl(cursor.getString(cursor.getColumnIndex(DatabaseHelper.KEY_IMAGE_URL)));
        matchItem.setDreLandingPageUrl(cursor.getString(cursor.getColumnIndex(DatabaseHelper.KEY_LANDING_PAGE_URL)));
        matchItem.setLiked(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.KEY_LIKED)));

        imageLoader.displayImage(matchItem.getImageUrl(),productImage, options, listener);
        productImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "http://www.myntra.com/" + matchItem.getDreLandingPageUrl();
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                context.startActivity(i);
            }
        });
        productName.setText(matchItem.getStyleName());
        productPrice.setText(matchItem.getDiscountedPrice());

    }
}
