package com.example.soen345_project.api;
import com.example.soen345_project.domain.services.AuthService;

public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    public void registerWithEmail(String email, String password, AuthService.AuthCallback callback) {
        authService.registerEmail(email, password, callback);
    }

    public void registerWithPhone(String phoneNum, String password, AuthService.AuthCallback callback) {
        authService.registerPhone(phoneNum, password, callback);
    }

    public void signInWithEmail(String email, String password, AuthService.AuthCallback callback) {
        authService.signInEmail(email, password, callback);
    }

    public void signInWithPhone(String phoneNum, String password, AuthService.AuthCallback callback) {
        authService.signInPhone(phoneNum, password, callback);
    }

    public void signOut() {
        authService.signOut();
    }
}