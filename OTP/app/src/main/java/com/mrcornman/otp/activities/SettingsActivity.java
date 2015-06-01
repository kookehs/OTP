package com.mrcornman.otp.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.support.v4.preference.PreferenceFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;

import com.mrcornman.otp.R;

public class SettingsActivity extends ActionBarActivity {

    public final static String PREF_NOTIFICATIONS_NEW_MATCH = "notifications_checkbox_new_match";
    public final static String PREF_NOTIFICATIONS_MATCH_LIKED = "notifications_checkbox_match_liked";
    public final static String PREF_NOTIFICATIONS_MESSAGES = "notifications_checkbox_messages";

    private String mTitle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mTitle = "Settings";

        // Set up toolbar_generic and tabs
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.mipmap.ic_nav_up);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(mTitle);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new SettingsPreferenceFragment()).commit();
    }

    public static class SettingsPreferenceFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.prefs_settings);

            onSharedPreferenceChanged(null, "");
        }

        @Override
        public void onResume() {
            super.onResume();
            // Set up a listener whenever a key changes
            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onPause() {
            super.onPause();
            // Set up a listener whenever a key changes
            getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            // just update all
            CheckBoxPreference messageNotificationsPref = (CheckBoxPreference) findPreference(PREF_NOTIFICATIONS_MESSAGES);
            //messageNotificationsPref.setSummary(sharedPreferences.getBoolean(PREF_NOTIFICATIONS_MESSAGES, false));
        }
    }
}
