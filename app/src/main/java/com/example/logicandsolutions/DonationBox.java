package com.example.logicandsolutions;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class DonationBox extends AppCompatActivity implements PaymentResultListener {

    SeekBar amountSeekBar;
    TextView selectedAmount;
    EditText messageInput;
    Button donationButton;
    int selectedval = 0;
    TextView welcome;
    private FirebaseAuth mauth;
    private DatabaseReference databaseReference;
    Button donationhistory;
  ImageView homeimg,developer,profile;
    String username = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donation_box);
        homeimg  = findViewById(R.id.homeimg);
        developer =  findViewById(R.id.developer);
        welcome = findViewById(R.id.welcome);
        mauth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        donationhistory = findViewById(R.id.donationhistory);
        amountSeekBar = findViewById(R.id.amountseekbar);
        selectedAmount = findViewById(R.id.selectedAmount);
        messageInput = findViewById(R.id.messageInput);
        donationButton = findViewById(R.id.donationbtn);
        profile = findViewById(R.id.profile);

        amountSeekBar.setMax(100);
        FetchUser();

        donationhistory.setOnClickListener(v -> {
            startActivity(new Intent(DonationBox.this, DonationHistory.class));
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        });
        homeimg.setOnClickListener(v -> {

            // Redirect to Home Page
            Intent intent = new Intent(DonationBox.this, Homeactivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            finish(); // Optional: close current activity

        });
        developer.setOnClickListener(v -> {

            // Redirect to Home Page
            Intent intent = new Intent(DonationBox.this, DeveloperInfo.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            finish(); // Optional: close current activity

        });
        profile.setOnClickListener(v -> {

            // Redirect to Home Page
            Intent intent = new Intent(DonationBox.this, Profileactivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            finish(); // Optional: close current activity

        });

        amountSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                selectedval = progress;
                selectedAmount.setText("₹ " + selectedval);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        donationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedval == 0) {
                    Toast.makeText(getApplicationContext(), "Please select an amount", Toast.LENGTH_LONG).show();
                } else {
                    String message = messageInput.getText().toString().trim();
                    startPayment(username, selectedval, message);
                }
            }
        });
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
                        username = Name;
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

    public void startPayment(String name, int amount, String message) {
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            Toast.makeText(getApplicationContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = user.getUid();
        DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("PendingDonations").child(userId);
        String donationId = dbref.push().getKey();

        if (donationId != null) {
            HashMap<String, Object> donationData = new HashMap<>();
            donationData.put("Name",username);
            donationData.put("Amount", amount);
            donationData.put("Message", message);
            donationData.put("Date", currentDate);
            donationData.put("Status", "Pending");

            dbref.child(donationId).setValue(donationData)
                    .addOnSuccessListener(unused -> Toast.makeText(getApplicationContext(), "Donation Recorded", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(getApplicationContext(), "Failed to record donation", Toast.LENGTH_SHORT).show());
        }

        // Now open Razorpay payment gateway
        Checkout checkout = new Checkout();
        checkout.setKeyID("rzp_test_XZNVr7e6nuS7wy");
        checkout.setImage(com.razorpay.R.drawable.rzp_logo);

        final Activity activity = this;

        try {
            JSONObject options = new JSONObject();
            options.put("name", "Live Donation Box");
            options.put("description", "Donation Payment");
            options.put("image", "https://i.postimg.cc/NM4Ns2SJ/img-1.png");
            options.put("theme.color", "#00796B");
            options.put("currency", "INR");
            options.put("amount", amount * 100);
            JSONObject method = new JSONObject();
            method.put("upi", true);
            method.put("card", true);
            method.put("netbanking", true);
            options.put("method", method);
            options.put("prefill.email", "gaurav.ailsinghani2000@gmail.com");
            options.put("prefill.contact", "+91 7709667027");
            JSONObject prefill = new JSONObject();
            prefill.put("email", "gaurav.ailsinghani2000@gmail.com");
            prefill.put("contact", "+91 7709667027");
            options.put("prefill", prefill);
            


            checkout.open(activity, options);
        } catch (Exception e) {
            Toast.makeText(activity, "Error in payment: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onPaymentSuccess(String razorpayPaymentID) {
        Toast.makeText(getApplicationContext(), "Payment Success!", Toast.LENGTH_SHORT).show();

        String message = messageInput.getText().toString().trim();
        int amount = selectedval;
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(getApplicationContext(), "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = user.getUid();
        DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("Donations").child(userId);
        String donationId = dbref.push().getKey();

        if (donationId != null) {
            HashMap<String, Object> donationData = new HashMap<>();
            donationData.put("Name",username);
            donationData.put("Amount", amount);
            donationData.put("Message", message);
            donationData.put("Date", currentDate);
            donationData.put("PaymentID", razorpayPaymentID);


            dbref.child(donationId).setValue(donationData)
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(getApplicationContext(), "Data Saved in Firebase!", Toast.LENGTH_LONG).show();
                        messageInput.setText("");
                        amountSeekBar.setProgress(0);
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getApplicationContext(), "Failed to save in Firebase", Toast.LENGTH_LONG).show();
                    });
        }
    }

    public void onPaymentError(int code, String response) {

        Toast.makeText(getApplicationContext(),"Payment Failed",Toast.LENGTH_LONG).show();

    }

}