package com.mrcornman.otp.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.mrcornman.otp.R;
import com.mrcornman.otp.adapters.pagers.CompleteProfilePagerAdapter;
import com.mrcornman.otp.fragments.CompleteProfileAboutFragment;
import com.mrcornman.otp.fragments.CompleteProfileArrivalFragment;
import com.mrcornman.otp.fragments.CompleteProfileWantFragment;
import com.mrcornman.otp.utils.ProfileBuilder;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

/**
 * Created by Jonathan on 5/9/2015.
 */
public class CompleteProfileActivity extends ActionBarActivity
        implements CompleteProfileArrivalFragment.OnArrivalInteractionListener, CompleteProfileAboutFragment.OnAboutInteractionListener, CompleteProfileWantFragment.OnWantInteractionListener {

    private CompleteProfilePagerAdapter completeProfilePagerAdapter;
    private ViewPager mViewPager;

    private ProgressBar mProgressBar;

    private String mAboutStr = null;
    private String mWantStr = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_profile);

        completeProfilePagerAdapter = new CompleteProfilePagerAdapter(this, getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(completeProfilePagerAdapter);
        mViewPager.setOffscreenPageLimit(CompleteProfilePagerAdapter.NUM_PAGES);

        mProgressBar = (ProgressBar) findViewById(R.id.complete_profile_progress);
        mProgressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onArrivalSubmit() {
        mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1);
    }

    @Override
    public void onAboutSubmit(String aboutStr) {
        mAboutStr = aboutStr;
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
        mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1);
    }

    @Override
    public void onWantSubmit(String wantStr) {
        mWantStr = wantStr;

        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }

        mProgressBar.setVisibility(View.VISIBLE);

        ParseUser user = ParseUser.getCurrentUser();
        user.put(ProfileBuilder.PROFILE_KEY_ABOUT, mAboutStr);
        user.put(ProfileBuilder.PROFILE_KEY_WANT, mWantStr);
        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e != null) {
                    Toast.makeText(CompleteProfileActivity.this, "There was a problem saving your information. Please try again.", Toast.LENGTH_LONG).show();
                } else {
                    final Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);

                    finish();
                }

                mProgressBar.setVisibility(View.INVISIBLE);
            }
        });
    }
}