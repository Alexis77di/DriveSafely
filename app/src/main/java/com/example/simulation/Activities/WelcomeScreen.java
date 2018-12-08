package com.example.simulation.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;

import com.example.simulation.R;

//--This is the first activity--//
//--This action remain for 2 seconds--//
//Goes to next screen(activity) automatically--//

public class WelcomeScreen extends AppCompatActivity {
    private static final long SPLASH_TIME = 2000;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_screen);

        TextView textView = findViewById(R.id.textView);
        Animation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(150);         // manage the blinking time with this parameter
        anim.setStartOffset(20);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);
        textView.startAnimation(anim);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                /* Create an Intent that will start the Menu-Activity. */
                Intent mainIntent = new Intent(WelcomeScreen.this, HomeActivity.class);
                WelcomeScreen.this.startActivity(mainIntent);
                WelcomeScreen.this.finish();
            }
        }, SPLASH_TIME);
    }
}
