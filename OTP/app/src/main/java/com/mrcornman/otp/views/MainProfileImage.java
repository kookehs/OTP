package com.mrcornman.otp.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by Aprusa on 5/24/2015.
 */
public class MainProfileImage extends ImageView {
    public MainProfileImage(Context context) {
        super(context);
    }

    public MainProfileImage(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MainProfileImage(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth()/2);
    }
}
