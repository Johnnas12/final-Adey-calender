package com.androidcalenderproject.ethiocalendar;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class splahActivity extends AppCompatActivity {
    EthioCalendarView ethioCalendarView;
    private static int splash_screen = 5000;
    Animation top, bottom;

    ImageView image;
    TextView logo, slogan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN );
        setContentView(R.layout.activity_splash);
        top= AnimationUtils.loadAnimation(this, R.anim.top_animation);
        bottom= AnimationUtils.loadAnimation(this, R.anim.bottom_animation);
        image = findViewById(R.id.imageView);
        logo = findViewById(R.id.textView);
        slogan= findViewById(R.id.textView2);
        image.setAnimation(top);
        logo.setAnimation(bottom);
        slogan.setAnimation(bottom);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, splash_screen);


        //  ethioCalendarView=(EthioCalendarView)findViewById(R.id.Ethio_Calendar_View);

    }
}
