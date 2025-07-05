package com.example.logicandsolutions;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class SocialMediaActivity extends AppCompatActivity {

    ImageView instagram, facebook, twitter, youtube;
    ImageView homeimg, profile, developer, donation, logouticon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social_media);

        // Social Media Icons
        instagram = findViewById(R.id.instagram);
        facebook = findViewById(R.id.facebook);
        twitter = findViewById(R.id.twitter);
        youtube = findViewById(R.id.youtube);

        // Open respective social media links
        instagram.setOnClickListener(v -> openLink("https://www.instagram.com/sohamfoundation/"));
        facebook.setOnClickListener(v -> openLink("https://www.facebook.com/www.sohamfoundations.org/"));
        twitter.setOnClickListener(v -> openLink("https://x.com/ngo_soham"));
        youtube.setOnClickListener(v -> openLink("https://www.youtube.com/channel/UCStENhzcijnR0-Qa5rkW8mQ"));

        // Footer Nav Icons
        homeimg = findViewById(R.id.homeimg);
        profile = findViewById(R.id.profile);
        developer = findViewById(R.id.developer);
        donation = findViewById(R.id.donationbtnnav);
        logouticon = findViewById(R.id.logout);

        // Home click
        homeimg.setOnClickListener(v -> {
            Intent i = new Intent(this, Homeactivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            finish();
        });

        // Profile click
        profile.setOnClickListener(v -> {
            Intent i = new Intent(this, Profileactivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            finish();
        });

        // Developer click
        developer.setOnClickListener(v -> {
            Intent i = new Intent(this, DeveloperInfo.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            finish();
        });

        // Donation click
        donation.setOnClickListener(v -> {
            Intent i = new Intent(this, DonationBox.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            finish();
        });

        // Logout
        logouticon.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(SocialMediaActivity.this, Loginactivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            finish();
        });
    }

    private void openLink(String url) {
        try {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(browserIntent);
        } catch (Exception e) {
            Toast.makeText(this, "Unable to open link", Toast.LENGTH_SHORT).show();
        }
    }
}
