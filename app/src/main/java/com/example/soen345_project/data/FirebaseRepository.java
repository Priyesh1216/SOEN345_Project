package com.example.soen345_project.data;

import com.example.soen345_project.domain.models.Event;
import com.example.soen345_project.domain.models.Reservation;
import com.example.soen345_project.domain.models.User;
import com.example.soen345_project.domain.services.EventService;
import com.example.soen345_project.domain.services.ReservationService;

import java.util.List;
import java.util.Map;

public class FirebaseRepository {

    // --- User ---
    public void getUser(String userId, GetUserCallback callback) {}
    public void saveUser(User user, SimpleCallback callback) {}

    // --- Events ---
    public void getEvent(String eventId, GetEventCallback callback) {}
    public void saveEvent(Event event, EventService.EventCallback callback) {}
    public void getFilteredEvents(Map<String, String> filters, EventService.EventListCallback callback) {}

    // --- Reservations ---
    public void getReservation(String reservationId, GetReservationCallback callback) {}
    public void getReservationsByUser(String userId, ReservationService.ReservationListCallback callback) {}
    public void createReservation(Reservation reservation, CreateReservationCallback callback) {}
    public void transaction(String eventId, int quantity, TransactionCallback callback) {}

    // --- Callbacks ---
    public interface GetUserCallback {
        void onSuccess(User user);
        void onFailure(Exception e);
    }
    public interface GetEventCallback {
        void onSuccess(Event event);
        void onFailure(Exception e);
    }
    public interface GetReservationCallback {
        void onSuccess(Reservation reservation);
        void onFailure(Exception e);
    }
    public interface CreateReservationCallback {
        void onSuccess(String reservationId);
        void onFailure(Exception e);
    }
    public interface TransactionCallback {
        void onResult(boolean success);
    }
    public interface SimpleCallback {
        void onSuccess();
        void onFailure(Exception e);
    }
}