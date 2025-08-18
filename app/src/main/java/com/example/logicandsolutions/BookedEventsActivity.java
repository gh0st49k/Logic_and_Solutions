package com.example.logicandsolutions;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class BookedEventsActivity extends AppCompatActivity {

    private RecyclerView bookedEventsRecyclerView;
    private EventAdapter eventAdapter;
    private List<Event> bookedEventsList;

    private FirebaseAuth mAuth;
    private DatabaseReference bookingsRef, eventsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booked_events);

        initViews();
        setupFirebase();
        setupRecyclerView();
        loadBookedEvents();
    }

    private void initViews() {
        bookedEventsRecyclerView = findViewById(R.id.bookedEventsRecyclerView);
    }

    private void setupFirebase() {
        mAuth = FirebaseAuth.getInstance();
        bookingsRef = FirebaseDatabase.getInstance().getReference("Bookings");
        eventsRef = FirebaseDatabase.getInstance().getReference("Events");
    }

    private void setupRecyclerView() {
        bookedEventsList = new ArrayList<>();
        eventAdapter = new EventAdapter(bookedEventsList, this::onEventClick);
        bookedEventsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        bookedEventsRecyclerView.setAdapter(eventAdapter);
    }

    private void loadBookedEvents() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            bookingsRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    bookedEventsList.clear();
                    for (DataSnapshot eventSnapshot : snapshot.getChildren()) {
                        if (eventSnapshot.hasChild(currentUser.getUid())) {
                            String eventId = eventSnapshot.getKey();
                            loadEventDetails(eventId);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(BookedEventsActivity.this, "Failed to load bookings", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void loadEventDetails(String eventId) {
        eventsRef.child(eventId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Event event = snapshot.getValue(Event.class);
                if (event != null) {
                    event.setId(eventId);
                    bookedEventsList.add(event);
                    eventAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }

    private void onEventClick(Event event) {
        Intent intent = new Intent(BookedEventsActivity.this, EventDetailsActivity.class);
        intent.putExtra("eventId", event.getId());
        startActivity(intent);
    }
}