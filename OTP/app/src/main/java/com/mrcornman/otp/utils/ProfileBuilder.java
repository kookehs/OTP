package com.mrcornman.otp.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.mrcornman.otp.models.PhotoFile;
import com.mrcornman.otp.models.PhotoItem;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Jonathan on 5/12/2015.
 */
public class ProfileBuilder {

    public final static int DEFAULT_NUM_PHOTOS = 2;
    public final static int MAX_NUM_PHOTOS = 4;

    public final static String PROFILE_KEY_NAME = "name";
    public final static String PROFILE_KEY_GENDER = "gender";
    public final static String PROFILE_KEY_BIRTHDATE = "birthdate";
    public final static String PROFILE_KEY_INTERESTED_IN = "interested_in";
    public final static String PROFILE_KEY_PHOTOS = "photos";
    public final static String PROFILE_KEY_ALBUMS = "photos";

    private final static String FACEBOOK_KEY_NAME = "first_name";
    private final static String FACEBOOK_KEY_GENDER = "gender";
    private final static String FACEBOOK_KEY_BIRTHDAY = "birthday";
    private final static String FACEBOOK_KEY_LOCATION = "location";
    private final static String FACEBOOK_KEY_INTERESTED_IN = "interested_in";
    public final static String FACEBOOK_KEY_ALBUMS = "albums";
    public final static String FACEBOOK_KEY_PHOTOS = "photos";

    private static Target[] targets;

    private static boolean userImagesFailed = false;
    private static int userImagesCount = 0;
    private static int userImagesThreshold = 0;

