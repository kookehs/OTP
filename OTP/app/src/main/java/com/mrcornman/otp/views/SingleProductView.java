package com.mrcornman.otp.views;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mrcornman.otp.R;
import com.mrcornman.otp.models.MatchItem;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

/**
 * Created by Anil on 7/18/2014.
 */
@EViewGroup(R.layout.card)
public class SingleProductView extends RelativeLayout implements CardStackView.CardStackListener {

    @ViewById
    ImageView picture;

    @ViewById
    TextView yes;

    @ViewById
    TextView no;

    @ViewById
    TextView styleName;

    @ViewById
    TextView discountedPrice;

    @ViewById
    TextView actualPrice;

    @ViewById
    ImageView yesicon;

    @ViewById
    ImageView noicon;

    public MatchItem matchItem;

    public SingleProductView(Context context) {
        super(context);
    }

    public void bind(MatchItem mMatchItem){
        matchItem = mMatchItem;
        return;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        yesicon.setAlpha((float) 0);
        noicon.setAlpha((float) 0);
        // todo: you can download the picture here or in the getView function of the ProductCardAdapter, for now things work so, i'm happy
    }

    @Override
    public void onUpdateProgress(boolean positif, float percent, View view) {
        if (positif) {
            yes.setAlpha(percent);
            yesicon.setAlpha(percent);
        } else {
            no.setAlpha(percent);
            noicon.setAlpha(percent);
        }
        // styleName.setAlpha(0);
        // actualPrice.setAlpha(0);
        // discountedPrice.setAlpha(0);
    }

    @Override
    public void onCancelled(View beingDragged) {
        yes.setAlpha(0);
        no.setAlpha(0);
        yesicon.setAlpha((float) 0);
        noicon.setAlpha((float) 0);
        styleName.setAlpha(1);
        actualPrice.setAlpha(1);
        discountedPrice.setAlpha(1);
    }

    @Override
    public void onChoiceMade(boolean choice, View beingDragged) {
        yes.setAlpha(0);
        no.setAlpha(0);
        yesicon.setAlpha((float) 0);
        noicon.setAlpha((float) 0);
        styleName.setAlpha(1);
        actualPrice.setAlpha(1);
        discountedPrice.setAlpha(1);
        // fixme: here you have to do what happens after the choice is made,
        // todo: can we make Product public in the main class..?
    }
}
