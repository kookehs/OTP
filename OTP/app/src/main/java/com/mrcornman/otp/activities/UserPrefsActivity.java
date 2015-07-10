package com.mrcornman.otp.activities;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.mrcornman.otp.R;
import com.mrcornman.otp.utils.ProfileBuilder;
import com.mrcornman.otp.views.RangeSeekBarPreference;
import com.mrcornman.otp.views.SeekBarPreference;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class UserPrefsActivity extends ActionBarActivity {

    private String mTitle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_prefs);

        mTitle = "Preferences";

        // Set up toolbar_generic and tabs
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.mipmap.ic_nav_up);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(mTitle);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        getFragmentManager().beginTransaction().replace(R.id.content_frame, new UserPrefsPreferenceFragment()).commit();
    }

    public static class UserPrefsPreferenceFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {

        private final static String KEY_AGE_RANGE = "age_range_slider";
        private final static String KEY_SEARCH_DISTANCE = "search_distance_slider";

        private final static String SUMMARY_FORMAT_AGE_RANGE = "%d - %d";
        private final static String SUMMARY_FORMAT_SEARCH_DISTANCE = "%d";

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.prefs_user_prefs);

            ParseUser user = ParseUser.getCurrentUser();

            RangeSeekBarPreference ageRangePreference = (RangeSeekBarPreference) findPreference(KEY_AGE_RANGE);
            ageRangePreference.setMinValue(user.getInt(ProfileBuilder.PROFILE_KEY_WANTED_AGE_MIN));
            ageRangePreference.setMaxValue(user.getInt(ProfileBuilder.PROFILE_KEY_WANTED_AGE_MAX));
            ageRangePreference.setSummaryFormat(SUMMARY_FORMAT_AGE_RANGE);
            ageRangePreference.setOnPreferenceChangeListener(this);

            SeekBarPreference searchDistancePreference = (SeekBarPreference) findPreference(KEY_SEARCH_DISTANCE);
            searchDistancePreference.setMax(100);
            searchDistancePreference.setOnPreferenceChangeListener(this);
        }

        @Override
        public void onPause() {
            savePrefs();

            super.onPause();
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            switch (preference.getKey()) {
                case KEY_SEARCH_DISTANCE:
                    preference.setSummary(String.format(SUMMARY_FORMAT_SEARCH_DISTANCE, (int)newValue));
                    break;
            }

            return true;
        }

        private void savePrefs() {
            ParseUser user = ParseUser.getCurrentUser();

            SeekBarPreference searchDistancePref = (SeekBarPreference) findPreference(KEY_SEARCH_DISTANCE);
            int searchDistance = searchDistancePref.getProgress();
            user.put(ProfileBuilder.PROFILE_KEY_WANTED_DISTANCE_MAX, searchDistance);

            RangeSeekBarPreference ageRangePref = (RangeSeekBarPreference) findPreference(KEY_AGE_RANGE);
            int ageRangeMin = ageRangePref.getMinValue();
            int ageRangeMax = ageRangePref.getMaxValue();
            user.put(ProfileBuilder.PROFILE_KEY_WANTED_AGE_MIN, ageRangeMin);
            user.put(ProfileBuilder.PROFILE_KEY_WANTED_AGE_MAX, ageRangeMax);

            user.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if(e != null) {
                        Toast.makeText(getActivity().getApplicationContext(), "There was a problem saving preferences. Please try again.", Toast.LENGTH_LONG).show();
                        return;
                    }

                    if(getActivity() != null)
                        Toast.makeText(getActivity().getApplicationContext(), "Preferences saved!", Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}