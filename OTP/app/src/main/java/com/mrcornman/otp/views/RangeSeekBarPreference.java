package com.mrcornman.otp.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mrcornman.otp.R;

public class RangeSeekBarPreference extends Preference implements RangeSeekBar.OnRangeSeekBarChangeListener<Integer> {
    private RangeSeekBar<Integer> mRangeSeekBar;
    private int mMin;
    private int mMax;

    private String summaryFormat = "%d - %d";

    public RangeSeekBarPreference(Context context) {
        super(context);
    }

    public RangeSeekBarPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RangeSeekBarPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected View onCreateView(ViewGroup parent)	{
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.preference_range_seekbar, parent, false);
        mRangeSeekBar = (RangeSeekBar) view.findViewById(R.id.range_seekbar);
        mRangeSeekBar.setNotifyWhileDragging(true);
        mRangeSeekBar.setOnRangeSeekBarChangeListener(this);
        return view;
    }

    @Override
    public void onRangeSeekBarValuesChanged(RangeSeekBar bar, Integer minValue, Integer maxValue) {
        setMinValue(minValue);
        setMaxValue(maxValue);

        setSummary(getSummary());
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getInt(index, 0);
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        setMinValue(mMin);
        setMaxValue(mMax);
    }

    @Override
    public CharSequence getSummary() {
        return String.format(summaryFormat, mMin, mMax);
    }

    public void setSummaryFormat(String format) {
        summaryFormat = format;
    }

    public void setMinValue(Number value) {
        int intValue = value.intValue();

        if (intValue != mMin) {
            mMin = intValue;
            //if(mRangeSeekBar != null) mRangeSeekBar.setSelectedMinValue();
        }
    }

    public void setMaxValue(Number value) {
        int intValue = value.intValue();

        if (intValue != mMax) {
            mMax = intValue;
            //if(mRangeSeekBar != null) mRangeSeekBar.setSelectedMaxValue(intValue);
        }
    }

    public int getMinValue() {
        return mMin;
    }
    public int getMaxValue() { return mMax; }
}