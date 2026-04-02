package com.example.soen345_project.ui;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.soen345_project.R;
import com.example.soen345_project.data.FirebaseRepository;
import com.example.soen345_project.domain.models.Reservation;
import com.example.soen345_project.domain.services.NotifService;
import com.example.soen345_project.domain.services.ReservationService;
import com.example.soen345_project.ui.adapters.ReservationAdapter;
import java.util.ArrayList;
import java.util.List;

public class ViewReservationsActivity extends AppCompatActivity {

    private ReservationService reservationService;
    private String eventId;
    private RecyclerView rvReservations;
    private ReservationAdapter adapter;
    private List<Reservation> reservationList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_reservations);

        reservationService = new ReservationService(new FirebaseRepository(), new NotifService());
        eventId = getIntent().getStringExtra("eventId");

        rvReservations = findViewById(R.id.rvReservations);
        rvReservations.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ReservationAdapter(reservationList);
        rvReservations.setAdapter(adapter);

        findViewById(R.id.btnBackReservations).setOnClickListener(v -> finish());

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Reservations");
        }

        loadReservations();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void loadReservations() {
        if (eventId == null) return;
        reservationService.getReservationsByEvent(eventId, new ReservationService.ReservationListCallback() {
            @Override
            public void onSuccess(List<Reservation> reservations) {
                runOnUiThread(() -> {
                    reservationList.clear();
                    reservationList.addAll(reservations);
                    adapter.notifyDataSetChanged();
                    fetchUserNames(reservations);
                });
            }

            @Override
            public void onFailure(Exception e) {
                // Handle failure
            }
        });
    }

    private void fetchUserNames(List<Reservation> reservations) {
        java.util.Map<String, String> namesMap = new java.util.HashMap<>();
        FirebaseRepository repo = new FirebaseRepository();
        for (Reservation res : reservations) {
            final String uid = res.getUserId();
            if (uid != null && !namesMap.containsKey(uid)) {
                repo.getUser(uid, new FirebaseRepository.GetUserCallback() {
                    @Override
                    public void onSuccess(com.example.soen345_project.domain.models.User user) {
                        runOnUiThread(() -> {
                            String name = user.getName() != null ? user.getName() : user.getEmail();
                            namesMap.put(uid, name);
                            adapter.updateNames(namesMap);
                        });
                    }

                    @Override
                    public void onFailure(Exception e) {}
                });
            }
        }
    }
}