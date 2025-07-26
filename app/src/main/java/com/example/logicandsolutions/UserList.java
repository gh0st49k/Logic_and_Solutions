package com.example.logicandsolutions;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.SimpleAdapter;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserList extends AppCompatActivity {
    ListView userListView;
    List<Map<String, String>> userList = new ArrayList<>();
    DatabaseReference userRef;
    SimpleAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);

        userListView = findViewById(R.id.userListView);
        userRef = FirebaseDatabase.getInstance().getReference("Users");

        adapter = new SimpleAdapter(
                this,
                userList,
                R.layout.item_list,
                new String[]{"Name", "Phone", "Email"}, // Using capitalized keys
                new int[]{R.id.nameText, R.id.phoneText, R.id.emailText}
        );

        userListView.setAdapter(adapter);
        fetchUsers();
    }

    private void fetchUsers() {
        // --- THIS IS THE DEBUGGING CODE ---
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            Log.d("UID_CHECK", "Requesting data as user: " + currentUser.getUid());
        } else {
            Log.e("UID_CHECK", "Requesting data but NO USER is logged in!");
        }
        // --- END OF DEBUGGING CODE ---

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    String name = userSnapshot.child("Name").getValue(String.class);
                    String phone = userSnapshot.child("Phone").getValue(String.class);
                    String email = userSnapshot.child("Email").getValue(String.class);

                    if (name != null && phone != null && email != null) {
                        Map<String, String> userMap = new HashMap<>();
                        // Using capitalized keys to match adapter
                        userMap.put("Name", "Name: " + name);
                        userMap.put("Phone", "Phone: " + phone);
                        userMap.put("Email", "Email: " + email);
                        userList.add(userMap);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Failed to load data!", Toast.LENGTH_LONG).show();
                Log.e("FirebaseError", error.getMessage());
            }
        });
    }
}