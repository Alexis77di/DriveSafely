package com.example.simulation.Listeners;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.annotation.NonNull;

public class MyLocationListener implements LocationListener {
    private double latitude;
    private double longtitude;
    private boolean gpsEnabled;

    @Override
    public void onLocationChanged(Location loc) {
        latitude = loc.getLatitude();
        longtitude = loc.getLongitude();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(String provider) {
        gpsEnabled = true;
    }

    @Override
    public void onProviderDisabled(String provider) {
        gpsEnabled = false;
    }

    @NonNull
    @Override
    public String toString() {
        return "" + latitude + "," + longtitude;
    }

    public boolean isGpsEnabled() {
        return gpsEnabled;
    }
}
