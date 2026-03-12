package com.example.soen345_project.domain.models;

import java.util.Date;

public class Reservation {
    public enum Status { CONFIRMED, CANCELLED }
    private Status status;
    private String id;
    private String eventId;
    private String userId;
    private int quantity;
    private Date createdAt;

    public Reservation() {}

    public Reservation(String eventId, String userId, int quantity) {
        this.eventId = eventId;
        this.userId = userId;
        this.quantity = quantity;
        this.status = Status.CONFIRMED;
        this.createdAt = new Date();
    }

    public void confirm() { this.status = Status.CONFIRMED; }
    public void cancel() { this.status = Status.CANCELLED; }
    // Getters
    public String getId() { return id; }
    public String getEventId() { return eventId; }
    public String getUserId() { return userId; }
    public int getQuantity() { return quantity; }
    public Status getStatus() { return status; }
    public Date getCreatedAt() { return createdAt; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setEventId(String eventId) { this.eventId = eventId; }
    public void setUserId(String userId) { this.userId = userId; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setStatus(Status status) { this.status = status; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}
