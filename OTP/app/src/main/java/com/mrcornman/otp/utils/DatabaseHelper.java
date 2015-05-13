package com.mrcornman.otp.utils;

import com.mrcornman.otp.models.MatchItem;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

/**
 * Common Database Functionality
 */
public class DatabaseHelper {

    // user specific
    public static final String USER_KEY_NAME = "name";
    public static final String USER_KEY_GENDER = "gender";
    public static final String USER_KEY_AGE = "age";
    public static final String USER_KEY_IMAGE_URL = "image_url";
    public static final String USER_KEY_LANDING_PAGE_URL = "landing_page_url";

    private DatabaseHelper() {}

    public static void insertNewMatchByPair(String firstId, String secondId) {
        MatchItem match = new MatchItem();
        match.setFirstId(firstId);
        match.setSecondId(secondId);
        match.setNumLikes(1);

        ParseRelation<ParseObject> relation = match.getRelation("followers");
        relation.add(ParseUser.getCurrentUser());
        match.saveInBackground();
    }

    public static void getUserById(String id, GetCallback<ParseUser> getCallback){
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.getInBackground(id, getCallback);
    }

    public static void getMatchById(String id, GetCallback<MatchItem> getCallback){
        ParseQuery<MatchItem> query = ParseQuery.getQuery(MatchItem.class);
        query.getInBackground(id, getCallback);
    }

    public static void getMatchByPair(String firstId, String secondId, GetCallback<MatchItem> getCallback){
        ParseQuery<MatchItem> query = ParseQuery.getQuery(MatchItem.class);
        query.whereEqualTo(MatchItem.MATCH_KEY_FIRST_ID, firstId);
        query.whereEqualTo(MatchItem.MATCH_KEY_SECOND_ID, secondId);
        query.getFirstInBackground(getCallback);
    }

    public static void findTopMatches(int limit, FindCallback<MatchItem> findCallback) {
        ParseQuery<MatchItem> query = ParseQuery.getQuery(MatchItem.class);
        query.orderByDescending(MatchItem.MATCH_KEY_NUM_LIKES);
        query.setLimit(limit);
        query.findInBackground(findCallback);
    }

    public static void updateMatchNumLikes(String matchId, final int numLikes){
        ParseQuery<MatchItem> query = ParseQuery.getQuery(MatchItem.class);
        query.getInBackground(matchId, new GetCallback<MatchItem>() {
            @Override
            public void done(MatchItem matchItem, ParseException e) {
                matchItem.setNumLikes(numLikes);
                matchItem.saveInBackground();
            }
        });
    }
}