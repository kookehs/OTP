package com.mrcornman.otp.adapters.pagers;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.mrcornman.otp.R;
import com.mrcornman.otp.fragments.MatchStatsFragment;

/**
 * Created by Jonathan on 5/17/2015.
 */
public class MakerMatchPagerAdapter extends FragmentPagerAdapter {

    public final static int NUM_PAGES = 1;

    private final static int PAGE_STATS = 0;

    private Context mContext;

    private String mMatchId;

    public MakerMatchPagerAdapter(Context context, FragmentManager fragmentManager, String matchId) {
        super(fragmentManager);

        mContext = context;
        mMatchId = matchId;
    }

    @Override
    public int getCount() {
        return NUM_PAGES;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch(position) {
            case PAGE_STATS:
                fragment = MatchStatsFragment.newInstance(mMatchId);
                break;
        }
        return fragment;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        CharSequence result = null;
        switch(position) {
            case PAGE_STATS:
                result = mContext.getString(R.string.action_stats);
                break;
        }

        return result;
    }
}