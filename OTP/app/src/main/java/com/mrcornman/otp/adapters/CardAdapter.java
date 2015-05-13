package com.mrcornman.otp.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mrcornman.otp.R;
import com.mrcornman.otp.views.CardView;
import com.mrcornman.otp.views.CardView_;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.parse.ParseUser;

import java.util.List;

public class CardAdapter extends BaseAdapter {

    List<ParseUser> mItems;

    private Context mContext;
    private int mResourceId;

    private ImageLoader imageLoader;
    private DisplayImageOptions options;

    public CardAdapter(Context context, int resourceId, List<ParseUser> users) {
        // fixme: should we download the json file here?
        mContext = context;
        mResourceId = resourceId;
        mItems = users;

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(mContext).build();
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(config);

        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();

    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public ParseUser getItem(int i) {
        return mItems.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        CardView cardView;
        if (convertView == null) {
            cardView = CardView_.build(mContext);
        } else {
            cardView = (CardView_) convertView;
        }

        ParseUser userItem = getItem(position);
        cardView.bind(userItem.getObjectId());

        ImageView productImage = (ImageView) cardView.findViewById(R.id.picture);
        // fixme: maybe we need a progressbar when the image is loading?

        TextView nameText = (TextView) cardView.findViewById(R.id.name_text);
        nameText.setText(userItem.getUsername());
        TextView ageText = (TextView) cardView.findViewById(R.id.age_text);
        ageText.setText(userItem.getEmail() + "");

        return cardView;
    }
}
