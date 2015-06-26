package com.mrcornman.otp.adapters.pagers;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.mrcornman.otp.R;
import com.mrcornman.otp.fragments.LeaderBoardFragment;
import com.mrcornman.otp.fragments.ScoreFragment;

/**
 * Created by Jonathan on 5/17/2015.
 */
public class ScorePagerAdapter extends FragmentPagerAdapter {

    public final static int NUM_PAGES = 2;

    private final static int PAGE_SCORE = 0;
    private final static int PAGE_LEADERBOARD = 1;

    private Context mContext;

    private String mUserScore;

    public ScorePagerAdapter(Context context, FragmentManager fragmentManager, String userScore) {
        super(fragmentManager);

        mContext = context;
        mUserScore = userScore;
    }

    @Override
    public int getCount() {
        return NUM_PAGES;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch(position) {
            case PAGE_SCORE:
                fragment = ScoreFragment.newInstance(mUserScore);
                break;
            case PAGE_LEADERBOARD:
                fragment = LeaderBoardFragment.newInstance(mUserScore);
                break;
        }
        return fragment;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        CharSequence result = null;
        switch(position) {
            case PAGE_SCORE:
                result = mContext.getString(R.string.action_score);
                break;
            case PAGE_LEADERBOARD:
                result = mContext.getString(R.string.action_leaderboard);
                break;
        }

        return result;
    }
}