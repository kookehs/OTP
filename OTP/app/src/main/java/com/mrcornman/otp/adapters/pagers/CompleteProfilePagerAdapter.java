package com.mrcornman.otp.adapters.pagers;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.mrcornman.otp.R;
import com.mrcornman.otp.fragments.CompleteProfileAboutFragment;
import com.mrcornman.otp.fragments.CompleteProfileArrivalFragment;
import com.mrcornman.otp.fragments.CompleteProfileWantFragment;

/**
 * Created by Jonathan on 5/17/2015.
 */
public class CompleteProfilePagerAdapter extends FragmentPagerAdapter {

    public final static int NUM_PAGES = 3;

    private final static int PAGE_ARRIVAL = 0;
    private final static int PAGE_ABOUT = 1;
    private final static int PAGE_WANT = 2;

    private Context mContext;

    public CompleteProfilePagerAdapter(Context context, FragmentManager fragmentManager) {
        super(fragmentManager);

        mContext = context;
    }

    @Override
    public int getCount() {
        return NUM_PAGES;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch(position) {
            case PAGE_ARRIVAL:
                fragment = CompleteProfileArrivalFragment.newInstance();
                break;
            case PAGE_ABOUT:
                fragment = CompleteProfileAboutFragment.newInstance();
                break;
            case PAGE_WANT:
                fragment = CompleteProfileWantFragment.newInstance();
        }
        return fragment;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        CharSequence result = null;
        switch(position) {
            case PAGE_ARRIVAL:
                result = mContext.getString(R.string.page_title_arrival);
                break;
            case PAGE_ABOUT:
                result = mContext.getString(R.string.page_title_about);
                break;
            case PAGE_WANT:
                result = mContext.getString(R.string.page_title_want);
                break;
        }

        return result;
    }
}