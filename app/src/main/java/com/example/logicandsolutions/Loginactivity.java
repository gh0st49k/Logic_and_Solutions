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

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String useremail = email.getText().toString().trim();
                String passwd = password.getText().toString().trim();

                // Validation
                if (useremail.isEmpty()) {
                    email.setError("Please enter your email");
                    email.requestFocus();
                    return;
                }

                if (passwd.isEmpty()) {
                    password.setError("Please enter your password");
                    password.requestFocus();
                    return;
                }

                if (useremail.equals("admin") && passwd.equals("1313")) {
                    Toast.makeText(Loginactivity.this, "Admin Login Successful", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(Loginactivity.this, AdminPanel.class));
                    finish();
                    return;
                }
                // Firebase login
                mauth.signInWithEmailAndPassword(useremail, passwd).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(Loginactivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(Loginactivity.this, Homeactivity.class));
                        finish();
                    } else {
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
}
