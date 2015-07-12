package com.mrcornman.otp.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mrcornman.otp.R;
import com.mrcornman.otp.utils.ProfileBuilder;
import com.mrcornman.otp.views.RangeSeekBar;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

/**
 * Created by Jonathan on 5/9/2015.
 */
public class UserPrefsActivity extends ActionBarActivity implements RangeSeekBar.OnRangeSeekBarChangeListener<Integer>, SeekBar.OnSeekBarChangeListener {

    private String mTitle = "Preferences";

    // Views
    private TextView ageRangeSub;
    private RangeSeekBar ageRangeSeekBar;

    private TextView searchDistanceSub;
    private SeekBar searchDistanceSeekBar;

    // Calculated defaults
    private int absSearchDistanceMin;
    private int absSearchDistanceMax;

    // Meta
    private boolean shouldSave;

    // Data
    private int ageRangeMin;
    private int ageRangeMax;
    private int searchDistance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_prefs);

        absSearchDistanceMin = getResources().getInteger(R.integer.pref_search_distance_min);
        absSearchDistanceMax = getResources().getInteger(R.integer.pref_search_distance_max);

        // Set up toolbar_generic and tabs
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.mipmap.ic_nav_up);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(mTitle);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        ParseUser user = ParseUser.getCurrentUser();
        shouldSave = false;

        // init age range views
        ageRangeSub = (TextView) findViewById(R.id.age_range_sub);
        ageRangeSeekBar = (RangeSeekBar) findViewById(R.id.age_range_seekbar);

        ageRangeSeekBar.setNotifyWhileDragging(true);
        ageRangeSeekBar.setOnRangeSeekBarChangeListener(this);

        // fill age range views
        ageRangeMin = user.getInt(ProfileBuilder.PROFILE_KEY_WANTED_AGE_MIN);
        ageRangeMax = user.getInt(ProfileBuilder.PROFILE_KEY_WANTED_AGE_MAX);

        ageRangeSeekBar.setSelectedMinValue(ageRangeMin);
        ageRangeSeekBar.setSelectedMaxValue(ageRangeMax);

        ageRangeSub.setText(ageRangeMin + " - " + ageRangeMax);

        // init search distance views
        searchDistanceSub = (TextView) findViewById(R.id.search_distance_sub);
        searchDistanceSeekBar = (SeekBar) findViewById(R.id.search_distance_seekbar);

        searchDistanceSeekBar.setOnSeekBarChangeListener(this);

        // fill search distance views
        searchDistance = user.getInt(ProfileBuilder.PROFILE_KEY_WANTED_DISTANCE_MAX);
        int searchDistanceProgress = (int)((searchDistance - absSearchDistanceMin) / (absSearchDistanceMax - absSearchDistanceMin) * 100f);

        searchDistanceSub.setText(searchDistance + "mi");
        searchDistanceSeekBar.setProgress(searchDistanceProgress);
    }

    @Override
    public void onPause() {
        savePrefs();

        super.onPause();
    }

    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if(!shouldSave) shouldSave = true;

        float normalizedProgress = (float)progress / 100f;

        searchDistance = (int)((absSearchDistanceMax - absSearchDistanceMin) * normalizedProgress + absSearchDistanceMin);

        searchDistanceSub.setText(searchDistance + "mi");
    }

    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    public void onRangeSeekBarValuesChanged(RangeSeekBar rangeSeekBar, Integer minValue, Integer maxValue) {
        if(!shouldSave) shouldSave = true;

        ageRangeMin = minValue;
        ageRangeMax = maxValue;

        ageRangeSub.setText(ageRangeMin + " - " + ageRangeMax);
    }

    private void savePrefs() {
        ParseUser user = ParseUser.getCurrentUser();

        user.put(ProfileBuilder.PROFILE_KEY_WANTED_DISTANCE_MAX, searchDistance);

        user.put(ProfileBuilder.PROFILE_KEY_WANTED_AGE_MIN, ageRangeMin);
        user.put(ProfileBuilder.PROFILE_KEY_WANTED_AGE_MAX, ageRangeMax);

        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e != null) {
                    Toast.makeText(UserPrefsActivity.this, "There was a problem saving preferences. Please try again.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(UserPrefsActivity.this, "Preferences saved!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}