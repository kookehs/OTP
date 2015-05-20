package com.mrcornman.otp.views;

import android.content.Context;
import android.widget.RelativeLayout;

import com.mrcornman.otp.R;

import org.androidannotations.annotations.EViewGroup;

@EViewGroup(R.layout.card_item)
public class CardView extends RelativeLayout implements CardStackLayout.CardStackListener {

    public String boundUserId;

    public CardView(Context context) {
        super(context);
    }

    public void bind(String userId){
        boundUserId = userId;
        return;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        // todo: you can download the picture here or in the getView function of the ProductCardAdapter, for now things work so, i'm happy
    }

    @Override
    public void onBeginProgress() {
    }

    @Override
    public void onUpdateProgress(boolean positif, float percent) {
    }

    @Override
    public void onCancelled() {
    }

    @Override
    public void onChoiceMade(boolean choice) {
        // fixme: here you have to do what happens after the choice is made,
        // todo: can we make Product public in the main class..?
    }
}
