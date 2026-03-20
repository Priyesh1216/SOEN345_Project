package com.example.soen345_project.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.soen345_project.R;
import com.example.soen345_project.data.MockDataStore;
import com.example.soen345_project.domain.models.MockTicket;
import com.example.soen345_project.ui.adapters.TicketAdapter;

import java.util.List;

public class MyTicketsActivity extends AppCompatActivity {

    private RecyclerView rvMyTickets;
    private TicketAdapter ticketAdapter;
    private List<MockTicket> myTickets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_tickets);

        setupBottomNavigation();

        rvMyTickets = findViewById(R.id.rvMyTickets);
        rvMyTickets.setLayoutManager(new LinearLayoutManager(this));

        myTickets = MockDataStore.getInstance().getMyTickets();

        ticketAdapter = new TicketAdapter(myTickets, (ticket, position) -> {
            MockDataStore.getInstance().removeTicket(ticket);
            ticketAdapter.notifyItemRemoved(position);
            ticketAdapter.notifyItemRangeChanged(position, myTickets.size());
        });

        rvMyTickets.setAdapter(ticketAdapter);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        if (ticketAdapter != null) {
            ticketAdapter.notifyDataSetChanged();
        }
    }

    private void setupBottomNavigation() {
        findViewById(R.id.navHome).setOnClickListener(v -> {
            startActivity(new Intent(MyTicketsActivity.this, HomeActivity.class).setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
        });
        findViewById(R.id.navTickets).setOnClickListener(v -> {
            // Already here
        });
        findViewById(R.id.navProfile).setOnClickListener(v -> {
            startActivity(new Intent(MyTicketsActivity.this, ProfileActivity.class).setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT));
        });
    }
}
