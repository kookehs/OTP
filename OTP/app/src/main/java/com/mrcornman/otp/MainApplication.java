package com.mrcornman.otp;

import android.app.Application;

import com.facebook.FacebookSdk;
import com.mrcornman.otp.models.MatchItem;
import com.parse.Parse;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;

/**
 * Created by Jonathan on 5/9/2015.
 */
public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // parse
        ParseObject.registerSubclass(MatchItem.class);
        Parse.initialize(this, getString(R.string.parse_app_id), getString(R.string.parse_client_key));

        // facebook
        FacebookSdk.sdkInitialize(getApplicationContext());
        ParseFacebookUtils.initialize(getApplicationContext());
    }
}