package com.mrcornman.otp.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.mrcornman.otp.R;
import com.mrcornman.otp.services.MessageService;
import com.mrcornman.otp.utils.ProfileBuilder;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
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
        final Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);

        final Intent serviceIntent = new Intent(getApplicationContext(), MessageService.class);
        getApplicationContext().startService(serviceIntent);

        //finish();
    }
}