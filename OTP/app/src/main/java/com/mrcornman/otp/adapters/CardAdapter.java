package com.mrcornman.otp.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.mrcornman.otp.R;

public class CardAdapter extends ArrayAdapter<String> {

    public CardAdapter(Context context, int resource) {
        super(context, resource);
    }

    @Override
    public View getView(int position, final View contentView, ViewGroup parent){
        TextView v = (TextView)(contentView.findViewById(R.id.content));
        v.setText(getItem(position));
        return contentView;
    }

}

