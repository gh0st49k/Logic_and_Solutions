package com.example.logicandsolutions;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Homeactivity extends AppCompatActivity {

    TextView welcome;
    private FirebaseAuth mauth;
    private DatabaseReference databaseReference;
    private ViewFlipper viewFlipper;

    private ImageView donation, contactus, aboutus, feedback, gallery, volunteer, socialmedia , logouticon,developer,homeimg,profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homeactivity);

        // Initialize Firebase and views
        profile = findViewById(R.id.profile);
        welcome = findViewById(R.id.welcome);
        mauth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        viewFlipper = findViewById(R.id.homeSlidder);
        viewFlipper.setFlipInterval(3000);
        viewFlipper.setAutoStart(true);
        viewFlipper.setInAnimation(AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left));
        viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right));
        viewFlipper.startFlipping();

        // Welcome Text Fade-in Animation
        Animation welcomeAnim = AnimationUtils.loadAnimation(this, R.anim.fadein_anim);
        welcome.startAnimation(welcomeAnim);
        homeimg = findViewById(R.id.homeimg);
        // Initialize navigation buttons
        developer = findViewById(R.id.developer);
        donation = findViewById(R.id.donationbtnnav);
        contactus = findViewById(R.id.contactus);
        aboutus = findViewById(R.id.aboutus);
        feedback = findViewById(R.id.feedback);
        gallery = findViewById(R.id.gallery);
        volunteer = findViewById(R.id.volunteer);
        socialmedia = findViewById(R.id.socialmedia);
        logouticon =findViewById(R.id.logout);
        logouticon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();

                startActivity(new Intent(Homeactivity.this, Loginactivity.class));
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right); // ✅ Slide animation

            }
        });

        // Set navigation click listeners
        donation.setOnClickListener(v -> {
            startActivity(new Intent(Homeactivity.this, DonationBox.class));
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right); // ✅ Slide animation
        });
        developer.setOnClickListener(v -> {
            startActivity(new Intent(Homeactivity.this, DeveloperInfo.class));
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right); // ✅ Slide animation
        });
        aboutus.setOnClickListener(v -> {
            startActivity(new Intent(Homeactivity.this, AboutUsActivity.class));
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right); // ✅ Slide animation
        });
        homeimg.setOnClickListener(v -> {
            if (Homeactivity.this.getClass().equals(Homeactivity.class)) {
                // Already on Homeactivity
                Toast.makeText(Homeactivity.this, "Already on Home Page", Toast.LENGTH_SHORT).show();
            } else {
                // Redirect to Home Page
                Intent intent = new Intent(this, Homeactivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                finish(); // Optional: close current activity
            }
        });
        profile.setOnClickListener(v -> {
            startActivity(new Intent(Homeactivity.this, Profileactivity.class));
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right); // ✅ Slide animation
        });
        gallery.setOnClickListener(v -> {
            startActivity(new Intent(Homeactivity.this, com.example.logicandsolutions.GalleryActivity.class));
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right); // ✅ Slide animation
        });

        contactus.setOnClickListener(v -> navigateWithSlide(ContactusActivity.class));
        aboutus.setOnClickListener(v -> navigateWithSlide(AboutUsActivity.class));
        feedback.setOnClickListener(v -> navigateWithSlide(FeedbackActivity.class));
        gallery.setOnClickListener(v -> navigateWithSlide(com.example.logicandsolutions.GalleryActivity.class));
        volunteer.setOnClickListener(v -> navigateWithSlide(VolunteerActivity.class));
        socialmedia.setOnClickListener(v -> navigateWithSlide(SocialMediaActivity.class));

        // Run sequential animations for dashboard tiles
        runTileAnimations();

        // Fetch welcome name
        FetchUser();
    }

    private void runTileAnimations() {
        aboutus.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fadein_aboutus));
        contactus.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fadein_contactus));
        feedback.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fadein_feedback));
        gallery.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fadein_gallery));
        socialmedia.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fadein_socialmedia));
        volunteer.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fadein_volunteer));

    }


    private void navigateWithSlide(Class<?> destinationActivity) {
        Intent intent = new Intent(Homeactivity.this, destinationActivity);
        startActivity(intent);
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    private void FetchUser() {
        FirebaseUser currentUser = mauth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            databaseReference.child(userId).child("Name").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String Name = snapshot.getValue(String.class);
                    if (Name != null && !Name.trim().isEmpty()) {
                        welcome.setText("Welcome, " + Name + "!");
                    } else {
                        welcome.setText("Welcome, Guest!");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getApplicationContext(), "Failed to load username!!", Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}
