package com.mrcornman.otp.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.mrcornman.otp.R;
import com.mrcornman.otp.utils.PrettyTime;
import com.mrcornman.otp.utils.ProfileBuilder;
import com.parse.ParseUser;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Jonathan on 5/9/2015.
 */
public class MissingInfoActivity extends Activity implements DatePickerDialog.OnDateSetListener {

    private TextView dateTextView;
    private Button submitButton;

    private Date birthDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_missing_info);

        birthDate = new Date();

        dateTextView = (TextView) findViewById(R.id.birthdate_text);
        dateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar now = Calendar.getInstance();
                now.setTime(birthDate);
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        MissingInfoActivity.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.show(getFragmentManager(), "Select Birthday");
            }
        });

        submitButton = (Button) findViewById(R.id.submit_button);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSubmit();
            }
        });
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int month, int day) {
        Calendar date = Calendar.getInstance();
        date.set(year, month, day);
        birthDate = date.getTime();
        dateTextView.setText((month + 1) + "/" + day + "/" + year);
    }

    private void onSubmit() {
        if(PrettyTime.getAgeFromBirthDate(birthDate) >= 13) {
            ParseUser user = ParseUser.getCurrentUser();
            user.put(ProfileBuilder.PROFILE_KEY_BIRTHDATE, birthDate);
            user.saveInBackground();

            final Intent intent = new Intent(getApplicationContext(), CompleteProfileActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, getString(R.string.missing_birthdate_under_threshold_warning), Toast.LENGTH_LONG).show();
        }
    }
}