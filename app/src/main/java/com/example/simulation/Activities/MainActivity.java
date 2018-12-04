package com.example.simulation.Activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.simulation.Listeners.AccelerometerListener;
import com.example.simulation.Listeners.MyLocationListener;
import com.example.simulation.R;
import com.example.simulation.util.MessagePublisher;
import com.example.simulation.util.MqttSubscriber;
import com.example.simulation.util.NetworkChangeReceiver;

import org.eclipse.paho.client.mqttv3.MqttException;

import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    public static String Port_Ip = "tcp://192.168.1.3:1883"; //by default
    public int rate = 5000; //by default
    public static String macAddress;
    public static String topic;

    private TextView t;
    private TextView TextView7;


    //--FlashLight--//
    private static final int CAMERA_REQUEST = 123;
    Button btnFlashLight;
    boolean hasCameraFlash = false;

    //--Sound--//
    Button btnSound;
    private int soundId;


    //--Location--//
    private LocationManager locationManager;
    private MyLocationListener locationListener;

    //--Connectivity--//
    private BroadcastReceiver networkChangeReceiver;

    //--Accelerometer--//
    private int threshold_x_axis;
    private int threshold_y_axis;
    private int threshold_z_axis;
    private AccelerometerListener accelero;

    //--Our Sensor Manager--//
    private SensorManager SM;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        macAddress = getMacAddr();

        t = findViewById(R.id.textView);

        TextView7 = findViewById(R.id.TextView7);


        networkChangeReceiver = new NetworkChangeReceiver();





        //---------FlashLight Event--------------------------------------------//

        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST);

        hasCameraFlash = getPackageManager().
                hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

        btnFlashLight = findViewById(R.id.btnFlashLightToggle);

        btnFlashLight.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
                if (hasCameraFlash) {
                    if (btnFlashLight.getText().toString().contains("ON")) {
                        btnFlashLight.setText("FLASHLIGHT OFF");
                        flashLightOff();
                    } else {
                        btnFlashLight.setText("FLASHLIGHT ON");
                        flashLightOn();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "No flash available on your device",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });


//        //---------------------------Sound Event-------------------------//
//        btnSound = findViewById(R.id.btnSound);
//        final MediaPlayer mp = MediaPlayer.create(this, R.raw.alarm);
//        btnSound.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                mp.start();
//
//            }
//        });


    }


    @Override
    protected void onResume() {
        super.onResume();


        //----------------Listener for the GPS Location-----------------------//
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new MyLocationListener(getApplicationContext(), TextView7);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.INTERNET
                }, 10);
            }
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);


        //-------------------Internet Connectivity------------------------------------//
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeReceiver, intentFilter);



        //---------------------Create our Sensor Manager----------------------------//
        SM = (SensorManager) getSystemService(SENSOR_SERVICE);


        //-----------Assign TextView-----------
        TextView[] textTable = new TextView[3];
        textTable[0] = findViewById(R.id.xText);
        textTable[1] = findViewById(R.id.yText);
        textTable[2] = findViewById(R.id.zText);


        Context context = getApplicationContext();

        //-----------------Accelerometer Sensor-----------------
        accelero = new AccelerometerListener(SM, threshold_x_axis, threshold_y_axis, threshold_z_axis, textTable, context);

        topic = "MacAddress = " + macAddress + "/" + accelero.getSensorValue() + "/" + locationListener.getDevLatitude() + "/" + locationListener.getDevLongtitude();


        final Handler handlerP = new Handler();
        handlerP.postDelayed(new Runnable() {
            @Override
            public void run() {
                MessagePublisher msgpb = new MessagePublisher(getApplicationContext(), topic);
                msgpb.publish();
                handlerP.postDelayed(this, rate);
            }
        }, 1500);


        new Thread(new Runnable() {
            public void run() {
                try {
                    MqttSubscriber subscriber = new MqttSubscriber(getApplicationContext(), "MQTT Examples", "tcp://localhost:1883");
                    subscriber.connect();
                    //subscriber.subscribe();
                    // subscriber.disconnect();
                } catch (MqttException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();


//        final Handler handlerS = new Handler();
//        handlerS.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                MqttSub subscriber = new MqttSub();
//                subscriber.main();
//            }
//        },1500);

    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(networkChangeReceiver);
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
            case R.id.connection_settings:
                Intent toy1 = new Intent(MainActivity.this, ConnectionActivity.class);
                startActivity(toy1);
                finish();
                break;
            case R.id.menu_mqtt_settings:
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
                alertDialog.setTitle("Connection Settings");
                alertDialog.setMessage("Please enter Ip/PortQ");

                final EditText input = new EditText(MainActivity.this);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                input.setText("tcp://192.168.1.2:1883");
                input.setLayoutParams(lp);
                alertDialog.setView(input);

                alertDialog.setNeutralButton("Save", new DialogInterface.OnClickListener() {

                    // click listener on the alert box
                    public void onClick(DialogInterface dialog, int which) {
                        Port_Ip = input.getText().toString();
                        dialog.dismiss();
                    }
                });
                alertDialog.show();
                break;
            case R.id.menu_Mqtt_Details:
                AlertDialog.Builder alertDialog1 = new AlertDialog.Builder(MainActivity.this);
                alertDialog1.setTitle("Mqtt Rate Settings");
                alertDialog1.setMessage("Please enter the rate you want");
                final EditText input1 = new EditText(MainActivity.this);
                LinearLayout.LayoutParams r = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                input1.setText("5000");
                input1.setLayoutParams(r);
                alertDialog1.setView(input1);

                alertDialog1.setNeutralButton("Save", new DialogInterface.OnClickListener() {

                    // click listener on the alert box
                    public void onClick(DialogInterface dialog, int which) {
                        rate = Integer.parseInt(input1.getText().toString());
                        dialog.dismiss();
                    }
                });
                alertDialog1.show();
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


    //-----------This function is used in order to Find out if the GPS of an Android device is enabled------------
    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }


    //-------------This function is used in order to unable the flashlight----------//
    @TargetApi(Build.VERSION_CODES.M)
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void flashLightOn() {
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

        try {
            String cameraId = cameraManager.getCameraIdList()[0];
            cameraManager.setTorchMode(cameraId, true);
        } catch (CameraAccessException e) {
        }
    }


    //-------------This function is used in order to disable the flashlight----------//
    @TargetApi(Build.VERSION_CODES.M)
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void flashLightOff() {
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            String cameraId = cameraManager.getCameraIdList()[0];
            cameraManager.setTorchMode(cameraId, false);
        } catch (CameraAccessException e) {
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case CAMERA_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    hasCameraFlash = getPackageManager().
                            hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
                }
                break;
        }
    }


}
