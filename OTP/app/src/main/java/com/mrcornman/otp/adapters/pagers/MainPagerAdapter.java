package com.mrcornman.otp.adapters.pagers;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.astuetz.PagerSlidingTabStrip;
import com.mrcornman.otp.R;
import com.mrcornman.otp.fragments.ClientListFragment;
import com.mrcornman.otp.fragments.GameFragment;
import com.mrcornman.otp.fragments.MakerListFragment;

/**
 * Created by Jonathan on 5/17/2015.
 */
public class MainPagerAdapter extends FragmentPagerAdapter implements PagerSlidingTabStrip.CustomTabProvider {

    public final static int NUM_PAGES = 3;

    public final static int PAGE_GAME = 0;
    public final static int PAGE_CLIENT = 1;
    public final static int PAGE_MAKER = 2;

    private Context mContext;

    public MainPagerAdapter(Context context, FragmentManager fragmentManager) {
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
            case PAGE_GAME:
                fragment = Fragment.instantiate(mContext, GameFragment.class.getName());
                break;
            case PAGE_CLIENT:
                fragment = Fragment.instantiate(mContext, ClientListFragment.class.getName());
                break;
            case PAGE_MAKER:
                fragment = Fragment.instantiate(mContext, MakerListFragment.class.getName());
                break;
        }
        return fragment;
    }

    @Override
    public int getItemPosition(Object object) {
        if(object instanceof ClientListFragment)
            ((ClientListFragment)object).refreshList();
        else if(object instanceof MakerListFragment)
            ((MakerListFragment)object).refreshList();

        return super.getItemPosition(object);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        CharSequence result = null;
        switch(position) {
            case PAGE_GAME:
                result = mContext.getString(R.string.action_game);
                break;
            case PAGE_CLIENT:
                result = mContext.getString(R.string.action_client);
                break;
            case PAGE_MAKER:
                result = mContext.getString(R.string.action_maker);
                break;
        }

        return result;
    }

    @Override
    public View getCustomTabView(ViewGroup parent, int position) {

        //View tabView = LayoutInflater.from(mContext).inflate(R.layout.psts_tab, parent, false);

        View tabView = LayoutInflater.from(mContext).inflate(R.layout.tab_item, parent, false);

        ImageView tabIconImage = (ImageView) tabView.findViewById(R.id.icon_image);

        switch(position) {
            case PAGE_GAME:
                tabIconImage.setImageResource(R.mipmap.ic_notification);
                break;
            case PAGE_CLIENT:
                tabIconImage.setImageResource(R.mipmap.ic_action_client);
                break;
            case PAGE_MAKER:
                tabIconImage.setImageResource(R.mipmap.ic_action_maker);
                break;
        }

        return tabView;
    }
}