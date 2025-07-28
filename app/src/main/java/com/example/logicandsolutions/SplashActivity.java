package com.example.logicandsolutions;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash); // Optional layout (see Step 4)

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
        String savedEmail = prefs.getString("email", "");

        if (currentUser != null) {
            if ("admin@sohamfoundation.com".equals(savedEmail)) {
                // Force admin to login again every time
                mAuth.signOut();
                prefs.edit().clear().apply();

                Intent intent = new Intent(SplashActivity.this, Loginactivity.class);
                intent.putExtra("admin_forced_login", true);
                startActivity(intent);
            } else {
                // Auto-login for regular user
                startActivity(new Intent(SplashActivity.this, Homeactivity.class));
            }
        } else {
            // No user logged in
            startActivity(new Intent(SplashActivity.this, Loginactivity.class));
        }

        finish(); // Close SplashActivity
    }
}