    /**
     * Builds the current user's profile and saves it to the database.
     * @param context The context of the method.
     * @param buildProfileCallback To execute once the profile is built or there is an error.
     */
    public static void buildCurrentProfile(Context context, ProfileBuilder.BuildProfileCallback buildProfileCallback) {

        final ParseUser user = ParseUser.getCurrentUser();

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

                        /*
                         * Name
                         */
                        String name = object.optString(FACEBOOK_KEY_NAME);
                        user.put(PROFILE_KEY_NAME, name);

                        /*
                         * Gender
                         */
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

                        user.put(PROFILE_KEY_GENDER, genderId);

                        /*
                         * Birthday
                         */
                        // TODO: birthday we get is not exact
                        String birthdayStr = object.optString(FACEBOOK_KEY_BIRTHDAY);
                        Date birthDate = PrettyTime.getDateFromBirthdayString(birthdayStr);
                        if(birthDate == null) {
                            Calendar cal = Calendar.getInstance();
                            cal.set(cal.get(Calendar.YEAR) - 13, cal.get(Calendar.MONTH), 1);
                            birthDate = cal.getTime();
                        }

                        user.put(PROFILE_KEY_BIRTHDATE, birthDate);


                        int i = 0;

                        /*
                         * Interested In
                         */
                        JSONArray interestedIn = object.optJSONArray(FACEBOOK_KEY_INTERESTED_IN);
                        List<Integer> interestedInList = new ArrayList<>();
                        if (interestedIn != null) {
                            for (; i < interestedIn.length(); i++) {
                                interestedInList.add(interestedIn.optString(i).equals("female") ? 1 : 0);
                            }
                        } else {
                            // if they have specified gender then default to being interested in opposite gender
                            // otherwise they are interested in both
                            if(genderId != -1) {
                                interestedInList.add(genderId == 0 ? 1 : 0);
                            } else {
                                interestedInList.add(0);
                                interestedInList.add(1);
                            }
                        }

                        user.put(PROFILE_KEY_INTERESTED_IN, interestedInList);

                        /*
                         * Profile Pictures
                         */

                        // first reset the book keeping for loaded user images
                        userImagesFailed = false;
                        userImagesCount = 0;

                        JSONObject albumsObj = object.optJSONObject(FACEBOOK_KEY_ALBUMS);

                        boolean skipPhotos = true;
                        if (albumsObj != null) {

                            // first get the list of albums
                            JSONArray albumsData = albumsObj.optJSONArray("data");


                            if (albumsData != null && albumsData.length() > 0) {
                                // now find the profile album
                                JSONObject albumObj = null;

                                for (i = 0; i < albumsData.length(); i++) {
                                    albumObj = albumsData.optJSONObject(i);
                                    if (albumObj.optString("type").equals("profile")) {

                                        skipPhotos = false;

                                        // fetch all the photo images from the profile album
                                        fetchPhotosFromAlbum(accessToken, albumObj.optString("id"), new FetchPhotosCallback() {
                                            @Override
                                            public void done(List<JSONObject> photoImageObjs, Object err) {
                                                if (err != null || photoImageObjs == null) {
                                                    buildCallback.done(null, err);
                                                    return;
                                                }

                                                // get some of the profile photo images and use them as our default pics
                                                JSONObject photoImageObj = null;

                                                int numPhotos = Math.min(DEFAULT_NUM_PHOTOS, photoImageObjs.size());
                                                final PhotoItem[] photos = new PhotoItem[MAX_NUM_PHOTOS];

                                                if(numPhotos == 0) {
                                                    user.saveInBackground(new SaveCallback() {
                                                        @Override
                                                        public void done(ParseException e) {
                                                            buildCallback.done(user, null);
                                                        }
                                                    });
                                                    return;
                                                }

                                                targets = new Target[numPhotos];

                                                // download each photo then upload to Parse
                                                for(int i = 0; i < numPhotos; i++) {

                                                    final List<PhotoFile> photoFiles = new ArrayList<PhotoFile>();
                                                    final int index = i;

                                                    targets[index] = new Target() {
                                                        @Override
                                                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                                            final Bitmap mBitmap = bitmap;
                                                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                                            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                                                            final byte[] imageBytes = stream.toByteArray();

                                                            final ParseFile imageFile = new ParseFile("prof_" + index + ".jpg", imageBytes);
                                                            imageFile.saveInBackground(new SaveCallback() {
                                                                @Override
                                                                public void done(ParseException e) {
                                                                    if(e != null) {
                                                                        buildCallback.done(null, e);
                                                                        return;
                                                                    }

                                                                    PhotoFile photoFile = new PhotoFile();
                                                                    photoFile.width = mBitmap.getWidth();
                                                                    photoFile.height = mBitmap.getHeight();
                                                                    photoFile.url = imageFile.getUrl();
                                                                    photoFiles.add(photoFile);

                                                                    PhotoItem photo = new PhotoItem();
                                                                    photo.setPhotoFiles(photoFiles);
                                                                    photos[index] = photo;

                                                                    userImagesCount++;
                                                                    if(userImagesCount >= userImagesThreshold) {
                                                                        if(!userImagesFailed) {

                                                                            List<Object> photoList = new ArrayList<Object>();
                                                                            for(int i = 0; i < photos.length; i++) {
                                                                                photoList.add(photos[i] != null ? photos[i] : JSONObject.NULL);
                                                                            }

                                                                            user.put(PROFILE_KEY_PHOTOS, photoList);

                                                                            // finally we have all our images and we are done building our profile
                                                                            user.saveInBackground(new SaveCallback() {
                                                                                @Override
                                                                                public void done(ParseException e) {
                                                                                    buildCallback.done(user, null);
                                                                                }
                                                                            });
                                                                        } else {
                                                                            buildCallback.done(user, new Exception("Profile images failed to load."));
                                                                        }
                                                                    }
                                                                }
                                                            });
                                                        }

                                                        @Override
                                                        public void onBitmapFailed(Drawable errorDrawable) {
                                                            userImagesFailed = true;
                                                            userImagesCount++;
                                                            if(userImagesCount >= userImagesThreshold) {
                                                                // Attempt to salvage the situation and save with all photos we got

                                                                List<Object> photoList = new ArrayList<Object>();
                                                                for(int i = 0; i < photos.length; i++) {
                                                                    photoList.add(photos[i] != null ? photos[i] : JSONObject.NULL);
                                                                }

                                                                user.put(PROFILE_KEY_PHOTOS, photoList);

                                                                user.saveInBackground(new SaveCallback() {
                                                                    @Override
                                                                    public void done(ParseException e) {
                                                                        buildCallback.done(user, null);
                                                                    }
                                                                });
                                                            }
                                                        }

                                                        @Override
                                                        public void onPrepareLoad(Drawable placeHolderDrawable) {
                                                        }
                                                    };

                                                    photoImageObj = photoImageObjs.get(i);
                                                    if(photoImageObj != null && photoImageObj.optString("source") != null)
                                                        Picasso.with(mContext).load(photoImageObj.optString("source")).into(targets[i]);
                                                }
                                            }
                                        });
                                        break;
                                    }

                                }

                            }
                        }

