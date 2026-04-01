package com.example.soen345_project.integration;

import static org.junit.Assert.*;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.soen345_project.data.FirebaseRepository;
import com.example.soen345_project.domain.models.User;
import com.example.soen345_project.domain.services.AuthService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@RunWith(AndroidJUnit4.class)
public class LoginIntegrationTest {

    private static FirebaseRepository repository;
    private static AuthService authService;

    @BeforeClass
    public static void setup() {
        try {
            FirebaseDatabase.getInstance().useEmulator("10.0.2.2", 9000);
            FirebaseAuth.getInstance().useEmulator("10.0.2.2", 9099);
        } catch (IllegalStateException e) {
            // Emulator already running
        }

        repository = new FirebaseRepository();
        authService = new AuthService(FirebaseAuth.getInstance(), repository);
    }

    @Before
    public void clearAuth() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            FirebaseAuth.getInstance().signOut();
        }
    }

    @Test
    public void signInEmail_validCredentials_success() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        authService.registerEmail(
                "login@example.com",
                "password123",
                "Login User",
                new AuthService.AuthCallback() {
                    @Override
                    public void onSuccess(User user) {

                        FirebaseAuth.getInstance().signOut();

                        authService.signInEmail(
                                "login@example.com",
                                "password123",
                                new AuthService.AuthCallback() {
                                    @Override
                                    public void onSuccess(User loggedInUser) {
                                        assertNotNull(loggedInUser);
                                        assertEquals("login@example.com", loggedInUser.getEmail());
                                        latch.countDown();
                                    }

                                    @Override
                                    public void onFailure(Exception e) {
                                        fail("Login failed: " + e.getMessage());
                                        latch.countDown();
                                    }
                                });
                    }

                    @Override
                    public void onFailure(Exception e) {
                        fail("Registration failed: " + e.getMessage());
                        latch.countDown();
                    }
                });

        assertTrue("Timeout waiting for login success", latch.await(30, TimeUnit.SECONDS));
    }

    @Test
    public void signInEmail_emptyFields_fails() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        authService.signInEmail("", "", new AuthService.AuthCallback() {
            @Override
            public void onSuccess(User user) {
                fail("Should have failed with empty fields");
                latch.countDown();
            }

            @Override
            public void onFailure(Exception e) {
                assertEquals("Fields cannot be empty", e.getMessage());
                latch.countDown();
            }
        });

        assertTrue("Timeout waiting for empty field failure", latch.await(30, TimeUnit.SECONDS));
    }

    @Test
    public void signInEmail_wrongPassword_fails() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        authService.registerEmail(
                "wrongpass@example.com",
                "correctpassword",
                "Wrong Pass User",
                new AuthService.AuthCallback() {
                    @Override
                    public void onSuccess(User user) {

                        FirebaseAuth.getInstance().signOut();

                        authService.signInEmail(
                                "wrongpass@example.com",
                                "wrongpassword",
                                new AuthService.AuthCallback() {
                                    @Override
                                    public void onSuccess(User u) {
                                        fail("Should have failed with wrong password");
                                        latch.countDown();
                                    }

                                    @Override
                                    public void onFailure(Exception e) {
                                        assertNotNull(e);
                                        latch.countDown();
                                    }
                                });
                    }

                    @Override
                    public void onFailure(Exception e) {
                        fail("Registration failed: " + e.getMessage());
                        latch.countDown();
                    }
                });

        assertTrue("Timeout waiting for wrong password failure", latch.await(30, TimeUnit.SECONDS));
    }

    @Test
    public void signInEmail_unregisteredEmail_fails() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        authService.signInEmail(
                "notregistered@example.com",
                "password123",
                new AuthService.AuthCallback() {
                    @Override
                    public void onSuccess(User user) {
                        fail("Should have failed with unregistered email");
                        latch.countDown();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        assertNotNull(e);
                        latch.countDown();
                    }
                });

        assertTrue("Timeout waiting for unregistered email failure", latch.await(30, TimeUnit.SECONDS));
    }
}