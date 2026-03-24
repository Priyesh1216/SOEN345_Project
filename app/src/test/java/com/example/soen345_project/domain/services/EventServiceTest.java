package com.example.soen345_project.domain.services;

import static org.mockito.Mockito.*;

import com.example.soen345_project.data.FirebaseRepository;
import com.example.soen345_project.domain.models.Event;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        EventServiceTest.AddEventTest.class,
        EventServiceTest.EditEventTest.class,
        EventServiceTest.CancelEventTest.class,
        EventServiceTest.ListSearchEventTest.class
})
public class EventServiceTest {

    // Add Event
    @RunWith(org.junit.runners.Parameterized.class)
    public static class AddEventTest {

        @org.junit.runners.Parameterized.Parameter(0) public String adminId;
        @org.junit.runners.Parameterized.Parameter(1) public String title;
        @org.junit.runners.Parameterized.Parameter(2) public String location;
        @org.junit.runners.Parameterized.Parameter(3) public String category;
        @org.junit.runners.Parameterized.Parameter(4) public int seats;
        @org.junit.runners.Parameterized.Parameter(5) public boolean shouldFail;

        @org.junit.runners.Parameterized.Parameters(name = "{index}: adminId={0} title={1} location={2} category={3} seats={4}")
        public static java.util.Collection<Object[]> data() {
            return java.util.Arrays.asList(new Object[][]{
                    { null,      "Comedy Show", "Montreal", "Comedy", 100,  true  },
                    { "",        "Comedy Show", "Montreal", "Comedy", 100,  true  },
                    { "adminId", null,          "Montreal", "Comedy", 100,  true  },
                    { "adminId", "",            "Montreal", "Comedy", 100,  true  },
                    { "adminId", "Comedy Show", null,       "Comedy", 100,  true  },
                    { "adminId", "Comedy Show", "",         "Comedy", 100,  true  },
                    { "adminId", "Comedy Show", "Montreal", null,     100,  true  },
                    { "adminId", "Comedy Show", "Montreal", "",       100,  true  },
                    { "adminId", "Comedy Show", "Montreal", "Comedy", 0,    true  },
                    { "adminId", "Comedy Show", "Montreal", "Comedy", -1,   true  },
                    { "adminId", "Comedy Show", "Montreal", "Comedy", 100,  false },
            });
        }

        private FirebaseRepository mockRepository;
        private EventService.EventCallback mockEventCallback;
        private EventService eventService;

        @Before
        public void setUp() {
            mockRepository = mock(FirebaseRepository.class);
            mockEventCallback = mock(EventService.EventCallback.class);
            eventService = new EventService(mockRepository);
        }

        @Test
        public void addEvent_test() {
            Event event = new Event(title, new Date(), location, category, seats);
            eventService.addEvent(adminId, event, mockEventCallback);
            if (shouldFail) {
                verify(mockEventCallback).onFailure(any(Exception.class));
            } else {
                verify(mockRepository).saveEvent(eq(event), any());
            }
        }

        @Test
        public void addEvent_nullEvent_fail() {
            eventService.addEvent("adminId", null, mockEventCallback);
            verify(mockEventCallback).onFailure(any(Exception.class));
        }
    }

    // Edit Event
    @RunWith(org.junit.runners.Parameterized.class)
    public static class EditEventTest {

        @org.junit.runners.Parameterized.Parameter(0) public String adminId;
        @org.junit.runners.Parameterized.Parameter(1) public String title;
        @org.junit.runners.Parameterized.Parameter(2) public boolean shouldFail;

        @org.junit.runners.Parameterized.Parameters(name = "{index}: adminId={0} title={1}")
        public static java.util.Collection<Object[]> data() {
            return java.util.Arrays.asList(new Object[][]{
                    { null,      "Comedy Show", true  },
                    { "",        "Comedy Show", true  },
                    { "adminId", null,          true  },
                    { "adminId", "",            true  },
                    { "adminId", "Comedy Show", false },
            });
        }

        private FirebaseRepository mockRepository;
        private EventService.EventCallback mockEventCallback;
        private EventService eventService;

        @Before
        public void setUp() {
            mockRepository = mock(FirebaseRepository.class);
            mockEventCallback = mock(EventService.EventCallback.class);
            eventService = new EventService(mockRepository);
        }

