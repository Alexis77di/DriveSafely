package com.example.simulation.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.widget.Toast;

import com.example.simulation.R;

public class NetworkChangeReceiver extends BroadcastReceiver {
    public static boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) {
            return activeNetwork.isConnected();
        } else {
            Intent w = new Intent(Settings.ACTION_WIFI_SETTINGS);
            context.startActivity(w);
        }
        return false;

    }

    public static String getStatus(Context context) {

        return isConnected(context) ? context.getString(R.string.connected) : context.getString(R.string.not_connected);
    }

    @Override
    public void onReceive(final Context context, final Intent intent) {
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
            Toast.makeText(context, getStatus(context), Toast.LENGTH_SHORT).show();
        }

    }

}
