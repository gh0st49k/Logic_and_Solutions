package com.example.logicandsolutions;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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
        Animation animation = AnimationUtils.loadAnimation(this,R.anim.fadein_anim);
        welcomelogo.startAnimation(animation);
        mauth = FirebaseAuth.getInstance();

    }
    protected void onStart(){
        super.onStart();
        FirebaseUser currentUser =  mauth.getCurrentUser();
        if(currentUser!=null){
            new Handler().postDelayed(() -> {
                Intent intent = new Intent(MainActivity.this, Homeactivity.class);
                startActivity(intent);
                finish();
            }, 3000); //

        }else{

            new Handler().postDelayed(() -> {
                Intent intent = new Intent(MainActivity.this, Loginactivity.class);
                startActivity(intent);
                finish();
            }, 3000); //
        }

    }
}