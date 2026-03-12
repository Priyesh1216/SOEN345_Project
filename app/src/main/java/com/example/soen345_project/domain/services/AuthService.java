package com.example.soen345_project.domain.services;

import com.example.soen345_project.data.FirebaseRepository;
import com.example.soen345_project.domain.models.User;

public class AuthService {

    private final FirebaseRepository repository;

    public AuthService(FirebaseRepository repository) {
        this.repository = repository;
    }

    public void registerEmail(String email, String password, AuthCallback callback) {
        // register with Firebase Auth (email) - on success: save user to repository
    }

    public void registerPhone(String phoneNum, String password, AuthCallback callback) {
        // register with Firebase Auth (phone) - on success: save user to repository
    }

    public void signInEmail(String email, String password, AuthCallback callback) {
        // sign in with Firebase (email)
    }

    public void signInPhone(String phoneNum, String password, AuthCallback callback) {
        // sign in with Firebase Auth (phone)
    }

    public void signOut() {
        // sign out
    }

    public interface AuthCallback {
        void onSuccess(User user);
        void onFailure(Exception e);
    }
}