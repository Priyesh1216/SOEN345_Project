package com.example.soen345_project.api;

import com.example.soen345_project.domain.services.ReservationService;

public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    public void reserveTickets(String userId, String eventId, int quantity,
                               ReservationService.ReservationCallback callback) {
        reservationService.reserveTickets(userId, eventId, quantity, callback);
    }

    public void listReservations(String userId, ReservationService.ReservationListCallback callback) {
        reservationService.listReservations(userId, callback);
    }

    public void cancelReservation(String userId, String reservationId,
                                  ReservationService.ReservationCallback callback) {
        reservationService.cancelReservation(userId, reservationId, callback);
    }
}