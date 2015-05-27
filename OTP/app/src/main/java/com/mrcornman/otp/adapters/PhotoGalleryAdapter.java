package com.mrcornman.otp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.mrcornman.otp.R;
import com.mrcornman.otp.items.gson.PhotoFile;
import com.mrcornman.otp.views.SquareImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aprusa on 5/24/2015.
 */
public class PhotoGalleryAdapter extends BaseAdapter {

    private Context context;
    private List<PhotoFile> photoFileItems;

    public PhotoGalleryAdapter(Context _context) {
        context = _context;
        photoFileItems = new ArrayList<>();
    }

    public void setPhotoFileItems(List<PhotoFile> photoFileItems) {
        this.photoFileItems = photoFileItems;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() { return photoFileItems.size(); }

    @Override
    public PhotoFile getItem(int i) { return photoFileItems.get(i); }

    @Override
    public long getItemId(int i) { return i; }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        PhotoFile w = getItem(position);

        // Inflate a new view if necessary.
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.photo_gallery_item, parent, false);
        }

        // Fills in the view.
        SquareImageView thumbImage = (SquareImageView)convertView.findViewById(R.id.thumb_image);
        Picasso.with(context).load(w.url).fit().centerCrop().into(thumbImage);

        // Set a listener for the whole list item.
        convertView.setTag(w.url);

        return convertView;
    }
}
