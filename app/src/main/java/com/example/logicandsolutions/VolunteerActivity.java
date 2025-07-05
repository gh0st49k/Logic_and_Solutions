package com.example.logicandsolutions;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class VolunteerActivity extends AppCompatActivity {

    EditText etName, etMobile, etReason;
    Button btnSubmit;
    DatabaseReference volunteerRef;

    ImageView homeimg, developer, donation, logouticon, profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volunteer);

        etName = findViewById(R.id.etVolunteerName);
        etMobile = findViewById(R.id.etVolunteerMobile);
        etReason = findViewById(R.id.etVolunteerReason);
        btnSubmit = findViewById(R.id.btnSubmitVolunteer);
        volunteerRef = FirebaseDatabase.getInstance().getReference("VolunteerData");

        btnSubmit.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String mobile = etMobile.getText().toString().trim();
            String reason = etReason.getText().toString().trim();

            if (name.isEmpty() || mobile.isEmpty() || reason.isEmpty()) {
                if (name.isEmpty()) etName.setError("Name required");
                if (mobile.isEmpty()) etMobile.setError("Mobile required");
                if (reason.isEmpty()) etReason.setError("Reason required");
            } else {
                saveVolunteer(name, mobile, reason);
            }
        });

        // Footer nav setup
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
            Intent i = new Intent(VolunteerActivity.this, Loginactivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            finish();
        });
    }

    private void saveVolunteer(String name, String mobile, String reason) {
        String id = volunteerRef.push().getKey();
        HashMap<String, String> data = new HashMap<>();
        data.put("Name", name);
        data.put("Mobile", mobile);
        data.put("Reason", reason);

        assert id != null;
        volunteerRef.child(id).setValue(data).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Thanks for volunteering!", Toast.LENGTH_SHORT).show();
                etName.setText("");
                etMobile.setText("");
                etReason.setText("");
            } else {
                Toast.makeText(this, "Failed to submit", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateTo(Class<?> activityClass) {
        Intent i = new Intent(VolunteerActivity.this, activityClass);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        finish();
    }
}
