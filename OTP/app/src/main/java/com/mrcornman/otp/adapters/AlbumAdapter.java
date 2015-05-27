package com.mrcornman.otp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mrcornman.otp.R;
import com.mrcornman.otp.models.AlbumItem;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aprusa on 5/24/2015.
 */
public class AlbumAdapter extends BaseAdapter {

    private Context context;
    private List<AlbumItem> albumItems;

    public AlbumAdapter(Context _context) {
        context = _context;
        albumItems = new ArrayList<>();
    }

    public void setAlbumItems(List<AlbumItem> albumItems) {
        this.albumItems = albumItems;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() { return albumItems.size(); }

    @Override
    public AlbumItem getItem(int i) { return albumItems.get(i); }

    @Override
    public long getItemId(int i) { return i; }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        AlbumItem w = getItem(position);

        // Inflate a new view if necessary.
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.row_album, parent, false);
        }

        // Fills in the view.
        TextView nameText = (TextView) convertView.findViewById(R.id.name_text);
        ImageView thumbImage = (ImageView)convertView.findViewById(R.id.thumb_image);
        TextView countText = (TextView) convertView.findViewById(R.id.count_text);

        Picasso.with(context).load(w.coverPhotoFile.url).fit().centerCrop().into(thumbImage);

        nameText.setText(w.name);
        countText.setText(w.count + " photos");

        // Set a listener for the whole list item.
        convertView.setTag(w.albumId);

        return convertView;
    }
}
