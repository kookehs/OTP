package com.mrcornman.otp.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mrcornman.otp.R;
import com.mrcornman.otp.models.PhotoFile;
import com.mrcornman.otp.models.PhotoItem;
import com.mrcornman.otp.utils.DatabaseHelper;
import com.mrcornman.otp.utils.PrettyTime;
import com.mrcornman.otp.utils.ProfileBuilder;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ThemProfileFragment extends Fragment {

    private final static String KEY_USER_ID = "user_id";

    private String userId;

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

        final FrameLayout pictureContainer = (FrameLayout) rootView.findViewById(R.id.picture_container);
        final ImageView pictureImage = (ImageView) rootView.findViewById(R.id.picture_image);

        pictureContainer.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                final int pictureWidth = pictureContainer.getWidth();
                pictureContainer.setLayoutParams(new RelativeLayout.LayoutParams(pictureWidth, pictureWidth));
            }
        });

        userId = getArguments().getString(KEY_USER_ID);

        DatabaseHelper.getUserById(userId, new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {
                if(e != null || parseUser == null) {
                    Log.e(getClass().getName(), "Got a null user for a profile");
                    return;
                }

                final ParseUser user = parseUser;

                nameText.setText(user.getString(ProfileBuilder.PROFILE_KEY_NAME) + ",");
                ageText.setText(PrettyTime.getAgeFromBirthDate(user.getDate(ProfileBuilder.PROFILE_KEY_BIRTHDATE)) + "");

                List<PhotoItem> photoItems = user.getList(ProfileBuilder.PROFILE_KEY_PHOTOS);
                if(photoItems != null && photoItems.size() > 0) {
                    PhotoItem mainPhoto = photoItems.get(0);
                    mainPhoto.fetchIfNeededInBackground(new GetCallback<PhotoItem>() {
                        @Override
                        public void done(PhotoItem photoItem, ParseException e) {
                            PhotoFile mainFile = photoItem.getPhotoFiles().get(0);
                            if (getActivity() != null)
                                Picasso.with(getActivity().getApplicationContext()).load(mainFile.url).fit().centerCrop().into(pictureImage);
                        }
                    });
                }
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