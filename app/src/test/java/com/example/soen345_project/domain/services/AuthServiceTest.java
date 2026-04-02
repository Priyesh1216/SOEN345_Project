package com.example.soen345_project.domain.services;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import com.example.soen345_project.data.FirebaseRepository;
import com.example.soen345_project.domain.models.User;
import com.google.firebase.auth.FirebaseAuth;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthResult;

@RunWith(MockitoJUnitRunner.class)
public class AuthServiceTest {

    @Mock
    private FirebaseRepository mockRepository;

    @Mock
    private FirebaseAuth mockFirebaseAuth;

    @Mock
    private AuthService.AuthCallback mockCallback;

    private AuthService authService;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        authService = new AuthService(mockFirebaseAuth, mockRepository);
        Task<AuthResult> mockTask = mock(Task.class);
        when(mockFirebaseAuth.createUserWithEmailAndPassword(anyString(), anyString())).thenReturn(mockTask);
        when(mockFirebaseAuth.signInWithEmailAndPassword(anyString(), anyString())).thenReturn(mockTask);
        when(mockTask.addOnSuccessListener(any())).thenReturn(mockTask);
        when(mockTask.addOnFailureListener(any())).thenReturn(mockTask);
    }

    @Test
    public void registerEmail_emptyEmail_shouldFail() {
        authService.registerEmail("", "password123", "John", mockCallback);
        verify(mockCallback).onFailure(any(Exception.class));
    }

    @Test
    public void registerEmail_emptyPassword_shouldFail() {
        authService.registerEmail("test@email.com", "", "John", mockCallback);
        verify(mockCallback).onFailure(any(Exception.class));
    }

    @Test
    public void registerEmail_emptyName_shouldFail() {
        authService.registerEmail("test@email.com", "password123", "", mockCallback);
        verify(mockCallback).onFailure(any(Exception.class));
    }

    @Test
    public void signInEmail_emptyEmail_shouldFail() {
        authService.signInEmail("", "password123", mockCallback);
        verify(mockCallback).onFailure(any(Exception.class));
    }

    @Test
    public void signInEmail_emptyPassword_shouldFail() {
        authService.signInEmail("test@email.com", "", mockCallback);
        verify(mockCallback).onFailure(any(Exception.class));
    }

    @Test
    public void signOut_shouldNotThrow() {
        try {
            authService.signOut();
        } catch (Exception e) {
            fail("signOut should not throw: " + e.getMessage());
        }
    }

    @Test
    public void registerEmail_validFields_callsFirebaseCreateUser() {
        authService.registerEmail("test@email.com", "password123", "John", mockCallback);
        verify(mockFirebaseAuth).createUserWithEmailAndPassword("test@email.com", "password123");
    }

    @Test
    public void signInEmail_validFields_callsFirebaseSignIn() {
        authService.signInEmail("test@email.com", "password123", mockCallback);
        verify(mockFirebaseAuth).signInWithEmailAndPassword("test@email.com", "password123");

    }

    @Test
    public void registerPhone_emptyPhone_shouldFail() {
        AuthService.PhoneCodeSentCallback codeSentCallback = mock(AuthService.PhoneCodeSentCallback.class);
        authService.registerPhone("", "John", null, mockCallback, codeSentCallback);
        verify(codeSentCallback).onFailure(any(Exception.class));
    }

    @Test
    public void registerPhone_emptyName_shouldFail() {
        AuthService.PhoneCodeSentCallback codeSentCallback = mock(AuthService.PhoneCodeSentCallback.class);
        authService.registerPhone("+15141234567", "", null, mockCallback, codeSentCallback);
        verify(codeSentCallback).onFailure(any(Exception.class));
    }

    @Test
    public void signInPhone_emptyPhone_shouldFail() {
        AuthService.PhoneCodeSentCallback codeSentCallback = mock(AuthService.PhoneCodeSentCallback.class);
        authService.signInPhone("", null, mockCallback, codeSentCallback);
        verify(mockCallback).onFailure(any(Exception.class));
    }

}