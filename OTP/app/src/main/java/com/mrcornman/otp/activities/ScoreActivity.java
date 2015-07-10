package com.mrcornman.otp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.mrcornman.otp.R;
import com.mrcornman.otp.utils.ProfileBuilder;
import com.parse.ParseUser;

public class ScoreActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        //userStats = new CalculateUserStats(ScoreActivity.this);

        // Set up toolbar_generic and tabs
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.mipmap.ic_nav_up);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        //Set the usersScore to the chart
        TextView score = (TextView) findViewById(R.id.score_text);
        final String userScore = ParseUser.getCurrentUser().getInt(ProfileBuilder.PROFILE_KEY_SCORE) + "";
        score.setText(userScore);

        View scoreShare = (View) findViewById(R.id.score_rectangle);
        scoreShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out my score " + userScore + " on Seedr");
                shareIntent.setType("text/plain");
                startActivity(Intent.createChooser(shareIntent, "Share with"));
            }
        });

        Intent matchData = getIntent();

        //Set the total matches made to the chart
        TextView totalMatches = (TextView) findViewById(R.id.matches_total_text);
        totalMatches.setText("Total Seeds \n Made: " + matchData.getIntExtra("total_match", 0));
        //userStats.getTotalMatches(totalMatches);

        //Set the successful matches made to the chart
        TextView successfulMatches = (TextView) findViewById(R.id.matches_successful_text);
        successfulMatches.setText("Successful Seeds \n Made: " + matchData.getIntExtra("successful_match", 0));
        //userStats.getSuccessfulMatches(successfulMatches);

        //Set the popular matches made to the chart
        TextView popMatches = (TextView) findViewById(R.id.popular_text);
        popMatches.setText("Popular Seeds \n Made: " + matchData.getIntExtra("popular_match", 0));
        //userStats.getPopularMatches(popMatches);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_score, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public interface OnScoreInteractionListener {
        void onRequestOpenScore(int totalMatch, int successfulMatch, int popularMatch);
    }
}
