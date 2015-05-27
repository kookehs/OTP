package com.mrcornman.otp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.astuetz.PagerSlidingTabStrip;
import com.mrcornman.otp.R;
import com.mrcornman.otp.adapters.pagers.MakerMatchPagerAdapter;
import com.mrcornman.otp.items.models.MatchItem;
import com.mrcornman.otp.utils.DatabaseHelper;
import com.mrcornman.otp.utils.ProfileBuilder;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class MakerMatchActivity extends ActionBarActivity {

    private MakerMatchPagerAdapter mPagerAdapter;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maker_match);

        Intent intent = getIntent();
        String matchId = intent.getStringExtra("match_id");

        // Set up toolbar and tabs
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.mipmap.ic_nav_up);
        setSupportActionBar(toolbar);

        mPagerAdapter = new MakerMatchPagerAdapter(this, getSupportFragmentManager(), matchId);
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setOffscreenPageLimit(MakerMatchPagerAdapter.NUM_PAGES);

        final PagerSlidingTabStrip tabStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabStrip.setViewPager(mViewPager);

        // populate toolbar with header about recipient
        DatabaseHelper.getMatchById(matchId, new GetCallback<MatchItem>() {
            @Override
            public void done(MatchItem matchItem, ParseException e) {
                if (e != null) {
                    Log.e("MakerMatchActivity", "There was a problem accessing a match.");
                    return;
                }

                final ActionBar actionBar = getSupportActionBar();
                actionBar.setTitle("");
                actionBar.setDisplayHomeAsUpEnabled(true);
                actionBar.setHomeButtonEnabled(true);

                DatabaseHelper.getUserById(matchItem.getFirstId(), new GetCallback<ParseUser>() {
                    @Override
                    public void done(ParseUser parseUser, ParseException e) {
                        actionBar.setTitle(parseUser.getString(ProfileBuilder.PROFILE_KEY_NAME) + ", " + actionBar.getTitle());
                    }
                });

                DatabaseHelper.getUserById(matchItem.getSecondId(), new GetCallback<ParseUser>() {
                    @Override
                    public void done(ParseUser parseUser, ParseException e) {
                        actionBar.setTitle(actionBar.getTitle() + parseUser.getString(ProfileBuilder.PROFILE_KEY_NAME));
                    }
                });
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}