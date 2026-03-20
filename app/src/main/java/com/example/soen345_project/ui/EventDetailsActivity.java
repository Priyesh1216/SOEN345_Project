package com.example.soen345_project.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.soen345_project.R;
import com.example.soen345_project.data.MockDataStore;
import com.example.soen345_project.domain.models.MockEvent;

public class EventDetailsActivity extends AppCompatActivity {

    private String eventId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

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

        TextView tvTitle = findViewById(R.id.tvDetailTitle);
        TextView tvDate = findViewById(R.id.tvDetailDate);
        TextView tvLocation = findViewById(R.id.tvDetailLocation);
        TextView tvDescription = findViewById(R.id.tvDetailDescription);
        Button btnBook = findViewById(R.id.btnBook);

        tvTitle.setText(event.getTitle());
        tvDate.setText("Date: " + event.getDate());
        tvLocation.setText("Location: " + event.getLocation());
        tvDescription.setText(event.getDescription());

        btnBook.setOnClickListener(v -> {
            Intent intent = new Intent(EventDetailsActivity.this, BookingActivity.class);
            intent.putExtra("EVENT_ID", eventId);
            startActivity(intent);
        });
    }
}