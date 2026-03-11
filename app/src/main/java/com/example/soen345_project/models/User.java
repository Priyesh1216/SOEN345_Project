package com.example.soen345_project.models;

public class User {
    private String id;
    private String name;
    private String email;
    private String phoneNumber;
    private boolean isAdmin = false;

    public User(String name, String email, String phoneNumber){
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    public String getName(){
        return name;
    }

    public String getEmail(){
        return email;
    }

    public String getPhoneNumber(){
        return phoneNumber;
    }

    public boolean getIsAdmin(){
        return isAdmin;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setEmail(String email){
        this.email = email;
    }

    public void setPhoneNumber(String phoneNumber){
        this.phoneNumber = phoneNumber;
    }

    public void setIsAdmin(boolean isAdmin){
        this.isAdmin = isAdmin;
    }
}
