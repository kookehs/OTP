package com.mrcornman.otp.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.mrcornman.otp.R;
import com.mrcornman.otp.services.MessageService;
import com.mrcornman.otp.utils.ProfileBuilder;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

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

        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            onSuccessfulLogin(currentUser);
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
                            ProfileBuilder.buildCurrentProfile(getApplicationContext(), new ProfileBuilder.BuildProfileCallback() {
                                @Override
                                public void done(ParseUser user, Object err) {
                                    if (err != null) {
                                        Log.e("LoginActivity", "Creating profile from Facebook failed.");
                                        return;
                                    }

                                    onSuccessfulLogin(user);
                                }
                            });
                        } else {
                            onSuccessfulLogin(user);
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

    private void onSuccessfulLogin(ParseUser user) {
        ProfileBuilder.buildCurrentProfile(getApplicationContext(), new ProfileBuilder.BuildProfileCallback() {
            @Override
            public void done(ParseUser user, Object err) {
                if(err != null) {
                    Toast.makeText(getApplicationContext(), "Creating profile from Facebook failed. Please try again.", Toast.LENGTH_LONG);
                    Log.e("LoginActivity", "Creating profile from Facebook failed.");
                    return;
                }

                final Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                final Intent serviceIntent = new Intent(getApplicationContext(), MessageService.class);

                startActivity(intent);
                startService(serviceIntent);

                finish();
            }
        });
    }

    @Override
    public void onDestroy() {
        stopService(new Intent(this, MessageService.class));

        super.onDestroy();
    }
}
