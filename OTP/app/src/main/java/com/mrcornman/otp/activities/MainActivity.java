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
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.mrcornman.otp.R;
import com.mrcornman.otp.adapters.MainPagerAdapter;
import com.mrcornman.otp.fragments.ClientListFragment;
import com.mrcornman.otp.fragments.NavFragment;
import com.mrcornman.otp.fragments.ProfileFragment;
import com.mrcornman.otp.fragments.SettingsFragment;

public class MainActivity extends ActionBarActivity implements NavFragment.NavigationDrawerCallbacks, ClientListFragment.ClientListInteractionListener {

    /**
     * Navigation Identifiers
     */
    public static final int NAV_PROFILE = 0;
    public static final int NAV_SETTINGS = 1;
    public static final int NAV_SHARE = 2;


    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavFragment mNavFragment;

    MainPagerAdapter mPagerAdapter;
    ViewPager mViewPager;

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

        mPagerAdapter = new MainPagerAdapter(this, getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mPagerAdapter);

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

        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver,
                new IntentFilter("com.mrcornman.otp.activities.MainActivity"));
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {

        switch(position) {
            case NAV_PROFILE:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, ProfileFragment.newInstance())
                                //.addToBackStack(null)
                        .commit();
                break;
            case NAV_SETTINGS:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, SettingsFragment.newInstance())
                                //.addToBackStack(null)
                        .commit();
                break;
            case NAV_SHARE:
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out this great new app that lets you match two people up! http://mrcornman.com/");
                shareIntent.setType("text/plain");
                startActivity(Intent.createChooser(shareIntent, "Share with"));
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.menu_main, menu);
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(mNavFragment.onOptionsItemSelected(item)) return true;

        return super.onOptionsItemSelected(item);
    }

    // fragment interface actions
    @Override
    public void onRequestOpenConversation(String recipientId) {
        openConversation(recipientId);
    }

    // helpers
    public void openConversation(String recipientId) {
        // TODO: Make sure the user exists when populating the list view in client list fragment so that there isn't the potential problem of the user not existing here
        Intent intent = new Intent(getApplicationContext(), MessagingActivity.class);
        intent.putExtra("recipient_id", recipientId);
        startActivity(intent);
        Log.i("MainActivity", "Beginning conversation with " + recipientId);
    }
}