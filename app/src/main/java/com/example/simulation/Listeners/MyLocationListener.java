package com.example.simulation.Listeners;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import static android.content.ContentValues.TAG;

public class MyLocationListener implements LocationListener {
    private Context context;
    private Double mylatitude;
    private Double mylongtitude;

    public MyLocationListener(Context context) {
        this.context = context;
    }


    @Override
    public void onLocationChanged(Location loc) {
        if (loc != null) {
            mylatitude = loc.getLatitude();
            mylongtitude = loc.getLongitude();

            /*Toast.makeText(context,
                    "Location changed: Lat: " + mylatitude + " Lng: "
                            + mylongtitude, Toast.LENGTH_SHORT).show();*/
            String longitude = "Longitude______: " + mylongtitude;
            Log.v(TAG, longitude);
            String latitude = "Latitude________: " + mylatitude;
            Log.v(TAG, latitude);
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(context, "Gps is turned on!! ",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(context, "Gps is turned off!! ",
                Toast.LENGTH_SHORT).show();
    }

    public Double getDevLatitude() {
        if (mylatitude != null)
            return mylatitude;
        return 0.0;
    }

    public Double getDevLongtitude() {
        if (mylongtitude != null)
            return mylongtitude;
        return 0.0;
    }
}
