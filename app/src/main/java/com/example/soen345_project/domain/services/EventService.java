package com.example.soen345_project.domain.services;

import com.example.soen345_project.data.FirebaseRepository;
import com.example.soen345_project.domain.models.Event;

import java.util.List;
import java.util.Map;

public class EventService {

    private final FirebaseRepository repository;

    public EventService(FirebaseRepository repository) {
        this.repository = repository;
    }

    public void listEvents(EventListCallback callback) {
        repository.getFilteredEvents(null, callback);
    }

    public void searchEvents(Map<String, String> filters, EventListCallback callback) {
        // filters can contain: location, date, category
        repository.getFilteredEvents(filters, callback);
    }

    public void addEvent(String adminId, Event event, EventCallback callback) {
        // verify admin then save
        repository.saveEvent(event, callback);
    }

    public void editEvent(String adminId, Event event, EventCallback callback) {
        // verify admin then update
        repository.saveEvent(event, callback);
    }

    public void cancelEvent(String adminId, String eventId, EventCallback callback) {
        // fetch event, mark CANCELLED, save
        repository.getEvent(eventId, new FirebaseRepository.GetEventCallback() {
            @Override
            public void onSuccess(Event event) {
                event.cancelEvent();
                repository.saveEvent(event, callback);
            }
            @Override
            public void onFailure(Exception e) {
                callback.onFailure(e);
            }
        });
    }

    public interface EventListCallback {
        void onSuccess(List<Event> events);
        void onFailure(Exception e);
    }

    public interface EventCallback {
        void onSuccess(Event event);
        void onFailure(Exception e);
    }
}