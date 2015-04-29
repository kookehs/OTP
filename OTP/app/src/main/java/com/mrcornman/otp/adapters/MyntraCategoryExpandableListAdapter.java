package com.mrcornman.otp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import com.mrcornman.otp.R;
import com.mrcornman.otp.models.MyntraCategory;

/**
 * Created by Anil on 8/24/2014.
 */
public class MyntraCategoryExpandableListAdapter extends BaseExpandableListAdapter {

    Context mContext;
    List<MyntraCategory.ProductHeadGroup> mHeadGroups;

    public MyntraCategoryExpandableListAdapter(Context context, List<MyntraCategory.ProductHeadGroup> headGroups) {
        mContext = context;
        mHeadGroups = headGroups;
    }

    @Override
    public int getGroupCount() {
        return mHeadGroups.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mHeadGroups.get(groupPosition).getChildGroups().size();
    }

    @Override
    public MyntraCategory.ProductHeadGroup getGroup(int groupPosition) {
        return mHeadGroups.get(groupPosition);
    }

    @Override
    public MyntraCategory.ProductGroup getChild(int groupPosition, int childPosition) {
        return mHeadGroups.get(groupPosition).getChildGroups().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        MyntraCategory.ProductHeadGroup headGroup = getGroup(groupPosition);
        String header = headGroup.getGroupName();
        if (headGroup.isHeader()) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.expandable_list_main_header, null);
            TextView headerTextView = (TextView) convertView.findViewById(R.id.idExpandableHeaderItem);
            headerTextView.setText(header);
        } else {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.expandable_list_header, null);
            ImageView imageView = (ImageView) convertView.findViewById(R.id.expandableHeaderIcon);
            if (isExpanded){
                imageView.setImageResource(R.drawable.ic_action_collapse_dark);
            } else {
                imageView.setImageResource(R.drawable.ic_action_expand_dark);
            }
            TextView headerTextView = (TextView) convertView.findViewById(R.id.idExpandableHeaderItem);
            headerTextView.setText(header);
        }
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        String childText = getChild(groupPosition, childPosition).getGroupLabel();
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.expandable_list_item, null);
        }
        TextView childTextView = (TextView) convertView.findViewById(R.id.idExpandableChildItem);
        childTextView.setText(childText);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
