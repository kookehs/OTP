package com.mrcornman.otp.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mrcornman.otp.R;
import com.mrcornman.otp.items.models.MatchItem;
import com.mrcornman.otp.items.gson.PhotoFileItem;
import com.mrcornman.otp.items.models.PhotoItem;
import com.mrcornman.otp.utils.DatabaseHelper;
import com.mrcornman.otp.utils.PrettyNumbers;
import com.mrcornman.otp.utils.ProfileBuilder;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MatchStatsFragment extends Fragment {

    private final static String KEY_MATCH_ID = "match_id";

    private String matchId;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static MatchStatsFragment newInstance(String matchId) {
        MatchStatsFragment fragment = new MatchStatsFragment();

        // arguments
        Bundle arguments = new Bundle();
        arguments.putString(KEY_MATCH_ID, matchId);
        fragment.setArguments(arguments);

        return fragment;
    }

    public MatchStatsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_match_stats, container, false);

        // init views first
        final FrameLayout pictureFrameFirst = (FrameLayout) rootView.findViewById(R.id.picture_frame_first);
        final ImageView pictureImageFirst = (ImageView) rootView.findViewById(R.id.picture_image_first);

        pictureFrameFirst.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                final int pictureWidth = pictureFrameFirst.getWidth();
                pictureFrameFirst.setLayoutParams(new RelativeLayout.LayoutParams(pictureWidth, pictureWidth));
            }
        });

        // init views second
        final FrameLayout pictureFrameSecond = (FrameLayout) rootView.findViewById(R.id.picture_frame_second);
        final ImageView pictureImageSecond = (ImageView) rootView.findViewById(R.id.picture_image_second);

        pictureFrameSecond.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                final int pictureWidth = pictureFrameSecond.getWidth();
                pictureFrameSecond.setLayoutParams(new RelativeLayout.LayoutParams(pictureWidth, pictureWidth));
            }
        });

        // init general views
        final TextView likesText = (TextView) rootView.findViewById(R.id.likes_value_text);
        final TextView numMessagesText = (TextView) rootView.findViewById(R.id.num_messages_value_text);

        matchId = getArguments().getString(KEY_MATCH_ID);

        DatabaseHelper.getMatchById(matchId, new GetCallback<MatchItem>() {
            @Override
            public void done(MatchItem matchItem, ParseException e) {

                // match user photos
                DatabaseHelper.getUserById(matchItem.getFirstId(), new GetCallback<ParseUser>() {
                    @Override
                    public void done(ParseUser parseUser, ParseException e) {
                        final ParseUser user = parseUser;

                        List<PhotoItem> photoItems = user.getList(ProfileBuilder.PROFILE_KEY_PHOTOS);
                        if(photoItems != null && photoItems.size() > 0) {
                            PhotoItem mainPhoto = photoItems.get(0);
                            mainPhoto.fetchIfNeededInBackground(new GetCallback<PhotoItem>() {
                                @Override
                                public void done(PhotoItem photoItem, ParseException e) {
                                    PhotoFileItem mainFile = photoItem.getPhotoFiles().get(0);
                                    Picasso.with(getActivity().getApplicationContext()).load(mainFile.url).fit().centerCrop().into(pictureImageFirst);
                                }
                            });
                        }
                    }
                });

                DatabaseHelper.getUserById(matchItem.getSecondId(), new GetCallback<ParseUser>() {
                    @Override
                    public void done(ParseUser parseUser, ParseException e) {
                        final ParseUser user = parseUser;

                        List<PhotoItem> photoItems = user.getList(ProfileBuilder.PROFILE_KEY_PHOTOS);
                        if(photoItems != null && photoItems.size() > 0) {
                            PhotoItem mainPhoto = photoItems.get(0);
                            mainPhoto.fetchIfNeededInBackground(new GetCallback<PhotoItem>() {
                                @Override
                                public void done(PhotoItem photoItem, ParseException e) {
                                    PhotoFileItem mainFile = photoItem.getPhotoFiles().get(0);
                                    Picasso.with(getActivity().getApplicationContext()).load(mainFile.url).fit().centerCrop().into(pictureImageSecond);
                                }
                            });
                        }
                    }
                });

                // match likes
                likesText.setText(PrettyNumbers.formatInteger(matchItem.getNumLikes()));

                // match num messages
                numMessagesText.setText(PrettyNumbers.formatInteger(matchItem.getNumMessages()));
            }
        });

        return rootView;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }
}