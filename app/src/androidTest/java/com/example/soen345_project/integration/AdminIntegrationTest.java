package com.example.soen345_project.integration;

import com.example.soen345_project.api.AdminController;
import com.example.soen345_project.data.FirebaseRepository;
import com.example.soen345_project.domain.models.Event;
import com.example.soen345_project.domain.services.EventService;
import com.google.firebase.database.FirebaseDatabase;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class AdminIntegrationTest {

    private static FirebaseRepository repository;
    private static EventService eventService;
    private static AdminController adminController;

    @BeforeClass
    public static void setup() {
        repository = new FirebaseRepository();
        try {
            FirebaseDatabase.getInstance().useEmulator("10.0.2.2", 9000);
        } catch (IllegalStateException e) {}

        eventService = new EventService(repository);
        adminController = new AdminController(eventService);
    }

    @After
    public void tearDown() {
        // Ensure old data doesn't interfere with new tests
        FirebaseDatabase.getInstance().getReference("events").setValue(null);
    }

    @Test
    public void testAddEvent() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Event event = new Event("New Event", new Date(), "Montreal", "Tech", 50);

        adminController.addEvent("admin123", event, new EventService.EventCallback() {
            @Override
            public void onSuccess(Event addedEvent) {
                assertNotNull(addedEvent.getId());
                assertEquals("New Event", addedEvent.getTitle());
                latch.countDown();
            }
            @Override
            public void onFailure(Exception e) {
                fail(e.getMessage());
                latch.countDown();
            }
        });
        assertTrue(latch.await(10, TimeUnit.SECONDS));
    }

    @Test
    public void testEditEvent() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Event event = new Event("Edit Test", new Date(), "Loc", "Cat", 10);

        adminController.addEvent("admin123", event, new EventService.EventCallback() {
            @Override
            public void onSuccess(Event addedEvent) {
                addedEvent.setTitle("New Title");
                adminController.editEvent("admin123", addedEvent, new EventService.EventCallback() {
                    @Override
                    public void onSuccess(Event editedEvent) {
                        assertEquals("New Title", editedEvent.getTitle());
                        latch.countDown();
                    }
                    @Override
                    public void onFailure(Exception e) {
                        latch.countDown();
                        fail("Edit failed: " + e.getMessage());
                    }
                });
            }
            @Override
            public void onFailure(Exception e) {
                latch.countDown();
                fail("Initial add failed: " + e.getMessage());
            }
        });

        boolean completed = latch.await(20, TimeUnit.SECONDS);
        assertTrue("Timed out", completed);
    }

    @Test
    public void testCancelEvent() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Event event = new Event("To Be Cancelled", new Date(), "Location", "Category", 10);

        adminController.addEvent("admin123", event, new EventService.EventCallback() {
            @Override
            public void onSuccess(Event addedEvent) {
                adminController.cancelEvent("admin123", addedEvent.getId(), new EventService.EventCallback() {
                    @Override
                    public void onSuccess(Event cancelledEvent) {
                        assertFalse("Event should be inactive after cancellation", cancelledEvent.isActive());
                        latch.countDown();
                    }
                    @Override
                    public void onFailure(Exception e) { fail(); latch.countDown(); }
                });
            }
            @Override
            public void onFailure(Exception e) { fail(); latch.countDown(); }
        });
        assertTrue(latch.await(10, TimeUnit.SECONDS));
    }

    @Test
    public void testSearchEvents() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);

        Event seed = new Event("Search", new Date(), "Montreal", "Workshop", 100);

        adminController.addEvent("admin123", seed, new EventService.EventCallback() {
            @Override
            public void onSuccess(Event addedEvent) {
                try { Thread.sleep(1000); } catch (InterruptedException e) {}

                eventService.searchEvents(null, new EventService.EventListCallback() {
                    @Override
                    public void onSuccess(List<Event> events) {
                        assertFalse("Search should find the seeded event", events.isEmpty());
                        latch.countDown();
                    }
                    @Override
                    public void onFailure(Exception e) { fail(); latch.countDown(); }
                });
            }
            @Override
            public void onFailure(Exception e) { fail(); latch.countDown(); }
        });
        assertTrue(latch.await(10, TimeUnit.SECONDS));
    }

    @Test
    public void testAddEventWithInvalidAdminId() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        Event event = new Event("Bad Admin Test", new Date(), "Loc", "Cat", 10);

        adminController.addEvent("", event, new EventService.EventCallback() {
            @Override
            public void onSuccess(Event e) {
                fail("Should have failed due to empty Admin ID");
                latch.countDown();
            }
            @Override
            public void onFailure(Exception e) {
                assertEquals("Admin ID can't be empty", e.getMessage());
                latch.countDown();
            }
        });
        assertTrue(latch.await(10, TimeUnit.SECONDS));
    }
}