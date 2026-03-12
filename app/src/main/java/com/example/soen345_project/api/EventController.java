package com.example.soen345_project.api;

import com.example.soen345_project.domain.models.Event;
import com.example.soen345_project.domain.services.EventService;

import java.util.Map;

public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    public void listEvents(EventService.EventListCallback callback) {
        eventService.listEvents(callback);
    }

    public void searchEvents(Map<String, String> filters, EventService.EventListCallback callback) {
        eventService.searchEvents(filters, callback);
    }

    public void getEvent(String eventId, EventService.EventCallback callback) {

    }
}