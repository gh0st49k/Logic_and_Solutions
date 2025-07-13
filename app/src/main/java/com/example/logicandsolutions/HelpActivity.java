package com.example.logicandsolutions;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class HelpActivity extends AppCompatActivity {

    EditText etName, etMobile, etDescription;
    Spinner helpTypeSpinner;
    Button btnSubmit;

    // Footer nav
    ImageView homeimg, donation, developer, profile, logout;

    DatabaseReference helpRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        // Initialize views
        etName = findViewById(R.id.etHelpName);
        etMobile = findViewById(R.id.etHelpMobile);
        etDescription = findViewById(R.id.etHelpDescription);
        helpTypeSpinner = findViewById(R.id.helpTypeSpinner);
        btnSubmit = findViewById(R.id.btnHelpSubmit);

        // Set up spinner data
        String[] helpTypes = {"Select Help Type", "Medical", "Educational", "Food & Essentials", "Legal", "Others"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, helpTypes);
        helpTypeSpinner.setAdapter(adapter);

        // Firebase database reference
        helpRef = FirebaseDatabase.getInstance().getReference("HelpRequests");

        // Submit button logic
        btnSubmit.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String mobile = etMobile.getText().toString().trim();
            String type = helpTypeSpinner.getSelectedItem().toString();
            String description = etDescription.getText().toString().trim();

            if (name.isEmpty()) {
                etName.setError("Name required");
                return;
            }
            if (mobile.isEmpty()) {
                etMobile.setError("Mobile required");
                return;
            }
            if (type.equals("Select Help Type")) {
                Toast.makeText(this, "Please select a valid help type", Toast.LENGTH_SHORT).show();
                return;
            }
            if (description.isEmpty()) {
                etDescription.setError("Description required");
                return;
            }

            // Push to Firebase
            String id = helpRef.push().getKey();
            HashMap<String, String> helpData = new HashMap<>();
            helpData.put("Name", name);
            helpData.put("Mobile", mobile);
            helpData.put("HelpType", type);
            helpData.put("Description", description);

            assert id != null;
            helpRef.child(id).setValue(helpData)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Help request submitted", Toast.LENGTH_SHORT).show();
                            etName.setText("");
                            etMobile.setText("");
                            etDescription.setText("");
                            helpTypeSpinner.setSelection(0);
                        } else {
                            Toast.makeText(this, "Submission failed", Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        // 🔻 Footer nav setup
        homeimg = findViewById(R.id.homeimg);
        donation = findViewById(R.id.donationbtnnav);
        developer = findViewById(R.id.developer);
        profile = findViewById(R.id.profile);
        logout = findViewById(R.id.logout);

        homeimg.setOnClickListener(v -> navigateTo(Homeactivity.class));
        donation.setOnClickListener(v -> navigateTo(DonationBox.class));
        developer.setOnClickListener(v -> navigateTo(DeveloperInfo.class));
        profile.setOnClickListener(v -> navigateTo(Profileactivity.class));
        logout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            navigateTo(Loginactivity.class);
        });
    }

    private void navigateTo(Class<?> destination) {
        Intent intent = new Intent(HelpActivity.this, destination);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        finish();
    }
}
