package com.example.simulation.Activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
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
import com.example.simulation.util.DriveSafely;
import com.example.simulation.util.EegTransmitter;
import com.example.simulation.util.MqttPublisher;
import com.example.simulation.util.MqttSubcriber;
import com.example.simulation.util.NetworkChangeReceiver;

public class MainActivity extends AppCompatActivity {
    public String ip_port = "tcp://192.168.43.4:1883"; //by default   "tcp://192.168.43.4:1883"
    public long rate = 4000; //by default
    private MqttSubcriber subscriber;
    private MqttPublisher publisher;

    //--FlashLight--//
    private static final int CAMERA_REQUEST = 123;
    Button btnFlashLight;

    //--Sound--//
    Button btnSound;

    //--Connectivity--//

    private BroadcastReceiver networkChangeReceiver;
    private MyLocationListener locationListener;
    private EegTransmitter eegTransmitter;
    private AccelerometerListener accelero;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);


        networkChangeReceiver = new NetworkChangeReceiver();

        eegTransmitter = new EegTransmitter(getApplicationContext().getAssets());

        accelero = new AccelerometerListener(this);

        //----------------Listener for the GPS Location-----------------------//
        locationListener = new MyLocationListener();
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.INTERNET
                }, 10);
            }
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);


        publisher = new MqttPublisher(ip_port, DriveSafely.getMacAddr());
        subscriber = new MqttSubcriber(ip_port, DriveSafely.getMacAddr(), getApplicationContext(), this);
        subscriber.subscribe();

        final Handler mHandler = new Handler();
        final Runnable mStatusChecker = new Runnable() {
            TextView xText = findViewById(R.id.xText);
            TextView yText = findViewById(R.id.yText);
            TextView zText = findViewById(R.id.zText);
            TextView locationText = findViewById(R.id.locationText);
            TextView csvText = findViewById(R.id.csvText);

            boolean isGpsEnabled = false;

            @Override
            public void run() {
                updateScreen();
                sendData();
                mHandler.postDelayed(this, rate);
            }

            private void sendData() {
                publisher.main(DriveSafely.getMacAddr() + "/" + accelero.toString() + "/" + locationListener.toString() + "/" + eegTransmitter.getContent());
            }

            private void updateScreen() {
                //accelero
                xText.setText(String.valueOf(accelero.getX()));
                yText.setText(String.valueOf(accelero.getY()));
                zText.setText(String.valueOf(accelero.getZ()));
                //location
                locationText.setText(locationListener.toString());
                if (isGpsEnabled != locationListener.isGpsEnabled()) {
                    isGpsEnabled = locationListener.isGpsEnabled();
                    Context context = getApplicationContext();
                    if (isGpsEnabled) {
                        Toast.makeText(context, "Gps is turned on!! ", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Gps is turned off!! ", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        context.startActivity(i);
                    }
                }
                //csv data
                eegTransmitter.nextFile();
                //csvText.setText(eegTransmitter.getContent());
            }
        };
        mStatusChecker.run();

        //---------FlashLight Event--------------------------------------------//
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST);

        btnFlashLight = findViewById(R.id.btnFlashLightToggle);
        btnFlashLight.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View view) {
                //hasCameraFlash?
                if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
                    if (btnFlashLight.getText().toString().contains("ON")) {
                        btnFlashLight.setText(getString(R.string.flashOFF));
                        flashLightOff();
                    } else {
                        btnFlashLight.setText(getString(R.string.flashON));
                        flashLightOn();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "No flash available on your device",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });


        //---------------------------Sound Event-------------------------//
        btnSound = findViewById(R.id.btnSound);
        final MediaPlayer mp = MediaPlayer.create(this, R.raw.sound);
        btnSound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mp.start();
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();

        accelero.register();


        //-------------------Internet Connectivity------------------------------------//
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangeReceiver, intentFilter);

    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(networkChangeReceiver);
        accelero.unregister();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.INTERNET
                }, 10);
            }

            return;
        }
//        locationManager.removeUpdates(locationListener);
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
            case R.id.menu_mqtt_settings:
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
                alertDialog.setTitle("Connection Settings");
                alertDialog.setMessage("Please enter Ip/PortQ");

                final EditText input = new EditText(MainActivity.this);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                input.setText(ip_port);
                input.setLayoutParams(lp);
                alertDialog.setView(input);

                alertDialog.setNeutralButton("Save changes", new DialogInterface.OnClickListener() {

                    // click listener on the alert box
                    public void onClick(DialogInterface dialog, int which) {
                        ip_port = input.getText().toString();
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
                input1.setText(String.valueOf(rate));
                input1.setLayoutParams(r);
                alertDialog1.setView(input1);

                alertDialog1.setNeutralButton("Save changes", new DialogInterface.OnClickListener() {

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

    //-------------This function is used in order to unable the flashlight----------//
    @TargetApi(Build.VERSION_CODES.M)
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void flashLightOn() {
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
    public void flashLightOff() {
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
//                    hasCameraFlash = getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
                }
                break;
        }
    }
}
