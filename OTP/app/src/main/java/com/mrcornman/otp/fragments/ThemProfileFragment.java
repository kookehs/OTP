package com.mrcornman.otp.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.mrcornman.otp.R;
import com.mrcornman.otp.adapters.pagers.CarouselPagerAdapter;
import com.mrcornman.otp.models.gson.PhotoFile;
import com.mrcornman.otp.models.models.PhotoItem;
import com.mrcornman.otp.utils.DatabaseHelper;
import com.mrcornman.otp.utils.PrettyTime;
import com.mrcornman.otp.utils.ProfileBuilder;
import com.mrcornman.otp.views.CirclePageIndicator;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import org.json.JSONObject;

import java.util.List;

public class ThemProfileFragment extends Fragment {

    private final static String KEY_USER_ID = "user_id";

    private String userId;
    private CarouselPagerAdapter mPagerAdapter;
    private ViewPager mViewPager;

    private int numLoadedImages = 0;

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
        final View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        userId = getArguments().getString(KEY_USER_ID);

        final TextView nameText = (TextView) rootView.findViewById(R.id.name_text);
        final TextView ageText = (TextView) rootView.findViewById(R.id.age_text);
        final TextView wantText = (TextView) rootView.findViewById(R.id.want_value_text);
        final TextView aboutText = (TextView) rootView.findViewById(R.id.about_value_text);

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
                wantText.setText(user.getString(ProfileBuilder.PROFILE_KEY_WANT));
                aboutText.setText(user.getString(ProfileBuilder.PROFILE_KEY_ABOUT));

                List<PhotoItem> photoItems = user.getList(ProfileBuilder.PROFILE_KEY_PHOTOS);

                numLoadedImages = 0;

                int count = 0;
                for(int i = 0; i < photoItems.size(); i++) if(photoItems.get(i) != null && photoItems.get(i) != JSONObject.NULL) count++;
                final int loadedImagesThreshold = count;

                if(loadedImagesThreshold > 0) {
                    final String[] loadedUrls = new String[loadedImagesThreshold];

                    if (photoItems != null && photoItems.size() == ProfileBuilder.MAX_NUM_PHOTOS) {
                        for (int i = 0; i < ProfileBuilder.MAX_NUM_PHOTOS; i++) {
                            final int index = i;

                            // now fill the image
                            if (photoItems.get(i) != null && photoItems.get(i) != JSONObject.NULL) {
                                final PhotoItem photo = photoItems.get(i);
                                photo.fetchIfNeededInBackground(new GetCallback<PhotoItem>() {
                                    @Override
                                    public void done(PhotoItem photoItem, com.parse.ParseException e) {
                                        if (photoItem != null && e == null) {
                                            PhotoFile photoFile = photoItem.getPhotoFiles().get(0);

                                            loadedUrls[index] = photoFile.url;
                                            numLoadedImages++;
                                            if (numLoadedImages >= loadedImagesThreshold) {
                                                mPagerAdapter = new CarouselPagerAdapter(getActivity(), getChildFragmentManager(), loadedUrls);
                                                mViewPager = (ViewPager) rootView.findViewById(R.id.pager);
                                                mViewPager.setAdapter(mPagerAdapter);
                                                final CirclePageIndicator circles = (CirclePageIndicator) rootView.findViewById(R.id.circles);
                                                circles.setViewPager(mViewPager);
                                            }
                                        }
                                    }
                                });
                            }
                        }
                    } else {
                        Toast.makeText(getActivity(), "Your account has become corrupted. Please contact us for assistance.", Toast.LENGTH_LONG).show();
                    }
                } else {
                    mPagerAdapter = new CarouselPagerAdapter(getActivity(), getChildFragmentManager(), new String[1]);
                    mViewPager = (ViewPager) rootView.findViewById(R.id.pager);
                    mViewPager.setAdapter(mPagerAdapter);
                    final CirclePageIndicator circles = (CirclePageIndicator) rootView.findViewById(R.id.circles);
                    circles.setViewPager(mViewPager);
                }
            }
        });

        return rootView;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                View view = getActivity().getCurrentFocus();
                if (view != null) {
                    InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }

                NavUtils.navigateUpFromSameTask(getActivity());
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}