package com.mrcornman.otp.adapters;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mrcornman.otp.R;
import com.mrcornman.otp.models.gson.PhotoFile;
import com.mrcornman.otp.models.gson.Recommendation;
import com.mrcornman.otp.models.models.PhotoItem;
import com.mrcornman.otp.utils.PrettyTime;
import com.mrcornman.otp.utils.ProfileBuilder;
import com.mrcornman.otp.views.CardView;
import com.mrcornman.otp.views.CardView_;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RecommendationAdapter extends BaseAdapter {

    private Context mContext;
    private List<Recommendation> mItems;

    public RecommendationAdapter(Context context) {
        mContext = context;
        mItems = new ArrayList<>();
    }

    public void setRecommendations(List<Recommendation> recommendations) {
        mItems = recommendations;
    }

    public List<Recommendation> getRecommendations() { return mItems; }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Recommendation getItem(int i) {
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

        final Recommendation recommendation = getItem(position);
        cardView.setTag(recommendation.userId);

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

        List<PhotoItem> photoItems = recommendation.photos;

        // front
        nameText.setText(recommendation.name + ",");
        ageText.setText(PrettyTime.getAgeFromBirthDate(recommendation.birthdate) + "");
        Log.i("Recommendation", recommendation.birthdate.toString());

        if(photoItems != null && photoItems.size() > 0 && photoItems.get(0) != null && photoItems.get(0) != JSONObject.NULL) {
            PhotoItem mainPhoto = photoItems.get(0);
            mainPhoto.fetchIfNeededInBackground(new GetCallback<PhotoItem>() {
                @Override
                public void done(PhotoItem photoItem, ParseException e) {
                    if(photoItem != null && e == null) {
                        PhotoFile mainFile = photoItem.getPhotoFiles().get(0);
                        Picasso.with(mContext.getApplicationContext()).load(mainFile.url).fit().centerCrop().into(pictureImageFront);
                    }
                }
            });
        } else {
            Picasso.with(mContext).load(R.drawable.com_facebook_profile_picture_blank_portrait).fit().centerCrop().into(pictureImageFront);
        }

        // back
        aboutText.setText(recommendation.about);
        wantText.setText(recommendation.want);

        if(photoItems != null && photoItems.size() == ProfileBuilder.MAX_NUM_PHOTOS) {
            for(int i = 0; i < Math.min(pictureImagesBack.length, photoItems.size()); i++) {
                final int index = i;
                if(photoItems.get(i) != null && photoItems.get(i) != JSONObject.NULL) {
                    PhotoItem photo = photoItems.get(i);
                    photo.fetchIfNeededInBackground(new GetCallback<PhotoItem>() {
                        @Override
                        public void done(PhotoItem photoItem, ParseException e) {
                            if(photoItem != null && e == null) {
                                PhotoFile photoFile = photoItem.getPhotoFiles().get(0);
                                pictureImagesBack[index].setVisibility(View.VISIBLE);
                                Picasso.with(mContext.getApplicationContext()).load(photoFile.url).fit().centerCrop().into(pictureImagesBack[index]);
                            }
                        }
                    });
                }
            }
        }

        return cardView;
    }
}