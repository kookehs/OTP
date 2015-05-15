package com.mrcornman.otp.adapters;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mrcornman.otp.R;
import com.mrcornman.otp.models.PhotoFile;
import com.mrcornman.otp.models.PhotoItem;
import com.mrcornman.otp.utils.PrettyTime;
import com.mrcornman.otp.utils.ProfileBuilder;
import com.mrcornman.otp.views.CardView;
import com.mrcornman.otp.views.CardView_;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

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

        final ImageView pictureImage = (ImageView) cardView.findViewById(R.id.picture);
        // fixme: maybe we need a progressbar when the image is loading?

        TextView nameText = (TextView) cardView.findViewById(R.id.name_text);
        nameText.setText(userItem.getString(ProfileBuilder.PROFILE_KEY_NAME));
        TextView ageText = (TextView) cardView.findViewById(R.id.age_text);
        ageText.setText(PrettyTime.getAgeFromBirthDate(userItem.getDate(ProfileBuilder.PROFILE_KEY_BIRTHDATE)) + "");

        List<PhotoItem> photoItems = userItem.getList(ProfileBuilder.PROFILE_KEY_PHOTOS);
        PhotoItem mainPhoto = photoItems.get(0);
        mainPhoto.fetchIfNeededInBackground(new GetCallback<PhotoItem>() {
            @Override
            public void done(PhotoItem photoItem, ParseException e) {
                PhotoFile mainFile = photoItem.getPhotoFiles().get(0);
                Log.i("CardAdapter", pictureImage.toString());
                Picasso.with(mContext).load(mainFile.url).into(pictureImage);
            }
        });

        return cardView;
    }
}
