package com.example.soen345_project.domain.models;

public class Admin extends User {

    public Admin() {}

    public Admin(String id, String name, String email, String phoneNumber) {
        super(id, name, email, phoneNumber);
        setIsAdmin(true);
    }
}
