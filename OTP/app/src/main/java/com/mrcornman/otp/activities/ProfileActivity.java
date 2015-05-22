package com.mrcornman.otp.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.mrcornman.otp.R;
import com.mrcornman.otp.adapters.MainPagerAdapter;
import com.mrcornman.otp.fragments.ClientListFragment;
import com.mrcornman.otp.fragments.GameFragment;
import com.mrcornman.otp.fragments.MakerListFragment;
import com.mrcornman.otp.fragments.NavFragment;
import com.mrcornman.otp.models.PhotoFile;
import com.mrcornman.otp.models.PhotoItem;
import com.mrcornman.otp.services.MessageService;
import com.mrcornman.otp.utils.PrettyTime;
import com.mrcornman.otp.utils.ProfileBuilder;
import com.parse.GetCallback;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import org.apache.http.ParseException;

import java.util.List;

public class ProfileActivity extends ActionBarActivity {

        private CharSequence mTitle;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_profile);

            mTitle = getTitle();

            // Set up toolbar and tabs
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            toolbar.setNavigationIcon(R.drawable.ic_ab_back_holo_light_am);
            setSupportActionBar(toolbar);
            android.support.v7.app.ActionBar actionBar = getSupportActionBar();
            actionBar.setTitle(mTitle);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);

            TextView nameText = (TextView) findViewById(R.id.name_text);
            TextView ageText = (TextView) findViewById(R.id.age_text);

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

            // and this is how you can get data from the user profile
            nameText.setText(user.getString(ProfileBuilder.PROFILE_KEY_NAME));
            ageText.setText(PrettyTime.getAgeFromBirthDate(user.getDate(ProfileBuilder.PROFILE_KEY_BIRTHDATE)) + "");

            // and this is how you grab an image from the user profile and put it into image view
            List<PhotoItem> photoItems = user.getList(ProfileBuilder.PROFILE_KEY_PHOTOS);
            if(photoItems != null && photoItems.size() > 0) {
                PhotoItem mainPhoto = photoItems.get(0);
                mainPhoto.fetchIfNeededInBackground(new GetCallback<PhotoItem>() {
                    @Override
                    public void done(PhotoItem photoItem, com.parse.ParseException e) {
                        PhotoFile mainFile = photoItem.getPhotoFiles().get(0);
                        Picasso.with(ProfileActivity.this.getApplicationContext()).load(mainFile.url).fit().centerCrop().into(pictureImage);
                    }
                });
            }
        }

   /* @Override
    protected void onResume() {
        super.onResume();
        ImageView profilePic = (ImageView) findViewById(R.id.picture_image);
        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("change", "your picture");
            }
        });
    }*/
}