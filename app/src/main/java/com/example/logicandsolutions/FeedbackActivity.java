package com.example.logicandsolutions;

import android.content.Intent;
import android.net.Uri;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;

public class FeedbackActivity extends AppCompatActivity {

    TextView welcome;
    private FirebaseAuth mauth;
    private DatabaseReference userRef;
    private DatabaseReference feedbackRef;

    EditText etName, etMobile, etFeedback;
    Button btnSubmit;
    ImageView donation;
    private static final int PICK_FILE_REQUEST = 1;
    private Uri fileUri = null;

    Button btnAttach;
    ImageView homeimg,developer,profile;
    TextView feedbackText;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback); // Make sure XML matches this name
        btnAttach = findViewById(R.id.btnAttach);

        btnAttach.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*"); // Use "image/*" for only images
            startActivityForResult(Intent.createChooser(intent, "Select Attachment"), PICK_FILE_REQUEST);
        });

        // Initialize Views

        profile = findViewById(R.id.profile);
        etName = findViewById(R.id.etname);
        etMobile = findViewById(R.id.etmobile);
        etFeedback = findViewById(R.id.etconcern);
        btnSubmit = findViewById(R.id.etBtnsubmit);
        donation = findViewById(R.id.donationbtnnav);
        welcome = findViewById(R.id.welcome);
        homeimg = findViewById(R.id.homeimg);
        developer = findViewById(R.id.developer);
        // Firebase Refs
        mauth = FirebaseAuth.getInstance();
        userRef = FirebaseDatabase.getInstance().getReference("Users");
        feedbackRef = FirebaseDatabase.getInstance().getReference("FeedbackData");


        FetchUser();

        feedbackText = findViewById(R.id.feedbackText);
        if (feedbackText != null) {
            feedbackText.setMovementMethod(LinkMovementMethod.getInstance());
        } else{
            Throwable e = null;
            Toast.makeText(FeedbackActivity.this, "Login Failed: " + e.getMessage(), Toast.LENGTH_LONG).show();


        }



        homeimg.setOnClickListener(v -> {

                // Redirect to Home Page
                Intent intent = new Intent(FeedbackActivity.this, Homeactivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                finish(); // Optional: close current activity

        });
        profile.setOnClickListener(v -> {
            startActivity(new Intent(FeedbackActivity.this, Profileactivity.class));
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right); // ✅ Slide animation
        });
        developer.setOnClickListener(v -> {

            // Redirect to Home Page
            Intent intent = new Intent(FeedbackActivity.this, DeveloperInfo.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            finish(); // Optional: close current activity

        });

        btnSubmit.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String mobile = etMobile.getText().toString().trim();
            String feedback = etFeedback.getText().toString().trim();

            if (name.isEmpty() || mobile.isEmpty() || feedback.isEmpty()) {
                if (name.isEmpty()) etName.setError("Name cannot be empty");
                if (mobile.isEmpty()) etMobile.setError("Mobile number cannot be empty");
                if (feedback.isEmpty()) etFeedback.setError("Feedback cannot be empty");
            } else {
                saveFeedback(name, mobile, feedback);
            }
        });

        donation.setOnClickListener(v -> {
            Intent intent = new Intent(FeedbackActivity.this, DonationBox.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            finish(); // Optional: close current activity
        });
    }

    private void FetchUser() {
        FirebaseUser currentUser = mauth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            userRef.child(userId).child("Name").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String Name = snapshot.getValue(String.class);
                    if (Name != null && !Name.trim().isEmpty()) {
                        welcome.setText("Welcome, " + Name + "! Your feedback matters.");
                    } else {
                        welcome.setText("Welcome, Guest!");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getApplicationContext(), "Failed to load user name!", Toast.LENGTH_LONG).show();
                }
            });
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FILE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            fileUri = data.getData();
            Toast.makeText(this, "File selected: " + fileUri.getLastPathSegment(), Toast.LENGTH_SHORT).show();
        }
    }


    private void saveFeedback(String name, String mobile, String feedback) {
        if (fileUri != null) {
            // Upload file first
            StorageReference storageRef = FirebaseStorage.getInstance()
                    .getReference("FeedbackAttachments/" + System.currentTimeMillis());

            storageRef.putFile(fileUri)
                    .addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        saveFeedbackToDatabase(name, mobile, feedback, uri.toString());
                    }))
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "File upload failed!", Toast.LENGTH_SHORT).show();
                        saveFeedbackToDatabase(name, mobile, feedback, null); // fallback
                    });
        } else {
            // No file, save directly
            saveFeedbackToDatabase(name, mobile, feedback, null);
        }

        }

    private void saveFeedbackToDatabase(String name, String mobile, String feedback, String fileUrl) {
        String id = feedbackRef.push().getKey();

        HashMap<String, String> feedbackMap = new HashMap<>();
        feedbackMap.put("Name", name);
        feedbackMap.put("Mobile", mobile);
        feedbackMap.put("Feedback", feedback);
        if (fileUrl != null) feedbackMap.put("AttachmentURL", fileUrl);

        assert id != null;
        feedbackRef.child(id).setValue(feedbackMap).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(FeedbackActivity.this, "Feedback submitted", Toast.LENGTH_SHORT).show();
                etName.setText("");
                etMobile.setText("");
                etFeedback.setText("");
                fileUri = null;
            } else {
                Toast.makeText(FeedbackActivity.this, "Submission failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
