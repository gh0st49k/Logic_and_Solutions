package com.example.logicandsolutions;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ManageEventsActivity extends AppCompatActivity {

    private RecyclerView eventsRecyclerView;
    private AdminEventAdapter eventAdapter;
    private List<Event> eventList;
    private DatabaseReference eventsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_events);

        initViews();
        setupFirebase();
        setupRecyclerView();
        loadEvents();
    }

    private void initViews() {
        eventsRecyclerView = findViewById(R.id.eventsRecyclerView);
    }

    private void setupFirebase() {
        eventsRef = FirebaseDatabase.getInstance().getReference("Events");
    }

    private void setupRecyclerView() {
        eventList = new ArrayList<>();
        eventAdapter = new AdminEventAdapter(eventList, new AdminEventAdapter.OnEventActionListener() {
            @Override
            public void onEventClick(Event event) {
                Intent intent = new Intent(ManageEventsActivity.this, EventDetailsActivity.class);
                intent.putExtra("eventId", event.getId());
                startActivity(intent);
            }

            @Override
            public void onDeleteEvent(Event event) {
                showDeleteConfirmation(event);
            }
        });
        eventsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        eventsRecyclerView.setAdapter(eventAdapter);
    }

    private void loadEvents() {
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
                Toast.makeText(ManageEventsActivity.this, "Failed to load events", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDeleteConfirmation(Event event) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Event")
                .setMessage("Are you sure you want to delete this event?")
                .setPositiveButton("Delete", (dialog, which) -> deleteEvent(event))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteEvent(Event event) {
        eventsRef.child(event.getId()).removeValue()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(ManageEventsActivity.this, "Event deleted successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ManageEventsActivity.this, "Failed to delete event", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}