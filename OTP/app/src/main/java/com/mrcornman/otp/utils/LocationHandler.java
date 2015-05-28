package com.mrcornman.otp.utils;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.parse.ParseGeoPoint;

/**
 * Created by Bill on 5/16/2015.
 */

public class LocationHandler {

    private static final int MAX_METERS = 40;

    private static LocationManager locationManager;
    private static OnLocationReceivedListener onLocationReceivedListener;

    /**
     * Assumes Location is enabled.
     * @param context
     * @return
     */
    public static ParseGeoPoint getQuickLocation(Context context) {
        locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);

        String provider = null;

        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            provider = LocationManager.NETWORK_PROVIDER;
        } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            provider = LocationManager.GPS_PROVIDER;
        }

        Location location = locationManager.getLastKnownLocation(provider);
        if (!location.hasAccuracy()) return null;
        final float accuracy = location.getAccuracy();

        if (accuracy <= MAX_METERS && accuracy != 0.0f) {
            final double latitude = location.getLatitude();
            final double longitude = location.getLongitude();

            return new ParseGeoPoint(latitude, longitude);
        }

        return null;
    }

    public static void requestLocation(Context context, OnLocationReceivedListener receivedListener) {
        onLocationReceivedListener = receivedListener;

        locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);

        if(!isLocationEnabled()) {
            onLocationReceivedListener.done(null, new Exception("Location is not enabled"));
            return;
        }

        String provider = null;

        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            provider = LocationManager.NETWORK_PROVIDER;
        } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            provider = LocationManager.GPS_PROVIDER;
        }

        if (provider != null) {
            locationManager.requestLocationUpdates(provider, 1000, 0, new LocationListener() {

                @Override
                public void onLocationChanged(Location location) {
                    if (location.hasAccuracy() == false) return;
                    final float accuracy = location.getAccuracy();

                    if (accuracy <= MAX_METERS && accuracy != 0.0f) {
                        final double latitude = location.getLatitude();
                        final double longitude = location.getLongitude();

                        ParseGeoPoint geoPoint = new ParseGeoPoint(latitude, longitude);
                        onLocationReceivedListener.done(geoPoint, null);

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

    public static boolean isLocationEnabled() {
        return locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public interface OnLocationReceivedListener {
        void done(ParseGeoPoint geoPoint, Exception e);
    }
}
