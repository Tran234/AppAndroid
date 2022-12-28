package com.example.app01;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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

                nextActivity();
            }
        }, 2000);
    }

    private void nextActivity() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null){
            Intent intent = new Intent(this, SignInActivity.class);
            startActivity(intent);
            //chua thanh cong
        }else {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            //thanh cong
        }
        finish();
    }
}