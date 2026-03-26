package com.example.soen345_project.ui;

import androidx.appcompat.app.AppCompatActivity;
import com.example.soen345_project.R;
import com.example.soen345_project.api.AdminController;
import com.example.soen345_project.data.FirebaseRepository;
import com.example.soen345_project.domain.models.Event;
import com.example.soen345_project.domain.services.EventService;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class AddEditEventActivity extends AppCompatActivity {

    private AdminController adminController;
    private EditText etTitle, etLocation, etCategory, etDate, etSeats;
    private String adminId;
    private String eventId;
    private boolean isEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_event);

        adminId = getIntent().getStringExtra("adminId");
        eventId = getIntent().getStringExtra("eventId");
        isEdit  = getIntent().getBooleanExtra("isEdit", false);

        adminController = new AdminController(new EventService(new FirebaseRepository()));

        TextView tvTitle  = findViewById(R.id.tvAddEditTitle);
        etTitle    = findViewById(R.id.etEventTitle);
        etLocation = findViewById(R.id.etEventLocation);
        etCategory = findViewById(R.id.etEventCategory);
        etDate     = findViewById(R.id.etEventDate);
        etSeats    = findViewById(R.id.etEventSeats);
        Button btnSave   = findViewById(R.id.btnSaveEvent);
        Button btnCancel = findViewById(R.id.btnCancelAddEdit);

        etDate.setFocusable(false);
        etDate.setClickable(true);
        etDate.setOnClickListener(v -> showDatePicker());

        if (isEdit) {
            tvTitle.setText("Edit Event");
            loadEventData();
        }

        btnSave.setOnClickListener(v -> handleSave());
        btnCancel.setOnClickListener(v -> finish());
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year  = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day   = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String date = selectedYear + "-"
                            + String.format("%02d", selectedMonth + 1) + "-"
                            + String.format("%02d", selectedDay);
                    etDate.setText(date);
                }, year, month, day);

        datePickerDialog.show();
    }

    private void loadEventData() {
        new FirebaseRepository().getEvent(eventId, new FirebaseRepository.GetEventCallback() {
            @Override
            public void onSuccess(Event event) {
                etTitle.setText(event.getTitle());
                etLocation.setText(event.getLocation());
                etCategory.setText(event.getCategory());
                etDate.setText(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        .format(event.getDate()));
                etSeats.setText(String.valueOf(event.getTotalSeats()));
            }
            @Override
            public void onFailure(Exception e) {
                Toast.makeText(AddEditEventActivity.this, "Failed to load the event", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleSave() {
        String title    = etTitle.getText().toString().trim();
        String location = etLocation.getText().toString().trim();
        String category = etCategory.getText().toString().trim();
        String dateStr  = etDate.getText().toString().trim();
        String seatsStr = etSeats.getText().toString().trim();

        if (title.isEmpty() || location.isEmpty() || category.isEmpty()
                || dateStr.isEmpty() || seatsStr.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Date date;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(dateStr);
        } catch (ParseException e) {
            Toast.makeText(this, "Invalid date format. Use: yyyy-MM-dd", Toast.LENGTH_SHORT).show();
            return;
        }

        int seats = Integer.parseInt(seatsStr);
        Event event = new Event(title, date, location, category, seats);

        if (isEdit) {
            event.setId(eventId);
            adminController.editEvent(adminId, event, new EventService.EventCallback() {
                @Override
                public void onSuccess(Event e) {
                    Toast.makeText(AddEditEventActivity.this, "Event has been updated", Toast.LENGTH_SHORT).show();
                    finish();
                }
                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(AddEditEventActivity.this, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        else {
            adminController.addEvent(adminId, event, new EventService.EventCallback() {
                @Override
                public void onSuccess(Event e) {
                    Toast.makeText(AddEditEventActivity.this, "Event added", Toast.LENGTH_SHORT).show();
                    finish();
                }
                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(AddEditEventActivity.this, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}