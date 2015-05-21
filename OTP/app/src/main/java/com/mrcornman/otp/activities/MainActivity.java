package com.mrcornman.otp.activities;

import android.app.ProgressDialog;
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
import android.util.Log;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.mrcornman.otp.R;
import com.mrcornman.otp.adapters.MainPagerAdapter;
import com.mrcornman.otp.fragments.ClientListFragment;
import com.mrcornman.otp.fragments.MakerListFragment;
import com.mrcornman.otp.fragments.NavFragment;
import com.mrcornman.otp.services.MessageService;

public class MainActivity extends ActionBarActivity implements NavFragment.NavigationDrawerCallbacks, ClientListFragment.OnClientListInteractionListener, MakerListFragment.OnMakerListInteractionListener {

    /**
     * Navigation Identifiers
     */
    public static final int NAV_PROFILE = 0;
    public static final int NAV_PREFERENCES = 1;
    public static final int NAV_SETTINGS = 2;
    public static final int NAV_SHARE = 3;

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavFragment mNavFragment;

    private MainPagerAdapter mPagerAdapter;
    private ViewPager mViewPager;

    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTitle = getTitle();

        // Set up toolbar and tabs
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(mTitle);
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
                Log.i("MainActivity", "Page selected");
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        // Set up the drawer.
        mNavFragment = (NavFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mNavFragment.setUp((DrawerLayout) findViewById(R.id.drawer_layout));

        // start up progress dialog until MessageService is started
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading");
        progressDialog.setTitle("Please wait...");
        progressDialog.show();

        // init receiver for successful startup broadcast from MessageService
        BroadcastReceiver mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                boolean success = intent.getBooleanExtra("success", false);
                progressDialog.dismiss();

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
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {

        switch(position) {
            case NAV_PROFILE:
                // start profile activity
                break;
            case NAV_PREFERENCES:
                // start prefs activity
                break;
            case NAV_SETTINGS:
                // start settings activity
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