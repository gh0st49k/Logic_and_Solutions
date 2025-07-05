package com.example.logicandsolutions;

import android.content.Intent;
import android.os.Bundle;
import android.graphics.Color;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DonationHistory extends AppCompatActivity {

    private TextView welcome, totalAmountText;
    private FirebaseAuth mauth;
    private DatabaseReference userRef, donationRef;
    private LinearLayout historyContainer;
    private int totalDonated = 0;
    ImageView homeimg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donation_history);
        homeimg = findViewById(R.id.homeimg);
        welcome = findViewById(R.id.welcome);
        totalAmountText = findViewById(R.id.totalAmountText);
        historyContainer = findViewById(R.id.historyContainer);

        mauth = FirebaseAuth.getInstance();
        userRef = FirebaseDatabase.getInstance().getReference("Users");
        String userId = mauth.getCurrentUser().getUid();
        donationRef = FirebaseDatabase.getInstance().getReference("Donations").child(userId);

        FetchUser();
        FetchDonationHistory();
        homeimg.setOnClickListener(v -> {

            // Redirect to Home Page
            Intent intent = new Intent(DonationHistory.this, Homeactivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            finish(); // Optional: close current activity

        });
    }

    private void FetchUser() {
        FirebaseUser currentUser = mauth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            userRef.child(userId).child("Name").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String name = snapshot.getValue(String.class);
                    if (name != null && !name.trim().isEmpty()) {
                        welcome.setText("Welcome, " + name + "!");
                    } else {
                        welcome.setText("Welcome, Guest!");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getApplicationContext(), "Failed to load name", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void FetchDonationHistory() {
        donationRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                totalDonated = 0;
                historyContainer.removeAllViews();

                if (!snapshot.exists()) {
                    Toast.makeText(getApplicationContext(), "No Donation History Found", Toast.LENGTH_SHORT).show();
                    return;
                }

                for (DataSnapshot donationSnap : snapshot.getChildren()) {
                    Object amountObj = donationSnap.child("Amount").getValue();
                    String date = donationSnap.child("Date").getValue(String.class);
                    String message = donationSnap.child("Message").getValue(String.class);

                    int amount = 0;
                    if (amountObj instanceof Long) {
                        amount = ((Long) amountObj).intValue();
                    } else if (amountObj instanceof Integer) {
                        amount = (Integer) amountObj;
                    }

                    totalDonated += amount;

                    // Stylized TextView for each donation
                    TextView donationView = new TextView(DonationHistory.this);
                    donationView.setText("₹" + amount + " on " + date + "\nMessage: " + (message != null ? message : "—"));
                    donationView.setTextSize(16);
                    donationView.setTextColor(Color.parseColor("#1B1B1B"));
                    donationView.setPadding(24, 24, 24, 24);
                    donationView.setBackgroundResource(R.drawable.donation_card_bg); // Custom background

                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    );
                    params.setMargins(0, 0, 0, 16); // Bottom margin between cards
                    donationView.setLayoutParams(params);

                    historyContainer.addView(donationView);
                }

                totalAmountText.setText("Total Donated: ₹" + totalDonated);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Failed To Load Data", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
