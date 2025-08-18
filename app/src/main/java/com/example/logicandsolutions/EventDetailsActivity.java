package com.example.logicandsolutions;

import android.os.Bundle;
import android.widget.Button;
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

public class EventDetailsActivity extends AppCompatActivity {

    private TextView titleText, descriptionText, dateTimeText, locationText, categoryText;
    private TextView priceText, attendeesText, organizerText;
    private Button bookEventButton;

    private String eventId;
    private Event currentEvent;
    private FirebaseAuth mAuth;
    private DatabaseReference eventsRef, bookingsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        initViews();
        setupFirebase();

        eventId = getIntent().getStringExtra("eventId");
        if (eventId != null) {
            loadEventDetails();
        } else {
            Toast.makeText(this, "Event not found", Toast.LENGTH_SHORT).show();
            finish();
        }

        bookEventButton.setOnClickListener(v -> bookEvent());
    }

    private void initViews() {
        titleText = findViewById(R.id.eventTitleText);
        descriptionText = findViewById(R.id.eventDescriptionText);
        dateTimeText = findViewById(R.id.eventDateTimeText);
        locationText = findViewById(R.id.eventLocationText);
        categoryText = findViewById(R.id.eventCategoryText);
        priceText = findViewById(R.id.eventPriceText);
        attendeesText = findViewById(R.id.eventAttendeesText);
        organizerText = findViewById(R.id.eventOrganizerText);
        bookEventButton = findViewById(R.id.bookEventButton);
    }

    private void setupFirebase() {
        mAuth = FirebaseAuth.getInstance();
        eventsRef = FirebaseDatabase.getInstance().getReference("Events");
        bookingsRef = FirebaseDatabase.getInstance().getReference("Bookings");
    }

    private void loadEventDetails() {
        eventsRef.child(eventId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                currentEvent = snapshot.getValue(Event.class);
                if (currentEvent != null) {
                    currentEvent.setId(eventId);
                    displayEventDetails();
                    checkBookingStatus();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(EventDetailsActivity.this, "Failed to load event details", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayEventDetails() {
        titleText.setText(currentEvent.getTitle());
        descriptionText.setText(currentEvent.getDescription());
        dateTimeText.setText(currentEvent.getDate() + " at " + currentEvent.getTime());
        locationText.setText(currentEvent.getLocation());
        categoryText.setText(currentEvent.getCategory());
        priceText.setText(currentEvent.getPrice() == 0 ? "Free" : "₹" + currentEvent.getPrice());
        attendeesText.setText(currentEvent.getCurrentAttendees() + "/" + currentEvent.getMaxAttendees() + " attendees");
        organizerText.setText("Organized by: " + currentEvent.getOrganizerName());

        // Check if event is full
        if (currentEvent.getCurrentAttendees() >= currentEvent.getMaxAttendees()) {
            bookEventButton.setText("Event Full");
            bookEventButton.setEnabled(false);
        }
    }

    private void checkBookingStatus() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            bookingsRef.child(eventId).child(currentUser.getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                bookEventButton.setText("Already Booked");
                                bookEventButton.setEnabled(false);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            // Handle error
                        }
                    });
        }
    }

    private void bookEvent() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Please login to book events", Toast.LENGTH_SHORT).show();
            return;
        }

        if (currentEvent.getCurrentAttendees() >= currentEvent.getMaxAttendees()) {
            Toast.makeText(this, "Event is full", Toast.LENGTH_SHORT).show();
            return;
        }

        bookEventButton.setEnabled(false);
        bookEventButton.setText("Booking...");

        // Create booking record
        HashMap<String, Object> booking = new HashMap<>();
        booking.put("userId", currentUser.getUid());
        booking.put("eventId", eventId);
        booking.put("eventTitle", currentEvent.getTitle());
        booking.put("bookingTime", System.currentTimeMillis());

        bookingsRef.child(eventId).child(currentUser.getUid()).setValue(booking)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Update attendee count
                        eventsRef.child(eventId).child("currentAttendees")
                                .setValue(currentEvent.getCurrentAttendees() + 1)
                                .addOnCompleteListener(updateTask -> {
                                    bookEventButton.setEnabled(true);
                                    if (updateTask.isSuccessful()) {
                                        bookEventButton.setText("Booked Successfully");
                                        Toast.makeText(EventDetailsActivity.this, "Event booked successfully!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        bookEventButton.setText("Book Event");
                                        Toast.makeText(EventDetailsActivity.this, "Booking failed", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        bookEventButton.setEnabled(true);
                        bookEventButton.setText("Book Event");
                        Toast.makeText(EventDetailsActivity.this, "Booking failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}