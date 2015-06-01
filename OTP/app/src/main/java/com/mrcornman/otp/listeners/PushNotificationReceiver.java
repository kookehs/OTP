package com.mrcornman.otp.listeners;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.mrcornman.otp.R;
import com.mrcornman.otp.activities.SettingsActivity;
import com.mrcornman.otp.models.gson.PushData;
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
        String rawData = intent.getStringExtra(KEY_PUSH_DATA);
        Gson gson = new Gson();
        PushData pushData = gson.fromJson(rawData, PushData.class);
        int notificationType = pushData.notificationType;

        // ignore the notifications if user has turned them off
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        if(notificationType == PushData.TYPE_MESSAGE && !prefs.getBoolean(SettingsActivity.PREF_NOTIFICATIONS_MESSAGES, true) ||
                notificationType == PushData.TYPE_MATCH_LIKED && !prefs.getBoolean(SettingsActivity.PREF_NOTIFICATIONS_MATCH_LIKED, true) ||
                notificationType == PushData.TYPE_NEW_MATCH && !prefs.getBoolean(SettingsActivity.PREF_NOTIFICATIONS_NEW_MATCH, true))
            return null;

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
