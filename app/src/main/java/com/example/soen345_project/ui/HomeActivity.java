package com.example.soen345_project.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.soen345_project.R;
import com.example.soen345_project.data.MockDataStore;
import com.example.soen345_project.domain.models.MockEvent;
import com.example.soen345_project.ui.adapters.EventAdapter;

public class HomeActivity extends AppCompatActivity {

    private RecyclerView rvEvents;
    private EventAdapter eventAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        setupBottomNavigation();

        rvEvents = findViewById(R.id.rvEvents);
        rvEvents.setLayoutManager(new LinearLayoutManager(this));

        eventAdapter = new EventAdapter(MockDataStore.getInstance().getEvents(), event -> {
            Intent intent = new Intent(HomeActivity.this, EventDetailsActivity.class);
            intent.putExtra("EVENT_ID", event.getId());
            startActivity(intent);
        });

        rvEvents.setAdapter(eventAdapter);
    }
    
    private void setupBottomNavigation() {
        findViewById(R.id.navHome).setOnClickListener(v -> {
            // Already here
        });
        findViewById(R.id.navTickets).setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, MyTicketsActivity.class).setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
        });
        findViewById(R.id.navProfile).setOnClickListener(v -> {
            startActivity(new Intent(HomeActivity.this, ProfileActivity.class).setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
        });
    }
}
