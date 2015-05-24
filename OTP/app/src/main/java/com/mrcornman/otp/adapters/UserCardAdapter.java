package com.mrcornman.otp.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mrcornman.otp.R;
import com.mrcornman.otp.items.gson.PhotoFileItem;
import com.mrcornman.otp.items.models.PhotoItem;
import com.mrcornman.otp.utils.PrettyTime;
import com.mrcornman.otp.utils.ProfileBuilder;
import com.mrcornman.otp.views.CardView;
import com.mrcornman.otp.views.CardView_;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class UserCardAdapter extends BaseAdapter {

    private Context mContext;
    private List<ParseUser> mItems;

    public UserCardAdapter(Context context) {
        // fixme: should we download the json file here?
        mContext = context;
        mItems = new ArrayList<>();
    }

    public void fillUsers(List<ParseUser> users) {
        mItems = users;
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

        final ParseUser user = getItem(position);
        cardView.setTag(user.getObjectId());

        // init views

        // front
        TextView nameText = (TextView) cardView.findViewById(R.id.name_text);
        TextView ageText = (TextView) cardView.findViewById(R.id.age_text);

        final ImageView pictureImageFront = (ImageView) cardView.findViewById(R.id.picture);

        // back
        TextView aboutText = (TextView) cardView.findViewById(R.id.about_text_value);
        TextView wantText = (TextView) cardView.findViewById(R.id.want_text_value);

        final ImageView[] pictureImagesBack = {
                (ImageView) cardView.findViewById(R.id.picture_0),
                (ImageView) cardView.findViewById(R.id.picture_1),
                (ImageView) cardView.findViewById(R.id.picture_2),
                (ImageView) cardView.findViewById(R.id.picture_3)
        };

        // fill views

        List<PhotoItem> photoItems = user.getList(ProfileBuilder.PROFILE_KEY_PHOTOS);

        // front
        nameText.setText(user.getString(ProfileBuilder.PROFILE_KEY_NAME) + ",");
        ageText.setText(PrettyTime.getAgeFromBirthDate(user.getDate(ProfileBuilder.PROFILE_KEY_BIRTHDATE)) + "");

        if(photoItems != null && photoItems.size() > 0) {
            PhotoItem mainPhoto = photoItems.get(0);
            mainPhoto.fetchIfNeededInBackground(new GetCallback<PhotoItem>() {
                @Override
                public void done(PhotoItem photoItem, ParseException e) {
                    PhotoFileItem mainFile = photoItem.getPhotoFiles().get(0);
                    Picasso.with(mContext.getApplicationContext()).load(mainFile.url).fit().centerCrop().into(pictureImageFront);
                }
            });
        }

        // back
        aboutText.setText(user.getString(ProfileBuilder.PROFILE_KEY_ABOUT));
        wantText.setText(user.getString(ProfileBuilder.PROFILE_KEY_WANT));

        if(photoItems != null && photoItems.size() > 0) {
            for(int i = 0; i < Math.min(pictureImagesBack.length, photoItems.size()); i++) {
                final int index = i;
                PhotoItem photo = photoItems.get(i);
                photo.fetchIfNeededInBackground(new GetCallback<PhotoItem>() {
                    @Override
                    public void done(PhotoItem photoItem, ParseException e) {
                        PhotoFileItem photoFile = photoItem.getPhotoFiles().get(0);
                        pictureImagesBack[index].setVisibility(View.VISIBLE);
                        Picasso.with(mContext.getApplicationContext()).load(photoFile.url).fit().centerCrop().into(pictureImagesBack[index]);
                    }
                });
            }
        }

        return cardView;
    }
}