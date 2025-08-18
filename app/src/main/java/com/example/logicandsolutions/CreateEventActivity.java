package com.example.logicandsolutions;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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

import java.util.Calendar;

public class CreateEventActivity extends AppCompatActivity {

    private EditText titleEditText, descriptionEditText, locationEditText, priceEditText, maxAttendeesEditText;
    private EditText dateEditText, timeEditText;
    private Spinner categorySpinner;
    private Button createEventButton;

    private FirebaseAuth mAuth;
    private DatabaseReference eventsRef, userRef;
    private String organizerName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        initViews();
        setupFirebase();
        setupSpinner();
        setupDateTimePickers();
        loadOrganizerName();

        createEventButton.setOnClickListener(v -> createEvent());
    }

    private void initViews() {
        titleEditText = findViewById(R.id.titleEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        locationEditText = findViewById(R.id.locationEditText);
        priceEditText = findViewById(R.id.priceEditText);
        maxAttendeesEditText = findViewById(R.id.maxAttendeesEditText);
        dateEditText = findViewById(R.id.dateEditText);
        timeEditText = findViewById(R.id.timeEditText);
        categorySpinner = findViewById(R.id.categorySpinner);
        createEventButton = findViewById(R.id.createEventButton);
    }

    private void setupFirebase() {
        mAuth = FirebaseAuth.getInstance();
        eventsRef = FirebaseDatabase.getInstance().getReference("Events");
        
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            userRef = FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid());
        }
    }

    private void setupSpinner() {
        String[] categories = {"Conference", "Workshop", "Seminar", "Concert", "Sports", "Exhibition", "Networking", "Other"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categories);
        categorySpinner.setAdapter(adapter);
    }

    private void setupDateTimePickers() {
        dateEditText.setOnClickListener(v -> showDatePicker());
        timeEditText.setOnClickListener(v -> showTimePicker());
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
            this,
            (view, year, month, dayOfMonth) -> {
                String date = dayOfMonth + "/" + (month + 1) + "/" + year;
                dateEditText.setText(date);
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void showTimePicker() {
        Calendar calendar = Calendar.getInstance();
        TimePickerDialog timePickerDialog = new TimePickerDialog(
            this,
            (view, hourOfDay, minute) -> {
                String time = String.format("%02d:%02d", hourOfDay, minute);
                timeEditText.setText(time);
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        );
        timePickerDialog.show();
    }

    private void loadOrganizerName() {
        if (userRef != null) {
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    organizerName = snapshot.child("name").getValue(String.class);
                    if (organizerName == null) organizerName = "Unknown";
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    organizerName = "Unknown";
                }
            });
        }
    }

    private void createEvent() {
        String title = titleEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();
        String location = locationEditText.getText().toString().trim();
        String priceStr = priceEditText.getText().toString().trim();
        String maxAttendeesStr = maxAttendeesEditText.getText().toString().trim();
        String date = dateEditText.getText().toString().trim();
        String time = timeEditText.getText().toString().trim();
        String category = categorySpinner.getSelectedItem().toString();

        if (title.isEmpty() || description.isEmpty() || location.isEmpty() || 
            date.isEmpty() || time.isEmpty() || maxAttendeesStr.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double price = 0;
        if (!priceStr.isEmpty()) {
            try {
                price = Double.parseDouble(priceStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid price format", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        int maxAttendees;
        try {
            maxAttendees = Integer.parseInt(maxAttendeesStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid max attendees format", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        createEventButton.setEnabled(false);
        createEventButton.setText("Creating...");

        Event event = new Event(title, description, date, time, location, category, 
                               price, maxAttendees, currentUser.getUid(), organizerName);

        String eventId = eventsRef.push().getKey();
        if (eventId != null) {
            eventsRef.child(eventId).setValue(event)
                    .addOnCompleteListener(task -> {
                        createEventButton.setEnabled(true);
                        createEventButton.setText("Create Event");
                        
                        if (task.isSuccessful()) {
                            Toast.makeText(CreateEventActivity.this, "Event created successfully!", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(CreateEventActivity.this, "Failed to create event", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}