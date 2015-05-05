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
import android.widget.TextView;

import com.mrcornman.otp.R;
import com.mrcornman.otp.models.MatchItem;
import com.mrcornman.otp.models.UserItem;
import com.mrcornman.otp.utils.DatabaseHelper;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * Created by Anil on 8/29/2014.
 */
public class MatchMakerListCursorAdapter extends CursorAdapter {

    ImageLoader imageLoader;
    DisplayImageOptions options;

    public MatchMakerListCursorAdapter(Context context, Cursor c, boolean autoRequery) {
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
        return inflater.inflate(R.layout.row_match_matchmaker, null);
    }

    @Override
    public void bindView(View row, final Context context, Cursor cursor) {

        // get the current match item
        final MatchItem matchItem = new MatchItem(cursor.getString(cursor.getColumnIndex(DatabaseHelper.KEY_ID)));
        matchItem.setFirstId(cursor.getString(cursor.getColumnIndex(DatabaseHelper.MATCH_KEY_FIRST_ID)));
        matchItem.setSecondId(cursor.getString(cursor.getColumnIndex(DatabaseHelper.MATCH_KEY_SECOND_ID)));
        matchItem.setMatchmakerId(cursor.getString(cursor.getColumnIndex(DatabaseHelper.MATCH_KEY_MATCHMAKER_ID)));
        matchItem.setNumLikes(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.MATCH_KEY_NUM_LIKES)));

        DatabaseHelper db = new DatabaseHelper(context);

        // make the first user view
        final UserItem userItem = db.getUserById(matchItem.getFirstId());

        final ProgressBar progressBar = (ProgressBar)row.findViewById(R.id.first_progress);
        final ImageView image = (ImageView)row.findViewById(R.id.first_image);

        progressBar.setVisibility(View.VISIBLE);
        image.setVisibility(View.INVISIBLE);

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
                image.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingCancelled(String s, View view) {

            }
        };

        imageLoader.displayImage(userItem.getImageUrl(), image, options, listener);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "http://www.myntra.com/" + userItem.getDreLandingPageUrl();
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                context.startActivity(i);
            }
        });

        TextView nameText = (TextView)row.findViewById(R.id.first_name);

        if(userItem != null) {
            nameText.setText(userItem.getName());
        }

        // make the second user view
        final UserItem userItem_ = db.getUserById(matchItem.getSecondId());

        final ProgressBar progressBar_ = (ProgressBar)row.findViewById(R.id.second_progress);
        final ImageView image_ = (ImageView)row.findViewById(R.id.second_image);

        progressBar_.setVisibility(View.VISIBLE);
        image_.setVisibility(View.INVISIBLE);

        ImageLoadingListener listener_ = new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String s, View view) {

            }

            @Override
            public void onLoadingFailed(String s, View view, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                progressBar_.setVisibility(View.INVISIBLE);
                image_.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingCancelled(String s, View view) {

            }
        };

        imageLoader.displayImage(userItem_.getImageUrl(), image_, options, listener_);
        image_.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "http://www.myntra.com/" + userItem_.getDreLandingPageUrl();
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                context.startActivity(i);
            }
        });

        TextView nameText_ = (TextView)row.findViewById(R.id.second_name);

        if(userItem_ != null) {
            nameText_.setText(userItem_.getName());
        }
    }
}
