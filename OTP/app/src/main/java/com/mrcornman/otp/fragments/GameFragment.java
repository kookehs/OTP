package com.mrcornman.otp.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mrcornman.otp.R;
import com.mrcornman.otp.adapters.CardAdapter;
import com.mrcornman.otp.layouts.CardStackLayout;

public class GameFragment extends BaseFragment {

    private CardStackLayout mCardStackFirst;
    private CardAdapter mCardAdapterFirst;

    private CardStackLayout mCardStackSecond;
    private CardAdapter mCardAdapterSecond;

    /**
     * Default empty constructor.
     */
    public GameFragment(){
        super();
    }

    /**
     * Static factory method
     * @param sectionNumber
     * @return
     */
    public static GameFragment newInstance(int sectionNumber) {
        GameFragment fragment = new GameFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create an empty loader and pre-initialize the photo list items as an empty list.
        Context context = getActivity().getBaseContext();

        mCardAdapterFirst = new CardAdapter(context, 0);
        mCardAdapterFirst.add("Bill");
        mCardAdapterFirst.add("Alan");
        mCardAdapterFirst.add("Craig");
        mCardAdapterFirst.add("Ricky");
        mCardAdapterFirst.add("Jonathan");

        mCardAdapterSecond = new CardAdapter(context, 0);
        mCardAdapterSecond.add("Tamsin");
        mCardAdapterSecond.add("Mary");
        mCardAdapterSecond.add("Jessica");
        mCardAdapterSecond.add("Carrie");
        mCardAdapterSecond.add("Tiffany");
    }

    /**
     * OnCreateView fragment override
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  null;
        view = inflater.inflate(R.layout.fragment_game, container, false);

        mCardStackFirst = (CardStackLayout)view.findViewById(R.id.cardstack_first);

        mCardStackFirst.setContentResource(R.layout.card_content);
        mCardStackFirst.setStackMargin(20);

        mCardStackFirst.setAdapter(mCardAdapterFirst);

        mCardStackSecond = (CardStackLayout)view.findViewById(R.id.cardstack_second);

        mCardStackSecond.setContentResource(R.layout.card_content);
        mCardStackSecond.setStackMargin(20);

        mCardStackSecond.setAdapter(mCardAdapterSecond);

        return view;
    }
}
