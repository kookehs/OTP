package com.mrcornman.otp.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mrcornman.otp.R;
import com.mrcornman.otp.models.gson.PhotoFile;
import com.mrcornman.otp.models.models.PhotoItem;
import com.mrcornman.otp.utils.DatabaseHelper;
import com.mrcornman.otp.utils.PrettyTime;
import com.mrcornman.otp.utils.ProfileBuilder;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.List;

public class ThemProfileFragment extends Fragment {

    private final static String KEY_USER_ID = "user_id";
    private static final int SWIPE_MIN_DISTANCE = 120;

    private String userId;
    private int currImage = 0;
    private long SWIPE_MIN_TIME = 120;
    private float x1 = 0;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static ThemProfileFragment newInstance(String userId) {
        ThemProfileFragment fragment = new ThemProfileFragment();

        // arguments
        Bundle arguments = new Bundle();
        arguments.putString(KEY_USER_ID, userId);
        fragment.setArguments(arguments);

        return fragment;
    }

    public ThemProfileFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        final TextView nameText = (TextView) rootView.findViewById(R.id.name_text);
        final TextView ageText = (TextView) rootView.findViewById(R.id.age_text);
        final TextView wantText = (TextView) rootView.findViewById(R.id.want_info);
        final TextView aboutText = (TextView) rootView.findViewById(R.id.about_user_info);

        final ImageView pictureImage = (ImageView) rootView.findViewById(R.id.picture_image);

        pictureImage.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(final View v, MotionEvent event) {
                if (event.getEventTime() > SWIPE_MIN_TIME) {
                    if (event.getX() - x1 > SWIPE_MIN_DISTANCE && currImage < ProfileBuilder.MAX_NUM_PHOTOS - 1) {
                        currImage += 1;
                        swipPhoto(v);
                    }
                    else if (x1 - event.getX() > SWIPE_MIN_DISTANCE && currImage > 0){
                        currImage -= 1;
                        swipPhoto(v);
                    }
                }

                pictureImage.getParent().requestDisallowInterceptTouchEvent(true);
                SWIPE_MIN_TIME = event.getEventTime() + 120;
                x1 = event.getX();
                return true;
            }
        });

        userId = getArguments().getString(KEY_USER_ID);

        DatabaseHelper.getUserById(userId, new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {
                if (e != null || parseUser == null) {
                    Log.e(getClass().getName(), "Got a null user for a profile");
                    return;
                }

                final ParseUser user = parseUser;

                nameText.setText(user.getString(ProfileBuilder.PROFILE_KEY_NAME) + ",");
                ageText.setText(PrettyTime.getAgeFromBirthDate(user.getDate(ProfileBuilder.PROFILE_KEY_BIRTHDATE)) + "");
                wantText.setText(user.getString(ProfileBuilder.PROFILE_KEY_WANT) + ",");
                aboutText.setText(user.getString(ProfileBuilder.PROFILE_KEY_ABOUT) + ",");
            }
        });

        swipPhoto(pictureImage);

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

    public void swipPhoto(View v) {
        final ImageView pictureImage = (ImageView) v.findViewById(R.id.picture_image);

        DatabaseHelper.getUserById(userId, new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {
                if (e != null || parseUser == null) {
                    Log.e(getClass().getName(), "Got a null user for a profile");
                    return;
                }

                final ParseUser user = parseUser;
                List<PhotoItem> photoItems = user.getList(ProfileBuilder.PROFILE_KEY_PHOTOS);
                if (photoItems != null && photoItems.size() > 0 && photoItems.get(0) != null && photoItems.get(0) != JSONObject.NULL) {
                    // now fill the image
                    if (photoItems.get(currImage) != null && photoItems.get(currImage) != JSONObject.NULL) {
                        PhotoItem photo = photoItems.get(currImage);
                        photo.fetchIfNeededInBackground(new GetCallback<PhotoItem>() {
                            @Override
                            public void done(PhotoItem photoItem, com.parse.ParseException e) {
                                PhotoFile photoFile = photoItem.getPhotoFiles().get(0);
                                if (getActivity() != null)
                                    Picasso.with(getActivity().getApplicationContext()).load(photoFile.url).fit().centerCrop().into(pictureImage);
                            }
                        });
                    }
                } else {
                    Toast.makeText(getActivity(), "User account has become corrupted. Please contact us for assistance.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}