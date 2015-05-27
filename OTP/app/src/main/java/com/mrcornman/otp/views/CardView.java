package com.mrcornman.otp.views;

import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;

import com.mrcornman.otp.R;
import com.mrcornman.otp.animations.FlipAnimation;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

@EViewGroup(R.layout.card_item)
public class CardView extends RelativeLayout {

    @ViewById
    public View cardRoot;

    @ViewById
    public View cardFront;

    @ViewById
    public View cardBack;

    public CardView(Context context) {
        super(context);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        // todo: you can download the picture here or in the getView function of the ProductCardAdapter, for now things work so, i'm happy
    }

    public void flipCard()
    {
        FlipAnimation flipAnimation = new FlipAnimation(cardFront, cardBack, 300);
        if (cardFront.getVisibility() == View.GONE)
        {
            flipAnimation.reverse();
        }
        cardRoot.startAnimation(flipAnimation);
    }
}