        @Test
        public void editEvent_test() {
            Event event = title != null ? new Event(title, new Date(), "Montreal", "Comedy", 100) : null;
            eventService.editEvent(adminId, event, mockEventCallback);
            if (shouldFail) {
                verify(mockEventCallback).onFailure(any(Exception.class));
            } else {
                verify(mockRepository).saveEvent(eq(event), any());
            }
        }

        @Test
        public void editEvent_nullEvent_fail() {
            eventService.editEvent("adminId", null, mockEventCallback);
            verify(mockEventCallback).onFailure(any(Exception.class));
        }
    }

    // Cancel Event
    @RunWith(org.junit.runners.Parameterized.class)
    public static class CancelEventTest {

        @org.junit.runners.Parameterized.Parameter(0) public String adminId;
        @org.junit.runners.Parameterized.Parameter(1) public String eventId;
        @org.junit.runners.Parameterized.Parameter(2) public boolean shouldFail;

        @org.junit.runners.Parameterized.Parameters(name = "{index}: adminId={0} eventId={1}")
        public static java.util.Collection<Object[]> data() {
            return java.util.Arrays.asList(new Object[][]{
                    { null,      "eventId", true  },
                    { "",        "eventId", true  },
                    { "adminId", null,      true  },
                    { "adminId", "",        true  },
                    { "adminId", "eventId", false },
            });
        }

        private FirebaseRepository mockRepository;
        private EventService.EventCallback mockEventCallback;
        private EventService eventService;

        @Before
        public void setUp() {
            mockRepository = mock(FirebaseRepository.class);
            mockEventCallback = mock(EventService.EventCallback.class);
            eventService = new EventService(mockRepository);
        }

        @Test
        public void cancelEvent_test() {
            eventService.cancelEvent(adminId, eventId, mockEventCallback);
            if (shouldFail) {
                verify(mockEventCallback).onFailure(any(Exception.class));
            } else {
                verify(mockRepository).getEvent(eq(eventId), any());
            }
        }

        @Test
        public void cancelEvent_onFailure_callsCallback() {
            Exception mockException = new Exception("Event not found");
            doAnswer(invocation -> {
                FirebaseRepository.GetEventCallback callback = invocation.getArgument(1);
                callback.onFailure(mockException);
                return null;
            }).when(mockRepository).getEvent(eq("eventId"), any());

            eventService.cancelEvent("adminId", "eventId", mockEventCallback);
            verify(mockEventCallback).onFailure(eq(mockException));
        }

        @Test
        public void cancelEvent_onSuccess_cancelsEvent() {
            Event mockEvent = mock(Event.class);
            doAnswer(invocation -> {
                FirebaseRepository.GetEventCallback callback = invocation.getArgument(1);
                callback.onSuccess(mockEvent);
                return null;
            }).when(mockRepository).getEvent(eq("eventId"), any());

            eventService.cancelEvent("adminId", "eventId", mockEventCallback);
            verify(mockEvent).cancelEvent();
        }

        @Test
        public void cancelEvent_onSuccess_savesEvent() {
            Event mockEvent = mock(Event.class);
            doAnswer(invocation -> {
                FirebaseRepository.GetEventCallback callback = invocation.getArgument(1);
                callback.onSuccess(mockEvent);
                return null;
            }).when(mockRepository).getEvent(eq("eventId"), any());

            eventService.cancelEvent("adminId", "eventId", mockEventCallback);
            verify(mockRepository).saveEvent(eq(mockEvent), any());
        }
    }

    // List & Search Events
    @RunWith(MockitoJUnitRunner.class)
    public static class ListSearchEventTest {

        @Mock private FirebaseRepository mockRepository;
        @Mock private EventService.EventListCallback mockEventListCallback;

        private EventService eventService;

        @Before
        public void setUp() {
            MockitoAnnotations.openMocks(this);
            eventService = new EventService(mockRepository);
        }

        @Test
        public void listEvents_callRepository() {
            eventService.listEvents(mockEventListCallback);
            verify(mockRepository).getFilteredEvents(eq(null), any());
        }

        @Test
        public void searchEvents_nullFilters_callRepository() {
            eventService.searchEvents(null, mockEventListCallback);
            verify(mockRepository).getFilteredEvents(eq(null), any());
        }

        @Test
        public void searchEvents_withFilters_callRepository() {
            Map<String, String> filters = new HashMap<>();
            filters.put("category", "Comedy");
            eventService.searchEvents(filters, mockEventListCallback);
            verify(mockRepository).getFilteredEvents(eq(filters), any());
        }
    }
}