package com.mrcornman.otp.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.mrcornman.otp.R;
import com.mrcornman.otp.adapters.PhotoGalleryAdapter;
import com.mrcornman.otp.models.PhotoFile;
import com.mrcornman.otp.utils.AlbumsBuilder;

import java.util.List;

/**
 * Created by Aprusa on 5/21/2015.
 */
public class PhotoGalleryFragment extends Fragment {

    private final static String KEY_ALBUM_ID = "album_id";

    private PhotoGalleryAdapter aa;

    private OnPhotoGalleryInteractionListener onPhotoGalleryInteractionListener;

    public static PhotoGalleryFragment newInstance(String albumId) {
        PhotoGalleryFragment fragment = new PhotoGalleryFragment();

        Bundle arguments = new Bundle();
        arguments.putString(KEY_ALBUM_ID, albumId);
        fragment.setArguments(arguments);

        return fragment;
    }

    public PhotoGalleryFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootview = inflater.inflate(R.layout.fragment_photo_gallery, container, false);

        if(getArguments() == null) return rootview;

        String albumId = getArguments().getString(KEY_ALBUM_ID);

        GridView gridView = (GridView) rootview.findViewById(R.id.grid_view);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String url = view.getTag().toString();
                onPhotoGalleryInteractionListener.onRequestPhoto(url);
            }
        });

        aa = new PhotoGalleryAdapter(getActivity());
        gridView.setAdapter(aa);

        AlbumsBuilder.findCurrentPhotosFromAlbum(albumId, new AlbumsBuilder.FindPhotosCallback() {
            @Override
            public void done(List<PhotoFile> photoFiles, Object err) {
                if (err != null) {
                    Log.e("PhotoGalleryFragment", "Accessing album photos from Facebook failed: " + err.toString());
                    return;
                }

                aa.setPhotoFileItems(photoFiles);
            }
        });

        return rootview;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            onPhotoGalleryInteractionListener = (OnPhotoGalleryInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnPhotoGalleryInteractionListener");
        }
    }

    public interface OnPhotoGalleryInteractionListener {
        void onRequestPhoto(String url);
    }
}
