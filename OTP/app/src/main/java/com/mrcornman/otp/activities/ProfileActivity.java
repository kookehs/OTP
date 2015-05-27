package com.mrcornman.otp.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;

import com.mrcornman.otp.R;
import com.mrcornman.otp.fragments.EditProfileFragment;


public class ProfileActivity extends ActionBarActivity implements FragmentManager.OnBackStackChangedListener, EditProfileFragment.OnEditProfileInteractionListener {

    private final static int PAGE_EDIT_PROFILE = 0;

    private CharSequence mTitle;

    private int currPageIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Set up toolbar and tabs
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_ab_back_holo_light_am);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        getSupportFragmentManager().addOnBackStackChangedListener(this);

        initPage(PAGE_EDIT_PROFILE);
    }

    @Override
    public void onBackStackChanged() {
    }

    @Override
    public boolean onSupportNavigateUp() {
        int backStackCount = getSupportFragmentManager().getBackStackEntryCount();
        if(backStackCount == 1)
            return super.onSupportNavigateUp();

        getSupportFragmentManager().popBackStack();

        currPageIndex = Math.max(0, currPageIndex - 1);
        updateTitle();
        return true;
    }

    // fragment interaction overrides

    @Override
    public void onRequestPhotoSelect(int slotIndex) {
    }

    private void initPage(int newPageIndex) {
        currPageIndex = newPageIndex;

        updateTitle();

        FragmentManager fragmentManager = getSupportFragmentManager();

        switch (currPageIndex) {
            case PAGE_EDIT_PROFILE:
                 fragmentManager.beginTransaction()
                        .replace(R.id.container, EditProfileFragment.newInstance())
                        .addToBackStack("EditProfile")
                        .commit();
                break;
        }
    }

    private void updateTitle() {
        switch(currPageIndex) {
            case PAGE_EDIT_PROFILE:
                mTitle = "Profile";
                break;
        }

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(mTitle);
    }
}