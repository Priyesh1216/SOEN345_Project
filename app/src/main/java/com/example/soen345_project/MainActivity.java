package com.example.soen345_project;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.soen345_project.data.FirebaseRepository;
import com.example.soen345_project.domain.models.Event;
import com.example.soen345_project.domain.services.EventService;

import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseRepository repo = new FirebaseRepository();

        // test saveEvent
        Event event = new Event("Test Concert", new Date(), "Montreal", "Music", 100);
        repo.saveEvent(event, new EventService.EventCallback() {
            @Override
            public void onSuccess(Event e) {
                Log.d(TAG, "Event saved with ID: " + e.getId());

                // test getEvent using the ID we just got back
                repo.getEvent(e.getId(), new FirebaseRepository.GetEventCallback() {
                    @Override
                    public void onSuccess(Event fetchedEvent) {
                        Log.d(TAG, "Event fetched: " + fetchedEvent.getTitle());
                    }
                    @Override
                    public void onFailure(Exception ex) {
                        Log.e(TAG, "getEvent failed: " + ex.getMessage());
                    }
                });
            }
            @Override
            public void onFailure(Exception e) {
                Log.e(TAG, "saveEvent failed: " + e.getMessage());
            }
        });
    }
}