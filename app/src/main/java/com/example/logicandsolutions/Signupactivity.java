package com.example.logicandsolutions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;


public class Signupactivity extends AppCompatActivity {
    EditText name, email, phone, password;
    Button signupbtn;
    private FirebaseAuth mauth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signupactivity);
        FirebaseApp.initializeApp(this);
        FirebaseAuth auth = FirebaseAuth.getInstance();

        name = findViewById(R.id.name);
        email = findViewById(R.id.useremail);
        phone = findViewById(R.id.phone);
        password = findViewById(R.id.password);
        signupbtn = findViewById(R.id.signupbtn);
        mauth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        signupbtn.setOnClickListener(v -> {
            String userName = name.getText().toString().trim();
            String userEmail = email.getText().toString().trim();
            String userPhone = phone.getText().toString().trim();
            String userPassword = password.getText().toString().trim();

            if (TextUtils.isEmpty(userName)) {
                name.setError("Name cannot be empty");
                return;
            }
            if (TextUtils.isEmpty(userEmail)) {
                email.setError("Email cannot be empty");
                return;
            }
            if (TextUtils.isEmpty(userPhone)) {
                phone.setError("Phone number cannot be empty");
                return;
            }
            if (TextUtils.isEmpty(userPassword)) {
                password.setError("Password cannot be empty");
                return;
            }

            mauth.createUserWithEmailAndPassword(userEmail, userPassword)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mauth.getCurrentUser();
                            if (user != null){
                                String userId = user.getUid();
                                HashMap<String,String> userData = new HashMap<>();
                                userData.put("Name",userName);
                                userData.put("Email",userEmail);
                                userData.put("Phone",userPhone);
                                databaseReference.child(userId).setValue(userData).addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()){
                                        Toast.makeText(Signupactivity.this, "Registered Successfully", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(Signupactivity.this,Homeactivity.class));
                                        finish();
                                    }
                                  else {
                                        Toast.makeText(Signupactivity.this, "Registration Failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();

                                    }



                                });



                            }
                            else {
                                Toast.makeText(Signupactivity.this, "Registration Failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();

                            }

                            Toast.makeText(Signupactivity.this, "Registered Successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(Signupactivity.this, "Registration Failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        });
    }
}
