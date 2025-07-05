package com.example.logicandsolutions;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

import java.util.HashMap;

public class ContactusActivity extends AppCompatActivity {

    TextView welcome;
    private FirebaseAuth mauth;
    private DatabaseReference databaseReference;
    private ImageView donation;
    EditText etName, etMobile, etMessage;
    Button btnSubmit;
    DatabaseReference databaseReference1;
    ImageView homeimg,developer,profile;
    TextView mobile,emailrd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contactus);

        // Initialize Views
        profile =  findViewById(R.id.profile);
        etName = findViewById(R.id.etname);
        etMobile = findViewById(R.id.etmobile);
        etMessage = findViewById(R.id.etconcern);
        btnSubmit = findViewById(R.id.etBtnsubmit);
        homeimg = findViewById(R.id.homeimg);
        developer = findViewById(R.id.developer);
        databaseReference1 = FirebaseDatabase.getInstance().getReference("ContactData");

        btnSubmit.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String mobile = etMobile.getText().toString().trim();
            String message = etMessage.getText().toString().trim();

            if (name.isEmpty() || mobile.isEmpty() || message.isEmpty()) {
                if (name.isEmpty()) etName.setError("Name cannot be empty");
                if (mobile.isEmpty()) etMobile.setError("Mobile number cannot be empty");
                if (message.isEmpty()) etMessage.setError("Message cannot be empty");
            } else {
                contactData(); // Save to Firebase
            }
        });
        TextView contact = findViewById(R.id.contactDetails);
        if (contact != null) {
            contact.setMovementMethod(LinkMovementMethod.getInstance());
        } else {
            Toast.makeText(this, "contactDetails view not found!", Toast.LENGTH_SHORT).show();
        }


        donation = findViewById(R.id.donationbtnnav);
        donation.setOnClickListener(v -> {
            Intent intent = new Intent(ContactusActivity.this, DonationBox.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            finish(); // Optional: close current activity
        });
        // Firebase setup
        welcome = findViewById(R.id.welcome);
        mauth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        homeimg.setOnClickListener(v -> {

            // Redirect to Home Page
            Intent intent = new Intent(ContactusActivity.this, Homeactivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            finish(); // Optional: close current activity

        });
        developer.setOnClickListener(v -> {

            // Redirect to Home Page
            Intent intent = new Intent(ContactusActivity.this, DeveloperInfo.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            finish(); // Optional: close current activity

        });
        profile.setOnClickListener(v -> {
            startActivity(new Intent(ContactusActivity.this, Profileactivity.class));
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right); // ✅ Slide animation
        });

        FetchUser();
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
                        welcome.setText("Welcome, " + Name + "! Your concern is deeply appreciated.");

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

    private void contactData() {
        String name = etName.getText().toString().trim();
        String mobile = etMobile.getText().toString().trim();
        String message = etMessage.getText().toString().trim();

        String id = databaseReference1.push().getKey();
        HashMap<String, String> contactData = new HashMap<>();
        contactData.put("Name", name);
        contactData.put("Mobile", mobile);
        contactData.put("Message", message);

        assert id != null;
        databaseReference1.child(id).setValue(contactData)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(ContactusActivity.this, "Concern sent successfully", Toast.LENGTH_SHORT).show();
                        etName.setText("");
                        etMobile.setText("");
                        etMessage.setText("");
                    } else {
                        Toast.makeText(ContactusActivity.this, "Failed to send message", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
