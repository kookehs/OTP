package com.mrcornman.otp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.mrcornman.otp.R;
import com.mrcornman.otp.adapters.AlbumAdapter;
import com.mrcornman.otp.models.AlbumItem;
import com.mrcornman.otp.utils.AlbumsBuilder;

import java.util.List;

/**
 * Created by Aprusa on 5/21/2015.
 */
public class AlbumListActivity extends ActionBarActivity {

    private CharSequence mTitle;

    private AlbumAdapter aa;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_album_list);

        mTitle = "Albums";

        // Set up toolbar and tabs
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_ab_back_holo_light_am);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(mTitle);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        ListView myListView = (ListView) findViewById(R.id.image_view);

        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(AlbumListActivity.this, PhotoGalleryActivity.class);
                intent.putExtra("album_id", view.getTag().toString());
                startActivity(intent);
            }
        });

        aa = new AlbumAdapter(this);
        myListView.setAdapter(aa);

        AlbumsBuilder.findCurrentAlbums(getApplicationContext(), new AlbumsBuilder.FindAlbumsCallback() {
            @Override
            public void done(List<AlbumItem> albumItems, Object err) {
                if (err != null) {
                    Log.e("AlbumListActivity", "Accessing profile albums from Facebook failed: " + err.toString());
                    return;
                }

                aa.setAlbumItems(albumItems);
            }
        });
    }
}
