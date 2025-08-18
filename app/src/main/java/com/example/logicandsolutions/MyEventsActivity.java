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

public class MyEventsActivity extends AppCompatActivity {

    private RecyclerView myEventsRecyclerView;
    private EventAdapter eventAdapter;
    private List<Event> myEventsList;

    private FirebaseAuth mAuth;
    private DatabaseReference eventsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_events);

        initViews();
        setupFirebase();
        setupRecyclerView();
        loadMyEvents();
    }

    private void initViews() {
        myEventsRecyclerView = findViewById(R.id.myEventsRecyclerView);
    }

    private void setupFirebase() {
        mAuth = FirebaseAuth.getInstance();
        eventsRef = FirebaseDatabase.getInstance().getReference("Events");
    }

    private void setupRecyclerView() {
        myEventsList = new ArrayList<>();
        eventAdapter = new EventAdapter(myEventsList, this::onEventClick);
        myEventsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        myEventsRecyclerView.setAdapter(eventAdapter);
    }

    private void loadMyEvents() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            eventsRef.orderByChild("organizerId").equalTo(currentUser.getUid())
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            myEventsList.clear();
                            for (DataSnapshot eventSnapshot : snapshot.getChildren()) {
                                Event event = eventSnapshot.getValue(Event.class);
                                if (event != null) {
                                    event.setId(eventSnapshot.getKey());
                                    myEventsList.add(event);
                                }
                            }
                            eventAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(MyEventsActivity.this, "Failed to load events", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void onEventClick(Event event) {
        Intent intent = new Intent(MyEventsActivity.this, EventDetailsActivity.class);
        intent.putExtra("eventId", event.getId());
        startActivity(intent);
    }
}