package com.mrcornman.otp.adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
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

        DatabaseHelper db = new DatabaseHelper(context);

        // init views first
        TextView countText = (TextView)row.findViewById(R.id.count_text);

        final ImageView thumbImageFirst = (ImageView)row.findViewById(R.id.thumb_image_first);
        TextView nameTextFirst = (TextView)row.findViewById(R.id.name_text_first);
        final ProgressBar progressBarFirst = (ProgressBar)row.findViewById(R.id.thumb_progress_first);

        progressBarFirst.setVisibility(View.VISIBLE);
        thumbImageFirst.setVisibility(View.INVISIBLE);

        ImageLoadingListener listenerFirst = new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String s, View view) {

            }

            @Override
            public void onLoadingFailed(String s, View view, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                progressBarFirst.setVisibility(View.INVISIBLE);
                thumbImageFirst.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingCancelled(String s, View view) {

            }
        };

        // init views second
        final ImageView thumbImageSecond = (ImageView)row.findViewById(R.id.thumb_image_second);
        TextView nameTextSecond = (TextView)row.findViewById(R.id.name_text_second);
        final ProgressBar progressBarSecond = (ProgressBar)row.findViewById(R.id.thumb_progress_second);

        progressBarSecond.setVisibility(View.VISIBLE);
        thumbImageSecond.setVisibility(View.INVISIBLE);

        ImageLoadingListener listenerSecond = new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String s, View view) {

            }

            @Override
            public void onLoadingFailed(String s, View view, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                progressBarSecond.setVisibility(View.INVISIBLE);
                thumbImageSecond.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingCancelled(String s, View view) {

            }
        };

        // use match item to fill views
        final MatchItem matchItem = new MatchItem(cursor.getString(cursor.getColumnIndex(DatabaseHelper.KEY_ID)));
        matchItem.setFirstId(cursor.getString(cursor.getColumnIndex(DatabaseHelper.MATCH_KEY_FIRST_ID)));
        matchItem.setSecondId(cursor.getString(cursor.getColumnIndex(DatabaseHelper.MATCH_KEY_SECOND_ID)));
        matchItem.setMatchmakerId(cursor.getString(cursor.getColumnIndex(DatabaseHelper.MATCH_KEY_MATCHMAKER_ID)));
        matchItem.setNumLikes(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.MATCH_KEY_NUM_LIKES)));

        // tag the view
        // TODO: Should I give the match itself instead of just the user?
        row.setTag(matchItem.getId());

        // the number of likes of the match
        countText.setText(matchItem.getNumLikes() + "");

        // fill first user
        final UserItem userItemFirst = db.getUserById(matchItem.getFirstId());

        if(userItemFirst != null) {
            // the other user image
            imageLoader.displayImage(userItemFirst.getImageUrl(), thumbImageFirst, options, listenerFirst);

            // the other user name
            nameTextFirst.setText(userItemFirst.getName());
        }

        // fill second user
        final UserItem userItemSecond = db.getUserById(matchItem.getSecondId());

        if(userItemSecond != null) {

            // the other user image
            imageLoader.displayImage(userItemSecond.getImageUrl(), thumbImageSecond, options, listenerSecond);

            // the other user name
            nameTextSecond.setText(userItemSecond.getName());
        }
    }
}
