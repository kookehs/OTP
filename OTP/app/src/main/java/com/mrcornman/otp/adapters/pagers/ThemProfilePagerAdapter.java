package com.mrcornman.otp.adapters.pagers;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.mrcornman.otp.fragments.MatchesProfileImageFragment;

/**
 * Created by Jonathan on 5/17/2015.
 */
public class ThemProfilePagerAdapter extends FragmentPagerAdapter {

    public final static int NUM_PAGES = 4;

    private final static int PAGE_MAIN = 0;
    private final static int PAGE_PROFILE_Image_1 = 1;
    private final static int PAGE_PROFILE_Image_2 = 2;
    private final static int PAGE_PROFILE_Image_3 = 3;

    private Context mContext;

    private String mOtherId;

    public ThemProfilePagerAdapter(Context context, FragmentManager fragmentManager, String otherId) {
        super(fragmentManager);

        mContext = context;
        mOtherId = otherId;
    }

    @Override
    public int getCount() {
        return NUM_PAGES;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch(position) {
            case PAGE_MAIN:
                fragment = MatchesProfileImageFragment.newInstance(mOtherId, PAGE_MAIN);
                break;
            case PAGE_PROFILE_Image_1:
                fragment = MatchesProfileImageFragment.newInstance(mOtherId, PAGE_PROFILE_Image_1);
                break;
            case PAGE_PROFILE_Image_2:
                fragment = MatchesProfileImageFragment.newInstance(mOtherId, PAGE_PROFILE_Image_2);
                break;
            case PAGE_PROFILE_Image_3:
                fragment = MatchesProfileImageFragment.newInstance(mOtherId, PAGE_PROFILE_Image_3);
                break;
        }
        return fragment;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        CharSequence result = null;
        switch(position) {
            case PAGE_MAIN:
                result = "Main";
                break;
            case PAGE_PROFILE_Image_1:
                result = "1";
                break;
            case PAGE_PROFILE_Image_2:
                result = "2";
                break;
            case PAGE_PROFILE_Image_3:
                result = "3";
                break;
        }

        return result;
    }
}