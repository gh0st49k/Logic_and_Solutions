package com.example.logicandsolutions;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class AboutUsActivity extends AppCompatActivity {

    TextView contact;
    ImageView homeimg, developer, donation,logouticon,profile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        contact = findViewById(R.id.contactDetails);
        if (contact != null) {
            contact.setMovementMethod(LinkMovementMethod.getInstance());
        }

        // Footer Navigation ImageView IDs from footernav.xml
        homeimg = findViewById(R.id.homeimg);
        developer = findViewById(R.id.developer);
        donation = findViewById(R.id.donationbtnnav);
        profile =  findViewById(R.id.profile);

        // Home Navigation
        if (homeimg != null) {
            homeimg.setOnClickListener(v -> {
                Intent intent = new Intent(AboutUsActivity.this, Homeactivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                finish();
            });
        }
        logouticon =findViewById(R.id.logout);
        logouticon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();

                startActivity(new Intent(AboutUsActivity.this, Loginactivity.class));
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right); // ✅ Slide animation

            }
        });

        // Developer Info Navigation
        if (developer != null) {
            developer.setOnClickListener(v -> {
                Intent intent = new Intent(AboutUsActivity.this, DeveloperInfo.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                finish();
            });
        }
        profile.setOnClickListener(v -> {

            // Redirect to Home Page
            Intent intent = new Intent(AboutUsActivity.this, Profileactivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            finish(); // Optional: close current activity

        });

        // Donation Navigation
        if (donation != null) {
            donation.setOnClickListener(v -> {
                Intent intent = new Intent(AboutUsActivity.this, DonationBox.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                finish();
            });
        }
    }
}
