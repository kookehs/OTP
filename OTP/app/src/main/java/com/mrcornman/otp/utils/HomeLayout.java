package com.mrcornman.otp.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.mrcornman.otp.R;

/**
 * Created by Aprusa on 6/3/2015.
 */
public class HomeLayout extends HorizontalScrollView {
    private static final int SWIPE_MIN_DISTANCE = 5;
    private static final int SWIPE_THRESHOLD_VELOCITY = 100;

    private ImageView[] mItems = null;
    private GestureDetector mGestureDetector;
    private int mActiveFeature = 0;
    private int featureWidth = 0;

    public HomeLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public HomeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HomeLayout(Context context) {
        super(context);
    }

    public void setFeatureItems(ImageView[] items){
        LinearLayout internalWrapper = (LinearLayout)findViewById(R.id.picture_column);
        this.mItems = items;
        for(int i = 0; i< items.length;i++) {
            //LinearLayout featureLayout = (LinearLayout) View.inflate(this.getContext(), R.layout.picture_column, null);
            //...
            //Create the view for each screen in the scroll view
            //...
           // internalWrapper.addView(items[i]);
            //}
            featureWidth = items[i].getWidth();
            items[i].setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    //If the user swipes
                    if (mGestureDetector.onTouchEvent(event)) {
                        return true;
                    } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                        int scrollX = getScrollX();
                        int featureWidth = v.getMeasuredWidth();
                        mActiveFeature = ((scrollX + (featureWidth / 2)) / featureWidth);
                        int scrollTo = mActiveFeature * featureWidth;
                        smoothScrollTo(scrollTo, 0);
                        return true;
                    } else {
                        return false;
                    }
                }

            });
        }
        mGestureDetector = new GestureDetector(new MyGestureDetector());
    }

    class MyGestureDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            try {
                //right to left
                if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    //int featureWidth = getMeasuredWidth();
                    mActiveFeature = (mActiveFeature < (mItems.length - 1))? mActiveFeature + 1:mItems.length -1;
                    smoothScrollTo(mActiveFeature*featureWidth, 0);
                    return true;
                }
                //left to right
                else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    //int featureWidth = getMeasuredWidth();
                    mActiveFeature = (mActiveFeature > 0)? mActiveFeature - 1:0;
                    smoothScrollTo(mActiveFeature*featureWidth, 0);
                    return true;
                }
            } catch (Exception e) {
                Log.e("Fling", "There was an error processing the Fling event:" + e.getMessage());
            }
            return false;
        }
    }
}