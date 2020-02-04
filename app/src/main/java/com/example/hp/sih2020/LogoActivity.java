package com.example.hp.sih2020;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class LogoActivity extends AppCompatActivity {
    TextView txt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logo);
        txt=(TextView)findViewById(R.id.textView4);

        final ValueAnimator valueAnimator=new ValueAnimator();
        valueAnimator.setDuration(1700);
        valueAnimator.setEvaluator(new ArgbEvaluator());
        valueAnimator.setIntValues(Color.BLUE,Color.RED);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                txt.setTextColor((int)valueAnimator.getAnimatedValue());
            }
        });
        valueAnimator.start();


        new Timer().schedule(
                new TimerTask(){

                    @Override
                    public void run(){

                        //if you need some code to run when the delay expires
                        Intent i=new Intent(LogoActivity.this,SelectSocietyActivity.class);
                        startActivity(i);
                        overridePendingTransition(android.R.anim.accelerate_interpolator,android.R.anim.decelerate_interpolator);
                    }

                },1700);
    }
}
