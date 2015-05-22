package com.mrcornman.otp.fragments;


import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.mrcornman.otp.R;
import com.mrcornman.otp.adapters.NavAdapter;
import com.mrcornman.otp.items.NavItem;
import com.mrcornman.otp.items.gson.PhotoFileItem;
import com.mrcornman.otp.items.models.PhotoItem;
import com.mrcornman.otp.utils.ProfileBuilder;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment used for managing interactions for and presentation of a navigation drawer.
 * See the <a href="https://developer.android.com/design/patterns/navigation-drawer.html#Interaction">
 * design guidelines</a> for a complete explanation of the behaviors implemented here.
 */
public class NavFragment extends Fragment {

    private final static String[] titles = {
            "Preferences",
            "Settings",
            "Share"
    };

    private final static int[] iconIds = {
            R.mipmap.ic_nav_prefs,
            R.mipmap.ic_nav_settings,
            R.mipmap.ic_nav_share
    };

    private NavigationDrawerCallbacks mCallbacks;

    private ActionBarDrawerToggle mDrawerToggle;

    private NavAdapter navAdapter;
    private DrawerLayout mDrawerLayout;
    private View mFragmentContainerView;

    public NavFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragmentView =  inflater.inflate(R.layout.fragment_nav, container, false);

        if(getActivity() == null) return fragmentView;

        ListView listView = (ListView) fragmentView.findViewById(R.id.nav_list);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem(position + 1);
            }
        });

        // init nav profile views
        final ImageView navProfileImage = (ImageView) fragmentView.findViewById(R.id.nav_profile_image);
        final TextView navProfileNameText = (TextView) fragmentView.findViewById(R.id.name_text);

        final ImageView navProfileListenerImage = (ImageView) fragmentView.findViewById(R.id.nav_profile_listener_backdrop);
        navProfileListenerImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectItem(0);
            }
        });

        // fill nav profile views
        ParseUser user = ParseUser.getCurrentUser();

        navProfileNameText.setText(user.getString(ProfileBuilder.PROFILE_KEY_NAME) + "'s Profile");

        List<PhotoItem> photoItems = user.getList(ProfileBuilder.PROFILE_KEY_PHOTOS);
        if(photoItems != null && photoItems.size() > 0) {
            PhotoItem mainPhoto = photoItems.get(0);
            mainPhoto.fetchIfNeededInBackground(new GetCallback<PhotoItem>() {
                @Override
                public void done(PhotoItem photoItem, ParseException e) {
                    PhotoFileItem mainFile = photoItem.getPhotoFiles().get(0);
                    Picasso.with(getActivity().getApplicationContext()).load(mainFile.url).fit().centerCrop().into(navProfileImage);
                }
            });
        }

        List<NavItem> navItems = new ArrayList<>();

        for(int i = 0; i < titles.length; i++) {
            NavItem newItem = new NavItem();
            newItem.setTitle(titles[i]);
            newItem.setIconResourceId(iconIds[i]);
            navItems.add(newItem);
        }

        navAdapter = new NavAdapter(getActivity(), navItems);
        listView.setAdapter(navAdapter);

        return fragmentView;
    }

    @Override
    public void onActivityCreated (Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Indicate that this fragment would like to influence the set of actions in the action bar.
        setHasOptionsMenu(true);
    }

    public boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
    }

    /**
     * Users of this fragment must call this method to set up the navigation drawer interactions.
     *
     * @param drawerLayout The DrawerLayout containing this fragment's UI.
     */
    public void setUp(DrawerLayout drawerLayout) {
        mFragmentContainerView = getActivity().findViewById(R.id.navigation_drawer);
        mDrawerLayout = drawerLayout;

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the navigation drawer and the action bar app icon.
        mDrawerToggle = new ActionBarDrawerToggle(getActivity(), mDrawerLayout,
                R.mipmap.ic_drawer, R.string.drawer_open, R.string.drawer_close) {

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (!isAdded()) return;
                getActivity().invalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (!isAdded()) return;
                getActivity().invalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }
        };

        // Defer code dependent on restoration of previous instance state.
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    private void selectItem(int position) {
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        }
        if (mCallbacks != null) {
            mCallbacks.onNavigationDrawerItemSelected(position);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (NavigationDrawerCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Forward the new configuration the drawer toggle component.
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) return true;

        return super.onOptionsItemSelected(item);
    }

    /**
     * Callbacks interface that all activities using this fragment must implement.
     */
    public interface NavigationDrawerCallbacks {
        /**
         * Called when an item in the navigation drawer is selected.
         */
        void onNavigationDrawerItemSelected(int position);
    }
}
