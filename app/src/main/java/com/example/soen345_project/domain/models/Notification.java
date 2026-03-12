package com.example.soen345_project.domain.models;

public class Notification {

    public enum NotificationType { CONFIRMATION, CANCELLATION }

    private String id;
    private NotificationType type;
    private String recipient;
    private String message;

    public Notification() {}

    public Notification(NotificationType type, String recipient, String message) {
        this.type = type;
        this.recipient = recipient;
        this.message = message;
    }

    // Getters
    public String getId() { return id; }
    public NotificationType getType() { return type; }
    public String getRecipient() { return recipient; }
    public String getMessage() { return message; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setType(String type) { this.type = NotificationType.valueOf(type); } // Firebase compat
    public void setRecipient(String recipient) { this.recipient = recipient; }
    public void setMessage(String message) { this.message = message; }
}