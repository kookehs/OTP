package com.mrcornman.otp.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.EditText;

/**
 * Created by Aprusa on 6/5/2015.
 */
public class EditTextBorder extends EditText {

    private Paint mPaint = new Paint();

    public EditTextBorder(Context context) {
        super(context);
    }

    public EditTextBorder(Context context, AttributeSet attrs) {
        super(context, attrs);
        super.setGravity(Gravity.LEFT);
    }

    public EditTextBorder(Context context, AttributeSet attrs, int defStyle) {
        super(context,attrs,defStyle);
        super.setGravity(Gravity.LEFT);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.BLACK);
        canvas.drawRect(0,0,getMeasuredWidth()-1,getMeasuredHeight()-1,mPaint);
        super.onDraw(canvas);
    }
}
