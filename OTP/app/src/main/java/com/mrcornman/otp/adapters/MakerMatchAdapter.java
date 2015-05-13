package com.mrcornman.otp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mrcornman.otp.R;
import com.mrcornman.otp.models.MatchItem;
import com.mrcornman.otp.utils.DatabaseHelper;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.List;

/**
 * Created by Anil on 8/29/2014.
 */
public class MakerMatchAdapter extends ArrayAdapter<MatchItem> {

    public MakerMatchAdapter(Context context, List<MatchItem> matches) {
        super(context, 0, matches);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_match_matchmaker, parent, false);
        }

        // init views first
        final TextView countText = (TextView) convertView.findViewById(R.id.count_text);

        final ImageView thumbImageFirst = (ImageView) convertView.findViewById(R.id.thumb_image_first);
        final TextView nameTextFirst = (TextView) convertView.findViewById(R.id.name_text_first);
        final ProgressBar progressBarFirst = (ProgressBar) convertView.findViewById(R.id.thumb_progress_first);

        progressBarFirst.setVisibility(View.VISIBLE);
        thumbImageFirst.setVisibility(View.INVISIBLE);

        // init views second
        final ImageView thumbImageSecond = (ImageView) convertView.findViewById(R.id.thumb_image_second);
        final TextView nameTextSecond = (TextView) convertView.findViewById(R.id.name_text_second);
        final ProgressBar progressBarSecond = (ProgressBar) convertView.findViewById(R.id.thumb_progress_second);

        progressBarSecond.setVisibility(View.VISIBLE);
        thumbImageSecond.setVisibility(View.INVISIBLE);

        // use match item to fill views
        MatchItem match = getItem(position);

        convertView.setTag(match.getObjectId());
        countText.setText(match.getNumLikes() + "");

        // fill first user
        DatabaseHelper.getUserById(match.getFirstId(), new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {
                if(parseUser != null) {
                    nameTextFirst.setText(parseUser.getUsername());
                }
            }
        });

        // fill second user
        DatabaseHelper.getUserById(match.getSecondId(), new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {
                if(parseUser != null) {
                    nameTextSecond.setText(parseUser.getUsername());
                }
            }
        });

        return convertView;
    }
}
