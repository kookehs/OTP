package com.mrcornman.otp.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.mrcornman.otp.R;
import com.mrcornman.otp.utils.ProfileBuilder;
import com.mrcornman.otp.utils.ProfileImageBuilder;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Aprusa on 5/21/2015.
 */
public class ImageListActivity extends ActionBarActivity {

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

    private CharSequence mTitle;

    private class ListElement {
        ListElement() {};

        public String textLabel;
    }

    static private ArrayList<ListElement> aList;

    private class MyAdapter extends ArrayAdapter<ListElement> {

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

    static private MyAdapter aa;

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

        LinearLayout root = (LinearLayout)findViewById(R.layout.image_elements);
        ProfileImageBuilder.initialize(ImageListActivity.this, root);
        ProfileImageBuilder.buildCurrentImages(this, new ProfileImageBuilder.BuildProfileCallback() {
            @Override
            public void done(final ParseUser user, Object err) {
                if (err != null) {
                    Log.e("LoginActivity", "Creating profile from Facebook failed: " + err.toString());
                    ParseFacebookUtils.unlinkInBackground(user, new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            user.deleteInBackground();
                        }
                    });
                }

            }
        });

        //for (Map.Entry<String, String> newNames : URLS.newsURLS.entrySet()) {
        //
          //  ael.textLabel = newNames.getKey();
           // aList.add(ael);
        //}
        //aa.notifyDataSetChanged();
    }
}
