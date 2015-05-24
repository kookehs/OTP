package com.mrcornman.otp.utils;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

/**
 * Created by Bill on 5/16/2015.
 */

public class LocationHandler {
    private static LocationManager locationManager;

    public static void updateLocation(Context context) {
        Log.d("LocationHandler", "Updating location");
        locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        String provider = "undefined";

        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            provider = LocationManager.NETWORK_PROVIDER;
            Log.d("LocationHandler", "Using network provider");
        } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            provider = LocationManager.GPS_PROVIDER;
            Log.d("LocationHandler", "Using GPS provider");
        } else {
            Log.d("LocationHandler", "Neither provider is available");
        }

        if (!provider.equals("undefined")) {
            Log.d("LocationHandler", "Requesting location updates");
            locationManager.requestLocationUpdates(provider, 1000, 0, new LocationListener() {
                private static final int MAX_METERS = 40;

                @Override
                public void onLocationChanged(Location location) {
                    if (location.hasAccuracy() == false) return;
                    Log.d("LocationHandler", "Location has changed");
                    final float accuracy = location.getAccuracy();

                    if (accuracy <= MAX_METERS && accuracy != 0.0f) {
                        Log.d("LocationHandler", "Location is within 40 meters");
                        final double latitude = location.getLatitude();
                        final double longitude = location.getLongitude();

                        ParseQuery<ParseObject> query = ParseQuery.getQuery("_User");
                        query.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());

                        query.getFirstInBackground(new GetCallback<ParseObject>() {
                            @Override
                            public void done(ParseObject parseObject, ParseException e) {
                                if (parseObject == null) {
                                    Log.d("LocationHandler", "Could not retrieve user");
                                } else {
                                    ParseGeoPoint geoPoint = new ParseGeoPoint(latitude, longitude);
                                    parseObject.put("location", geoPoint);
                                    parseObject.saveInBackground();
                                    Log.d("LocationHandler", "Location has been saved");
                                }
                            }
                        });
                        Log.d("LocationHandler", "Removing listener from updates");
                        locationManager.removeUpdates(this);
                    }
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {
                    Log.d("LocationHandler", "Status changed");
                }

                @Override
                public void onProviderEnabled(String provider) {
                    Log.d("LocationHandler", "Provider enabled");
                }

                @Override
                public void onProviderDisabled(String provider) {
                    Log.d("LocationHandler", "Provider disabled");
                }
            });
        }
    }
}
