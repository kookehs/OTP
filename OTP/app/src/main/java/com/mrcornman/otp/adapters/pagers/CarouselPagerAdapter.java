package com.mrcornman.otp.adapters.pagers;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.mrcornman.otp.R;
import com.mrcornman.otp.fragments.CarouselImageFragment;

/**
 * Created by Jonathan on 5/17/2015.
 */
public class CarouselPagerAdapter extends FragmentPagerAdapter {

    private Context mContext;
    private String[] mUrls;

    public CarouselPagerAdapter(Context context, FragmentManager fragmentManager, String[] urls) {
        super(fragmentManager);

        mContext = context;
        mUrls = urls;
    }

    @Override
    public int getCount() {
        return mUrls.length;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        if(mUrls[position] != null)
            fragment = CarouselImageFragment.newInstance(mUrls[position]);
        else
            fragment = CarouselImageFragment.newInstance(R.drawable.com_facebook_profile_picture_blank_portrait);

        return fragment;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "Photo";
    }
}