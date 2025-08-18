package com.example.logicandsolutions;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
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

public class ManageUsersActivity extends AppCompatActivity {

    private RecyclerView usersRecyclerView;
    private UserAdapter userAdapter;
    private List<User> userList;
    private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_users);

        initViews();
        setupFirebase();
        setupRecyclerView();
        loadUsers();
    }

    private void initViews() {
        usersRecyclerView = findViewById(R.id.usersRecyclerView);
    }

    private void setupFirebase() {
        usersRef = FirebaseDatabase.getInstance().getReference("Users");
    }

    private void setupRecyclerView() {
        userList = new ArrayList<>();
        userAdapter = new UserAdapter(userList);
        usersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        usersRecyclerView.setAdapter(userAdapter);
    }

    private void loadUsers() {
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    User user = userSnapshot.getValue(User.class);
                    if (user != null) {
                        user.setId(userSnapshot.getKey());
                        userList.add(user);
                    }
                }
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ManageUsersActivity.this, "Failed to load users", Toast.LENGTH_SHORT).show();
            }
        });
    }
}