package com.example.soen345_project.domain.services;

import android.app.Activity;
import com.example.soen345_project.data.FirebaseRepository;
import com.example.soen345_project.domain.models.User;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import java.util.concurrent.TimeUnit;

public class AuthService {

    private final FirebaseAuth firebaseAuth;
    private final FirebaseRepository repository;

    public AuthService(FirebaseRepository repository) {
        this.firebaseAuth = FirebaseAuth.getInstance();
        this.repository = repository;
    }

    public void registerEmail(String email, String password, String name, AuthCallback callback) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(result -> {
                    FirebaseUser firebaseUser = result.getUser();
                    if (firebaseUser != null) {
                        String uid = firebaseUser.getUid();
                        User user = new User();
                        user.setId(uid);
                        user.setName(name);
                        user.setEmail(email);
                        repository.saveUser(uid, user, new FirebaseRepository.SimpleCallback() {
                            @Override
                            public void onSuccess() { callback.onSuccess(user); }
                            @Override
                            public void onFailure(Exception e) { callback.onFailure(e); }
                        });
                    }
                })
                .addOnFailureListener(callback::onFailure);
    }

    public void registerPhone(String phoneNum, String name, Activity activity, AuthCallback callback) {
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(firebaseAuth)
                .setPhoneNumber(phoneNum)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(activity)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential credential) {
                        firebaseAuth.signInWithCredential(credential)
                                .addOnSuccessListener(result -> {
                                    FirebaseUser firebaseUser = result.getUser();
                                    if (firebaseUser != null) {
                                        String uid = firebaseUser.getUid();
                                        User user = new User();
                                        user.setId(uid);
                                        user.setName(name);
                                        user.setPhoneNumber(phoneNum);
                                        repository.saveUser(uid, user, new FirebaseRepository.SimpleCallback() {
                                            @Override
                                            public void onSuccess() { callback.onSuccess(user); }
                                            @Override
                                            public void onFailure(Exception e) { callback.onFailure(e); }
                                        });
                                    }
                                })
                                .addOnFailureListener(callback::onFailure);
                    }
                    @Override
                    public void onVerificationFailed(FirebaseException e) {
                        callback.onFailure(e);
                    }
                })
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    public void signInEmail(String email, String password, AuthCallback callback) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(result -> {
                    FirebaseUser firebaseUser = result.getUser();
                    if (firebaseUser != null) {
                        repository.getUser(firebaseUser.getUid(), new FirebaseRepository.GetUserCallback() {
                            @Override
                            public void onSuccess(User user) { callback.onSuccess(user); }
                            @Override
                            public void onFailure(Exception e) { callback.onFailure(e); }
                        });
                    }
                })
                .addOnFailureListener(callback::onFailure);
    }

    public void signInPhone(String phoneNum, Activity activity, AuthCallback callback) {
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(firebaseAuth)
                .setPhoneNumber(phoneNum)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(activity)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential credential) {
                        firebaseAuth.signInWithCredential(credential)
                                .addOnSuccessListener(result -> {
                                    FirebaseUser firebaseUser = result.getUser();
                                    if (firebaseUser != null) {
                                        repository.getUser(firebaseUser.getUid(), new FirebaseRepository.GetUserCallback() {
                                            @Override
                                            public void onSuccess(User user) { callback.onSuccess(user); }
                                            @Override
                                            public void onFailure(Exception e) { callback.onFailure(e); }
                                        });
                                    }
                                })
                                .addOnFailureListener(callback::onFailure);
                    }
                    @Override
                    public void onVerificationFailed(FirebaseException e) {
                        callback.onFailure(e);
                    }
                })
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    public void signOut() {
        firebaseAuth.signOut();
    }

    public interface AuthCallback {
        void onSuccess(User user);
        void onFailure(Exception e);
    }
}