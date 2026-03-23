package com.example.soen345_project.api;

import android.app.Activity;
import com.example.soen345_project.domain.services.AuthService;

public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    public void registerWithEmail(String email, String password, String name,
                                  AuthService.AuthCallback callback) {
        authService.registerEmail(email, password, name, callback);
    }

    public void registerWithPhone(String phoneNum, String name, Activity activity,
                                  AuthService.AuthCallback callback) {
        authService.registerPhone(phoneNum, name, activity, callback);
    }

    public void signInWithEmail(String email, String password, AuthService.AuthCallback callback) {
        authService.signInEmail(email, password, callback);
    }

    public void signInWithPhone(String phoneNum, Activity activity,
                                AuthService.AuthCallback callback) {
        authService.signInPhone(phoneNum, activity, callback);
    }

    public void signOut() {
        authService.signOut();
    }
}