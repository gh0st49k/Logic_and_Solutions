package com.example.logicandsolutions;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AdminPanelActivity extends AppCompatActivity {

    private TextView totalEventsText, totalUsersText, totalBookingsText;
    private DatabaseReference eventsRef, usersRef, bookingsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_panel);

        initViews();
        setupFirebase();
        loadStatistics();

        findViewById(R.id.manageEventsButton).setOnClickListener(v -> {
            startActivity(new Intent(AdminPanelActivity.this, ManageEventsActivity.class));
        });

        findViewById(R.id.manageUsersButton).setOnClickListener(v -> {
            startActivity(new Intent(AdminPanelActivity.this, ManageUsersActivity.class));
        });

        findViewById(R.id.logoutButton).setOnClickListener(v -> {
            com.google.firebase.auth.FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(AdminPanelActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void initViews() {
        totalEventsText = findViewById(R.id.totalEventsText);
        totalUsersText = findViewById(R.id.totalUsersText);
        totalBookingsText = findViewById(R.id.totalBookingsText);
    }

    private void setupFirebase() {
        eventsRef = FirebaseDatabase.getInstance().getReference("Events");
        usersRef = FirebaseDatabase.getInstance().getReference("Users");
        bookingsRef = FirebaseDatabase.getInstance().getReference("Bookings");
    }

    private void loadStatistics() {
        // Load total events
        eventsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                totalEventsText.setText("Total Events: " + snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                totalEventsText.setText("Total Events: 0");
            }
        });

        // Load total users
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                totalUsersText.setText("Total Users: " + snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                totalUsersText.setText("Total Users: 0");
            }
        });

        // Load total bookings
        bookingsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int totalBookings = 0;
                for (DataSnapshot eventSnapshot : snapshot.getChildren()) {
                    totalBookings += eventSnapshot.getChildrenCount();
                }
                totalBookingsText.setText("Total Bookings: " + totalBookings);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                totalBookingsText.setText("Total Bookings: 0");
            }
        });
    }
}