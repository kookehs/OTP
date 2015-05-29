package com.mrcornman.otp.items.models;

import android.util.Log;

import com.google.gson.Gson;
import com.mrcornman.otp.items.gson.PhotoFileItem;
import com.parse.ParseClassName;
import com.parse.ParseObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

@ParseClassName("Photo")
public class PhotoItem extends ParseObject {

    // key identifiers
    public static final String PHOTO_KEY_FILES = "files";

    public List<PhotoFileItem> getPhotoFiles() {
        List<PhotoFileItem> photoFileItems = new ArrayList<>();
        try {
            JSONArray photoFilesData = getJSONArray(PHOTO_KEY_FILES);
            JSONObject photoFileObj = null;
            Gson gson = new Gson();
            for (int i = 0; i < photoFilesData.length(); i++) {
                photoFileObj = photoFilesData.getJSONObject(i);
                PhotoFileItem photoFileItem = gson.fromJson(photoFileObj.toString(), PhotoFileItem.class);
                photoFileItems.add(photoFileItem);
            }
        } catch(JSONException e) {
            Log.e("PhotoItem", "Error getting files from a photo item.");
            return null;
        }

        return photoFileItems;
    }

    public void setPhotoFiles(List<PhotoFileItem> photoFileItems) {

        JSONArray photoFilesData = new JSONArray();
        Gson gson = new Gson();
        String photoFileObjStr = null;
        for(int i = 0; i < photoFileItems.size(); i++) {
            photoFileObjStr = gson.toJson(photoFileItems.get(i));

            try {
                photoFilesData.put(new JSONObject(photoFileObjStr));
            } catch (JSONException e) {
                Log.e("PhotoItem", "There was a problem adding a new photo file to a photo item.");
            }
        }

        put(PHOTO_KEY_FILES, photoFilesData);
    }
}