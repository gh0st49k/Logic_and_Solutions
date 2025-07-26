package com.example.logicandsolutions;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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

        // Initialization
        email = findViewById(R.id.username);
        password = findViewById(R.id.passwordlogin);
        loginBtn = findViewById(R.id.button);
        RedirectToRegister = findViewById(R.id.Register);
        mauth = FirebaseAuth.getInstance();

        // Login Button Click
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String useremail = email.getText().toString().trim();
                String passwd = password.getText().toString().trim();

                // Validation... (Your validation code is fine)

                // Firebase login
                mauth.signInWithEmailAndPassword(useremail, passwd).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if ("admin@sohamfoundation.com".equals(useremail)) {
                            // Admin login
                            startActivity(new Intent(Loginactivity.this, AdminPanel.class));
                        } else {
                            // Normal user login
                            startActivity(new Intent(Loginactivity.this, Homeactivity.class));
                        }
                        finish(); // Close LoginActivity
                    } else {
                        // Login failed
                        Toast.makeText(Loginactivity.this, "Login Failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        // Redirect to register
        RedirectToRegister.setOnClickListener(v -> {
            startActivity(new Intent(Loginactivity.this, Signupactivity.class));
        });
    }

    // THIS METHOD IS NOW IN THE CORRECT PLACE
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            // User is already signed in, go to home
            startActivity(new Intent(Loginactivity.this, Homeactivity.class));
            finish();
        }
    }
}