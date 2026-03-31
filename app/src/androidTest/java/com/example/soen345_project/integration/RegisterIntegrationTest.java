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
public class RegisterIntegrationTest {

    private static FirebaseRepository repository;
    private static AuthService authService;

    @BeforeClass
    public static void setup() {
        try {
            // Targeting the emulators started via: npx firebase emulators:start --only auth,database
            FirebaseDatabase.getInstance().useEmulator("10.0.2.2", 9000);
            FirebaseAuth.getInstance().useEmulator("10.0.2.2", 9099);
        } catch (IllegalStateException e) {
            // Emulator already configured
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

    /**
     * Generates a unique email for every test execution to prevent
     * collisions in the persistent Emulator environment.
     */
    private String getUniqueEmail(String prefix) {
        return prefix + "_" + System.currentTimeMillis() + "@example.com";
    }

    @Test
    public void registerEmail_validCredentials_success() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        String email = getUniqueEmail("valid_reg");

        authService.registerEmail(
                email,
                "password123",
                "Register User",
                new AuthService.AuthCallback() {
                    @Override
                    public void onSuccess(User user) {
                        assertNotNull(user);
                        assertNotNull(user.getId());
                        assertEquals("Register User", user.getName());
                        assertEquals(email, user.getEmail());
                        latch.countDown();
                    }
                    @Override
                    public void onFailure(Exception e) {
                        fail("Registration failed: " + e.getMessage());
                        latch.countDown();
                    }
                });

        assertTrue("Timeout: Registration success expected", latch.await(10, TimeUnit.SECONDS));
    }

    @Test
    public void registerEmail_emptyFields_fails() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        authService.registerEmail("", "", "", new AuthService.AuthCallback() {
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

        assertTrue("Timeout: Validation failure expected", latch.await(5, TimeUnit.SECONDS));
    }

    @Test
    public void registerEmail_userSavedToDatabase() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        String email = getUniqueEmail("db_save");

        authService.registerEmail(
                email,
                "password123",
                "DB Test User",
                new AuthService.AuthCallback() {
                    @Override
                    public void onSuccess(User user) {
                        // After registration, verify the data exists in the Realtime Database emulator
                        repository.getUser(user.getId(), new FirebaseRepository.GetUserCallback() {
                            @Override
                            public void onSuccess(User fetchedUser) {
                                assertNotNull("Fetched user should not be null", fetchedUser);
                                assertEquals("DB Test User", fetchedUser.getName());
                                assertEquals(email, fetchedUser.getEmail());
                                latch.countDown();
                            }
                            @Override
                            public void onFailure(Exception e) {
                                fail("User not found in database: " + e.getMessage());
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

        assertTrue("Timeout: Database verification expected", latch.await(10, TimeUnit.SECONDS));
    }

    @Test
    public void registerEmail_duplicateEmail_fails() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        String email = getUniqueEmail("dup_check");

        // First registration
        authService.registerEmail(
                email,
                "password123",
                "First User",
                new AuthService.AuthCallback() {
                    @Override
                    public void onSuccess(User user) {
                        FirebaseAuth.getInstance().signOut();

                        // Attempt second registration with same email
                        authService.registerEmail(
                                email,
                                "password123",
                                "Second User",
                                new AuthService.AuthCallback() {
                                    @Override
                                    public void onSuccess(User u) {
                                        fail("Should have failed with duplicate email");
                                        latch.countDown();
                                    }
                                    @Override
                                    public void onFailure(Exception e) {
                                        // Failure is expected here
                                        assertNotNull(e.getMessage());
                                        latch.countDown();
                                    }
                                });
                    }
                    @Override
                    public void onFailure(Exception e) {
                        fail("First registration failed: " + e.getMessage());
                        latch.countDown();
                    }
                });

        assertTrue("Timeout: Duplicate check expected", latch.await(15, TimeUnit.SECONDS));
    }
}