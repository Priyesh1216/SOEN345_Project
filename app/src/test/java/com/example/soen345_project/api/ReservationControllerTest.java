package com.example.soen345_project.api;

import static org.mockito.Mockito.*;

import com.example.soen345_project.domain.services.ReservationService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ReservationControllerTest {

    @Mock
    private ReservationService mockService;

    private ReservationController controller;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        controller = new ReservationController(mockService);
    }

    @Test
    public void reserveTickets_delegatesToServiceWithCorrectArgs() {
        ReservationService.ReservationCallback callback = mock(ReservationService.ReservationCallback.class);
        controller.reserveTickets("user1", "event1", 2, callback);
        verify(mockService).reserveTickets("user1", "event1", 2, callback);
    }

    @Test
    public void listReservations_delegatesToService() {
        ReservationService.ReservationListCallback callback = mock(ReservationService.ReservationListCallback.class);
        controller.listReservations("user1", callback);
        verify(mockService).listReservations("user1", callback);
    }

    @Test
    public void cancelReservation_delegatesToServiceWithCorrectArgs() {
        ReservationService.ReservationCallback callback = mock(ReservationService.ReservationCallback.class);
        controller.cancelReservation("user1", "res1", callback);
        verify(mockService).cancelReservation("user1", "res1", callback);
    }

    @Test
    public void reserveTickets_quantityOne_correctDelegation() {
        ReservationService.ReservationCallback callback = mock(ReservationService.ReservationCallback.class);
        controller.reserveTickets("userA", "eventB", 1, callback);
        verify(mockService).reserveTickets("userA", "eventB", 1, callback);
        verifyNoMoreInteractions(mockService);
    }

    @Test
    public void cancelReservation_doesNotCallReserveTickets() {
        ReservationService.ReservationCallback callback = mock(ReservationService.ReservationCallback.class);
        controller.cancelReservation("user1", "res1", callback);
        verify(mockService, never()).reserveTickets(anyString(), anyString(), anyInt(), any());
    }
}