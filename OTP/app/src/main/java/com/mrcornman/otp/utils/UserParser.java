package com.mrcornman.otp.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.mrcornman.otp.models.UserItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UserParser {

    public static List<UserItem> getUsersFromFileAndUpdateMaxUsers(Context context, String filename, String maxUsersKey, SharedPreferences sharedPreferences) {
        /* todo: check if the we are parsing the json properly. extract extra info for each property if needed
         * http://www.androidhive.info/2012/01/android-json-parsing-tutorial/
         */
        Log.e("getting users from file", filename);
        List<UserItem> userItems = new ArrayList<UserItem>();
        String json = null;
        UserItem userItem = null;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        try {
            FileInputStream fis = context.openFileInput(filename);
            int size = fis.available();
            byte[] buffer = new byte[size];
            fis.read(buffer);
            fis.close();
            json = new String(buffer, "UTF-8");
            if (json != null) {
                JSONObject jsonObject = new JSONObject(json);
                JSONObject response1 = jsonObject.getJSONObject("response1");
                editor.putString(maxUsersKey, response1.getString("totalProductsCount"));
                editor.commit();
                JSONArray productObjects = response1.getJSONArray("products");
                for (int i = 0; i < productObjects.length(); i++) {
                    JSONObject p = productObjects.getJSONObject(i);
                    userItem = new UserItem();
                    userItem.setName(p.getString("stylename"));
                    userItem.setAge(21);
                    userItem.setDreLandingPageUrl(p.getString("dre_landing_page_url"));
                    userItem.setImageUrl(getImageUrlFromJson(p.getString("imageEntry_default")));
                    Log.e("user returned", userItem.getName());
                    userItems.add(userItem);
                    /*
                     * todo: get image url from the json object imageEntry_default, rather than search_image
                     * todo: add more getter and setters to the Product class to extract even more data from json object
                     */
                }

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return userItems;
    }


    /*
    * todo: add new properties to Product model, like "image_domain", "image_realative_path"
    * also update the db, with new columns to store the above newly added properties
    * in the above parsing functions do this,
    * imageEntry_default_json_object = new JSONObject(p.getString("imageEntry_default"));
    * p.setImageDomain = imageEntry_default_json_object.getString("domain");
    * p.setImageRelativePath = imageEntry_default_json_object.getString("relativePath");
    * */
    public static String getImageUrlFromJson(String imageEntry_default){
        String domain = "";
        String relativePath = "";
        String middle = "h_350,q_100,w_300/";
        try {
            if (imageEntry_default != null){
                JSONObject json = new JSONObject(imageEntry_default);
                domain = json.getString("domain");
                relativePath = json.getString("relativePath");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return domain + middle + relativePath;
    }


}
