package com.example.soen345_project.domain.models;

import java.io.Serializable;
import java.util.UUID;

public class MockTicket implements Serializable {
    private String ticketId;
    private MockEvent event;
    private int quantity;

    public MockTicket(MockEvent event, int quantity) {
        this.ticketId = UUID.randomUUID().toString();
        this.event = event;
        this.quantity = quantity;
    }

    public String getTicketId() { return ticketId; }
    public MockEvent getEvent() { return event; }
    public int getQuantity() { return quantity; }
}
