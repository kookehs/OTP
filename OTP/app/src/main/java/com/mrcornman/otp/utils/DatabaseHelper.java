package com.mrcornman.otp.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import com.mrcornman.otp.models.MatchItem;

import static android.database.DatabaseUtils.sqlEscapeString;

/**
 * Created by Anil on 8/7/2014.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    // version
    private static final int DATABASE_VERSION = 1;
    // database name
    public static final String DATABASE_NAME = "anil.MyntraProducts.db";
    // table name
    public static final String TABLE_NAME = "myntra_products";

    public static final String MEN_SHOES_GROUP_LABEL = "men_shoes";
    public static final String WOMEN_SHOES_gROUP_LABEL = "women_shoes";

    // column names
    public static final String KEY_ID = "id";
    public static final String KEY_UNIQUE_PRODUCT_GROUP = "unique_product_group";
    public static final String KEY_PRODUCT_GROUP = "product_group";
    public static final String KEY_STYLE_NAME = "style_name";
    public static final String KEY_DISCOUNTED_PRICE = "discounted_price";
    public static final String KEY_DISCOUNT = "discount";
    public static final String KEY_PRICE = "price";
    public static final String KEY_STYLE_ID = "style_id";
    public static final String KEY_IMAGE_URL = "image_url";
    public static final String KEY_LANDING_PAGE_URL = "landing_page_url";
    public static final String KEY_LIKED = "is_liked";

    // fixme: make sure database connections are closed

    public static final int VALUE_LIKED = 1;
    public static final int VALUE_DISLIKED = 2;
    public static final int VALUE_NONE = 0;

    // table create statements
    private static final String CREATE_TABLE = "CREATE TABLE "
            + TABLE_NAME + "(" + KEY_ID + " INTEGER PRIMARY KEY,"
            + KEY_UNIQUE_PRODUCT_GROUP + " TEXT,"
            + KEY_PRODUCT_GROUP + " TEXT,"
            + KEY_STYLE_NAME + " TEXT,"
            + KEY_DISCOUNTED_PRICE + " TEXT,"
            + KEY_DISCOUNT + " TEXT,"
            + KEY_PRICE + " TEXT,"
            + KEY_STYLE_ID + " TEXT UNIQUE,"
            + KEY_IMAGE_URL + " TEXT,"
            + KEY_LANDING_PAGE_URL + " TEXT,"
            + KEY_LIKED + " INTEGER" // fixed: liked is integer in table
            + ")";

//    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
//        super(context, name, factory, version);
//    }

    public DatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // things to do generally when a database is created..
        // todo: crate indexes
        sqLiteDatabase.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
        // todo: handle what to do when you upgrade the database
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

        onCreate(sqLiteDatabase);
    }

    public void deleteTable(String table){
        SQLiteDatabase db = this.getWritableDatabase();
        Log.d("databse helper", "deleted table" + table);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public long insertNewProduct(MatchItem matchItem, String table) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_UNIQUE_PRODUCT_GROUP, matchItem.getUniqueProductGroup());
        values.put(KEY_PRODUCT_GROUP, matchItem.getProductGroup());
        values.put(KEY_STYLE_NAME, matchItem.getStyleName());
        values.put(KEY_DISCOUNTED_PRICE, matchItem.getDiscountedPrice());
        values.put(KEY_DISCOUNT, matchItem.getDiscount());
        values.put(KEY_PRICE, matchItem.getPrice());
        values.put(KEY_STYLE_ID, matchItem.getStyleId());
        values.put(KEY_IMAGE_URL, matchItem.getImageUrl());
        values.put(KEY_LANDING_PAGE_URL, matchItem.getDreLandingPageUrl());
        matchItem.setLiked(VALUE_NONE);
        values.put(KEY_LIKED, VALUE_NONE);

        long product_id = db.insert(table, null, values);
        db.close();
        return product_id;
    }


    // todo: have some clarity on when a database is closed, is it really necessary? but close all cursors.. for now closing all database instances..
    public void insertOrIgnoreProducts(List<MatchItem> matchItems, String table){

        int length = matchItems.size();
        String valuesString = "";
        for (int i = 0; i < length; i++) {
            MatchItem matchItem = matchItems.get(i);
            valuesString += "("
                     + sqlEscapeString(matchItem.getUniqueProductGroup()) + ","
                     + sqlEscapeString(matchItem.getProductGroup()) + ","
                     + sqlEscapeString(matchItem.getStyleName()) + ","
                     + sqlEscapeString(matchItem.getDiscountedPrice()) + ","
                     + sqlEscapeString(matchItem.getDiscount()) + ","
                     + sqlEscapeString(matchItem.getPrice()) + ","
                     + sqlEscapeString(matchItem.getStyleId()) + ","
                     + sqlEscapeString(matchItem.getImageUrl()) + ","
                     + sqlEscapeString(matchItem.getDreLandingPageUrl()) + ","
                     + String.valueOf(matchItem.getLiked()) + "),";
        }
        Log.e("values being inserted", valuesString);
        if (valuesString.length() > 0) {
            valuesString = valuesString.substring(0, valuesString.length() - 1);
            String SQL_INSERT_OR_IGNORE = "INSERT OR IGNORE INTO " + table + " ("
                    + KEY_UNIQUE_PRODUCT_GROUP + ","
                    + KEY_PRODUCT_GROUP + ","
                    + KEY_STYLE_NAME + ","
                    + KEY_DISCOUNTED_PRICE + ","
                    + KEY_DISCOUNT + ","
                    + KEY_PRICE + ","
                    + KEY_STYLE_ID + ","
                    + KEY_IMAGE_URL + ","
                    + KEY_LANDING_PAGE_URL + ","
                    + KEY_LIKED
                    + ")" + " VALUES " + valuesString;
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL(SQL_INSERT_OR_IGNORE);
            db.close();
        }

    }

    public MatchItem getProduct(String tableName, String columnName, String value){
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + tableName + " WHERE "
                + columnName + " = " + value;
        Cursor c = db.rawQuery(selectQuery, null);
        if (c != null)
            c.moveToFirst();
        MatchItem matchItem = new MatchItem(c.getInt(c.getColumnIndex(KEY_ID))); // fixme: this is wrong, confusion betweet KEY_ID, mId, unique style id from the website
        matchItem.setUniqueProductGroup(c.getString(c.getColumnIndex(KEY_UNIQUE_PRODUCT_GROUP)));
        matchItem.setProductGroup(c.getString(c.getColumnIndex(KEY_PRODUCT_GROUP)));
        matchItem.setDiscountedPrice(c.getString(c.getColumnIndex(KEY_DISCOUNTED_PRICE)));
        matchItem.setStyleName(c.getString(c.getColumnIndex(KEY_STYLE_NAME)));
        matchItem.setDiscount(c.getString(c.getColumnIndex(KEY_DISCOUNT)));
        matchItem.setPrice(c.getString(c.getColumnIndex(KEY_PRICE)));
        matchItem.setStyleId(c.getString(c.getColumnIndex(KEY_STYLE_ID)));
        matchItem.setImageUrl(c.getString(c.getColumnIndex(KEY_IMAGE_URL)));
        matchItem.setDreLandingPageUrl(c.getString(c.getColumnIndex(KEY_LANDING_PAGE_URL)));
        matchItem.setLiked(c.getInt(c.getColumnIndex(KEY_LIKED)));
        c.close();
        db.close();
        return matchItem;
    }

    //todo: add an extra parameter to limit number of products
    //fixed: the columns are a mess, done in a hurry, fix this shit..
    public List<MatchItem> getProducts(String tableName, String columnName, String value, String limit){
        List<MatchItem> matchItems = new ArrayList<MatchItem>();
        String selectQuery = "SELECT * FROM " + tableName + " WHERE "
                + columnName + " = '" + value + "' LIMIT " + limit;
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor c = sqLiteDatabase.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                MatchItem matchItem = new MatchItem(c.getInt(c.getColumnIndex(KEY_ID)));
                matchItem.setUniqueProductGroup(c.getString(c.getColumnIndex(KEY_UNIQUE_PRODUCT_GROUP)));
                matchItem.setProductGroup(c.getString(c.getColumnIndex(KEY_PRODUCT_GROUP)));
                matchItem.setDiscountedPrice(c.getString(c.getColumnIndex(KEY_DISCOUNTED_PRICE)));
                matchItem.setStyleName(c.getString(c.getColumnIndex(KEY_STYLE_NAME)));
                matchItem.setDiscount(c.getString(c.getColumnIndex(KEY_DISCOUNT)));
                matchItem.setPrice(c.getString(c.getColumnIndex(KEY_PRICE)));
                matchItem.setStyleId(c.getString(c.getColumnIndex(KEY_STYLE_ID)));
                matchItem.setImageUrl(c.getString(c.getColumnIndex(KEY_IMAGE_URL)));
                matchItem.setDreLandingPageUrl(c.getString(c.getColumnIndex(KEY_LANDING_PAGE_URL)));
                matchItem.setLiked(c.getInt(c.getColumnIndex(KEY_LIKED)));
                matchItems.add(matchItem);
            } while (c.moveToNext());
        }
        c.close();
        sqLiteDatabase.close();
        return matchItems;
    }

    public List<MatchItem> getProductsFromGroup(String productGroup, int limit){
        String limitString = String.valueOf(limit);
        return getProducts(TABLE_NAME, KEY_UNIQUE_PRODUCT_GROUP, productGroup, limitString);
    }

    public List<MatchItem> getUnseenProductsFromGroup(String productGroup, int limit) {
        String limitString = String.valueOf(limit);
        List<MatchItem> matchItems = new ArrayList<MatchItem>();
        String selectQuery = "SELECT * FROM " + TABLE_NAME + " WHERE "
                + KEY_UNIQUE_PRODUCT_GROUP + " = '" + productGroup
                + "' AND " + KEY_LIKED + " = 0"
                + " LIMIT " + limit;
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor c = sqLiteDatabase.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                MatchItem matchItem = new MatchItem(c.getInt(c.getColumnIndex(KEY_ID)));
                matchItem.setUniqueProductGroup(c.getString(c.getColumnIndex(KEY_UNIQUE_PRODUCT_GROUP)));
                matchItem.setProductGroup(c.getString(c.getColumnIndex(KEY_PRODUCT_GROUP)));
                matchItem.setDiscountedPrice(c.getString(c.getColumnIndex(KEY_DISCOUNTED_PRICE)));
                matchItem.setStyleName(c.getString(c.getColumnIndex(KEY_STYLE_NAME)));
                matchItem.setDiscount(c.getString(c.getColumnIndex(KEY_DISCOUNT)));
                matchItem.setPrice(c.getString(c.getColumnIndex(KEY_PRICE)));
                matchItem.setStyleId(c.getString(c.getColumnIndex(KEY_STYLE_ID)));
                matchItem.setImageUrl(c.getString(c.getColumnIndex(KEY_IMAGE_URL)));
                matchItem.setDreLandingPageUrl(c.getString(c.getColumnIndex(KEY_LANDING_PAGE_URL)));
                matchItem.setLiked(c.getInt(c.getColumnIndex(KEY_LIKED)));
                matchItems.add(matchItem);
            } while (c.moveToNext());
        }
        c.close();
        sqLiteDatabase.close();
        Log.e("gettign unseen products", matchItems.toString());
        return matchItems;
    }

    public Cursor getLikedProductsFromGroup(String productGroupName){
        // a cursor object that is fed into a cursor adapter must have a column name "_id"
        // for that instead of changing our model, we can do
        // SELECT id _id, * FROM // instead of
        // SELECT * FROM //
        // in the above query "id" is our actual column name
        // http://stackoverflow.com/a/7494398/544102
        String selectQuery = "SELECT id _id, * FROM " + TABLE_NAME + " WHERE "
                + KEY_UNIQUE_PRODUCT_GROUP + " = '" + productGroupName
                + "' AND " + KEY_LIKED + " = 1";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        return c;
    }

    public void updateLikeStatus(MatchItem matchItem, int likeStatus){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        String likeStatusStr = String.valueOf(likeStatus);
        String UPDATE_QUERY = "UPDATE " + TABLE_NAME
                + " SET " + KEY_LIKED + " = " + likeStatusStr
                + " WHERE " + KEY_STYLE_ID + " = " + matchItem.getStyleId();
        sqLiteDatabase.execSQL(UPDATE_QUERY);
        sqLiteDatabase.close();
    }
}
