package com.example.logicandsolutions;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
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

public class ProfileActivity extends AppCompatActivity {

    private TextView emailText;
    private EditText nameEditText, phoneEditText, newPasswordEditText, confirmPasswordEditText;
    private Button updateProfileButton, changePasswordButton, logoutButton;

    private FirebaseAuth mAuth;
    private DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initViews();
        setupFirebase();
        loadUserData();

        updateProfileButton.setOnClickListener(v -> updateProfile());
        changePasswordButton.setOnClickListener(v -> changePassword());
        logoutButton.setOnClickListener(v -> logout());
    }

    private void initViews() {
        emailText = findViewById(R.id.emailText);
        nameEditText = findViewById(R.id.nameEditText);
        phoneEditText = findViewById(R.id.phoneEditText);
        newPasswordEditText = findViewById(R.id.newPasswordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        updateProfileButton = findViewById(R.id.updateProfileButton);
        changePasswordButton = findViewById(R.id.changePasswordButton);
        logoutButton = findViewById(R.id.logoutButton);
    }

    private void setupFirebase() {
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            userRef = FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid());
            emailText.setText(currentUser.getEmail());
        }
    }

    private void loadUserData() {
        if (userRef != null) {
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String name = snapshot.child("name").getValue(String.class);
                    String phone = snapshot.child("phone").getValue(String.class);
                    
                    if (name != null) nameEditText.setText(name);
                    if (phone != null) phoneEditText.setText(phone);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(ProfileActivity.this, "Failed to load profile data", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void updateProfile() {
        String name = nameEditText.getText().toString().trim();
        String phone = phoneEditText.getText().toString().trim();

        if (name.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        updateProfileButton.setEnabled(false);
        updateProfileButton.setText("Updating...");

        HashMap<String, Object> updates = new HashMap<>();
        updates.put("name", name);
        updates.put("phone", phone);

        userRef.updateChildren(updates)
                .addOnCompleteListener(task -> {
                    updateProfileButton.setEnabled(true);
                    updateProfileButton.setText("Update Profile");
                    
                    if (task.isSuccessful()) {
                        Toast.makeText(ProfileActivity.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ProfileActivity.this, "Failed to update profile", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void changePassword() {
        String newPassword = newPasswordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();

        if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please fill password fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords don't match", Toast.LENGTH_SHORT).show();
            return;
        }

        if (newPassword.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        changePasswordButton.setEnabled(false);
        changePasswordButton.setText("Changing...");

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            currentUser.updatePassword(newPassword)
                    .addOnCompleteListener(task -> {
                        changePasswordButton.setEnabled(true);
                        changePasswordButton.setText("Change Password");
                        
                        if (task.isSuccessful()) {
                            Toast.makeText(ProfileActivity.this, "Password changed successfully", Toast.LENGTH_SHORT).show();
                            newPasswordEditText.setText("");
                            confirmPasswordEditText.setText("");
                        } else {
                            Toast.makeText(ProfileActivity.this, "Failed to change password", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void logout() {
        mAuth.signOut();
        startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
        finish();
    }
}