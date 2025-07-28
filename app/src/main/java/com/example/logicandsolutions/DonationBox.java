package com.example.logicandsolutions;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class DonationBox extends AppCompatActivity {

    // A constant for our UPI request
    private static final int UPI_PAYMENT_REQUEST_CODE = 1;

    // UI Elements
    private SeekBar amountSeekBar;
    private TextView selectedAmount, welcome;
    private EditText messageInput, amountInput;
    private Button donationButton, donationhistory;
    private ImageView homeimg, developer, profile;

    // Firebase
    private FirebaseAuth mauth;

    // Class variables
    private int selectedval = 0;
    private String username = ""; // Store the fetched username

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donation_box);

        initializeViews();
        mauth = FirebaseAuth.getInstance();
        fetchUser(); // Fetch user's name on create
        setupListeners();
    }

    private void initializeViews() {
        homeimg = findViewById(R.id.homeimg);
        developer = findViewById(R.id.developer);
        profile = findViewById(R.id.profile);
        welcome = findViewById(R.id.welcome);
        donationhistory = findViewById(R.id.donationhistory);
        amountSeekBar = findViewById(R.id.amountseekbar);
        selectedAmount = findViewById(R.id.selectedAmount);
        messageInput = findViewById(R.id.messageInput);
        donationButton = findViewById(R.id.donationbtn);
        amountInput = findViewById(R.id.amountInput);
        amountSeekBar.setMax(10000); // Set max donation amount
    }

    private void setupListeners() {
        donationhistory.setOnClickListener(v -> startActivity(new Intent(DonationBox.this, DonationHistory.class)));

        // Other navigation listeners...
        homeimg.setOnClickListener(v -> startActivity(new Intent(DonationBox.this, Homeactivity.class)));
        developer.setOnClickListener(v -> startActivity(new Intent(DonationBox.this, DeveloperInfo.class)));
        profile.setOnClickListener(v -> startActivity(new Intent(DonationBox.this, Profileactivity.class)));

        amountSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                selectedval = progress;
                selectedAmount.setText("₹ " + selectedval);
                if (fromUser) {
                    amountInput.setText(String.valueOf(progress));
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        donationButton.setOnClickListener(v -> {
            String amountString = amountInput.getText().toString();
            if (amountString.isEmpty() || Integer.parseInt(amountString) == 0) {
                Toast.makeText(getApplicationContext(), "Please select or enter an amount", Toast.LENGTH_LONG).show();
            } else {
                startPayment();
            }
        });
    }

    private void fetchUser() {
        FirebaseUser currentUser = mauth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId).child("Name");
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String name = snapshot.getValue(String.class);
                    if (name != null && !name.trim().isEmpty()) {
                        welcome.setText("Welcome, " + name + "!");
                        username = name; // Store username for later
                    } else {
                        welcome.setText("Welcome, Guest!");
                        username = "Guest";
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getApplicationContext(), "Failed to load username!", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void startPayment() {
        int amount = Integer.parseInt(amountInput.getText().toString());
        String message = messageInput.getText().toString().trim();

        String upiId = "rajendra.dethe3283@okaxis"; // Your UPI ID
        String foundationName = "Soham Foundation";
        String note = message.isEmpty() ? "Donation" : message;

        Uri uri = new Uri.Builder()
                .scheme("upi")
                .authority("pay")
                .appendQueryParameter("pa", upiId)
                .appendQueryParameter("pn", foundationName)
                .appendQueryParameter("tn", note)
                .appendQueryParameter("am", String.valueOf(amount))
                .appendQueryParameter("cu", "INR")
                .build();

        Intent upiPayIntent = new Intent(Intent.ACTION_VIEW);
        upiPayIntent.setData(uri);

        Intent chooser = Intent.createChooser(upiPayIntent, "Pay with");

        if (chooser.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(chooser, UPI_PAYMENT_REQUEST_CODE);
        } else {
            Toast.makeText(this, "No UPI app found on your device.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == UPI_PAYMENT_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                String response = data.getStringExtra("response");
                if (response != null && response.toLowerCase().contains("success")) {
                    // Payment was successful, now record it in Firebase
                    recordDonationInFirebase(response);
                } else {
                    Toast.makeText(this, "Payment Failed or Cancelled", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Payment Failed or Cancelled", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void recordDonationInFirebase(String upiResponse) {
        FirebaseUser user = mauth.getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "You're not logged in.", Toast.LENGTH_SHORT).show();
            return;
        }

        String uid = user.getUid();
        DatabaseReference donationRef = FirebaseDatabase.getInstance().getReference("Donations").child(uid);

        // Prepare the data to be saved
        HashMap<String, Object> donationData = new HashMap<>();
        donationData.put("Name", username); // The username we fetched earlier
        donationData.put("Amount", Integer.parseInt(amountInput.getText().toString()));
        donationData.put("Message", messageInput.getText().toString().trim());
        donationData.put("Date", new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date()));
        donationData.put("PaymentId", upiResponse); // Save the full UPI response as the ID

        // Push the data to Firebase
        donationRef.push().setValue(donationData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Thank you! Your donation has been recorded.", Toast.LENGTH_LONG).show();
                    // Reset fields after successful donation
                    amountSeekBar.setProgress(0);
                    messageInput.setText("");
                    amountInput.setText("");
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Payment successful, but failed to record donation.", Toast.LENGTH_SHORT).show());
    }
}