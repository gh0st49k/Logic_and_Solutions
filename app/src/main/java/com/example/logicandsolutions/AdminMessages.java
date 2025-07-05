package com.example.logicandsolutions;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminMessages extends AppCompatActivity {
    ListView messageListView;
    List<Map<String, String>> msgList = new ArrayList<>();
    DatabaseReference userRef;
    SimpleAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_messages);

        // Link the ListView
        messageListView = findViewById(R.id.messageListView);

        // Firebase reference
        userRef = FirebaseDatabase.getInstance().getReference("ContactData");

        // Setup SimpleAdapter
        adapter = new SimpleAdapter(
                this,
                msgList,
                R.layout.msg_list,
                new String[]{"Name", "Message"},
                new int[]{R.id.nameText, R.id.msgText}
        );

        messageListView.setAdapter(adapter);

        // Fetch data from Firebase
        fetchUsers();
    }

    private void fetchUsers() {
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                msgList.clear();
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    String name = userSnapshot.child("Name").getValue(String.class);
                    String message = userSnapshot.child("Message").getValue(String.class);

                    Log.d("DataCheck", "Fetched: Name=" + name + ", Message=" + message);

                    if (name != null && message != null) {
                        Map<String, String> userMap = new HashMap<>();
                        userMap.put("Name", "Name: " + name);
                        userMap.put("Message", "Message: " + message);
                        msgList.add(userMap);
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