                        if(skipPhotos) {
                            user.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    buildCallback.done(user, null);
                                }
                            });
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

    /**
     * Gets information about the user's default photos.
     * @param accessToken The current user access token.
     * @param getMePhotosInfoCallback Callback
     */
    public static void getMePhotosInfo(AccessToken accessToken, GetMePhotosInfoCallback getMePhotosInfoCallback) {
        final GetMePhotosInfoCallback mCallback = getMePhotosInfoCallback;

        GraphRequest meRequest = GraphRequest.newMeRequest(
                accessToken,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        if (response.getError() != null || object == null) {
                            mCallback.done(null, -1, response.getError());
                            return;
                        }

                        JSONObject photosObj = object.optJSONObject(FACEBOOK_KEY_PHOTOS);

                        if (photosObj != null) {
                            JSONArray photosData = photosObj.optJSONArray("data");
                            if (photosData != null && photosData.length() > 0) {
                                int photoCount = photosData.length();
                                JSONObject firstPhotoImage = null;

                                JSONObject photoObj = photosData.optJSONObject(0);
                                JSONArray photoImages = photoObj.optJSONArray("images");

                                for (int j = 0; j < photoImages.length(); j++) {
                                    // TODO: How does the images array work? All the image sources returned seem to be the same so I'm just getting the first one here
                                    firstPhotoImage = photoImages.optJSONObject(j);
                                    break;
                                }

                                mCallback.done(firstPhotoImage, photoCount, null);
                            } else {
                                // soft problem or user is not tagged in any photos
                                mCallback.done(null, 0, null);
                            }
                        }
                    }
                });

                Bundle parameters = new Bundle();
                String fieldParamsStr = FACEBOOK_KEY_PHOTOS;
                parameters.putString("fields", fieldParamsStr);
                meRequest.setParameters(parameters);
                meRequest.executeAsync();
            }

            /**
             * Fetches photos from a user's album.
             * @param accessToken The access token for the user.
             * @param albumId The album id. If the album id is not defined then the default photos are fetched.
             * @param fetchPhotosCallback The callback for when the photos are fetched.
             */
            public static void fetchPhotosFromAlbum(AccessToken accessToken, String albumId, FetchPhotosCallback fetchPhotosCallback) {
                final FetchPhotosCallback photosCallback = fetchPhotosCallback;

                GraphRequest photoRequest = GraphRequest.newGraphPathRequest(accessToken, (albumId.length() > 0 ? albumId + "/" : accessToken.getUserId() + "/") + FACEBOOK_KEY_PHOTOS, new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse response) {
                        if (response.getError() != null || response.getJSONObject() == null) {
                            photosCallback.done(null, response.getError());
                        }

                        List<JSONObject> photoImages = new ArrayList<JSONObject>();

                        JSONArray photosData = response.getJSONObject().optJSONArray("data");
                        if (photosData != null) {
                            JSONObject photoObj = null;
                            JSONArray photoImagesData = null;
                            int j;
                            for (int i = 0; i < photosData.length(); i++) {
                                photoObj = photosData.optJSONObject(i);
                                photoImagesData = photoObj.optJSONArray("images");
                                for (j = 0; j < photoImagesData.length(); j++) {
                                    // TODO: How does the images array work? All the image sources returned seem to be the same so I'm just getting the first one here
                                    photoImages.add(photoImagesData.optJSONObject(j));
                                    break;
                                }
                            }
                        }

                        photosCallback.done(photoImages, null);
                    }
                });

                photoRequest.executeAsync();
            }

            public interface GetMePhotosInfoCallback {
                void done(JSONObject firstPhotoImageObj, int numPhotos, Object err);
            }

            public interface FetchPhotosCallback {
                void done(List<JSONObject> photoImageObjs, Object err);
            }

            public interface BuildProfileCallback {
                void done(ParseUser user, Object err);
            }
        }
