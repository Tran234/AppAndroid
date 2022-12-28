package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

public class SplashActivity extends AppCompatActivity {

    private TextView txtlogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        txtlogo = findViewById(R.id.txtlogo);
        Typeface face = Typeface.createFromAsset(getAssets(), "font/NABILA.TTF");
        txtlogo.setTypeface(face);


        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent intent = new Intent(SplashActivity.this, SignInActivity.class);
                startActivity(intent);
            }
        }, 2000);
    }
}