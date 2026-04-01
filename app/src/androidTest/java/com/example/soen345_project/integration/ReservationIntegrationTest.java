package com.example.soen345_project.integration;

import static org.junit.Assert.*;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import com.example.soen345_project.data.FirebaseRepository;
import com.example.soen345_project.domain.models.Reservation;
import com.example.soen345_project.domain.services.NotifService;
import com.example.soen345_project.domain.services.ReservationService;
import com.google.firebase.database.FirebaseDatabase;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@RunWith(AndroidJUnit4.class)
public class ReservationIntegrationTest {

    private static FirebaseRepository repository;
    private static ReservationService reservationService;

    @BeforeClass
    public static void setup() {
        try {
            // Connect to the local Firebase Realtime Database emulator
            FirebaseDatabase.getInstance().useEmulator("10.0.2.2", 9000);
        } catch (IllegalStateException e) {
            // Emulator connection already established
        }

        repository = new FirebaseRepository();

        // Create a manual dummy for NotifService to avoid Mockito issues on Android 14
        NotifService dummyNotif = new NotifService() {
            @Override
            public void sendConfirmationMsg(String userId, String msg) {
                // Skip notification logic for this test
            }
            @Override
            public void sendCancellationMsg(String userId, String msg) {
                // Skip notification logic for this test
            }
        };

        reservationService = new ReservationService(repository, dummyNotif);
    }

    @Test
    public void testReserveTickets_integration() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        // Tests the transaction logic in FirebaseRepository
        reservationService.reserveTickets("user123", "event456", 1,
                new ReservationService.ReservationCallback() {
                    @Override
                    public void onSuccess(Reservation reservation) {
                        assertNotNull(reservation.getId());
                        assertEquals(Reservation.Status.CONFIRMED, reservation.getStatus());
                        latch.countDown();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        fail("Reservation failed: " + e.getMessage());
                        latch.countDown();
                    }
                });

        assertTrue("Timeout: Database transaction took too long", latch.await(10, TimeUnit.SECONDS));
    }
}