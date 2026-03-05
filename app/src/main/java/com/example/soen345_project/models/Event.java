package com.example.soen345_project.models;

import java.util.Date;

public class Event{
    private String id;
    private String title;
    private Date date;
    private String location;
    private String category;

    public Event (String title, Date date, String location, String category){
        this.title = title;
        this.date = date;
        this.location = location;
        this.category = category;
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
}