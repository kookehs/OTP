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
public class ClientMatchAdapter extends ArrayAdapter<MatchItem> {

    public ClientMatchAdapter(Context context, List<MatchItem> matches) {
        super(context, 0, matches);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_match_client, parent, false);
        }

        // init views
        final TextView nameText = (TextView) convertView.findViewById(R.id.name_text);
        final TextView countText = (TextView) convertView.findViewById(R.id.count_text);

        final ImageView thumbImage = (ImageView) convertView.findViewById(R.id.thumb_image_first);
        final ProgressBar progressBar = (ProgressBar) convertView.findViewById(R.id.thumb_progress_first);

        progressBar.setVisibility(View.VISIBLE);
        thumbImage.setVisibility(View.INVISIBLE);

        MatchItem match = getItem(position);

        convertView.setTag(match.getSecondId());
        countText.setText(match.getNumLikes() + "");

        DatabaseHelper.getUserById(match.getFirstId(), new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {
                if (parseUser != null) {
                    nameText.setText(parseUser.getUsername());
                }
            }
        });

        return convertView;
    }
}
