package com.mrcornman.otp.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.mrcornman.otp.R;
import com.mrcornman.otp.activities.ImageListActivity;
import com.mrcornman.otp.activities.MainActivity;
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
public class ProfileImageBuilder {

    public final static int DEFAULT_NUM_PHOTOS = 2;

    public final static String PROFILE_KEY_NAME = "name";
    public final static String PROFILE_KEY_GENDER = "gender";
    public final static String PROFILE_KEY_BIRTHDATE = "birthdate";
    public final static String PROFILE_KEY_INTERESTED_IN = "interested_in";
    public final static String PROFILE_KEY_PHOTOS = "photos";

    private final static String FACEBOOK_KEY_NAME = "first_name";
    private final static String FACEBOOK_KEY_GENDER = "gender";
    private final static String FACEBOOK_KEY_BIRTHDAY = "birthday";
    private final static String FACEBOOK_KEY_LOCATION = "location";
    private final static String FACEBOOK_KEY_INTERESTED_IN = "interested_in";
    private final static String FACEBOOK_KEY_ALBUMS = "albums";
    private final static String FACEBOOK_KEY_PHOTOS = "photos";

    private static Target[] targets;

    private static boolean userImagesFailed = false;
    private static int userImagesCount = 0;
    private static int userImagesThreshold = 0;

    static private class ListElement {
        ListElement() {};

        public String textLabel;
    }

    static private ArrayList<ListElement> aList;

    private static class MyAdapter extends ArrayAdapter<ListElement> {

        int resource;
        Context context;

        public MyAdapter(Context _context, int _resource, List<ListElement> items) {
            super(_context, _resource, items);
            resource = _resource;
            context = _context;
            this.context = _context;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LinearLayout newView;

            ListElement w = getItem(position);

            // Inflate a new view if necessary.
            if (convertView == null) {
                newView = new LinearLayout(getContext());
                String inflater = Context.LAYOUT_INFLATER_SERVICE;
                LayoutInflater vi = (LayoutInflater) getContext().getSystemService(inflater);
                vi.inflate(resource,  newView, true);
            } else {
                newView = (LinearLayout) convertView;
            }

            // Fills in the view.
            TextView tv = (TextView) newView.findViewById(R.id.itemText);

            tv.setText(w.textLabel);

            // Set a listener for the whole list item.
            newView.setTag(position);
            newView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Gets the ID of the message.
                    int idx = ((Integer) v.getTag()).intValue();
                    // WIll take us to the images from album chosen
                }
            });

            return newView;
        }
    }

    static public MyAdapter aa;

    public static void initialize(ImageListActivity main, LinearLayout root){
        aList = new ArrayList<>();
        aa = new MyAdapter(main, R.layout.image_elements, aList);
        ListView myListView = (ListView) root.findViewById(R.id.image_view);
        myListView.setAdapter(aa);
        aa.notifyDataSetChanged();
    }

    /**
     * Builds the current user's profile and saves it to the database.
     * @param context The context of the method.
     * @param buildProfileCallback To execute once the profile is built or there is an error.
     */
    public static void buildCurrentImages(Context context, ProfileImageBuilder.BuildProfileCallback buildProfileCallback) {

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
                            buildCallback.done(user, response.getError());
                            return;
                        }

                        /*
                         * Profile Pictures
                         */

                        // first reset the book keeping for loaded user images
                        userImagesFailed = false;
                        userImagesCount = 0;

                        JSONObject albumsObj = object.optJSONObject(FACEBOOK_KEY_ALBUMS);
                        // Fills aList, so we can fill the listView.
                        aList.clear();
                        Log.i("stuff-----", albumsObj + "bhjshjdbfnlbflvnzxm");
                        Log.i("stuff-----", albumsObj+" ");
                        Log.i("stuff-----", albumsObj+"snnss");

                        ListElement ael = new ListElement();

                        if (albumsObj != null) {

                            // first get the list of albums
                            JSONArray albumsData = albumsObj.optJSONArray("data");
                            if (albumsData != null && albumsData.length() > 0) {
                                // now find the profile album
                                JSONObject albumObj = null;
                                for (int i = 0; i < albumsData.length(); i++) {
                                    albumObj = albumsData.optJSONObject(i);
                                    //ael.textLabel = albumObj.optString("type");
                                    ael.textLabel = "fjdnfjbhdsbvzjhvbz ygshgddshvbh";
                                    aList.add(ael);
                                }
                                ael.textLabel = "fjdnfjbhdsbvzjhvbz ygshgddshvbh";
                                aList.add(ael);
                                Log.i("stuff-----", "bhjshjdbfnlbflvnzxm");
                                Log.i("stuff-----", "bhjshjdrbfnlbflvnzxm");
                                Log.i("stuff-----", "bhjshjrdbfnlbflvnzxm");
                                Log.i("stuff-----", "bhjshmjdbfnlbflvnzxm");
                                Log.i("stuff-----", "bhjshjdb fnlbflvnzxm");Log.i("stuff-----", "bhjjshjdbfnlbflvnzxm");
                                Log.i("stuff-----", "bhjshjdbfsnlbflvnzxm");
                                Log.i("stuff-----", "bhjshjdbfnhlbflvnzxm");


                                aa.notifyDataSetChanged();
                            }
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
     * Fetches photos from a user's album.
     * @param accessToken The access token for the user.
     * @param albumId The album id.
     * @param fetchPhotosCallback The callback for when the photos are fetched.
     */
    public static void fetchPhotosFromAlbum(AccessToken accessToken, String albumId, FetchPhotosCallback fetchPhotosCallback) {
        final FetchPhotosCallback photosCallback = fetchPhotosCallback;

        GraphRequest photoRequest = GraphRequest.newGraphPathRequest(accessToken, albumId + "/" + FACEBOOK_KEY_PHOTOS, new GraphRequest.Callback() {
            @Override
            public void onCompleted(GraphResponse response) {
                if (response.getError() != null || response.getJSONObject() == null) {
                    photosCallback.done(null, response.getError());
                }

                List<JSONObject> photos = new ArrayList<JSONObject>();

                JSONArray photosData = response.getJSONObject().optJSONArray("data");
                if (photosData != null) {
                    JSONObject photoObj = null;
                    JSONArray photoImages = null;
                    int j;
                    for (int i = 0; i < photosData.length(); i++) {
                        photoObj = photosData.optJSONObject(i);
                        photoImages = photoObj.optJSONArray("images");
                        for (j = 0; j < photoImages.length(); j++) {
                            // TODO: How does the images array work? All the image sources returned seem to be the same so I'm just getting the first one here
                            photos.add(photoImages.optJSONObject(j));
                            break;
                        }
                    }
                }

                photosCallback.done(photos, null);
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
