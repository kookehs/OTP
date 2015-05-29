package com.mrcornman.otp.activities;

import android.os.Bundle;
import android.preference.Preference;
import android.support.v4.preference.PreferenceFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;

import com.mrcornman.otp.R;

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

        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new UserPrefsPreferenceFragment()).commit();
    }

    public static class UserPrefsPreferenceFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.prefs_user_prefs);

            onPreferenceChange(null, "");
        }

        @Override
        public void onResume() {
            super.onResume();

            //getPreferenceScreen().getPrefere
        }

        @Override
        public void onPause() {
            super.onPause();

            //getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object object) {
            // just update all
            //messageNotificationsPref.setSummary(sharedPreferences.getBoolean(PREF_NOTIFICATIONS_MESSAGES, false));
            return false;
        }
    }
}
