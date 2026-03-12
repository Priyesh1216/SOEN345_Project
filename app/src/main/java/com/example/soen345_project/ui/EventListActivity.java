package com.example.soen345_project.ui;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.soen345_project.R;
import com.example.soen345_project.api.EventController;
import com.example.soen345_project.data.FirebaseRepository;
import com.example.soen345_project.domain.services.EventService;

public class EventListActivity extends AppCompatActivity {

    private EventController eventController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);

        eventController = new EventController(new EventService(new FirebaseRepository()));

        loadEvents();
        setupListeners();
    }

    private void loadEvents() {
        // call eventController.listEvents()
        // success: populate RecyclerView with events
        // failure: show error message
    }

    private void setupListeners() {
        // search bar input: call eventController.searchEvents(filters)
        // filter button click: apply filters and call eventController.searchEvents(filters)
        // event item click: navigate to EventDetailsActivity
    }
}