package com.example.logicandsolutions;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {

    private ImageView logoImage;
    private TextView appName;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        logoImage = findViewById(R.id.logoImage);
        appName = findViewById(R.id.appName);
        mAuth = FirebaseAuth.getInstance();

        // Start animations
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        Animation slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);
        
        logoImage.startAnimation(fadeIn);
        appName.startAnimation(slideUp);

        // Navigate after delay
        new Handler().postDelayed(() -> {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            Intent intent;
            
            if (currentUser != null) {
                String email = currentUser.getEmail();
                if ("admin@eventmanager.com".equals(email)) {
                    intent = new Intent(SplashActivity.this, AdminPanelActivity.class);
                } else {
                    intent = new Intent(SplashActivity.this, MainActivity.class);
                }
            } else {
                intent = new Intent(SplashActivity.this, LoginActivity.class);
            }
            
            startActivity(intent);
            finish();
        }, 3000);
    }
}