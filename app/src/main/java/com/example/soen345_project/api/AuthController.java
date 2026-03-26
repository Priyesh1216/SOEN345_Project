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
                                  AuthService.AuthCallback authCallback, AuthService.PhoneCodeSentCallback codeSentCallback) {
        authService.registerPhone(phoneNum, name, activity, authCallback, codeSentCallback);
    }

    public void signInWithEmail(String email, String password, AuthService.AuthCallback callback) {
        authService.signInEmail(email, password, callback);
    }

    public void signInWithPhone(String phoneNum, Activity activity,
                                AuthService.AuthCallback authCallback, AuthService.PhoneCodeSentCallback codeSentCallback) {
        authService.signInPhone(phoneNum, activity, authCallback, codeSentCallback);
    }

    public void verifyOtpAndLogin(String verificationId, String otp,
                                  AuthService.AuthCallback callback) {
        authService.verifyOtpAndLogin(verificationId, otp, callback);
    }

    public void verifyOtpAndRegister(String verificationId, String otp, String phoneNum,
                                     String name, AuthService.AuthCallback callback) {
        authService.verifyOtpAndRegister(verificationId, otp, phoneNum, name, callback);
    }

    public void signOut() {
        authService.signOut();
    }
}