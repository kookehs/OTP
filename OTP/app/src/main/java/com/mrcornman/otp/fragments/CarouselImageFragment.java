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

    public CarouselImageFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile_photo_scroll, container, false);

        if(getArguments() == null) return rootView;

        final ImageView pictureImage = (ImageView) rootView.findViewById(R.id.picture_image);

        final String url = getArguments().getString(KEY_URL);

        if (getActivity() != null)
            Picasso.with(getActivity().getApplicationContext()).load(url).fit().centerCrop().into(pictureImage);

        return rootView;

    }
}
