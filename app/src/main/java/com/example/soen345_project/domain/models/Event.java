package com.example.soen345_project.domain.models;

import java.util.Date;

public class Event{

    public enum EventStatus {ACTIVE, CANCELLED}

    private String id;
    private String title;
    private Date date;
    private String location;
    private String category;
    private int totalSeats;
    private int openSeats;
    private EventStatus eventStatus;

    public Event(){}

    public Event(String title, Date date, String location, String category,
                 int totalSeats) {
        this.title = title;
        this.date = date;
        this.location = location;
        this.category = category;
        this.totalSeats = totalSeats;
        this.openSeats = totalSeats;
        this.eventStatus = EventStatus.ACTIVE;
    }

    public boolean hasEnoughSeats(int quantity) { return openSeats >= quantity; }
    public void reserveSeats(int quantity) { openSeats -= quantity; }
    public void cancelSeats(int quantity) { openSeats = Math.min(totalSeats, openSeats + quantity); }
    public void cancelEvent() { this.eventStatus = EventStatus.CANCELLED; }
    public boolean isActive() { return eventStatus == EventStatus.ACTIVE; }

    public String getId() { 
        return id; 
    }

    public String getTitle(){
        return title;
    }

    public Date getDate(){
        return date;
    }

    public String getLocation(){
        return location;
    }

    public String getCategory() {
        return category;
    }

    public int getTotalSeats() { return totalSeats; }
    public int getOpenSeats() { return openSeats; }
    public EventStatus getEventStatus() { return eventStatus; }

    public void setId(String id) {
        this.id = id; 
    }

    public void setTitle(String title){
        this.title = title;
    }

    public void setDate(Date date){
        this.date = date;
    }

    public void setLocation(String location){
        this.location = location;
    }

    public void setCategory(String category){
        this.category = category;
    }

    public void setTotalSeats(int totalSeats) { this.totalSeats = totalSeats; }
    public void setOpenSeats(int openSeats) { this.openSeats = openSeats; }
    public void setEventStatus(EventStatus eventStatus) { this.eventStatus = eventStatus; }

}