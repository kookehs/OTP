package com.mrcornman.otp.adapters.pagers;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.mrcornman.otp.R;
import com.mrcornman.otp.fragments.MessagingFragment;
import com.mrcornman.otp.fragments.ThemProfileFragment;

/**
 * Created by Jonathan on 5/17/2015.
 */
public class ClientMatchPagerAdapter extends FragmentPagerAdapter {

    public final static int NUM_PAGES = 2;

    private final static int PAGE_MESSAGING = 0;
    private final static int PAGE_PROFILE = 1;

    private Context mContext;

    private String mOtherId;

    public ClientMatchPagerAdapter(Context context, FragmentManager fragmentManager, String otherId) {
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
            case PAGE_MESSAGING:
                fragment = MessagingFragment.newInstance(mOtherId);
                break;
            case PAGE_PROFILE:
                fragment = ThemProfileFragment.newInstance(mOtherId);
                break;
        }
        return fragment;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        CharSequence result = null;
        switch(position) {
            case PAGE_MESSAGING:
                result = mContext.getString(R.string.action_messaging);
                break;
            case PAGE_PROFILE:
                result = mContext.getString(R.string.action_profile);
                break;
        }

        return result;
    }
}