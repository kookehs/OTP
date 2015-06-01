package com.mrcornman.otp.models.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Jonathan on 5/14/2015.
 */
public class PushData {
    public final static int TYPE_MESSAGE = 1;
    public final static int TYPE_NEW_MATCH = 2;
    public final static int TYPE_MATCH_LIKED = 3;

    @SerializedName("notificationType")
    public int notificationType;

    @SerializedName("large_icon_url")
    public String largeIconUrl;
}
