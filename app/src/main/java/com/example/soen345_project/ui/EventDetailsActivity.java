package com.example.soen345_project.ui;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.soen345_project.R;
import com.example.soen345_project.api.ReservationController;
import com.example.soen345_project.data.FirebaseRepository;
import com.example.soen345_project.domain.services.NotifService;
import com.example.soen345_project.domain.services.ReservationService;

public class EventDetailsActivity extends AppCompatActivity {

    private ReservationController reservationController;
    private String eventId;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        reservationController = new ReservationController(
                new ReservationService(new FirebaseRepository(), new NotifService()));

        eventId = getIntent().getStringExtra("eventId");
        userId = getIntent().getStringExtra("userId");

        setupListeners();
    }

    private void setupListeners() {
        // display event details (name, date, location, category, available seats)
        // reserve button click: calls reservationController.reserveTickets(userId, eventId, quantity)
        // success: show confirmation message
        // failure: show error message
    }
}