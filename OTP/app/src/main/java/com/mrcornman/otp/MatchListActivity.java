package com.mrcornman.otp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;


/**
 * An activity representing a list of Matches. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link MatchDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 * <p/>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link MatchListFragment} and the item details
 * (if present) is a {@link MatchDetailFragment}.
 * <p/>
 * This activity also implements the required
 * {@link MatchListFragment.Callbacks} interface
 * to listen for item selections.
 */
public class MatchListActivity extends FragmentActivity
        implements MatchListFragment.Callbacks {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_list);

        // TODO: If exposing deep links into your app, handle intents here.
    }

    /**
     * Callback method from {@link MatchListFragment.Callbacks}
     * indicating that the item with the given ID was selected.
     */
    @Override
    public void onItemSelected(String id) {
        // In single-pane mode, simply start the detail activity
        // for the selected item ID.
        Intent detailIntent = new Intent(this, MatchDetailActivity.class);
        detailIntent.putExtra(MatchDetailFragment.ARG_ITEM_ID, id);
        startActivity(detailIntent);
    }
}
