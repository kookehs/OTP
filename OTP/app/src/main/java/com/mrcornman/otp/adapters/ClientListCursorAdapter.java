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
public class ClientListCursorAdapter extends CursorAdapter {

    ImageLoader imageLoader;
    DisplayImageOptions options;

    public ClientListCursorAdapter(Context context, Cursor c, boolean autoRequery) {
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
        RelativeLayout row = (RelativeLayout)inflater.inflate(R.layout.row_match_client, null);
        return row;

    }

    @Override
    public void bindView(View row, final Context context, Cursor cursor) {

        final ImageView secondImage = (ImageView)row.findViewById(R.id.second_image);
        TextView secondName = (TextView)row.findViewById(R.id.second_name);
        final ProgressBar progressBar = (ProgressBar)row.findViewById(R.id.first_progress);

        progressBar.setVisibility(View.VISIBLE);
        secondImage.setVisibility(View.INVISIBLE);

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
                secondImage.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingCancelled(String s, View view) {

            }
        };

        final MatchItem matchItem = new MatchItem(cursor.getString(cursor.getColumnIndex(DatabaseHelper.KEY_ID)));
        matchItem.setFirstId(cursor.getString(cursor.getColumnIndex(DatabaseHelper.MATCH_KEY_FIRST_ID)));
        matchItem.setSecondId(cursor.getString(cursor.getColumnIndex(DatabaseHelper.MATCH_KEY_SECOND_ID)));
        matchItem.setMatchmakerId(cursor.getString(cursor.getColumnIndex(DatabaseHelper.MATCH_KEY_MATCHMAKER_ID)));
        matchItem.setNumLikes(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.MATCH_KEY_NUM_LIKES)));

        DatabaseHelper db = new DatabaseHelper(context);

        final UserItem userItem = db.getUserById(matchItem.getSecondId());
        imageLoader.displayImage(userItem.getImageUrl(), secondImage, options, listener);
        secondImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "http://www.myntra.com/" + userItem.getDreLandingPageUrl();
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                context.startActivity(i);
            }
        });

        if(userItem != null) {
            secondName.setText(userItem.getName());
        }
    }
}
