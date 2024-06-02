package com.example.compassandgpscamera;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;

import java.util.Timer;
import java.util.TimerTask;

public class HomePage extends AppCompatActivity {
    Button b1;
    LinearLayout ll;
    ProgressBar pb;
    int c;

    ImageView iv;

    TextView textView;
    CharSequence originalText = "Compass And GPS Location Tracker";
    int index = 0;
    long delay = 150;
    private int progressStatus = 0;
    private Handler handler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        b1 = findViewById(R.id.b1);
        iv=findViewById(R.id.imgview);
        pb = findViewById(R.id.progressBar);

        startProgressAnimation();



        textView = findViewById(R.id.tv1);
        animateText();


        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create a new ImageView
                Intent goToNextActivity = new Intent(getApplicationContext(), HomeScreen.class);
                startActivity(goToNextActivity);

            }
        });

        iv.setBackgroundResource(R.drawable.logo_animation);
        AnimationDrawable animationDrawable = (AnimationDrawable)iv.getBackground();
        animationDrawable.start();


    }

    private void startProgressAnimation() {
        new Thread(() -> {
            while (progressStatus < 100) {
                progressStatus++;
                handler.post(() -> pb.setProgress(progressStatus));

                try {
                    Thread.sleep(50); // Adjust the delay as needed
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            // When progress reaches 100, hide the progress bar and show the button
            handler.post(() -> {
                pb.setVisibility(View.INVISIBLE);
                b1.setVisibility(View.VISIBLE);
            });
        }).start();
    }

    private void animateText() {
        if (index < originalText.length()+1) {
            textView.setText(originalText.subSequence(0, index++));
            new Handler().postDelayed(this::animateText, delay);
        }
    }
}
