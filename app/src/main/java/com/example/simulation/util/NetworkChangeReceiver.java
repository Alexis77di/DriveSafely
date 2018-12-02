package com.example.simulation.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class NetworkChangeReceiver extends BroadcastReceiver {


    public static boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (null != activeNetwork) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                return true;

            return activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE;
        }
        return false;
    }

    @Override
    public void onReceive(final Context context, final Intent intent) {
        boolean connected = isConnected(context);
        if ("android.net.conn.CONNECTIVITY_CHANGE".equals(intent.getAction())) {
            if (connected) {
                Toast.makeText(context, "connected!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "No internet connection!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
