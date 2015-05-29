package com.mrcornman.otp.models;

public class NavItem {

    private String mTitle;
    private int mIconResourceId;

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public int getIconResourceId() {
        return mIconResourceId;
    }

    public void setIconResourceId(int iconResourceId) {
        mIconResourceId = iconResourceId;
    }
}