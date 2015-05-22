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
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.mrcornman.otp.R;
import com.mrcornman.otp.models.PhotoFile;
import com.mrcornman.otp.models.PhotoItem;
import com.mrcornman.otp.utils.PrettyTime;
import com.mrcornman.otp.utils.ProfileBuilder;
import com.mrcornman.otp.utils.ProfileImageBuilder;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Aprusa on 5/21/2015.
 */
public class ImageListActivity extends ActionBarActivity {

    private CharSequence mTitle;

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
            PhotoItem mainPhoto = photoItems.get(0);
            mainPhoto.fetchIfNeededInBackground(new GetCallback<PhotoItem>() {
                @Override
                public void done(PhotoItem photoItem, com.parse.ParseException e) {
                    PhotoFile mainFile = photoItem.getPhotoFiles().get(0);
                    Picasso.with(ImageListActivity.this.getApplicationContext()).load(mainFile.url).fit().centerCrop().into(pictureImage);
                }
            });
        }
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

        LinearLayout root = (LinearLayout)findViewById(R.id.list_elements);
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
