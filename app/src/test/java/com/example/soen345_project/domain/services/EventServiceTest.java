package com.example.soen345_project.domain.services;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import com.example.soen345_project.data.FirebaseRepository;
import com.example.soen345_project.domain.models.Event;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class EventServiceTest {

    @Mock
    private FirebaseRepository mockRepository;

    @Mock
    private EventService.EventCallback mockEventCallback;

    @Mock
    private EventService.EventListCallback mockEventListCallback;

    private EventService eventService;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        eventService = new EventService(mockRepository);
    }

    // addEvent
    @Test
    public void addEvent_emptyAdminId_fail() {
        Event event = new Event("Concert", new Date(), "Montreal", "Music", 100);
        eventService.addEvent("", event, mockEventCallback);
        verify(mockEventCallback).onFailure(any(Exception.class));
    }

    @Test
    public void addEvent_nullEvent_shouldFail() {
        eventService.addEvent("adminId", null, mockEventCallback);
        verify(mockEventCallback).onFailure(any(Exception.class));
    }

    @Test
    public void addEvent_emptyTitle_fail() {
        Event event = new Event("", new Date(), "Montreal", "Music", 100);
        eventService.addEvent("adminId", event, mockEventCallback);
        verify(mockEventCallback).onFailure(any(Exception.class));
    }

    @Test
    public void addEvent_emptyLocation_fail() {
        Event event = new Event("Concert", new Date(), "", "Music", 100);
        eventService.addEvent("adminId", event, mockEventCallback);
        verify(mockEventCallback).onFailure(any(Exception.class));
    }

    @Test
    public void addEvent_zeroSeats_fail() {
        Event event = new Event("Concert", new Date(), "Montreal", "Music", 0);
        eventService.addEvent("adminId", event, mockEventCallback);
        verify(mockEventCallback).onFailure(any(Exception.class));
    }

    @Test
    public void addEvent_validEvent_callRepository() {
        Event event = new Event("Concert", new Date(), "Montreal", "Music", 100);
        eventService.addEvent("adminId", event, mockEventCallback);
        verify(mockRepository).saveEvent(eq(event), any());
    }

    // editEvent
    @Test
    public void editEvent_emptyAdminId_fail() {
        Event event = new Event("Concert", new Date(), "Montreal", "Music", 100);
        eventService.editEvent("", event, mockEventCallback);
        verify(mockEventCallback).onFailure(any(Exception.class));
    }

    @Test
    public void editEvent_nullEvent_fail() {
        eventService.editEvent("adminId", null, mockEventCallback);
        verify(mockEventCallback).onFailure(any(Exception.class));
    }

    @Test
    public void editEvent_validEvent_callRepository() {
        Event event = new Event("Concert", new Date(), "Montreal", "Music", 100);
        eventService.editEvent("adminId", event, mockEventCallback);
        verify(mockRepository).saveEvent(eq(event), any());
    }

    // cancelEvent
    @Test
    public void cancelEvent_emptyAdminId_fail() {
        eventService.cancelEvent("", "eventId", mockEventCallback);
        verify(mockEventCallback).onFailure(any(Exception.class));
    }

    @Test
    public void cancelEvent_emptyEventId_fail() {
        eventService.cancelEvent("adminId", "", mockEventCallback);
        verify(mockEventCallback).onFailure(any(Exception.class));
    }

    @Test
    public void cancelEvent_validIds_callRepository() {
        eventService.cancelEvent("adminId", "eventId", mockEventCallback);
        verify(mockRepository).getEvent(eq("eventId"), any());
    }

    // listEvents
    @Test
    public void listEvents_callRepository() {
        eventService.listEvents(mockEventListCallback);
        verify(mockRepository).getFilteredEvents(eq(null), any());
    }

    // searchEvents
    @Test
    public void searchEvents_nullFilters_callRepository() {
        eventService.searchEvents(null, mockEventListCallback);
        verify(mockRepository).getFilteredEvents(eq(null), any());
    }
}