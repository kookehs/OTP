package com.mrcornman.otp.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
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
import com.mrcornman.otp.utils.PrettyTime;
import com.mrcornman.otp.utils.ProfileBuilder;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Aprusa on 5/15/2015.
 */
public class EditPageFragment extends Fragment {

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static EditPageFragment newInstance() {
        EditPageFragment fragment = new EditPageFragment();

        return fragment;
    }

    public EditPageFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_setting, container, false);

        TextView ageText = (TextView) rootView.findViewById(R.id.age_text);

        final FrameLayout pictureContainer = (FrameLayout) rootView.findViewById(R.id.picture_container);
        final ImageView pictureImage = (ImageView) rootView.findViewById(R.id.picture_image);

        // need this to get the finalized width of the framelayout after the match_parent width is calculated
        pictureContainer.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int pictureWidth = pictureContainer.getWidth();
                Log.i("ProfileFragment", pictureWidth + "");
                pictureContainer.setLayoutParams(new RelativeLayout.LayoutParams(pictureWidth, pictureWidth));
            }
        });

        // NOTE: This is how you get the current user once they've logged in from Facebook
        ParseUser user = ParseUser.getCurrentUser();

        // and this is how you can get data from the user profile
        ageText.setText(PrettyTime.getAgeFromBirthDate(user.getDate(ProfileBuilder.PROFILE_KEY_BIRTHDATE)) + "");

        // and this is how you grab an image from the user profile and put it into image view
        List<PhotoItem> photoItems = user.getList(ProfileBuilder.PROFILE_KEY_PHOTOS);
        PhotoItem mainPhoto = photoItems.get(0);
        mainPhoto.fetchIfNeededInBackground(new GetCallback<PhotoItem>() {
            @Override
            public void done(PhotoItem photoItem, ParseException e) {
                PhotoFile mainFile = photoItem.getPhotoFiles().get(0);
                Picasso.with(getActivity().getApplicationContext()).load(mainFile.url).resize(pictureImage.getMeasuredWidth(), pictureImage.getMeasuredHeight()).centerCrop().into(pictureImage);
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
