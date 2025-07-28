package com.example.logicandsolutions;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    ImageView welcomelogo;
    FirebaseAuth mauth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        welcomelogo = findViewById(R.id.welcomelogo);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.fadein_anim);
        welcomelogo.startAnimation(animation);

        mauth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mauth.getCurrentUser();

        new Handler().postDelayed(() -> {
            if (currentUser == null) {
                // No user logged in → go to Login
                startActivity(new Intent(MainActivity.this, Loginactivity.class));
            } else {
                String email = currentUser.getEmail();
                if ("admin@sohamfoundation.com".equals(email)) {
                    // Admin must re-login
                    mauth.signOut();
                    startActivity(new Intent(MainActivity.this, Loginactivity.class));
                } else {
                    // Regular user → go to Home
                    startActivity(new Intent(MainActivity.this, Homeactivity.class));
                }
            }
            finish(); // Close splash/Main screen
        }, 3000); // Delay to show animation
    }
}
