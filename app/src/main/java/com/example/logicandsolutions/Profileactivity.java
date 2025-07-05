package com.example.logicandsolutions;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;

public class Profileactivity extends AppCompatActivity {

    private TextView tvUserName, usrname, usremail, phno;
    private EditText etNewPassword, etConfirmPassword;
    private Button btnResetPassword;
    private ImageView usrlogout, profileImage;

    private FirebaseAuth mAuth;
    private DatabaseReference userRef;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profileactivity);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        userRef = FirebaseDatabase.getInstance().getReference("Users");

        // Link Views
        tvUserName = findViewById(R.id.tvUserName);
        usrname = findViewById(R.id.usrname);
        usremail = findViewById(R.id.usremail);
        phno = findViewById(R.id.phno);
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnResetPassword = findViewById(R.id.btnResetPassword);
        usrlogout = findViewById(R.id.usrlogout);
        profileImage = findViewById(R.id.profileImage);

        fetchUserData(); // 🟡 Fetch name, email, phone

        // 🔴 Reset Password Logic
        btnResetPassword.setOnClickListener(v -> {
            String newPass = etNewPassword.getText().toString().trim();
            String confirmPass = etConfirmPassword.getText().toString().trim();

            if (newPass.isEmpty() || confirmPass.isEmpty()) {
                Toast.makeText(this, "Please fill both fields", Toast.LENGTH_SHORT).show();
            } else if (!newPass.equals(confirmPass)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            } else if (newPass.length() < 6) {
                Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            } else {
                currentUser.updatePassword(newPass)
                        .addOnSuccessListener(unused -> {
                            Toast.makeText(this, "Password updated successfully", Toast.LENGTH_SHORT).show();
                            etNewPassword.setText("");
                            etConfirmPassword.setText("");
                        })
                        .addOnFailureListener(e -> Toast.makeText(this, "Failed: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }
        });

        // 🔵 Logout logic
        usrlogout.setOnClickListener(v -> {
            mAuth.signOut();
            Toast.makeText(Profileactivity.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
            finish(); // Optional: finish profile screen
        });
    }

    private void fetchUserData() {
        if (currentUser != null) {
            String uid = currentUser.getUid();

            userRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String name = snapshot.child("Name").getValue(String.class);
                    String email = snapshot.child("Email").getValue(String.class);
                    String phone = snapshot.child("Phone").getValue(String.class);

                    tvUserName.setText("Welcome, " + (name != null ? name : "User") + "!");
                    usrname.setText("Name: " + (name != null ? name : "N/A"));
                    usremail.setText("Email: " + (email != null ? email : "N/A"));
                    phno.setText("Phone: " + (phone != null ? phone : "N/A"));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(Profileactivity.this, "Error loading profile", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
