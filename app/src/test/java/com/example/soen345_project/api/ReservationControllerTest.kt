package com.example.soen345_project.api

import com.example.soen345_project.domain.models.Reservation
import com.example.soen345_project.domain.services.ReservationService
import com.example.soen345_project.domain.services.ReservationService.ReservationCallback
import com.example.soen345_project.domain.services.ReservationService.ReservationListCallback
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class ReservationControllerTest {

    @Mock
    private lateinit var mockService: ReservationService

    private lateinit var controller: ReservationController

    @Before
    fun setUp() {
        controller = ReservationController(mockService)
    }

    @Test
    fun `reserveTickets - delegates to service with correct args`() {
        val callback = mock(ReservationCallback::class.java)
        controller.reserveTickets("user1", "event1", 2, callback)
        verify(mockService).reserveTickets("user1", "event1", 2, callback)
    }

    @Test
    fun `listReservations - delegates to service`() {
        val callback = mock(ReservationListCallback::class.java)
        controller.listReservations("user1", callback)
        verify(mockService).listReservations("user1", callback)
    }

    @Test
    fun `cancelReservation - delegates to service with correct args`() {
        val callback = mock(ReservationCallback::class.java)
        controller.cancelReservation("user1", "res1", callback)
        verify(mockService).cancelReservation("user1", "res1", callback)
    }

    @Test
    fun `reserveTickets - quantity 1 - correct delegation`() {
        val callback = mock(ReservationCallback::class.java)
        controller.reserveTickets("userA", "eventB", 1, callback)
        verify(mockService).reserveTickets("userA", "eventB", 1, callback)
        verifyNoMoreInteractions(mockService)
    }

    @Test
    fun `cancelReservation - does not call reserveTickets`() {
        val callback = mock(ReservationCallback::class.java)
        controller.cancelReservation("user1", "res1", callback)
        verify(mockService, never()).reserveTickets(anyString(), anyString(), anyInt(), any())
    }
}