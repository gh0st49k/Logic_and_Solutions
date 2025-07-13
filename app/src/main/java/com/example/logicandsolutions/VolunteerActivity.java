package com.example.logicandsolutions;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseAuth;

import java.util.HashMap;

public class VolunteerActivity extends AppCompatActivity {

    EditText etName, etMobile, etReason;
    Button btnSubmit;
    Uri upiUri;
    DatabaseReference volunteerRef;
    private static final int UPI_PAYMENT_REQUEST = 200;

    ImageView homeimg, profile, developer, donation, logouticon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volunteer);

        etName = findViewById(R.id.etVolunteerName);
        etMobile = findViewById(R.id.etVolunteerMobile);
        etReason = findViewById(R.id.etVolunteerReason);
        btnSubmit = findViewById(R.id.btnSubmitVolunteer);

        volunteerRef = FirebaseDatabase.getInstance().getReference("Volunteers");

        btnSubmit.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String mobile = etMobile.getText().toString().trim();
            String reason = etReason.getText().toString().trim();

            if (name.isEmpty()) {
                etName.setError("Name required");
            } else if (mobile.isEmpty()) {
                etMobile.setError("Mobile required");
            } else if (reason.isEmpty()) {
                etReason.setError("Reason required");
            } else {
                launchUPIPayment("200");
            }
        });

        // Footer nav
        homeimg = findViewById(R.id.homeimg);
        profile = findViewById(R.id.profile);
        developer = findViewById(R.id.developer);
        donation = findViewById(R.id.donationbtnnav);
        logouticon = findViewById(R.id.logout);

        homeimg.setOnClickListener(v -> navigateTo(Homeactivity.class));
        profile.setOnClickListener(v -> navigateTo(Profileactivity.class));
        developer.setOnClickListener(v -> navigateTo(DeveloperInfo.class));
        donation.setOnClickListener(v -> navigateTo(DonationBox.class));
        logouticon.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            navigateTo(Loginactivity.class);
        });
    }

    private void launchUPIPayment(String amount) {
        Uri uri = Uri.parse("upi://pay").buildUpon()
                .appendQueryParameter("pa", "7738356103@okbizaxis") // replace with your actual UPI ID
                .appendQueryParameter("pn", "Soham Foundation")
                .appendQueryParameter("tn", "Volunteer Registration")
                .appendQueryParameter("am", amount)
                .appendQueryParameter("cu", "INR")
                .build();

        Intent upiIntent = new Intent(Intent.ACTION_VIEW);
        upiIntent.setData(uri);
        Intent chooser = Intent.createChooser(upiIntent, "Pay ₹200 via UPI");

        if (upiIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(chooser, UPI_PAYMENT_REQUEST);
        } else {
            Toast.makeText(this, "No UPI app found", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == UPI_PAYMENT_REQUEST) {
            if (data != null && data.getStringExtra("response") != null &&
                    data.getStringExtra("response").toLowerCase().contains("success")) {
                saveVolunteerData(); // Only save after successful payment
            } else {
                Toast.makeText(this, "Payment failed or cancelled", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void saveVolunteerData() {
        String name = etName.getText().toString().trim();
        String mobile = etMobile.getText().toString().trim();
        String reason = etReason.getText().toString().trim();

        String id = volunteerRef.push().getKey();
        HashMap<String, String> data = new HashMap<>();
        data.put("Name", name);
        data.put("Mobile", mobile);
        data.put("Reason", reason);

        if (id != null) {
            volunteerRef.child(id).setValue(data).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(this, "Registered as Volunteer!", Toast.LENGTH_SHORT).show();
                    etName.setText("");
                    etMobile.setText("");
                    etReason.setText("");
                } else {
                    Toast.makeText(this, "Failed to register!", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void navigateTo(Class<?> target) {
        Intent i = new Intent(VolunteerActivity.this, target);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        finish();
    }
}
