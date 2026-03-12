package com.example.soen345_project.domain.models;

public class Customer extends User {
    public Customer() {}

    public Customer(String id, String name, String email, String phoneNumber) {
        super(id, name, email, phoneNumber);
        setIsAdmin(false);
    }
}
