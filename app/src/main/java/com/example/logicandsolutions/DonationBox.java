package com.example.logicandsolutions;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class DonationBox extends AppCompatActivity {

    SeekBar amountSeekBar;
    TextView selectedAmount;
    EditText messageInput, amountInput;
    Button donationButton;
    int selectedval = 0;
    TextView welcome;
    private FirebaseAuth mauth;
    private DatabaseReference databaseReference;
    Button donationhistory;
    ImageView homeimg, developer, profile;
    String username = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donation_box);

        homeimg = findViewById(R.id.homeimg);
        developer = findViewById(R.id.developer);
        welcome = findViewById(R.id.welcome);
        mauth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        donationhistory = findViewById(R.id.donationhistory);
        amountSeekBar = findViewById(R.id.amountseekbar);
        selectedAmount = findViewById(R.id.selectedAmount);
        messageInput = findViewById(R.id.messageInput);
        donationButton = findViewById(R.id.donationbtn);
        profile = findViewById(R.id.profile);
        amountInput = findViewById(R.id.amountInput);

        amountSeekBar.setMax(10000);
        FetchUser();

        donationhistory.setOnClickListener(v -> startActivity(new Intent(DonationBox.this, DonationHistory.class)));

        homeimg.setOnClickListener(v -> {
            Intent intent = new Intent(DonationBox.this, Homeactivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        developer.setOnClickListener(v -> {
            Intent intent = new Intent(DonationBox.this, DeveloperInfo.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        profile.setOnClickListener(v -> {
            Intent intent = new Intent(DonationBox.this, Profileactivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        amountInput.setOnEditorActionListener((v, actionId, event) -> {
            try {
                int enteredAmount = Integer.parseInt(amountInput.getText().toString());
                if (enteredAmount <= 10000) {
                    amountSeekBar.setProgress(enteredAmount);
                } else {
                    Toast.makeText(getApplicationContext(), "Maximum is ₹10000", Toast.LENGTH_SHORT).show();
                }
            } catch (NumberFormatException e) {}
            return false;
        });

        amountSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                selectedval = progress;
                selectedAmount.setText("₹ " + selectedval);
                amountInput.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        donationButton.setOnClickListener(v -> {
            if (selectedval == 0) {
                Toast.makeText(getApplicationContext(), "Please select an amount", Toast.LENGTH_LONG).show();
            } else {
                String message = messageInput.getText().toString().trim();
                startPayment(username, selectedval, message);
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
        if (amount <= 0 || amount > 10000) {
            Toast.makeText(this, "Enter a valid amount up to ₹10000", Toast.LENGTH_SHORT).show();
            return;
        }

        String upiId = "7738356103@okbizaxis";
        String upiUrl = "upi://pay?pa=" + upiId +
                "&pn=" + Uri.encode("Soham Foundation") +
                "&tn=" + Uri.encode(message.isEmpty() ? "Donation" : message) +
                "&am=" + amount +
                "&cu=INR";

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(upiUrl));

        Intent chooser = Intent.createChooser(intent, "Pay with UPI");
        if (chooser.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(chooser, 1);
        } else {
            Toast.makeText(this, "No UPI app found!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if (data != null && data.getStringExtra("response") != null) {
                String response = data.getStringExtra("response");
                if (response.toLowerCase().contains("status=success")) {
                    onUPIPaymentSuccess();
                } else {
                    Toast.makeText(this, "Payment Failed or Cancelled", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "No response from UPI app", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void onUPIPaymentSuccess() {
        Toast.makeText(this, "Payment Success!", Toast.LENGTH_SHORT).show();

        String message = messageInput.getText().toString().trim();
        int amount = selectedval;
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        String userId = user.getUid();
        DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("Donations").child(userId);
        String donationId = dbref.push().getKey();

        if (donationId != null) {
            HashMap<String, Object> donationData = new HashMap<>();
            donationData.put("Name", username);
            donationData.put("Amount", amount);
            donationData.put("Message", message);
            donationData.put("Date", currentDate);
            donationData.put("PaymentID", "UPI_" + System.currentTimeMillis());

            dbref.child(donationId).setValue(donationData)
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(this, "Donation recorded", Toast.LENGTH_SHORT).show();
                        amountSeekBar.setProgress(0);
                        messageInput.setText("");
                        amountInput.setText("");
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Failed to record donation", Toast.LENGTH_SHORT).show());
        }
    }
}
