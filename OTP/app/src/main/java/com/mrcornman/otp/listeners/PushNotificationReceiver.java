package com.mrcornman.otp.listeners;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;

import com.google.gson.Gson;
import com.mrcornman.otp.R;
import com.mrcornman.otp.items.gson.PushData;
import com.parse.ParseAnalytics;
import com.parse.ParsePushBroadcastReceiver;

/**
 * Created by Jonathan on 5/21/2015.
 */
public class PushNotificationReceiver extends ParsePushBroadcastReceiver {

    private final static String DATA_KEY_LARGE_ICON_URL = "large_icon_url";

    @Override
    protected Notification getNotification(Context context, Intent intent) {
        Notification n = super.getNotification(context, intent);
        if(n == null) return null;

        n.defaults = Notification.DEFAULT_VIBRATE;
        n.sound = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.sound_notification_default);
        return n;
    }

    @Override
    public void onPushOpen(Context context, Intent intent) {

        //To track "App Opens"
        ParseAnalytics.trackAppOpenedInBackground(intent);

        //Here is data you sent
        String rawData = intent.getStringExtra(KEY_PUSH_DATA);
        Gson gson = new Gson();
        gson.fromJson(rawData, PushData.class);

        Intent i = new Intent(context, getActivity(context, intent));
        i.putExtras(intent.getExtras());
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }

    @Override
    protected Bitmap getLargeIcon(Context context, Intent intent) {
        /*
        try {
            JSONObject dataObject = new JSONObject(intent.getStringExtra(KEY_PUSH_DATA));

            String urlString = dataObject.optString(DATA_KEY_LARGE_ICON_URL);
            if (urlString != null) {
                //return BitmapUtils.getBitmapFromUrl(urlString);
            }
        } catch (JSONException j) {}
*/
        return null;
    }
}
