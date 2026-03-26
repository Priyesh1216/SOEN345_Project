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

    public AuthService(FirebaseAuth firebaseAuth, FirebaseRepository repository) {
        this.firebaseAuth = firebaseAuth;
        this.repository = repository;
    }

    public void registerEmail(String email, String password, String name, AuthCallback callback) {
        if (email.isEmpty() || password.isEmpty() || name.isEmpty()) {
            callback.onFailure(new Exception("Fields cannot be empty"));
            return;
        }
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

    public void registerPhone(String phoneNum, String name, Activity activity, AuthCallback authCallback, PhoneCodeSentCallback codeSentCallback) {
        if (phoneNum.isEmpty() || name.isEmpty()) {
            codeSentCallback.onFailure(new Exception("Fields cannot be empty"));
            return;
        }
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(firebaseAuth)
                .setPhoneNumber(phoneNum)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(activity)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential credential){
                        signInWithCredentialAndSave(credential, phoneNum, name, authCallback);
                    }
                    @Override
                    public void onVerificationFailed(FirebaseException e) {
                        codeSentCallback.onFailure(e);
                    }
                    @Override
                    public void onCodeSent(String verificationId,
                                           PhoneAuthProvider.ForceResendingToken token) {
                        codeSentCallback.onCodeSent(verificationId);
                    }
                })
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    public void signInEmail(String email, String password, AuthCallback callback) {
        if (email.isEmpty() || password.isEmpty()) {
            callback.onFailure(new Exception("Fields cannot be empty"));
            return;
        }
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

    public void signInPhone(String phoneNum, Activity activity, AuthCallback authCallback, PhoneCodeSentCallback codeSentCallback) {
        if (phoneNum.isEmpty()) {
            authCallback.onFailure(new Exception("Phone number cannot be empty"));
            return;
        }
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
                                            public void onSuccess(User user) { authCallback.onSuccess(user); }
                                            @Override
                                            public void onFailure(Exception e) { authCallback.onFailure(e); }
                                        });
                                    }
                                })
                                .addOnFailureListener(authCallback::onFailure);
                    }
                    @Override
                    public void onVerificationFailed(FirebaseException e) {
                        authCallback.onFailure(e);
                    }
                    @Override
                    public void onCodeSent(String verificationId,
                                           PhoneAuthProvider.ForceResendingToken token) {
                        codeSentCallback.onCodeSent(verificationId);
                    }
                })
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    public void signOut() {
        firebaseAuth.signOut();
    }

    public void verifyOtpAndRegister(String verificationId, String otp, String phoneNum,
                                     String name, AuthCallback callback) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, otp);
        signInWithCredentialAndSave(credential, phoneNum, name, callback);
    }

    public void verifyOtpAndLogin(String verificationId, String otp, AuthCallback callback) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, otp);
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

    private void signInWithCredentialAndSave(PhoneAuthCredential credential, String phoneNum,
                                             String name, AuthCallback callback) {
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

    public interface AuthCallback {
        void onSuccess(User user);
        void onFailure(Exception e);
    }

    public interface PhoneCodeSentCallback{
        void onCodeSent(String verificationId);
        void onFailure(Exception e);
    }
}