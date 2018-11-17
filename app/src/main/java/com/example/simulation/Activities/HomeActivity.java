package com.example.simulation.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.simulation.R;

public class HomeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);

        //------------ Info Message ---------------------//
        final Button infobtn;
        infobtn = findViewById(R.id.infoBtn);
        infobtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView starttxt = findViewById(R.id.startText);
                starttxt.setVisibility(View.VISIBLE);
                infobtn.setEnabled(false);
            }
        });

        //-------------- Go to Next Screen(Activity) ----------------//
        Button startbtn;
        startbtn = findViewById(R.id.startBtn);
        startbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toy = new Intent(HomeActivity.this, MainActivity.class);
                startActivity(toy);
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}