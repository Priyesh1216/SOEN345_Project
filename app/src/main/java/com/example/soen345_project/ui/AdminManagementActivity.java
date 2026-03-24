package com.example.soen345_project.ui;


import com.example.soen345_project.R;
import com.example.soen345_project.api.AdminController;
import com.example.soen345_project.data.FirebaseRepository;
import com.example.soen345_project.domain.models.Event;
import com.example.soen345_project.domain.services.EventService;
import com.example.soen345_project.ui.adapters.AdminEventAdapter;
import java.util.ArrayList;
import java.util.List;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class AdminManagementActivity extends AppCompatActivity {

    private AdminController adminController;
    private AdminEventAdapter adapter;
    private final List<Event> eventList = new ArrayList<>();
    private String adminId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_management);

        adminId = getIntent().getStringExtra("adminId");
        adminController = new AdminController(new EventService(new FirebaseRepository()));

        RecyclerView rvAdminEvents = findViewById(R.id.rvAdminEvents);
        rvAdminEvents.setLayoutManager(new LinearLayoutManager(this));

        adapter = new AdminEventAdapter(eventList, new AdminEventAdapter.AdminEventListener() {
            @Override
            public void onEdit(Event event) {
                Intent intent = new Intent(AdminManagementActivity.this, AddEditEventActivity.class);
                intent.putExtra("adminId", adminId);
                intent.putExtra("eventId", event.getId());
                intent.putExtra("isEdit", true);
                startActivity(intent);
            }
            @Override
            public void onCancel(Event event) {
                new AlertDialog.Builder(AdminManagementActivity.this)
                        .setTitle("Cancel Event")
                        .setMessage("Are you sure you want to cancel " + event.getTitle() + "?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            adminController.cancelEvent(adminId, event.getId(), new EventService.EventCallback() {
                                @Override
                                public void onSuccess(Event e) {
                                    Toast.makeText(AdminManagementActivity.this, "Event cancelled", Toast.LENGTH_SHORT).show();
                                    loadEvents();
                                }
                                @Override
                                public void onFailure(Exception e) {
                                    Toast.makeText(AdminManagementActivity.this, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });

        rvAdminEvents.setAdapter(adapter);

        Button btnAddEvent = findViewById(R.id.btnAddEvent);
        btnAddEvent.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddEditEventActivity.class);
            intent.putExtra("adminId", adminId);
            intent.putExtra("isEdit", false);
            startActivity(intent);
        });

        loadEvents();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadEvents();
    }

    private void loadEvents() {
        adminController.listEvents(new EventService.EventListCallback() {
            @Override
            public void onSuccess(List<Event> events) {
                eventList.clear();
                eventList.addAll(events);
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onFailure(Exception e) {
                Toast.makeText(AdminManagementActivity.this, "Failed to load events: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}