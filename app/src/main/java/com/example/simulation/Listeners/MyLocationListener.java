package com.example.simulation.Listeners;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.TextView;
import android.widget.Toast;

public class MyLocationListener implements LocationListener {
    private Context context;
    private Double mylatitude;
    private Double mylongtitude;
    private TextView textView;

    public MyLocationListener(Context context, TextView textView) {
        this.context = context;
        this.textView = textView;
    }

    @Override
    public void onLocationChanged(Location loc) {

        mylatitude = loc.getLatitude();
        mylongtitude = loc.getLongitude();
        textView.setText("Longtitude: " + mylongtitude + "\n" + "Latitude: " + mylatitude);
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
        Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        context.startActivity(i);
    }

    public Double getDevLatitude() {
        return mylatitude;
    }

    public Double getDevLongtitude() {
        return mylongtitude;
    }
}
