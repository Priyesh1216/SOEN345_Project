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
        if (adminId == null || adminId.isEmpty()) {
            callback.onFailure(new Exception("Admin ID can't be empty"));
            return;
        }
        if (event == null) {
            callback.onFailure(new Exception("Event can't be null"));
            return;
        }
        if (event.getTitle() == null || event.getTitle().isEmpty()) {
            callback.onFailure(new Exception("Event title can't be empty"));
            return;
        }
        if (event.getLocation() == null || event.getLocation().isEmpty()) {
            callback.onFailure(new Exception("Event location can't be empty"));
            return;
        }
        if (event.getTotalSeats() <= 0) {
            callback.onFailure(new Exception("Event seats must be greater than 0"));
            return;
        }

        if (event.getCategory() == null || event.getCategory().isEmpty()) {
            callback.onFailure(new Exception("Event category cannot be empty"));
            return;
        }
        repository.saveEvent(event, callback);
    }

    public void editEvent(String adminId, Event event, EventCallback callback) {
        if (adminId == null || adminId.isEmpty()) {
            callback.onFailure(new Exception("Admin ID cannot be empty"));
            return;
        }
        if (event == null) {
            callback.onFailure(new Exception("Event cannot be null"));
            return;
        }
        if (event.getTitle() == null || event.getTitle().isEmpty()) {
            callback.onFailure(new Exception("Event title cannot be empty"));
            return;
        }
        repository.saveEvent(event, callback);
    }

    public void cancelEvent(String adminId, String eventId, EventCallback callback) {
        if (adminId == null || adminId.isEmpty()) {
            callback.onFailure(new Exception("Admin ID can't be empty"));
            return;
        }
        if (eventId == null || eventId.isEmpty()) {
            callback.onFailure(new Exception("Event ID can't be empty"));
            return;
        }
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