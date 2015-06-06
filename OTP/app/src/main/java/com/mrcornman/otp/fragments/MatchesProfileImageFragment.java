package com.mrcornman.otp.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.mrcornman.otp.R;
import com.mrcornman.otp.models.gson.PhotoFile;
import com.mrcornman.otp.models.models.PhotoItem;
import com.mrcornman.otp.utils.DatabaseHelper;
import com.mrcornman.otp.utils.ProfileBuilder;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by Aprusa on 6/3/2015.
 */
public class MatchesProfileImageFragment extends Fragment {

    private final static String KEY_USER_ID = "user_id";
    private final static String KEY_USER_IMAGES = "images";

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static MatchesProfileImageFragment newInstance(String userId, int imageIndex) {
        MatchesProfileImageFragment fragment = new MatchesProfileImageFragment();

        // arguments
        Bundle arguments = new Bundle();
        arguments.putString(KEY_USER_ID, userId);
        arguments.putInt(KEY_USER_IMAGES, imageIndex);
        fragment.setArguments(arguments);

        return fragment;
    }

    public MatchesProfileImageFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile_photo_scroll, container, false);

        final ImageView pictureImage = (ImageView) rootView.findViewById(R.id.picture_image);

        final String userId = getArguments().getString(KEY_USER_ID);
        final int userImageIndex = getArguments().getInt(KEY_USER_IMAGES);

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

                        if (photoItems.get(userImageIndex) != null && photoItems.get(userImageIndex) != JSONObject.NULL) {
                            PhotoItem photo = photoItems.get(userImageIndex);
                            photo.fetchIfNeededInBackground(new GetCallback<PhotoItem>() {
                                @Override
                                public void done(PhotoItem photoItem, com.parse.ParseException e) {
                                    PhotoFile photoFile = photoItem.getPhotoFiles().get(0);
                                    if (getActivity() != null) {
                                        Picasso.with(getActivity().getApplicationContext()).load(photoFile.url).fit().centerCrop().into(pictureImage);
                                    }
                                }
                            });
                        }
                } else {
                    Toast.makeText(getActivity(), "User account has become corrupted. Please contact us for assistance.", Toast.LENGTH_LONG).show();
                }
            }
        });

        return rootView;

    }
}
