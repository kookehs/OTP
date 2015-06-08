package com.mrcornman.otp.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.mrcornman.otp.R;
import com.squareup.picasso.Picasso;

/**
 * Created by Aprusa on 6/3/2015.
 */
public class CarouselImageFragment extends Fragment {

    private final static String KEY_URL = "url";
    private final static String KEY_RESOURCE_ID = "resource_id";

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static CarouselImageFragment newInstance(String url) {
        CarouselImageFragment fragment = new CarouselImageFragment();

        // arguments
        Bundle arguments = new Bundle();
        arguments.putString(KEY_URL, url);
        fragment.setArguments(arguments);

        return fragment;
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number. Overload to allow resource ids.
     */
    public static CarouselImageFragment newInstance(int resourceId) {
        CarouselImageFragment fragment = new CarouselImageFragment();

        // arguments
        Bundle arguments = new Bundle();
        arguments.putInt(KEY_RESOURCE_ID, resourceId);
        fragment.setArguments(arguments);

        return fragment;
    }

    public CarouselImageFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.carousel_photo_item, container, false);

        if(getArguments() == null) return rootView;

        final ImageView pictureImage = (ImageView) rootView.findViewById(R.id.picture_image);

        final String url = getArguments().getString(KEY_URL);
        final int resourceId = getArguments().getInt(KEY_RESOURCE_ID);

        if (getActivity() != null) {
            if (url != null)
                Picasso.with(getActivity().getApplicationContext()).load(url).fit().centerCrop().into(pictureImage);
            else
                Picasso.with(getActivity().getApplicationContext()).load(resourceId).fit().centerCrop().into(pictureImage);
        }

        return rootView;

    }
}
