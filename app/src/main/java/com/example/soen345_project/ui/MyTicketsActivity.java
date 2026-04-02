package com.example.soen345_project.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.soen345_project.R;
import com.example.soen345_project.api.ReservationController;
import com.example.soen345_project.data.FirebaseRepository;
import com.example.soen345_project.domain.models.Event;
import com.example.soen345_project.domain.models.Reservation;
import com.example.soen345_project.domain.services.NotifService;
import com.example.soen345_project.domain.services.ReservationService;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.widget.Toast;
import com.example.soen345_project.ui.adapters.TicketAdapter;

public class MyTicketsActivity extends AppCompatActivity {

    private RecyclerView rvMyTickets;
    private TicketAdapter ticketAdapter;
    private List<Reservation> reservationList = new ArrayList<>();
    private Map<String, Event> eventMap = new HashMap<>();
    private ReservationService reservationService;
    private FirebaseRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_tickets);

        repository = new FirebaseRepository();
        reservationService = new ReservationService(repository, new NotifService());

        rvMyTickets = findViewById(R.id.rvMyTickets);
        rvMyTickets.setLayoutManager(new LinearLayoutManager(this));

        ticketAdapter = new TicketAdapter(reservationList, (reservation, position) -> {
            cancelReservation(reservation, position);
        });

        rvMyTickets.setAdapter(ticketAdapter);
        
        setupBottomNavigation();
        loadData();
    }

    private void loadData() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        reservationService.listReservations(userId, new ReservationService.ReservationListCallback() {
            @Override
            public void onSuccess(List<Reservation> reservations) {
                runOnUiThread(() -> {
                    reservationList.clear();
                    reservationList.addAll(reservations);
                    ticketAdapter.notifyDataSetChanged();
                    fetchEventDetails(reservations);
                });
            }

            @Override
            public void onFailure(Exception e) {}
        });
    }

    private void fetchEventDetails(List<Reservation> reservations) {
        for (Reservation res : reservations) {
            if (res.getEventId() != null && !eventMap.containsKey(res.getEventId())) {
                repository.getEvent(res.getEventId(), new FirebaseRepository.GetEventCallback() {
                    @Override
                    public void onSuccess(Event event) {
                        runOnUiThread(() -> {
                            eventMap.put(event.getId(), event);
                            ticketAdapter.setEventMap(eventMap);
                        });
                    }

                    @Override
                    public void onFailure(Exception e) {}
                });
            }
        }
    }

    private void cancelReservation(Reservation reservation, int position) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        reservationService.cancelReservation(userId, reservation.getId(), new ReservationService.ReservationCallback() {
            @Override
            public void onSuccess(Reservation res) {
                runOnUiThread(() -> {
                    reservationList.remove(position);
                    ticketAdapter.notifyItemRemoved(position);
                    Toast.makeText(MyTicketsActivity.this, "Reservation cancelled", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onFailure(Exception e) {
                runOnUiThread(() -> Toast.makeText(MyTicketsActivity.this, "Failed to cancel: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        });
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        if (ticketAdapter != null) {
            ticketAdapter.notifyDataSetChanged();
        }
    }

    private void setupBottomNavigation() {
        com.google.android.material.bottomnavigation.BottomNavigationView bottomNavigation = findViewById(R.id.bottomNavigation);
        bottomNavigation.setSelectedItemId(R.id.nav_tickets);
        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                startActivity(new Intent(MyTicketsActivity.this, EventListActivity.class));
                return true;
            } else if (itemId == R.id.nav_tickets) {
                return true;
            }
            return false;
        });
    }
}
