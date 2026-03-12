package com.example.soen345_project.ui;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.soen345_project.R;
import com.example.soen345_project.api.ReservationController;
import com.example.soen345_project.data.FirebaseRepository;
import com.example.soen345_project.domain.services.NotifService;
import com.example.soen345_project.domain.services.ReservationService;

public class ViewReservationsActivity extends AppCompatActivity {

    private ReservationController reservationController;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_reservations);

        reservationController = new ReservationController(
                new ReservationService(new FirebaseRepository(), new NotifService()));

        userId = getIntent().getStringExtra("userId");

        loadReservations();
        setupListeners();
    }

    private void loadReservations() {
        // call reservationController.listReservations(userId)
        // success: populate RecyclerView with reservations
        // failure: show error message
    }

    private void setupListeners() {
        // cancel button on each reservation item
        // call reservationController.cancelReservation(userId, reservationId)
        // success: refresh list
        // failure: show error message
    }
}