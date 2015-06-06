package com.mrcornman.otp.fragments;

import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.mrcornman.otp.R;
import com.mrcornman.otp.adapters.pagers.ThemProfilePagerAdapter;
import com.mrcornman.otp.utils.DatabaseHelper;
import com.mrcornman.otp.utils.PrettyTime;
import com.mrcornman.otp.utils.ProfileBuilder;
import com.mrcornman.otp.views.CirclePageIndicator;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class ThemProfileFragment extends Fragment {

    private final static String KEY_USER_ID = "user_id";

    private String userId;
    private ThemProfilePagerAdapter mPagerAdapter;
    private ViewPager mViewPager;

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

        userId = getArguments().getString(KEY_USER_ID);

        final TextView nameText = (TextView) rootView.findViewById(R.id.name_text);
        final TextView ageText = (TextView) rootView.findViewById(R.id.age_text);
        final TextView wantText = (TextView) rootView.findViewById(R.id.want_info);
        final TextView aboutText = (TextView) rootView.findViewById(R.id.about_user_info);

        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        mPagerAdapter = new ThemProfilePagerAdapter(getActivity(), getChildFragmentManager(), userId, getActivity());
        mViewPager = (ViewPager) rootView.findViewById(R.id.pager);
        mViewPager.getLayoutParams().height = (height*3)/5;
        mViewPager.getLayoutParams().width = width;
        mViewPager.requestLayout();
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setOffscreenPageLimit(ThemProfilePagerAdapter.NUM_PAGES);
        final CirclePageIndicator circles = (CirclePageIndicator) rootView.findViewById(R.id.circles);
        circles.setViewPager(mViewPager);

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