package com.example.simulation.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.example.simulation.R;

public class MqttDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.mqtt_details_activity);
        //------------ Info Message ---------------------//

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(MqttDetailsActivity.this, MainActivity.class));
    }
}
