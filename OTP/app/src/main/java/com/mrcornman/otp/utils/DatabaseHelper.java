package com.mrcornman.otp.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.mrcornman.otp.models.MatchItem;
import com.mrcornman.otp.models.UserItem;

import java.util.ArrayList;
import java.util.List;

import static android.database.DatabaseUtils.sqlEscapeString;

/**
 * Created by Anil on 8/7/2014.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    // version
    private static final int DATABASE_VERSION = 1;
    // database name
    public static final String DATABASE_NAME = "com.mrcornman.otp.db";
    // table name
    public static final String MATCH_TABLE_NAME = "otp_match";
    public static final String USER_TABLE_NAME = "otp_user";

    // column names
    public static final String KEY_ID = "id";

    // match specific
    public static final String MATCH_KEY_FIRST_ID = "first_id";
    public static final String MATCH_KEY_SECOND_ID = "second_id";
    public static final String MATCH_KEY_MATCHMAKER_ID = "matchmaker_id";
    public static final String MATCH_KEY_NUM_LIKES = "num_likes";

    // user specific
    public static final String USER_KEY_NAME = "name";
    public static final String USER_KEY_GENDER = "gender";
    public static final String USER_KEY_AGE = "age";
    public static final String USER_KEY_IMAGE_URL = "image_url";
    public static final String USER_KEY_LANDING_PAGE_URL = "landing_page_url";

    // fixme: make sure database connections are closed

    // table create statements
    private static final String CREATE_MATCH_TABLE = "CREATE TABLE "
            + MATCH_TABLE_NAME + "(" + KEY_ID + " INTEGER PRIMARY KEY,"
            + MATCH_KEY_FIRST_ID + " TEXT,"
            + MATCH_KEY_SECOND_ID + " TEXT,"
            + MATCH_KEY_MATCHMAKER_ID + " TEXT,"
            + MATCH_KEY_NUM_LIKES + " INTEGER" // fixed: liked is integer in table
            + ")";

    private static final String CREATE_USER_TABLE = "CREATE TABLE "
            + USER_TABLE_NAME + "(" + KEY_ID + " INTEGER PRIMARY KEY,"
            + USER_KEY_NAME + " TEXT,"
            + USER_KEY_GENDER + " INTEGER,"
            + USER_KEY_AGE + " INTEGER,"
            + USER_KEY_IMAGE_URL + " TEXT,"
            + USER_KEY_LANDING_PAGE_URL + " TEXT"
            + ")";


    public DatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // things to do generally when a database is created..
        // todo: crate indexes
        sqLiteDatabase.execSQL(CREATE_MATCH_TABLE);
        sqLiteDatabase.execSQL(CREATE_USER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
        // todo: handle what to do when you upgrade the database
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MATCH_TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + USER_TABLE_NAME);

        onCreate(sqLiteDatabase);
    }

    public void onResetTables() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + MATCH_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + USER_TABLE_NAME);
        onCreate(db);
    }

    public String insertNewMatch(MatchItem matchItem) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(MATCH_KEY_FIRST_ID, matchItem.getFirstId());
        values.put(MATCH_KEY_SECOND_ID, matchItem.getSecondId());
        values.put(MATCH_KEY_MATCHMAKER_ID, matchItem.getSecondId());
        values.put(MATCH_KEY_NUM_LIKES, matchItem.getNumLikes());

        long product_id = db.insert(MATCH_TABLE_NAME, null, values);
        db.close();

        return product_id + "";
    }

    public String insertNewUser(UserItem userItem) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(USER_KEY_NAME, userItem.getName());
        values.put(USER_KEY_GENDER, userItem.getGender());
        values.put(USER_KEY_AGE, userItem.getAge());
        values.put(USER_KEY_IMAGE_URL, userItem.getImageUrl());
        values.put(USER_KEY_LANDING_PAGE_URL, userItem.getDreLandingPageUrl());

        long product_id = db.insert(USER_TABLE_NAME, null, values);
        db.close();
        return product_id + "";
    }

    public MatchItem getMatchById(String value){
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + MATCH_TABLE_NAME + " WHERE "
                + KEY_ID + " = " + value;
        Cursor c = db.rawQuery(selectQuery, null);
        if (c != null)
            c.moveToFirst();
        else
            return null;

        MatchItem match = new MatchItem(c.getString(c.getColumnIndex(KEY_ID)));
        match.setFirstId(c.getString(c.getColumnIndex(MATCH_KEY_FIRST_ID)));
        match.setSecondId(c.getString(c.getColumnIndex(MATCH_KEY_SECOND_ID)));
        match.setMatchmakerId(c.getString(c.getColumnIndex(MATCH_KEY_MATCHMAKER_ID)));
        match.setNumLikes(c.getInt(c.getColumnIndex(MATCH_KEY_NUM_LIKES)));
        c.close();
        db.close();
        return match;
    }

    public UserItem getUserById(String value){
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + USER_TABLE_NAME + " WHERE "
                + KEY_ID + " = " + value;
        Cursor c = db.rawQuery(selectQuery, null);
        if (c != null)
            c.moveToFirst();
        else
            return null;

        UserItem user = new UserItem(c.getString(c.getColumnIndex(KEY_ID)));
        user.setName(c.getString(c.getColumnIndex(USER_KEY_NAME)));
        user.setGender(c.getInt(c.getColumnIndex(USER_KEY_GENDER)));
        user.setAge(c.getInt(c.getColumnIndex(USER_KEY_AGE)));
        user.setImageUrl(c.getString(c.getColumnIndex(USER_KEY_IMAGE_URL)));
        user.setDreLandingPageUrl(c.getString(c.getColumnIndex(USER_KEY_LANDING_PAGE_URL)));
        c.close();
        db.close();
        return user;
    }

    // needs to get top 20 based on number of likes
    public List<UserItem> getPotentialUsers(List<UserItem> others, int limit) {
        SQLiteDatabase db = this.getReadableDatabase();

        String listQuery = "()";
        if(others != null && others.size() > 0) {
            listQuery = "(" + others.get(0).getId();
            for (int i = 1, il = others.size(); i < il; ++i) {
                listQuery += (", " + others.get(i).getId());
            }
            listQuery += ")";
        }

        String selectQuery = "SELECT * FROM " + USER_TABLE_NAME
                + " WHERE " + KEY_ID + " NOT IN " + listQuery
                + " LIMIT " + limit;
        Cursor c = db.rawQuery(selectQuery, null);

        List<UserItem> userItems = new ArrayList<UserItem>();

        if (c.moveToFirst()) {
            do {
                UserItem userItem = new UserItem(c.getString(c.getColumnIndex(KEY_ID)));
                userItem.setName(c.getString(c.getColumnIndex(USER_KEY_NAME)));
                userItem.setAge(c.getInt(c.getColumnIndex(USER_KEY_AGE)));
                userItem.setGender(c.getInt(c.getColumnIndex(USER_KEY_GENDER)));
                userItem.setImageUrl(c.getString(c.getColumnIndex(USER_KEY_IMAGE_URL)));
                userItems.add(userItem);
            } while (c.moveToNext());
        }
        c.close();
        db.close();
        return userItems;
    }

    public MatchItem getMatchByPairIds(String firstId, String secondId){
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + MATCH_TABLE_NAME + " WHERE "
                + MATCH_KEY_FIRST_ID + " = " + firstId + " AND "
                + MATCH_KEY_SECOND_ID + " = " + secondId;
        Cursor c = db.rawQuery(selectQuery, null);
        if (c != null && c.getCount() > 0)
            c.moveToFirst();
        else
            return null;

        MatchItem match = new MatchItem(c.getString(c.getColumnIndex(KEY_ID)));
        match.setFirstId(c.getString(c.getColumnIndex(MATCH_KEY_FIRST_ID)));
        match.setSecondId(c.getString(c.getColumnIndex(MATCH_KEY_SECOND_ID)));
        match.setMatchmakerId(c.getString(c.getColumnIndex(MATCH_KEY_MATCHMAKER_ID)));
        match.setNumLikes(c.getInt(c.getColumnIndex(MATCH_KEY_NUM_LIKES)));
        c.close();
        db.close();
        return match;
    }

    // todo: have some clarity on when a database is closed, is it really necessary? but close all cursors.. for now closing all database instances..
    public void insertOrIgnoreMatches(List<MatchItem> matchItems){
        int length = matchItems.size();
        String valuesString = "";
        for (int i = 0; i < length; i++) {
            MatchItem matchItem = matchItems.get(i);
            valuesString += "("
                     + sqlEscapeString(matchItem.getFirstId()) + ","
                     + sqlEscapeString(matchItem.getSecondId()) + ","
                     + sqlEscapeString(matchItem.getMatchmakerId()) + ","
                     + String.valueOf(matchItem.getNumLikes()) + "),";
        }
        Log.e("values being inserted", valuesString);
        if (valuesString.length() > 0) {
            valuesString = valuesString.substring(0, valuesString.length() - 1);
            String SQL_INSERT_OR_IGNORE = "INSERT OR IGNORE INTO " + MATCH_TABLE_NAME + " ("
                    + MATCH_KEY_FIRST_ID + ","
                    + MATCH_KEY_SECOND_ID + ","
                    + MATCH_KEY_MATCHMAKER_ID + ","
                    + MATCH_KEY_NUM_LIKES
                    + ")" + " VALUES " + valuesString;
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL(SQL_INSERT_OR_IGNORE);
            db.close();
        }
    }

    public void insertOrIgnoreUsers(List<UserItem> userItems){
        int length = userItems.size();
        String valuesString = "";
        for (int i = 0; i < length; i++) {
            UserItem userItem = userItems.get(i);
            valuesString += "("
                    + sqlEscapeString(userItem.getName()) + ","
                    + sqlEscapeString(userItem.getGender() + "") + ","
                    + sqlEscapeString(userItem.getAge() + "") + ","
                    + sqlEscapeString(userItem.getImageUrl()) + ","
                    + sqlEscapeString(userItem.getDreLandingPageUrl()) + "),";
        }
        Log.e("values being inserted", valuesString);
        if (valuesString.length() > 0) {
            valuesString = valuesString.substring(0, valuesString.length() - 1);
            String SQL_INSERT_OR_IGNORE = "INSERT OR IGNORE INTO " + USER_TABLE_NAME + " ("
                    + USER_KEY_NAME + ","
                    + USER_KEY_GENDER + ","
                    + USER_KEY_AGE + ","
                    + USER_KEY_IMAGE_URL + ","
                    + USER_KEY_LANDING_PAGE_URL
                    + ")" + " VALUES " + valuesString;
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL(SQL_INSERT_OR_IGNORE);
            db.close();
        }
    }

    public Cursor getTopMatches(int limit){
        // a cursor object that is fed into a cursor adapter must have a column name "_id"
        // for that instead of changing our model, we can do
        // SELECT id _id, * FROM // instead of
        // SELECT * FROM //
        // in the above query "id" is our actual column name
        // http://stackoverflow.com/a/7494398/544102
        String selectQuery = "SELECT id _id, * FROM " + MATCH_TABLE_NAME
                + " ORDER BY " + MATCH_KEY_NUM_LIKES + " DESC"
                + " LIMIT " + limit;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        return c;
    }

    public List<MatchItem> getPotentialMatches(int limit) {
        List<MatchItem> matchItems = new ArrayList<MatchItem>();
        String selectQuery = "SELECT * FROM " + MATCH_TABLE_NAME
                + " ORDER BY " + MATCH_KEY_NUM_LIKES + " DESC"
                + " LIMIT " + limit;
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor c = sqLiteDatabase.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                MatchItem matchItem = new MatchItem(c.getString(c.getColumnIndex(KEY_ID)));
                matchItem.setFirstId(c.getString(c.getColumnIndex(MATCH_KEY_FIRST_ID)));
                matchItem.setSecondId(c.getString(c.getColumnIndex(MATCH_KEY_SECOND_ID)));
                matchItem.setMatchmakerId(c.getString(c.getColumnIndex(MATCH_KEY_MATCHMAKER_ID)));
                matchItem.setNumLikes(c.getInt(c.getColumnIndex(MATCH_KEY_NUM_LIKES)));
                matchItems.add(matchItem);
            } while (c.moveToNext());
        }
        c.close();
        sqLiteDatabase.close();
        Log.e("getting unseen matches", matchItems.toString());
        return matchItems;
    }

    public void updateNumMatchLikes(MatchItem matchItem, int numLikes){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String numLikesStr = String.valueOf(numLikes);
        Log.i("Bam", matchItem.getId());
        String UPDATE_QUERY = "UPDATE " + MATCH_TABLE_NAME
                + " SET " + MATCH_KEY_NUM_LIKES + " = " + numLikesStr
                + " WHERE " + KEY_ID + " = " + matchItem.getId();
        sqLiteDatabase.execSQL(UPDATE_QUERY);
        sqLiteDatabase.close();
    }
}