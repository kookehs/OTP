package com.mrcornman.otp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.mrcornman.otp.R;
import com.mrcornman.otp.adapters.AlbumAdapter;
import com.mrcornman.otp.adapters.PhotoGalleryAdapter;
import com.mrcornman.otp.models.AlbumItem;
import com.mrcornman.otp.models.PhotoFile;
import com.mrcornman.otp.utils.AlbumsBuilder;
import com.mrcornman.otp.utils.ProfileBuilder;
import com.parse.ParseUser;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by Aprusa on 5/21/2015.
 */
public class PhotoGalleryActivity extends ActionBarActivity {

    private CharSequence mTitle;

    private PhotoGalleryAdapter aa;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_photo_gallery);

        mTitle = "Photo Gallery";

        final Intent intent = getIntent();
        String albumId = intent.getStringExtra("album_id");

        // Set up toolbar and tabs
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_ab_back_holo_light_am);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(mTitle);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        GridView gridView = (GridView) findViewById(R.id.grid_view);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String url = view.getTag().toString();
                Toast.makeText(PhotoGalleryActivity.this, "Clicked: " + url, Toast.LENGTH_LONG).show();
                Intent profilePage = new Intent(PhotoGalleryActivity.this, ProfileActivity.class);
                profilePage.putExtra("url", url);
                NavUtils.navigateUpTo(PhotoGalleryActivity.this, profilePage);
            }
        });

        aa = new PhotoGalleryAdapter(this);
        gridView.setAdapter(aa);

        AlbumsBuilder.findCurrentPhotosFromAlbum(albumId, new AlbumsBuilder.FindPhotosCallback() {
            @Override
            public void done(List<PhotoFile> photoFiles, Object err) {
                if (err != null) {
                    Log.e("PhotoGalleryActivity", "Accessing album photos from Facebook failed: " + err.toString());
                    return;
                }

                aa.setPhotoFileItems(photoFiles);
            }
        });
    }
}
