package com.mrcornman.otp.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.mrcornman.otp.R;
import com.mrcornman.otp.adapters.ClientMatchAdapter;
import com.mrcornman.otp.listeners.OnRefreshListener;
import com.mrcornman.otp.items.models.MatchItem;
import com.mrcornman.otp.utils.DatabaseHelper;
import com.parse.FindCallback;
import com.parse.ParseException;

import java.util.List;

public class ClientListFragment extends Fragment implements OnRefreshListener {

    private OnClientListInteractionListener onClientListInteractionListener;

    private ClientMatchAdapter clientMatchAdapter;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static ClientListFragment newInstance() {
        ClientListFragment fragment = new ClientListFragment();
        return fragment;
    }

    public ClientListFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // init views
        View rootView = inflater.inflate(R.layout.fragment_client_list, container, false);
        ListView listView = (ListView) rootView.findViewById(R.id.client_list);

        // set up list view input
        // set up on click
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String otherId = view.getTag().toString();
                onClientListInteractionListener.onRequestOpenClientMatch(otherId);
            }
        });

        clientMatchAdapter = new ClientMatchAdapter(getActivity());
        listView.setAdapter(clientMatchAdapter);

        // fill list up
        refreshList();

        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            onClientListInteractionListener = (OnClientListInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnClientListInteractionListener");
        }
    }

    @Override
    public void onRefresh() {
        refreshList();
    }

    public void refreshList() {
        DatabaseHelper.findTopMatches(20, new FindCallback<MatchItem>() {
            @Override
            public void done(List<MatchItem> list, ParseException e) {
                if(e == null) {
                    clientMatchAdapter.clearMatches();

                    for (MatchItem match : list) {
                        clientMatchAdapter.addMatch(match);
                    }
                }
            }
        });
    }

    public interface OnClientListInteractionListener {
        void onRequestOpenClientMatch(String otherId);
    }
}