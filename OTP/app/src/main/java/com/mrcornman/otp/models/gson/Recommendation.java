package com.mrcornman.otp.models.gson;

import com.google.gson.annotations.SerializedName;
import com.mrcornman.otp.models.models.PhotoItem;
import com.parse.ParseGeoPoint;

import java.util.Date;
import java.util.List;

/**
 * Created by Jonathan on 6/16/2015.
 */
public class Recommendation {
    @SerializedName("userId")
    public String userId;

    @SerializedName("name")
    public String name;

    @SerializedName("gender")
    public int gender;

    @SerializedName("birthdate")
    public Date birthdate;

    @SerializedName("location")
    public ParseGeoPoint location;

    @SerializedName("photos")
    public List<PhotoItem> photos;

    @SerializedName("about")
    public String about;

    @SerializedName("want")
    public String want;
}