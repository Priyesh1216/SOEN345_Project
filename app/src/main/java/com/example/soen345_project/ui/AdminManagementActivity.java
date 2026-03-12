package com.example.soen345_project.ui;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.soen345_project.R;
import com.example.soen345_project.api.AdminController;
import com.example.soen345_project.data.FirebaseRepository;
import com.example.soen345_project.domain.services.EventService;

public class AdminManagementActivity extends AppCompatActivity {

    private AdminController adminController;
    private String adminId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_management);

        adminController = new AdminController(new EventService(new FirebaseRepository()));

        adminId = getIntent().getStringExtra("adminId");

        loadEvents();
        setupListeners();
    }

    private void loadEvents() {
        // load all events managed by this admin
        // populate view with events
    }

    private void setupListeners() {
        // add event button click: call adminController.addEvent(adminId, event)
        // edit event button click: call adminController.editEvent(adminId, event)
        // cancel event button click: call adminController.cancelEvent(adminId, eventId)
        // success: refresh list
        // failure: show error message
    }
}