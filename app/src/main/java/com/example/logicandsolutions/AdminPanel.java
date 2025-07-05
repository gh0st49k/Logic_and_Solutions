package com.example.logicandsolutions;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AdminPanel extends AppCompatActivity {

     ImageView alluser,admindonation,alladminmessages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_panel);
        alluser = findViewById(R.id.alluser);
        alluser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminPanel.this, UserList.class);
                startActivity(intent);
            }
        });
        admindonation = findViewById(R.id.adminddonation);
        alladminmessages = findViewById(R.id.adminallmessages);
        alladminmessages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminPanel.this, AdminMessages.class);
                startActivity(intent);
            }
        });

        admindonation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminPanel.this, AdminDonationHistory.class);
                startActivity(intent);
            }
        });

    }
}