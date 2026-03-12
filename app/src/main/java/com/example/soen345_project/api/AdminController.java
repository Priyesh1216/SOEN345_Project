package com.example.soen345_project.api;

import com.example.soen345_project.domain.models.Event;
import com.example.soen345_project.domain.services.EventService;

public class AdminController {

    private final EventService eventService;

    public AdminController(EventService eventService) {
        this.eventService = eventService;
    }

    public void addEvent(String adminId, Event event, EventService.EventCallback callback) {
        eventService.addEvent(adminId, event, callback);
    }

    public void editEvent(String adminId, Event event, EventService.EventCallback callback) {
        eventService.editEvent(adminId, event, callback);
    }

    public void cancelEvent(String adminId, String eventId, EventService.EventCallback callback) {
        eventService.cancelEvent(adminId, eventId, callback);
    }
}