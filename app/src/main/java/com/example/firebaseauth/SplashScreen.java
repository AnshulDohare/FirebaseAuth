package com.example.firebaseauth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

public class SplashScreen extends AppCompatActivity {
    TextView tvFirebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        tvFirebaseAuth = findViewById(R.id.tvFirebaseAuth);
        Animation scale = AnimationUtils.loadAnimation(this,R.anim.splash_anim);
        tvFirebaseAuth.setAnimation(scale);
        new Handler().postDelayed(()->{
                Intent intent = new Intent(SplashScreen.this,SignInPage.class);
                startActivity(intent);
                finish();
        },7500);

        /*new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

            }
        },700);*/

    }
}