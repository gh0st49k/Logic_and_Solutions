package com.example.logicandsolutions;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView welcomeText;
    private RecyclerView eventsRecyclerView;
    private FloatingActionButton createEventFab;
    private EventAdapter eventAdapter;
    private List<Event> eventList;
    
    private FirebaseAuth mAuth;
    private DatabaseReference userRef, eventsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        setupFirebase();
        setupRecyclerView();
        loadUserData();
        loadEvents();

        createEventFab.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, CreateEventActivity.class));
        });

        findViewById(R.id.profileButton).setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, ProfileActivity.class));
        });

        findViewById(R.id.myEventsButton).setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, MyEventsActivity.class));
        });

        findViewById(R.id.bookedEventsButton).setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, BookedEventsActivity.class));
        });

        findViewById(R.id.logoutButton).setOnClickListener(v -> {
            mAuth.signOut();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void initViews() {
        welcomeText = findViewById(R.id.welcomeText);
        eventsRecyclerView = findViewById(R.id.eventsRecyclerView);
        createEventFab = findViewById(R.id.createEventFab);
    }

    private void setupFirebase() {
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            userRef = FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid());
            eventsRef = FirebaseDatabase.getInstance().getReference("Events");
        }
    }

    private void setupRecyclerView() {
        eventList = new ArrayList<>();
        eventAdapter = new EventAdapter(eventList, this::onEventClick);
        eventsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        eventsRecyclerView.setAdapter(eventAdapter);
    }

    private void loadUserData() {
        if (userRef != null) {
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String name = snapshot.child("name").getValue(String.class);
                    if (name != null) {
                        welcomeText.setText("Welcome, " + name + "!");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(MainActivity.this, "Failed to load user data", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void loadEvents() {
        if (eventsRef != null) {
            eventsRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    eventList.clear();
                    for (DataSnapshot eventSnapshot : snapshot.getChildren()) {
                        Event event = eventSnapshot.getValue(Event.class);
                        if (event != null) {
                            event.setId(eventSnapshot.getKey());
                            eventList.add(event);
                        }
                    }
                    eventAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(MainActivity.this, "Failed to load events", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void onEventClick(Event event) {
        Intent intent = new Intent(MainActivity.this, EventDetailsActivity.class);
        intent.putExtra("eventId", event.getId());
        startActivity(intent);
    }
}