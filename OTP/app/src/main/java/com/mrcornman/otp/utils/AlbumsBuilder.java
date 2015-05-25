package com.mrcornman.otp.utils;

import android.content.Context;
import android.os.Bundle;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.mrcornman.otp.models.AlbumItem;
import com.mrcornman.otp.models.PhotoFile;
import com.mrcornman.otp.models.PhotoItem;
import com.parse.ParseException;
import com.parse.SaveCallback;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aprusa on 5/23/2015.
 */
public class AlbumsBuilder {

    public final static int DEFAULT_NUM_ALBUMS = 25;

    public final static String PROFILE_KEY_ALBUMS = "album";

    private final static String FACEBOOK_KEY_ALBUMS = "albums";
    private final static String FACEBOOK_KEY_PHOTOS = "photos";

    private static boolean albumsFailed = false;
    private static int albumsCount = 0;
    private static int albumsThreshold = 0;


    /**
     * Builds the current user's profile and saves it to the database.
     * @param context The context of the method.
     * @param findAlbumsCallback To execute once the profile is built or there is an error.
     */
    public static void findCurrentAlbums(Context context, final FindAlbumsCallback findAlbumsCallback) {

        final Context mContext = context;
        final FindAlbumsCallback buildCallback = findAlbumsCallback;
        final AccessToken accessToken = AccessToken.getCurrentAccessToken();

        final List<AlbumItem> albumItems = new ArrayList<>();

        albumsCount = 0;
        albumsThreshold = 0;
        albumsFailed = false;

        GraphRequest meRequest = GraphRequest.newMeRequest(
                accessToken,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        if (response.getError() != null || object == null) {
                            buildCallback.done(null, response.getError());
                            return;
                        }

                        int i = 0;

                    /*
                     * Albums
                     */
                    JSONObject albumsObj = object.optJSONObject(FACEBOOK_KEY_ALBUMS);

                    if (albumsObj != null) {

                        // first get the list of albums
                        JSONArray albumsData = albumsObj.optJSONArray("data");

                        if (albumsData != null && albumsData.length() > 0) {
                            // now find the profile album
                            JSONObject albumObj = null;

                            albumsThreshold = albumsData.length();

                            for (i = 0; i < albumsThreshold; i++) {
                                albumObj = albumsData.optJSONObject(i);
                                final AlbumItem albumItem = new AlbumItem();

                                albumItem.albumId = albumObj.optString("id");
                                albumItem.name = albumObj.optString("name");
                                albumItem.count = albumObj.optInt("count");
                                final String coverPhotoId = albumObj.optString("cover_photo");

                                // fetch all the photo images from the profile album
                                fetchCoverPhoto(accessToken, coverPhotoId, new PhotoCallback() {
                                    @Override
                                    public void done(JSONObject photoImageObj, Object err) {
                                        if (err != null || photoImageObj == null) {
                                            buildCallback.done(null, err);
                                            return;
                                        }

                                        PhotoFile coverPhoto = new PhotoFile();
                                        coverPhoto.height = photoImageObj.optInt("height");
                                        coverPhoto.width = photoImageObj.optInt("width");
                                        coverPhoto.url = photoImageObj.optString("source");
                                        albumItem.coverPhotoFile = coverPhoto;

                                        albumItems.add(albumItem);
                                        albumsCount++;

                                        if(albumsCount >= albumsThreshold) {
                                            findAlbumsCallback.done(albumItems, null);
                                        }
                                    }
                                });
                            }
                        }
                    }
                }
            });

        Bundle parameters = new Bundle();
        String fieldParamsStr = FACEBOOK_KEY_ALBUMS;
        parameters.putString("fields", fieldParamsStr);
        meRequest.setParameters(parameters);
        meRequest.executeAsync();
    }

    public static void findCurrentPhotosFromAlbum(String albumId, FindPhotosCallback callback) {

        final FindPhotosCallback mCallback = callback;

        ProfileBuilder.fetchPhotosFromAlbum(AccessToken.getCurrentAccessToken(), albumId, new ProfileBuilder.FetchPhotosCallback() {
            @Override
            public void done(List<JSONObject> photoImages, Object err) {
                if (err != null || photoImages == null) {
                    mCallback.done(null, err);
                    return;
                }

                final List<PhotoFile> photoFiles = new ArrayList<PhotoFile>();

                if(photoImages.size() == 0) {
                    mCallback.done(null, null);
                }

                JSONObject photoImageObj = null;

                // download each photo then upload to Parse
                for(int i = 0; i < photoImages.size(); i++) {
                    photoImageObj = photoImages.get(i);

                    PhotoFile photoFile = new PhotoFile();
                    photoFile.height = photoImageObj.optInt("height");
                    photoFile.width = photoImageObj.optInt("width");
                    photoFile.url = photoImageObj.optString("source");

                    photoFiles.add(photoFile);
                }

                mCallback.done(photoFiles, null);
            }
        });
    }

    /**
     * Fetches albums from a user's album.
     * @param accessToken The access token for the user.
     * @param photoId The album id.
     * @param photoCallback The callback for when the albums are fetched.
     */
    public static void fetchCoverPhoto(AccessToken accessToken, final String photoId, PhotoCallback photoCallback) {
        final PhotoCallback coverPhotoCallback = photoCallback;

        GraphRequest photoRequest = GraphRequest.newGraphPathRequest(accessToken, photoId, new GraphRequest.Callback() {
            @Override
            public void onCompleted(GraphResponse response) {
                if (response.getError() != null || response.getJSONObject() == null) {
                    coverPhotoCallback.done(null, response.getError());
                }

                JSONObject coverPhotoImageObj = null;

                JSONObject coverPhotoObj = response.getJSONObject();
                JSONArray photoImages = coverPhotoObj.optJSONArray("images");
                for(int i = 0; i < photoImages.length(); i++){
                    coverPhotoImageObj = photoImages.optJSONObject(i);
                }

                coverPhotoCallback.done(coverPhotoImageObj, null);
            }
        });

        photoRequest.executeAsync();
    }

    public interface PhotoCallback {
        void done(JSONObject photo, Object err);
    }

    public interface FindAlbumsCallback {
        void done(List<AlbumItem> albums, Object err);
    }

    public interface FindPhotosCallback {
        void done(List<PhotoFile> photoFiles, Object err);
    }
}
