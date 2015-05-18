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
import com.mrcornman.otp.models.PhotoFile;
import com.mrcornman.otp.models.PhotoItem;
import com.mrcornman.otp.utils.PrettyTime;
import com.mrcornman.otp.utils.ProfileBuilder;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ProfileFragment extends Fragment {


    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();

        return fragment;
    }

    public ProfileFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        TextView nameText = (TextView) rootView.findViewById(R.id.name_text);
        TextView ageText = (TextView) rootView.findViewById(R.id.age_text);

        final FrameLayout pictureContainer = (FrameLayout) rootView.findViewById(R.id.picture_container);
        final ImageView pictureImage = (ImageView) rootView.findViewById(R.id.picture_image);

        final ParseUser user = ParseUser.getCurrentUser();

        nameText.setText(user.getString(ProfileBuilder.PROFILE_KEY_NAME));
        ageText.setText(PrettyTime.getAgeFromBirthDate(user.getDate(ProfileBuilder.PROFILE_KEY_BIRTHDATE)) + "");

        // need this to get the finalized width of the framelayout after the match_parent width is calculated
        pictureContainer.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                final int pictureWidth = pictureContainer.getWidth();
                pictureContainer.setLayoutParams(new RelativeLayout.LayoutParams(pictureWidth, pictureWidth));

                List<PhotoItem> photoItems = user.getList(ProfileBuilder.PROFILE_KEY_PHOTOS);
                PhotoItem mainPhoto = photoItems.get(0);
                mainPhoto.fetchIfNeededInBackground(new GetCallback<PhotoItem>() {
                    @Override
                    public void done(PhotoItem photoItem, ParseException e) {
                        PhotoFile mainFile = photoItem.getPhotoFiles().get(0);
                        Picasso.with(getActivity().getApplicationContext()).load(mainFile.url).fit().centerCrop().into(pictureImage);
                    }
                });
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