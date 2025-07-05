package com.example.logicandsolutions;

import android.os.Bundle;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminDonationHistory extends AppCompatActivity {
    ListView donationListView;
    TextView total_donation;
    DatabaseReference donationRef;
    SimpleAdapter simpleAdapter;
    int  TotalAmount =  0;

    List<Map<String ,String>> donationList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_donation_history);
        donationListView = findViewById(R.id.donationListView);

         total_donation = findViewById(R.id.totalDonationAmount);
         donationRef = FirebaseDatabase.getInstance().getReference("Donations" );
        simpleAdapter = new SimpleAdapter(
                this,
                donationList,
                R.layout.donation_list,
                new String[]{"Name", "Amount", "Date"},
                new int[]{R.id.nameText, R.id.amountText, R.id.dateText}

        );

        donationListView.setAdapter(simpleAdapter);
        FetchDonations();








    }
    private void FetchDonations() {
        donationRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                donationList.clear();
                TotalAmount = 0;

                for (DataSnapshot userDonations : snapshot.getChildren()) {
                    for (DataSnapshot donationSnapshot : userDonations.getChildren()) {
                        String name = donationSnapshot.child("Name").getValue(String.class);
                        String amountStr = String.valueOf(donationSnapshot.child("Amount").getValue());
                        String date = donationSnapshot.child("Date").getValue(String.class);

                        if (amountStr != null) {
                            try {
                                int amount = Integer.parseInt(amountStr);
                                TotalAmount += amount;

                                // Handle null name or date
                                String nameStr = (name != null) ? "Name: " + name : "Name: Anonymous";
                                String dateStr = (date != null) ? "Date: " + date : "Date: Unknown";

                                Map<String, String> donationMap = new HashMap<>();
                                donationMap.put("Name", nameStr);
                                donationMap.put("Amount", "Amount: ₹" + amount);
                                donationMap.put("Date", dateStr);

                                donationList.add(donationMap);
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }

                total_donation.setText("Total Donation Amount: ₹" + TotalAmount);
                simpleAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Failed to load data", Toast.LENGTH_LONG).show();
            }
        });
    }}
