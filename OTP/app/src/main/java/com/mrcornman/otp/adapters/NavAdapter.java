package com.mrcornman.otp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mrcornman.otp.R;
import com.mrcornman.otp.models.NavItem;
import com.squareup.picasso.Picasso;

import java.util.List;

public class NavAdapter extends BaseAdapter {

    private Context mContext;
    private List<NavItem> mItems;

    public NavAdapter(Context context, List<NavItem> items) {
        mContext = context;
        mItems = items;
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Object getItem(int i) {
        return mItems.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater)
                    mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.nav_item, null);
        }

        ImageView iconImage = (ImageView) convertView.findViewById(R.id.icon_image);
        TextView titleText = (TextView) convertView.findViewById(R.id.title_text);

        NavItem navItem = mItems.get(position);

        titleText.setText(navItem.getTitle());
        Picasso.with(mContext).load(navItem.getIconResourceId()).into(iconImage);

        return convertView;
    }
}
