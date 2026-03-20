package com.example.soen345_project.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.soen345_project.R;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        setupBottomNavigation();

        Button btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void setupBottomNavigation() {
        findViewById(R.id.navHome).setOnClickListener(v -> {
            startActivity(new Intent(ProfileActivity.this, HomeActivity.class).setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
        });
        findViewById(R.id.navTickets).setOnClickListener(v -> {
            startActivity(new Intent(ProfileActivity.this, MyTicketsActivity.class).setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
        });
        findViewById(R.id.navProfile).setOnClickListener(v -> {
            // Already here
        });
    }
}
