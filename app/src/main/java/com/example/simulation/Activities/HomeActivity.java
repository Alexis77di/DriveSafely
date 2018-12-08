package com.example.simulation.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.example.simulation.R;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);

        //------------ Info Message ---------------------//
        final Button infobtn = findViewById(R.id.infoBtn);
        infobtn.setOnClickListener(new View.OnClickListener() {
            private final View starttxt = findViewById(R.id.startText);

            @Override
            public void onClick(View v) {
                starttxt.setVisibility(View.VISIBLE);
                infobtn.setEnabled(false);
            }
        });

        //-------------- Go to Next Screen(Activity) ----------------//
        Button startbtn = findViewById(R.id.startBtn);
        startbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toy = new Intent(HomeActivity.this, MainActivity.class);
                startActivity(toy);
            }
        });
    }
}