package com.example.soen345_project.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.soen345_project.R;

public class ConfirmationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation);

        String eventTitle = getIntent().getStringExtra("EVENT_TITLE");
        String ticketId = getIntent().getStringExtra("TICKET_ID");

        TextView tvDetails = findViewById(R.id.tvConfirmationDetails);
        tvDetails.setText("You have successfully booked tickets for:\n\n" + eventTitle + "\n\nTicket ID: " + ticketId);

        Button btnGoToMyTickets = findViewById(R.id.btnGoToMyTickets);
        btnGoToMyTickets.setOnClickListener(v -> {
            Intent intent = new Intent(ConfirmationActivity.this, MyTicketsActivity.class);
            // Clear back stack and return to home as root, with MyTickets on top
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });
    }
}
