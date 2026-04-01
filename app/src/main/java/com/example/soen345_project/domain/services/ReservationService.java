package com.example.soen345_project.domain.services;

import com.example.soen345_project.data.FirebaseRepository;
import com.example.soen345_project.domain.models.Reservation;

import java.util.List;

public class ReservationService {

    private final FirebaseRepository repository;
    private final NotifService notifService;

    public ReservationService(FirebaseRepository repository, NotifService notifService) {
        this.repository = repository;
        this.notifService = notifService;
    }

    public void reserveTickets(String userId, String eventId, int quantity,
                               ReservationCallback callback) {
        // Pass -quantity to decrement seats atomically
        repository.transaction(eventId, -quantity, success -> {
            if (success) {
                Reservation reservation = new Reservation(eventId, userId, quantity);
                repository.createReservation(reservation, new FirebaseRepository.CreateReservationCallback() {
                    @Override
                    public void onSuccess(String reservationId) {
                        reservation.setId(reservationId);
                        notifService.sendConfirmationMsg(userId,
                                "Reservation for event " + eventId + " is confirmed.");
                        callback.onSuccess(reservation);
                    }
                    @Override
                    public void onFailure(Exception e) {
                        callback.onFailure(e);
                    }
                });
            } else {
                callback.onFailure(new Exception("Not enough seats available"));
            }
        });
    }

    public void listReservations(String userId, ReservationListCallback callback) {
        repository.getReservationsByUser(userId, callback);
    }

    public void cancelReservation(String userId, String reservationId,
                                  ReservationCallback callback) {
        repository.getReservation(reservationId, new FirebaseRepository.GetReservationCallback() {
            @Override
            public void onSuccess(Reservation reservation) {
                // Pass +quantity to restore seats atomically
                repository.transaction(reservation.getEventId(), reservation.getQuantity(), success -> {
                    repository.deleteReservation(reservationId, new FirebaseRepository.SimpleCallback() {
                        @Override
                        public void onSuccess() {
                            reservation.cancel();
                            notifService.sendCancellationMsg(userId,
                                    "Reservation " + reservationId + " has been cancelled.");
                            callback.onSuccess(reservation);
                        }
                        @Override
                        public void onFailure(Exception e) {
                            callback.onFailure(e);
                        }
                    });
                });
            }
            @Override
            public void onFailure(Exception e) {
                callback.onFailure(e);
            }
        });
    }

    public interface ReservationCallback {
        void onSuccess(Reservation reservation);
        void onFailure(Exception e);
    }

    public interface ReservationListCallback {
        void onSuccess(List<Reservation> reservations);
        void onFailure(Exception e);
    }
}