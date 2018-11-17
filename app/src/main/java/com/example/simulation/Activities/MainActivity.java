package com.example.simulation.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.example.simulation.R;

import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;


public class MainActivity extends AppCompatActivity {


    public static String Port_Ip = "tcp://192.168.1.2:1883"; //by default

    public static String macAddress;
    Intent intent1;
    private MenuItem item;
    private MenuItem item2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        macAddress = getMacAddr();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    //----------------Creating Options_menu---------------------------------------------------------//


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.menu_AndroidSettings:
                Intent toy1 = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(toy1);
                finish();
                break;
            case R.id.menu_mqtt_settings:
                break;
            case R.id.menu_Exit:
                AlertDialog.Builder ad = new AlertDialog.Builder(this);
                ad.setTitle(getResources().getString(R.string.exitDB));
                ad.setMessage(getResources().getString(R.string.questionDB));
                ad.setPositiveButton(getResources().getString(R.string.yesDB), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        finish();
                    }
                });
                ad.setNegativeButton(getResources().getString(R.string.noDB), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                ad.show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder ad = new AlertDialog.Builder(this);
        ad.setTitle(getResources().getString(R.string.exitDB));
        ad.setMessage(getResources().getString(R.string.questionDB));
        ad.setPositiveButton(getResources().getString(R.string.yesDB), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                finish();
            }
        });
        ad.setNegativeButton(getResources().getString(R.string.noDB), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        ad.show();
    }

    //--------------This function is used in order to find the mac address of the device--------------//
    private String getMacAddr() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(String.format("%02X:", b));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {
        }
        return "02:00:00:00:00:00"; // <Android 6.0.
    }


}
