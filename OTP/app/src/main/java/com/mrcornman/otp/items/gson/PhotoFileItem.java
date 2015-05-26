package com.mrcornman.otp.items.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Jonathan on 5/14/2015.
 */
public class PhotoFileItem {

    @SerializedName("width")
    public int width;

    @SerializedName("height")
    public int height;

    @SerializedName("url")
    public String url;
}