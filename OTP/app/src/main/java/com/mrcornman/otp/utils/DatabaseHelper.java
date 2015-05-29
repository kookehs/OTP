package com.mrcornman.otp.utils;

import android.util.Log;

import com.mrcornman.otp.models.models.MatchItem;
import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.GetCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Common Database Functionality
 */
public class DatabaseHelper {

    private DatabaseHelper() {}

    public static void insertMatchByPair(String makerId, String firstId, String secondId) {
        final String mMakerId = makerId;
        final String mFirstId = firstId;
        final String mSecondId = secondId;
        Log.i("DatabaseHelper", "Attempting to match " + mFirstId + " : " + mSecondId);
        // TODO: Possibly make it update on insert match instead of doing a costly check beforehand
        DatabaseHelper.getMatchByPair(firstId, secondId, new GetCallback<MatchItem>() {
            @Override
            public void done(MatchItem matchItem, ParseException e) {
                if (matchItem == null) {
                    matchItem = new MatchItem();
                    matchItem.setFirstId(mFirstId);
                    matchItem.setSecondId(mSecondId);
                    matchItem.setNumLikes(1);
                    matchItem.setNumMessages(0);
                } else {
                    matchItem.incrNumLikes();
                }

                ParseRelation<ParseUser> relation = matchItem.getRelation(MatchItem.MATCH_KEY_FOLLOWERS);
                relation.add(ParseUser.getCurrentUser());

                matchItem.saveInBackground();
            }
        });
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

        String[] userIds = { firstId, secondId };
        query.whereContainedIn(MatchItem.MATCH_KEY_FIRST_ID, Arrays.asList(userIds));
        query.whereContainedIn(MatchItem.MATCH_KEY_SECOND_ID, Arrays.asList(userIds));
        query.getFirstInBackground(getCallback);
    }

    public static void findTopMatches(int limit, FindCallback<MatchItem> findCallback) {
        ParseQuery<MatchItem> query = ParseQuery.getQuery(MatchItem.class);
        query.orderByDescending(MatchItem.MATCH_KEY_NUM_LIKES);
        query.setLimit(limit);
        query.findInBackground(findCallback);
    }

    /**
     * Find a maker user's matches. Sorts in descending order based on number of likes.
     * @param limit The limit for number of matches to find.
     * @param callback Callback when the matches are found.
     */
    public static void findMakerMatches(String makerId, int limit, FunctionCallback<List<MatchItem>> callback) {
        Map<String, Object> params = new HashMap<>();
        params.put("makerId", makerId);
        params.put("limit", limit + "");

        ParseCloud.callFunctionInBackground("findMakerMatches", params, callback);
    }

    /**
     * Find a client user's matches. Sorts in descending order based on number of likes.
     * @param limit The limit for number of matches to find.
     * @param callback Callback when the matches are found.
     */
    public static void findClientMatches(String clientId, int limit, FunctionCallback<List<MatchItem>> callback) {
        Map<String, Object> params = new HashMap<>();
        params.put("clientId", clientId);
        params.put("limit", limit + "");

        ParseCloud.callFunctionInBackground("findClientMatches", params, callback);
    }

    /**
     * Recommend potential users using matching recommendation algorithm.
     * @param otherId The other user to recommend based off of.
     * @param excludedIds List of user ids to exclude from recommendations.
     * @param location The location to base off of. If the other user is supplied this location will be
     *                 ignored and the other user's location will be used.
     * @param callback Callback when the users are found.
     */
    public static void findPotentialUsers(String otherId, List<String> excludedIds, ParseGeoPoint location, int limit, int searchDistance, FunctionCallback<List<ParseUser>> callback) {
        Map<String, Object> params = new HashMap<>();
        params.put("otherId", otherId);
        params.put("excludedIds", excludedIds);
        params.put("location", location);
        params.put("searchDistance", searchDistance + "");
        params.put("limit", limit + "");

        ParseCloud.callFunctionInBackground("findPotentialUsers", params, callback);
    }

    public static void updateMatchNumMessages(String matchId, final int numMessages){
        ParseQuery<MatchItem> query = ParseQuery.getQuery(MatchItem.class);
        query.getInBackground(matchId, new GetCallback<MatchItem>() {
            @Override
            public void done(MatchItem matchItem, ParseException e) {
                matchItem.setNumMessages(numMessages);
                matchItem.saveInBackground();
            }
        });
    }
}