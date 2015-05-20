package com.mrcornman.otp.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.astuetz.PagerSlidingTabStrip;
import com.mrcornman.otp.R;
import com.mrcornman.otp.adapters.ClientMatchPagerAdapter;
import com.mrcornman.otp.utils.DatabaseHelper;
import com.mrcornman.otp.utils.ProfileBuilder;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class ClientMatchActivity extends ActionBarActivity {

    private ClientMatchPagerAdapter mPagerAdapter;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_match);

        Intent intent = getIntent();
        String otherId = intent.getStringExtra("other_id");

        // Set up toolbar and tabs
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mPagerAdapter = new ClientMatchPagerAdapter(this, getSupportFragmentManager(), otherId);
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setOffscreenPageLimit(ClientMatchPagerAdapter.NUM_PAGES);

        final PagerSlidingTabStrip tabStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabStrip.setViewPager(mViewPager);

        // populate toolbar with header about recipient
        DatabaseHelper.getUserById(otherId, new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {
                if (e != null) {
                    Log.e("ClientMatchActivity", "There was a problem accessing a recipient user.");
                    return;
                }

                ActionBar actionBar = getSupportActionBar();
                actionBar.setTitle(parseUser.getString(ProfileBuilder.PROFILE_KEY_NAME));
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setHomeButtonEnabled(true);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                View view = this.getCurrentFocus();
                if (view != null) {
                    InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }

                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}