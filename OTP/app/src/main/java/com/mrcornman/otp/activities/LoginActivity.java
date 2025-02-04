package com.mrcornman.otp.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.mrcornman.otp.R;
import com.mrcornman.otp.utils.LocationHandler;
import com.mrcornman.otp.utils.ProfileBuilder;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseGeoPoint;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Jonathan on 5/9/2015.
 */
public class LoginActivity extends Activity {

    private static final List<String> permissions = Collections.unmodifiableList(
            new ArrayList<String>() {{
                add("public_profile");
                add("user_friends");
                add("email");
                add("user_birthday");
                add("user_likes");
                add("user_location");
                add("user_photos");
                add("user_relationship_details");
            }}
    );

    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final ProgressBar loginProgress = (ProgressBar) findViewById(R.id.login_progress);

        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null && currentUser.getUsername() != null) {
            loginProgress.setVisibility(View.VISIBLE);
            onSuccessfulLogin();
            return;
        }

        loginButton = (Button) findViewById(R.id.login_button);
        final LoginActivity context = this;
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ParseFacebookUtils.logInWithReadPermissionsInBackground(LoginActivity.this, permissions, new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException err) {
                        if (err != null) {
                            Log.e("LoginActivity", "There was an error logging in through Facebook.");
                            return;
                        }

                        if (user == null) {
                            Log.d("LoginActivity", "The user cancelled the Facebook login.");
                        } else if (user.isNew()) {
                            loginProgress.setVisibility(View.VISIBLE);

                            ProfileBuilder.buildCurrentProfile(getApplicationContext(), new ProfileBuilder.BuildProfileCallback() {
                                @Override
                                public void done(final ParseUser user, Object err) {
                                    if (err != null) {
                                        Log.e("LoginActivity", "Creating profile from Facebook failed: " + err.toString());
                                        ParseFacebookUtils.unlinkInBackground(user, new SaveCallback() {
                                            @Override
                                            public void done(ParseException e) {
                                                user.deleteInBackground();
                                            }
                                        });
                                        return;
                                    }

                                    onSuccessfulLogin();
                                }
                            });
                        } else {
                            loginProgress.setVisibility(View.VISIBLE);

                            onSuccessfulLogin();
                        }
                    }
                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }

    private void onSuccessfulLogin() {
        final ParseUser user = ParseUser.getCurrentUser();

        ParseGeoPoint quickLocation = LocationHandler.getQuickLocation(getApplicationContext());

        if (quickLocation != null) {
            user.put(ProfileBuilder.PROFILE_KEY_LOCATION, quickLocation);
            user.saveInBackground();
        }

        LocationHandler.requestLocation(getApplicationContext(), new LocationHandler.OnLocationReceivedListener() {
            @Override
            public void done(ParseGeoPoint geoPoint, Exception e) {
                if (e == null && geoPoint != null) {
                    user.put(ProfileBuilder.PROFILE_KEY_LOCATION, geoPoint);
                    user.saveInBackground();
                }
            }
        });

        if(user.getDate(ProfileBuilder.PROFILE_KEY_BIRTHDATE) != null) {
            if(!user.isNew()) {
                final Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            } else {
                final Intent intent = new Intent(getApplicationContext(), CompleteProfileActivity.class);
                startActivity(intent);
            }
        } else {
            final Intent intent = new Intent(getApplicationContext(), MissingInfoActivity.class);
            startActivity(intent);
        }

        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        String currUserId = installation.getString("userId");
        if(currUserId == null || !currUserId.equals(user.getObjectId())) {
            installation.put("userId", user.getObjectId());
            installation.saveInBackground();
        }

        finish();
    }
}