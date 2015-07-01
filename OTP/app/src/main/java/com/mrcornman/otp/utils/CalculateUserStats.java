package com.mrcornman.otp.utils;

import android.content.Context;
import android.util.Log;
import android.widget.TextView;

import com.mrcornman.otp.models.models.MatchItem;
import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.Date;
import java.util.List;

/**
 * Created by Aprusa on 6/23/2015.
 */
public class CalculateUserStats {

    Context activityCont;

    //Item counters
    private int successCount = 0;
    private int popularCount = 0;
    private int topCount = 0;

    //item reminders
    private String lastKnownMatchId = "";
    private Date lastKnownTime = new Date();
    private int lastKnownSuccessCount = 0;
    private int lastKnownPopularCount = 0;
    private int lastKnownTopCount = 0;

    //test conditions and score start
    private boolean alreadyHit500 = false;
    private boolean first = true;
    private int score = ParseUser.getCurrentUser().getInt(ProfileBuilder.PROFILE_KEY_SCORE);

    //to get match info for score activity
    public int totalMatches = 0;
    public int popularMatches = 0;
    public int successfulMatches = 0;

    public CalculateUserStats(Context cont){
        activityCont = cont;
        score = 0; //delete this so the score won't be replaced at every start

        successCount = 0;
        popularCount = 0;

        ParseUser user = ParseUser.getCurrentUser();
        lastKnownTime = user.getUpdatedAt();
    }

    private void getSuccessfulMatches(List<MatchItem> matchItems, final TextView scoreText){
        if (matchItems.size() > lastKnownSuccessCount) {
            for (int i = lastKnownSuccessCount; i < matchItems.size(); i++) {
                MatchItem match = matchItems.get(i);
                // match num messages
                int messageCount = match.getNumMessages();
                if (messageCount > 0) {
                    successCount++;
                    score += 10; //calculate score to matches that have communicated
                }
            }

            saveScore(ParseUser.getCurrentUser(), score, scoreText);
            Log.i("CalculateUserStatsSucce", successCount + "");

            lastKnownSuccessCount = matchItems.size();
        }
    }

    private void getPopularMatches(List<MatchItem> matchItems, final TextView scoreText){
        if (matchItems.size() > lastKnownPopularCount) {
            for (int i = lastKnownPopularCount; i < matchItems.size(); i++) {
                MatchItem match = matchItems.get(i);
                // match num messages
                long likesCount = (long) match.getNumLikes();
                long dataBaseUserSize = 0;
                try {
                    dataBaseUserSize = (long) ParseUser.getQuery().find().size();
                } catch (Exception err) {
                    Log.i("CalculateUserStats", "There are no users in the database or the database is unreachable " + err);
                }

                long percentageOfUsers = Math.round(dataBaseUserSize * 0.3); //30% of user
                if (likesCount >= percentageOfUsers) {
                    popularCount++;

                    //calculate score to matches that have been liked by many others
                    score += 20;
                }
            }

            saveScore(ParseUser.getCurrentUser(), score, scoreText);
            Log.i("CalculateUserStatsPop", popularCount + "");

            lastKnownPopularCount = matchItems.size();
        }
    }

    private void getTopMatches(final List<MatchItem> matchItems, int limit, final TextView scoreText){
        //just top 2 for now but will be top 5
        DatabaseHelper.findTopMatches(limit, new FindCallback<MatchItem>() {
            @Override
            public void done(List<MatchItem> list, ParseException e) {
                if (e == null && matchItems.size() > lastKnownTopCount) {
                    for (int i = lastKnownTopCount; i < matchItems.size(); i++) {
                        MatchItem match = matchItems.get(i);
                        String matchId = match.getObjectId();
                        for (int j = 0; j < list.size() && j < 2; j++) {
                            MatchItem top = list.get(j);
                            if (top.getObjectId().equals(matchId)) {
                                topCount++;

                                //calculate score to matches that are from top 5
                                score += 40;
                                break;
                            }
                            //Log.i("CalculateScoreStatsTop", top.getObjectId());
                        }
                        //Log.i("CalculateScoreStatsYou", matchId);
                    }

                    saveScore(ParseUser.getCurrentUser(), score, scoreText);
                    Log.i("CalculateUserStatsTop", topCount + "");

                    lastKnownTopCount = matchItems.size();
                }
            }
        });
    }

    public void calculateScore(TextView v){
        final TextView scoreText = v;

        final ParseUser user = ParseUser.getCurrentUser();
        DatabaseHelper.findMakerMatches(user.getObjectId(), 20, new FunctionCallback<List<MatchItem>>() {
            @Override
            public void done(List<MatchItem> matchItems, ParseException e) {
                //to determine the success of matches
                getSuccessfulMatches(matchItems, scoreText);

                //calculate score to matches that have been liked by many others
                getPopularMatches(matchItems, scoreText);

                //calculate score to matches that are from top 5
                getTopMatches(matchItems, 2, scoreText);

                //if any new ones add to score the new points
                //save what the last matchid in list is
                //compare lastid to lastsavedid
                MatchItem lastMatch = matchItems.get(matchItems.size() - 1);
                if (!lastKnownMatchId.equals(lastMatch.getObjectId())) {
                    lastKnownMatchId = lastMatch.getObjectId();
                    //add to score for your first match made
                    //for now extra condition for user that didn't get the initial 30 start from first match
                    if (matchItems.size() == 1 || first) score += 30;
                    first = false;

                    //for when you've made many matchesa
                    if (matchItems.size() > 500 && !alreadyHit500) {
                        score += 30;
                        alreadyHit500 = true;
                        Log.i("CalculateUserStatsHigh", "hit 500");
                    }

                    saveScore(user, score, scoreText);
                }

                totalMatches = matchItems.size();
                popularMatches = popularCount;
                successfulMatches = successCount;
            }
        });
    }

    private void saveScore(ParseUser user, int score, TextView scoreText){
        if(user.getInt(ProfileBuilder.PROFILE_KEY_SCORE) < score) {
            user.put(ProfileBuilder.PROFILE_KEY_SCORE, score);

            scoreText.setText(user.getInt(ProfileBuilder.PROFILE_KEY_SCORE) + "");

            // finally we have a score to save
            //save the new score in background
            user.saveInBackground();
        }
    }

    public void checkIfUpdated(TextView scoreText){
        ParseUser user = ParseUser.getCurrentUser();

        Date updateDate = user.getUpdatedAt();

        long lastUpdate = updateDate.getTime();

        Log.i("CalculateStats", lastKnownTime.getTime() + " " + lastUpdate);

        if(lastKnownTime.getTime() < lastUpdate) calculateScore(scoreText);

        lastKnownTime = updateDate;
    }
}
