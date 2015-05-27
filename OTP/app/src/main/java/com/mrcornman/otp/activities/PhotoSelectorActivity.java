package com.mrcornman.otp.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;

import com.mrcornman.otp.R;
import com.mrcornman.otp.fragments.AlbumListFragment;
import com.mrcornman.otp.fragments.PhotoGalleryFragment;


public class PhotoSelectorActivity extends ActionBarActivity implements FragmentManager.OnBackStackChangedListener,
        AlbumListFragment.OnAlbumListInteractionListener, PhotoGalleryFragment.OnPhotoGalleryInteractionListener {

    private final static int PAGE_ALBUMS = 0;
    private final static int PAGE_PHOTO_GALLERY = 1;

    private CharSequence mTitle;

    private int currPageIndex = 0;
    private int slotIndex;
    private String albumId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_selector);

        Intent intent = getIntent();
        slotIndex = intent.getIntExtra("slot_index", -1);

        // Set up toolbar and tabs
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.mipmap.ic_nav_up);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        getSupportFragmentManager().addOnBackStackChangedListener(this);

        initPage(PAGE_ALBUMS);
    }

    @Override
    public void onBackStackChanged() {
        int backStackCount = getSupportFragmentManager().getBackStackEntryCount();
        if(backStackCount == 0)
            NavUtils.navigateUpFromSameTask(this);
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
    public void onRequestPhoto(String url) {
        Intent intent = new Intent();
        intent.putExtra("photo_url", url);
        intent.putExtra("slot_index", slotIndex);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    @Override
    public void onRequestPhotoGallery(String chosenAlbumId) {
        albumId = chosenAlbumId;
        initPage(PAGE_PHOTO_GALLERY);
    }

    private void initPage(int newPageIndex) {
        currPageIndex = newPageIndex;

        updateTitle();

        FragmentManager fragmentManager = getSupportFragmentManager();

        switch (currPageIndex) {
            case PAGE_ALBUMS:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, AlbumListFragment.newInstance())
                        .addToBackStack("Albums")
                        .commit();
                break;
            case PAGE_PHOTO_GALLERY:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, PhotoGalleryFragment.newInstance(albumId))
                        .addToBackStack("Photos")
                        .commit();
                break;
        }
    }

    private void updateTitle() {
        switch(currPageIndex) {
            case PAGE_ALBUMS:
                mTitle = "Albums";
                break;
            case PAGE_PHOTO_GALLERY:
                mTitle = "Photos";
                break;
        }

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(mTitle);
    }
}