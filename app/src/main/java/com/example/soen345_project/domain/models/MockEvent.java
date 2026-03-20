package com.example.soen345_project.domain.models;

import java.io.Serializable;

public class MockEvent implements Serializable {
    private String id;
    private String title;
    private String date;
    private String location;
    private String description;

    public MockEvent(String id, String title, String date, String location, String description) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.location = location;
        this.description = description;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getDate() { return date; }
    public String getLocation() { return location; }
    public String getDescription() { return description; }
}
