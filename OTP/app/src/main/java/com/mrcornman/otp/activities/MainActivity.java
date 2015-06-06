package com.mrcornman.otp.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.mrcornman.otp.R;
import com.mrcornman.otp.adapters.pagers.MainPagerAdapter;
import com.mrcornman.otp.fragments.ClientListFragment;
import com.mrcornman.otp.fragments.GameFragment;
import com.mrcornman.otp.fragments.MakerListFragment;
import com.mrcornman.otp.fragments.NavFragment;
import com.mrcornman.otp.services.MessageService;
import com.mrcornman.otp.utils.ProfileBuilder;
import com.parse.ParseUser;

public class MainActivity extends ActionBarActivity implements NavFragment.NavigationDrawerCallbacks,
        GameFragment.OnGameInteractionListener, ClientListFragment.OnClientListInteractionListener, MakerListFragment.OnMakerListInteractionListener {

    /**
     * Navigation Identifiers
     */
    public static final int NAV_PROFILE = 0;
    public static final int NAV_PREFERENCES = 3;
    public static final int NAV_SETTINGS = 1;
    public static final int NAV_SHARE = 2;

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavFragment mNavFragment;

    private MainPagerAdapter mPagerAdapter;
    private ViewPager mViewPager;

    private TextView scoreText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up toolbar_generic and tabs
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.mipmap.ic_drawer);
        scoreText = (TextView) findViewById(R.id.score_text);
        scoreText.setText(ParseUser.getCurrentUser().getInt(ProfileBuilder.PROFILE_KEY_SCORE) + "");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        mPagerAdapter = new MainPagerAdapter(this, getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setOffscreenPageLimit(MainPagerAdapter.NUM_PAGES);

        final PagerSlidingTabStrip tabStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabStrip.setViewPager(mViewPager);

        // TODO: Use push notifications to update the list fragments not this shit
        tabStrip.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        // Set up the drawer.
        mNavFragment = (NavFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mNavFragment.setUp((DrawerLayout) findViewById(R.id.drawer_layout));

        // Start up MessageService
        final Intent intent = new Intent(getApplicationContext(), MessageService.class);
        startService(intent);

        // init receiver for successful startup broadcast from MessageService
        BroadcastReceiver mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                boolean success = intent.getBooleanExtra("success", false);

                if(!success) {
                    Toast.makeText(getApplicationContext(),
                            "There was a problem with the messaging service, please restart the app",
                            Toast.LENGTH_LONG).show();
                }
            }
        };

        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(mReceiver,
                new IntentFilter("com.mrcornman.otp.activities.MainActivity"));
    }

    @Override
    public void onDestroy() {
        getApplicationContext().stopService(new Intent(getApplicationContext(), MessageService.class));
        super.onDestroy();
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {

        switch(position) {
            case NAV_PROFILE:
                Intent profileIntent = new Intent(this, ProfileActivity.class);
                startActivity(profileIntent);
                break;
            case NAV_PREFERENCES:
                Intent prefsIntent = new Intent(this, UserPrefsActivity.class);
                startActivity(prefsIntent);
                break;
            case NAV_SETTINGS:
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                break;
            case NAV_SHARE:
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out this great new app that lets you match two people up! http://mrcornman.com/");
                shareIntent.setType("text/plain");
                startActivity(Intent.createChooser(shareIntent, "Share with"));
                break;
        }
    }

    // fragment interface actions
    @Override
    public void onCreateMatch() {
        mPagerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onRequestOpenClientMatch(String otherId) {
        // TODO: Make sure the user exists when populating the list view in client list fragment so that there isn't the potential problem of the user not existing here
        Intent intent = new Intent(getApplicationContext(), ClientMatchActivity.class);
        intent.putExtra("other_id", otherId);
        startActivity(intent);
    }

    @Override
    public void onRequestOpenMakerMatch(String matchId) {
        // TODO: Make sure the user exists when populating the list view in client list fragment so that there isn't the potential problem of the user not existing here
        Intent intent = new Intent(getApplicationContext(), MakerMatchActivity.class);
        intent.putExtra("match_id", matchId);
        startActivity(intent);
    }
}