package com.example.soen345_project.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.soen345_project.R;
import com.example.soen345_project.data.MockDataStore;
import com.example.soen345_project.domain.models.MockEvent;
import com.example.soen345_project.domain.models.MockTicket;

public class BookingActivity extends AppCompatActivity {

    private String eventId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        eventId = getIntent().getStringExtra("EVENT_ID");
        if (eventId == null) {
            Toast.makeText(this, "Error: Event not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        MockEvent event = MockDataStore.getInstance().getEventById(eventId);
        if (event == null) {
            Toast.makeText(this, "Error: Event not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        TextView tvTitle = findViewById(R.id.tvBookingEventTitle);
        tvTitle.setText(event.getTitle());

        EditText etQuantity = findViewById(R.id.etTicketQuantity);
        Button btnConfirm = findViewById(R.id.btnConfirmBooking);

        btnConfirm.setOnClickListener(v -> {
            String qtyStr = etQuantity.getText().toString();
            if (qtyStr.isEmpty()) {
                Toast.makeText(BookingActivity.this, "Please enter a quantity", Toast.LENGTH_SHORT).show();
                return;
            }
            int qty = Integer.parseInt(qtyStr);
            if (qty <= 0) {
                Toast.makeText(BookingActivity.this, "Quantity must be at least 1", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create ticket
            MockTicket ticket = new MockTicket(event, qty);
            MockDataStore.getInstance().addTicket(ticket);

            Intent intent = new Intent(BookingActivity.this, ConfirmationActivity.class);
            intent.putExtra("TICKET_ID", ticket.getTicketId());
            intent.putExtra("EVENT_TITLE", event.getTitle());
            startActivity(intent);
            finish();
        });
    }
}
