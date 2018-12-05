package com.example.simulation.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.simulation.R;

public class ConnectionActivity extends AppCompatActivity {

    private static final int WIFI_ENABLE_REQUEST = 0x1006;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.connection_activity);

        Button open = findViewById(R.id.open);
        open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intent=new Intent(Settings.ACTION_WIFI_SETTINGS);
                //startActivity(intent);

                startActivityForResult(new Intent(Settings.ACTION_WIFI_SETTINGS), 0);


            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {

        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(ConnectionActivity.this, MainActivity.class));
    }


}
