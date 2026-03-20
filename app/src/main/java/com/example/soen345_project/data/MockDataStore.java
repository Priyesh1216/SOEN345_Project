package com.example.soen345_project.data;

import com.example.soen345_project.domain.models.MockEvent;
import com.example.soen345_project.domain.models.MockTicket;

import java.util.ArrayList;
import java.util.List;

public class MockDataStore {
    private static MockDataStore instance;
    private List<MockEvent> events;
    private List<MockTicket> myTickets;

    private MockDataStore() {
        events = new ArrayList<>();
        myTickets = new ArrayList<>();

        // Generate 5 mock events
        events.add(new MockEvent("1", "Summer Music Festival", "2026-06-15", "Montreal Downtown", "A great outdoor music experience featuring local artists."));
        events.add(new MockEvent("2", "Tech Conference 2026", "2026-08-20", "Palais des congrès", "Annual tech gathering for developers and designers."));
        events.add(new MockEvent("3", "Comedy Night", "2026-05-10", "Comedy Club Hub", "Stand-up comedy featuring international comedians."));
        events.add(new MockEvent("4", "Art Workshop", "2026-04-22", "Community Center", "Learn painting basics in a relaxing environment."));
        events.add(new MockEvent("5", "Marathon 2026", "2026-09-12", "City Park", "Join the annual city marathon. Categories for all levels."));
    }

    public static MockDataStore getInstance() {
        if (instance == null) {
            instance = new MockDataStore();
        }
        return instance;
    }

    public List<MockEvent> getEvents() {
        return events;
    }

    public MockEvent getEventById(String id) {
        for (MockEvent e : events) {
            if (e.getId().equals(id)) return e;
        }
        return null;
    }

    public List<MockTicket> getMyTickets() {
        return myTickets;
    }

    public void addTicket(MockTicket ticket) {
        myTickets.add(ticket);
    }

    public void removeTicket(MockTicket ticket) {
        myTickets.remove(ticket);
    }
}
