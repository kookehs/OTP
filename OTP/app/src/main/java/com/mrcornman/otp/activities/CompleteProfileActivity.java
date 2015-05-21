package com.mrcornman.otp.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.mrcornman.otp.R;
import com.parse.ParseFacebookUtils;

/**
 * Created by Jonathan on 5/9/2015.
 */
public class CompleteProfileActivity extends Activity {

    private Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_profile);

        submitButton = (Button) findViewById(R.id.login_button);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }

    private void onSubmit() {
        final Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }
}