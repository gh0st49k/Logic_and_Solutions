package com.example.logicandsolutions;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Loginactivity extends AppCompatActivity {

    EditText email, password;
    Button loginBtn;
    TextView RedirectToRegister;
    private FirebaseAuth mauth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginactivity);

        // Initialize views
        email = findViewById(R.id.username);
        password = findViewById(R.id.passwordlogin);
        loginBtn = findViewById(R.id.button);
        RedirectToRegister = findViewById(R.id.Register);
        mauth = FirebaseAuth.getInstance();

        // 🔔 Check if admin was forced to re-login
        if (getIntent().getBooleanExtra("admin_forced_login", false)) {
            Toast.makeText(this, "Admin must log in again.", Toast.LENGTH_SHORT).show();
        }

        loginBtn.setOnClickListener(v -> {
            String useremail = email.getText().toString().trim();
            String passwd = password.getText().toString().trim();

            if (useremail.isEmpty() || passwd.isEmpty()) {
                Toast.makeText(Loginactivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            mauth.signInWithEmailAndPassword(useremail, passwd).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // 🧠 Save session info for non-admins
                    SharedPreferences prefs = getSharedPreferences("UserSession", MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("email", useremail);
                    editor.apply();

                    if ("admin@sohamfoundation.com".equals(useremail)) {
                        startActivity(new Intent(Loginactivity.this, AdminPanel.class));
                    } else {
                        startActivity(new Intent(Loginactivity.this, Homeactivity.class));
                    }
                    finish();
                } else {
                    Toast.makeText(Loginactivity.this, "Login Failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        });

        // 👉 Redirect to sign-up
        RedirectToRegister.setOnClickListener(v -> startActivity(new Intent(Loginactivity.this, Signupactivity.class)));
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mauth.getCurrentUser();

        if (currentUser != null) {
            String userEmail = currentUser.getEmail();

            if ("admin@sohamfoundation.com".equals(userEmail)) {
                // 🔐 Admin must always log in again manually
                Toast.makeText(this, "Admin access requires login", Toast.LENGTH_SHORT).show();
                mauth.signOut(); // Clear Firebase session
                // Don't navigate — stay on login screen
            } else {
                // 🚀 Auto-login regular user
                startActivity(new Intent(Loginactivity.this, Homeactivity.class));
                finish();
            }
        }
    }
}
