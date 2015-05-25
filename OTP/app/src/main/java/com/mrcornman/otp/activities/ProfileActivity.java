package com.mrcornman.otp.activities;

import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mrcornman.otp.R;
import com.mrcornman.otp.models.PhotoFile;
import com.mrcornman.otp.models.PhotoItem;
import com.mrcornman.otp.utils.AlbumsBuilder;
import com.mrcornman.otp.utils.PrettyTime;
import com.mrcornman.otp.utils.ProfileBuilder;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
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
            ActionBar actionBar = getSupportActionBar();
            actionBar.setTitle(mTitle);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);

            displayProfileImages(0, R.id.picture_image_0, R.id.picture_frame_0);
            displayProfileImages(1, R.id.picture_image_1, R.id.picture_frame_1);
            displayProfileImages(2, R.id.picture_image_2, R.id.picture_frame_2);
            displayProfileImages(3, R.id.picture_image_3, R.id.picture_frame_3);

        }

    private void displayProfileImages(final int imageSlot, int imageNum, int frameNum){
        TextView nameText = (TextView) findViewById(R.id.name_text);
        TextView ageText = (TextView) findViewById(R.id.age_text);

        final FrameLayout pictureContainer = (FrameLayout) findViewById(frameNum);
        final ImageView pictureImage = (ImageView) findViewById(imageNum);

        ParseUser user = ParseUser.getCurrentUser();

        nameText.setText(user.getString(ProfileBuilder.PROFILE_KEY_NAME));
        ageText.setText(PrettyTime.getAgeFromBirthDate(user.getDate(ProfileBuilder.PROFILE_KEY_BIRTHDATE)) + "");


        try {
            Intent newPicture = getIntent();
            String newURL = newPicture.getStringExtra("url");
            Log.d("url of pic", "yoo: " + newURL);
            if (newURL != null) {
                changePicture(newURL);
                return;
            }
        }
        catch (Exception err){
            Log.e("ProfileActivity", "Can't change user image" + err);
        }

        pictureImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ns = new Intent(ProfileActivity.this, AlbumListActivity.class);
                startActivity(ns);
            }
        });

        List<PhotoItem> photoItems = user.getList(ProfileBuilder.PROFILE_KEY_PHOTOS);
        if(photoItems != null && photoItems.size() > 0) {
            if(imageSlot >= photoItems.size()) return;
            PhotoItem mainPhoto = photoItems.get(imageSlot);
            mainPhoto.fetchIfNeededInBackground(new GetCallback<PhotoItem>() {
                @Override
                public void done(PhotoItem photoItem, com.parse.ParseException e) {
                    PhotoFile mainFile = photoItem.getPhotoFiles().get(0);
                    Picasso.with(ProfileActivity.this.getApplicationContext()).load(mainFile.url).fit().centerCrop().into(pictureImage);
                }
            });
        }
    }

    private void changePicture(String newURL){
        Log.i("ProfileActivity", "hey");
        newURL = null;
       // displayProfileImages
        /*String urlChange = newURL;
        Target target = new Target() {
            final AlbumsBuilder.PhotoCallback coverPhotoCallback = photoCallback;
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
                            coverPhotoCallback.done(null, e);
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
                                List<PhotoItem> photoList = new ArrayList<>();

                                for(PhotoItem temp : photos)
                                    if(temp != null) photoList.add(temp);

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
            public void onBitmapFailed(Drawable
            errorDrawable) {
                userImagesFailed = true;
                userImagesCount++;
                if(userImagesCount >= userImagesThreshold) {
                    // Attempt to salvage the situation and save with all photos we got
                    List<PhotoItem> photoList = new ArrayList<>();

                    for(PhotoItem temp : photos)
                        if(temp != null) photoList.add(temp);

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
        };*/
    }

}