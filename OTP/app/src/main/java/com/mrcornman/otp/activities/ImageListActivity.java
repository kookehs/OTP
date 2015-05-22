package com.mrcornman.otp.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mrcornman.otp.R;
import com.mrcornman.otp.models.PhotoItem;
import com.mrcornman.otp.utils.ProfileBuilder;
import com.parse.GetCallback;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aprusa on 5/21/2015.
 */
public class ImageListActivity extends ActionBarActivity {

    private CharSequence mTitle;

    static private class ListElement {
        ListElement() {};

        public String textLabel;
        public Drawable itemView;
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
            ImageView iv = (ImageView)newView.findViewById(R.id.itemImage);

            tv.setText(w.textLabel);
           // iv.setImageDrawable(w.itemView);

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_list);

        mTitle = getTitle();

        // Set up toolbar and tabs
        Button toolbar = (Button) findViewById(R.id.backPro);
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ns = new Intent(ImageListActivity.this, ProfileActivity.class);
                ns.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(ns);
                Log.i("ToolBAr", "Go back to profile");
            }
        });

        aList = new ArrayList<>();
        aa = new MyAdapter(this, R.layout.image_elements, aList);
        ListView myListView = (ListView) findViewById(R.id.image_view);
        myListView.setAdapter(aa);
        aa.notifyDataSetChanged();


    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        String result = settings.getString("Profile", null);

        try {
            displayResult();
        } catch (Exception e) {
            // Removes settings that can't be correctly decoded.
            Log.w("ImageFragment", "Failed to display old messages: " + result + " " + e);
            SharedPreferences.Editor editor = settings.edit();
            editor.remove("Profile");
            editor.apply();
        }
    }

    private void displayResult() {
        // Fills aList, so we can fill the listView.
        //aList.clear();

       // ListElement ael = new ListElement();

        final FrameLayout pictureContainer = (FrameLayout) findViewById(R.id.picture_container);
        final ImageView pictureImage = (ImageView) findViewById(R.id.picture_image);

        // need this to get the finalized width of the framelayout after the match_parent width is calculated
        pictureContainer.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int pictureWidth = pictureContainer.getWidth();
                pictureContainer.setLayoutParams(new RelativeLayout.LayoutParams(pictureWidth, pictureWidth));
            }
        });

        // NOTE: This is how you get the current user once they've logged in from Facebook
        ParseUser user = ParseUser.getCurrentUser();

        // and this is how you grab an image from the user profile and put it into image view
        List<PhotoItem> photoItems = user.getList(ProfileBuilder.PROFILE_KEY_PHOTOS);
        if(photoItems != null && photoItems.size() > 0) {
            for(int i = 0; i < photoItems.size(); i++) {
                PhotoItem mainPhoto = photoItems.get(i);
                mainPhoto.fetchIfNeededInBackground(new GetCallback<PhotoItem>() {
                    @Override
                    public void done(PhotoItem photoItem, com.parse.ParseException e) {
                        aList.clear();

                        ListElement ael = new ListElement();

                        JSONObject albumsObj = photoItem.getJSONObject("albums");

                        JSONArray albumsData = albumsObj.optJSONArray("data");
                        if (albumsData != null && albumsData.length() > 0) {

                            // now find the profile album
                            JSONObject albumObj = null;
                            //for (i = 0; i < albumsData.length(); i++) {
                                albumObj = albumsData.optJSONObject(0);
                            Picasso.with(ImageListActivity.this.getApplicationContext()).load(albumObj.optString("type")).fit().centerCrop().into(pictureImage);
                            ael.textLabel = albumObj.optString("type");
                            //ael.itemView = mainFile.url;
                            aList.add(ael);
                            Log.i("list_photo", albumObj + "");
                        }

                        aa.notifyDataSetChanged();
                    }
                });
            }
        }

        //for (Map.Entry<String, String> newNames : URLS.newsURLS.entrySet()) {
        //
          //  ael.textLabel = newNames.getKey();
           // aList.add(ael);
        //}
        //aa.notifyDataSetChanged();
    }
}
