package com.mrcornman.otp.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Jonathan on 5/12/2015.
 */
public class ProfileBuilder {

    private final static String FACEBOOK_KEY_NAME = "first_name";
    private final static String FACEBOOK_KEY_GENDER = "gender";
    private final static String FACEBOOK_KEY_BIRTHDAY = "birthday";
    private final static String FACEBOOK_KEY_LOCATION = "location";
    private final static String FACEBOOK_KEY_INTERESTED_IN = "interested_in";
    private final static String FACEBOOK_KEY_ALBUMS = "albums";
    private final static String FACEBOOK_KEY_PHOTOS = "photos";

    public static void buildCurrentProfile(Context context, ProfileBuilder.BuildProfileCallback buildProfileCallback) {

        final Context mContext = context;
        final BuildProfileCallback buildCallback = buildProfileCallback;
        final AccessToken accessToken = AccessToken.getCurrentAccessToken();

        GraphRequest meRequest = GraphRequest.newMeRequest(
                accessToken,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        if (response.getError() != null || object == null) {
                            buildCallback.done(null, response.getError());
                            return;
                        }

                        try {
                            // name
                            String name = object.getString(FACEBOOK_KEY_NAME);

                            // gender
                            String gender = object.optString(FACEBOOK_KEY_GENDER);
                            int genderId = -1;
                            switch (gender) {
                                case "male":
                                    genderId = 0;
                                    break;
                                case "female":
                                    genderId = 1;
                                    break;
                            }

                            // birthday
                            // TODO: birthday we get is not exact
                            String birthdayStr = object.getString(FACEBOOK_KEY_BIRTHDAY);
                            Date birthDate = PrettyTime.getDateFromBirthdayString(birthdayStr);

                            int i = 0;

                            // interested in
                            JSONArray interestedIn = object.optJSONArray(FACEBOOK_KEY_INTERESTED_IN);
                            List<Integer> interestedInList = new ArrayList<>();
                            if (interestedIn != null) {
                                for (; i < interestedIn.length(); i++) {
                                    interestedInList.add(interestedIn.getString(i).equals("female") ? 1 : 0);
                                }
                            }

                            // profile pictures
                            JSONObject albumsObj = object.getJSONObject(FACEBOOK_KEY_ALBUMS);
                            if (albumsObj != null) {

                                // first get the list of albums
                                JSONArray albumsData = albumsObj.optJSONArray("data");
                                if (albumsData != null) {

                                    // now find the profile album
                                    JSONObject albumObj = null;
                                    for (i = 0; i < albumsData.length(); i++) {
                                        albumObj = albumsData.getJSONObject(i);
                                        if (albumObj.getString("type").equals("profile")) {

                                            // fetch all the photo images from the profile album
                                            fetchPhotosFromAlbum(accessToken, albumObj.getString("id"), new FetchPhotosCallback() {
                                                @Override
                                                public void done(List<JSONObject> photoImages, Object err) {
                                                    if (err != null || photoImages == null) {
                                                        buildCallback.done(null, err);
                                                        return;
                                                    }

                                                    // get 3 of the profile photo images and use them as our default pics
                                                    try {
                                                        JSONObject photoImageObj = null;
                                                        Target target = new Target() {
                                                            @Override
                                                            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

                                                            }

                                                            @Override
                                                            public void onBitmapFailed(Drawable errorDrawable) {
                                                                buildCallback.done(null, errorDrawable);
                                                            }

                                                            @Override
                                                            public void onPrepareLoad(Drawable placeHolderDrawable) {

                                                            }
                                                        };

                                                        for(int i = 0; i < photoImages.size(); i++) {
                                                            photoImageObj = photoImages.get(i);
                                                            Picasso.with(mContext).load(photoImageObj.getString("source")).into(target);
                                                        }
                                                    } catch(Exception e) {
                                                        buildCallback.done(null, e);
                                                        return;
                                                    }
                                                }
                                            });
                                            break;
                                        }
                                    }
                                }
                            }

                            Log.i("ProfileBuilder", "Generated new profile -> Name = " + name + " | " + "Gender = " + genderId + " | " + "Birthday = " + birthDate.toString() + " | " + "Interested in = " + interestedInList.toString());

                        } catch (Exception e) {
                            buildCallback.done(null, e);
                            return;
                        }
                    }
                });

        Bundle parameters = new Bundle();
        String fieldParamsStr =
                FACEBOOK_KEY_NAME + "," +
                FACEBOOK_KEY_GENDER + "," +
                FACEBOOK_KEY_BIRTHDAY + "," +
                FACEBOOK_KEY_LOCATION + "," +
                FACEBOOK_KEY_INTERESTED_IN + "," +
                FACEBOOK_KEY_ALBUMS;
        parameters.putString("fields", fieldParamsStr);
        meRequest.setParameters(parameters);
        meRequest.executeAsync();
    }

    public static void fetchPhotosFromAlbum(AccessToken accessToken, String albumId, FetchPhotosCallback fetchPhotosCallback) {
        final FetchPhotosCallback photosCallback = fetchPhotosCallback;

        GraphRequest photoRequest = GraphRequest.newGraphPathRequest(accessToken, albumId + "/" + FACEBOOK_KEY_PHOTOS, new GraphRequest.Callback() {
            @Override
            public void onCompleted(GraphResponse response) {
                if(response.getError() != null || response.getJSONObject() == null) {
                    photosCallback.done(null, response.getError());
                }

                try {
                    List<JSONObject> photos = new ArrayList<JSONObject>();

                    JSONArray photosData = response.getJSONObject().getJSONArray("data");
                    JSONObject photoObj = null;
                    JSONArray photoImages = null;
                    int j;
                    for(int i = 0; i < photosData.length(); i++) {
                        photoObj = photosData.getJSONObject(i);
                        photoImages = photoObj.getJSONArray("images");
                        for(j = 0; j < photoImages.length(); j++) {
                            // TODO: How does the images array work? All the image sources returned seem to be the same so I'm just getting the first one here
                            photos.add(photoImages.getJSONObject(j));
                            break;
                        }
                    }

                    photosCallback.done(photos, null);
                } catch(Exception e) {
                    photosCallback.done(null, e);
                    return;
                }
            }
        });

        photoRequest.executeAsync();
    }

    public interface FetchPhotosCallback {
        void done(List<JSONObject> photos, Object err);
    }

    public interface BuildProfileCallback {
        void done(ParseUser user, Object err);
    }
}
