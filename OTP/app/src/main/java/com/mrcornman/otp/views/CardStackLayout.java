package com.mrcornman.otp.views;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.RelativeLayout;

import com.mrcornman.otp.adapters.UserCardAdapter;

import java.util.Iterator;
import java.util.LinkedList;

public class CardStackLayout extends RelativeLayout {

    private final static int STACK_SIZE = 4;

    public interface CardStackListener {
        void onBeginProgress();
        void onUpdateProgress(float percent);
        void onCancelled();
        void onChoiceAccepted();
    }

    private CardView draggedCard;
    private OnCardTouchListener onCardTouchListener = new OnCardTouchListener();;

    private UserCardAdapter mAdapter;
    private int mCurrentPosition;

    // the minimum distance before the card swipe gesture will be accepted as a choice
    private int mMinAcceptDistance;

    // the maximum distance before the card gesture will be taken as a click
    private int mClickDistanceEpsilon;

    private float cardDelta;
    private float cardStartPosition;

    protected LinkedList<CardView> mCards = new LinkedList<>();

    private CardStackListener mCardStackListener;

    public CardStackLayout(Context context) {
        super(context);
        setup();
    }

    public CardStackLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup();
    }

    public CardStackLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setup();
    }

    private void setup() {
        Resources r = getContext().getResources();
        mMinAcceptDistance = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                96, r.getDisplayMetrics());
        mClickDistanceEpsilon = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                5, r.getDisplayMetrics());

        mCurrentPosition = 0;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (draggedCard != null) cardDelta = draggedCard.getTranslationY();

        Iterator<CardView> it = mCards.descendingIterator();

        while (it.hasNext()){
            CardView card = it.next();
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public boolean hasMoreItems() {
        return mCurrentPosition < mAdapter.getCount();
    }

    public View getDraggedCard() {
        return draggedCard;
    }

    public CardView getTopCard() { return mCards.size() > 0 ? mCards.get(mCurrentPosition) : null; }
    private boolean isTopCard(CardView card) {
        return mCards.size() > 0 ? card == mCards.get(mCurrentPosition) : false;
    }

    public void setCardStackListener(CardStackListener mCardStackListener) {
        this.mCardStackListener = mCardStackListener;
    }

    public void setAdapter(UserCardAdapter adapter) {
        mAdapter = adapter;
        mCards.clear();
        removeAllViews();
        mCurrentPosition = 0;
    }

    public void refreshStack() {

        mCards.clear();

        int position = 0;
        for (; position < mCurrentPosition + STACK_SIZE; position++) {
            if (position >= mAdapter.getCount()) break;

            CardView card = (CardView) mAdapter.getView(position, null, null);
            mCards.offer(card);

            card.setOnTouchListener(onCardTouchListener);

            addView(card, 0);
        }

        Log.i("CardStackLayout", "Refreshed with " + mCards.size() + " cards");

        mCurrentPosition = 0;
    }

    public void updateStack() {

    }

    private class OnCardTouchListener implements OnTouchListener {

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            final CardView card = (CardView) view;
            if (!isTopCard(card)) {
                return false;
            }

            final int motionY = (int) motionEvent.getRawY();

            final int action = motionEvent.getAction();
            switch (action & MotionEvent.ACTION_MASK){
                case MotionEvent.ACTION_DOWN:
                    cardStartPosition = motionY;
                    mCardStackListener.onBeginProgress();
                    break;
                case MotionEvent.ACTION_UP:
                    if (!canAcceptChoice()) {
                        if(!isClickGesture()) {
                            requestLayout();
                            mCardStackListener.onCancelled();

                            draggedCard = null;
                            cardDelta = 0;
                            cardStartPosition = 0;

                            ObjectAnimator translationAnimation = ObjectAnimator.ofFloat(card, "translationY", card.getTranslationY(), 0).setDuration(100);

                            translationAnimation.addListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    requestLayout();
                                }
                            });

                            ValueAnimator.AnimatorUpdateListener onUpdate = new ValueAnimator.AnimatorUpdateListener() {
                                @Override
                                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                                    cardDelta = card.getTranslationY();
                                }
                            };
                            translationAnimation.addUpdateListener(onUpdate);
                            translationAnimation.start();
                        } else {
                            card.flipCard();
                        }
                    } else {
                        draggedCard = null;
                        cardDelta = 0;
                        cardStartPosition = 0;

                        final ViewGroup parent = (ViewGroup) card.getParent();
                        parent.removeView(card);
                        mCurrentPosition++;

                        requestLayout();

                        mCardStackListener.onChoiceAccepted();
                    }

                    break;
                case MotionEvent.ACTION_MOVE:
                    float progress = getStackProgress();

                    cardDelta = motionY - cardStartPosition;
                    card.setTranslationY(cardDelta);

                    draggedCard = card;
                    requestLayout();

                    mCardStackListener.onUpdateProgress(progress);

                    break;
            }
            return true;
        }
    }

    private float getStackProgress() {
        LinearInterpolator interpolator = new LinearInterpolator();
        float progress = Math.min(Math.abs(cardDelta) / (mMinAcceptDistance * 5.0f), 1);
        progress = interpolator.getInterpolation(progress);
        return progress;
    }

    private boolean canAcceptChoice() {
        return Math.abs(cardDelta) > mMinAcceptDistance;
    }

    private boolean isClickGesture() { return Math.abs(cardDelta) < mClickDistanceEpsilon; }
}