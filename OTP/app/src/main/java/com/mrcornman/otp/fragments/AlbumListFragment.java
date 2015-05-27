package com.mrcornman.otp.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.mrcornman.otp.R;
import com.mrcornman.otp.adapters.AlbumAdapter;
import com.mrcornman.otp.models.AlbumItem;
import com.mrcornman.otp.utils.AlbumBuilder;

import java.util.List;

/**
 * Created by Aprusa on 5/21/2015.
 */
public class AlbumListFragment extends Fragment {

    private OnAlbumListInteractionListener onAlbumListInteractionListener;

    private AlbumAdapter aa;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static AlbumListFragment newInstance() {
        AlbumListFragment fragment = new AlbumListFragment();
        return fragment;
    }

    public AlbumListFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // init views
        View rootView = inflater.inflate(R.layout.fragment_album_list, container, false);

        ListView myListView = (ListView) rootView.findViewById(R.id.image_view_albums);

        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onAlbumListInteractionListener.onRequestPhotoGallery(view.getTag().toString());
            }
        });

        aa = new AlbumAdapter(getActivity());
        myListView.setAdapter(aa);

        AlbumBuilder.findCurrentAlbums(getActivity(), new AlbumBuilder.FindAlbumsCallback() {
            @Override
            public void done(List<AlbumItem> albumItems, Object err) {
                if (err != null) {
                    Log.e("AlbumListFragment", "Accessing profile albums from Facebook failed: " + err.toString());
                    return;
                }

                aa.setAlbumItems(albumItems);
            }
        });

        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            onAlbumListInteractionListener = (OnAlbumListInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnAlbumListInteractionListener");
        }
    }

    public interface OnAlbumListInteractionListener {
        void onRequestPhotoGallery(String albumId);
    }
}
