package com.example.soen345_project.domain.services

import com.example.soen345_project.data.FirebaseRepository
import com.example.soen345_project.data.FirebaseRepository.*
import com.example.soen345_project.domain.models.Reservation
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class ReservationServiceTest {

    @Mock
    private lateinit var mockRepository: FirebaseRepository

    @Mock
    private lateinit var mockNotifService: NotifService

    private lateinit var service: ReservationService

    @Before
    fun setUp() {
        service = ReservationService(mockRepository, mockNotifService)
    }

    // --- reserveTickets ---

    @Test
    fun reserveTickets_transactionSucceeds_createsReservationAndNotifies() {
        val txCaptor = ArgumentCaptor.forClass(TransactionCallback::class.java)
        val createCaptor = ArgumentCaptor.forClass(CreateReservationCallback::class.java)
        val getUserCaptor = ArgumentCaptor.forClass(GetUserCallback::class.java)
        val getEventCaptor = ArgumentCaptor.forClass(GetEventCallback::class.java)

        var successCalled = false
        var capturedReservation: Reservation? = null

        service.reserveTickets("user1", "event1", 2, object : ReservationService.ReservationCallback {
            override fun onSuccess(reservation: Reservation) {
                successCalled = true
                capturedReservation = reservation
            }
            override fun onFailure(e: Exception) {}
        })

        // Reserving quantity=2 must decrement seats → delta = -2
        verify(mockRepository).transaction(eq("event1"), eq(-2), txCaptor.capture())
        txCaptor.value.onResult(true)

        verify(mockRepository).createReservation(any(), createCaptor.capture())
        createCaptor.value.onSuccess("resId123")

        verify(mockRepository).getUser(eq("user1"), getUserCaptor.capture())
        val mockUser = com.example.soen345_project.domain.models.User()
        mockUser.setEmail("user1@test.com")
        getUserCaptor.value.onSuccess(mockUser)
        println("interactions: ${mockingDetails(mockRepository).invocations}")

        verify(mockRepository).getEvent(eq("event1"), getEventCaptor.capture())
        val mockEvent = com.example.soen345_project.domain.models.Event(
            "Test Event", java.util.Date(), "Montreal", "Music", 10
        )
        getEventCaptor.value.onSuccess(mockEvent)

        assertEquals(true, successCalled)
        assertEquals("resId123", capturedReservation?.id)
        verify(mockNotifService).sendConfirmationMsg(eq("user1@test.com"), anyString())
    }

    @Test
    fun reserveTickets_transactionFails_propagatesFailure() {
        val txCaptor = ArgumentCaptor.forClass(TransactionCallback::class.java)
        var failureMessage: String? = null

        service.reserveTickets("user1", "event1", 2, object : ReservationService.ReservationCallback {
            override fun onSuccess(reservation: Reservation) {}
            override fun onFailure(e: Exception) { failureMessage = e.message }
        })

        verify(mockRepository).transaction(eq("event1"), eq(-2), txCaptor.capture())
        txCaptor.value.onResult(false)

        assertEquals("Not enough seats available", failureMessage)
        verify(mockRepository, never()).createReservation(any(), any())
    }

    @Test
    fun reserveTickets_createReservationFails_propagatesFailure() {
        val txCaptor = ArgumentCaptor.forClass(TransactionCallback::class.java)
        val createCaptor = ArgumentCaptor.forClass(CreateReservationCallback::class.java)
        var failureCalled = false

        service.reserveTickets("user1", "event1", 2, object : ReservationService.ReservationCallback {
            override fun onSuccess(reservation: Reservation) {}
            override fun onFailure(e: Exception) { failureCalled = true }
        })

        verify(mockRepository).transaction(eq("event1"), eq(-2), txCaptor.capture())
        txCaptor.value.onResult(true)

        verify(mockRepository).createReservation(any(), createCaptor.capture())
        createCaptor.value.onFailure(Exception("DB write error"))

        assertEquals(true, failureCalled)
        verify(mockNotifService, never()).sendConfirmationMsg(anyString(), anyString())
    }

    // --- listReservations ---

    @Test
    fun listReservations_delegatesToRepository() {
        val callback = mock(ReservationService.ReservationListCallback::class.java)
        service.listReservations("user1", callback)
        verify(mockRepository).getReservationsByUser("user1", callback)
    }

    // --- cancelReservation ---

    @Test
    fun cancelReservation_reservationFound_restoresSeatsAndDeletes() {
        val getCaptor = ArgumentCaptor.forClass(GetReservationCallback::class.java)
        val txCaptor = ArgumentCaptor.forClass(TransactionCallback::class.java)
        val deleteCaptor = ArgumentCaptor.forClass(SimpleCallback::class.java)
        val getUserCaptor = ArgumentCaptor.forClass(GetUserCallback::class.java)
        val getEventCaptor = ArgumentCaptor.forClass(GetEventCallback::class.java)

        val reservation = Reservation("event1", "user1", 2)
        reservation.id = "res1"

        var successCalled = false
        var capturedReservation: Reservation? = null

        service.cancelReservation("user1", "res1", object : ReservationService.ReservationCallback {
            override fun onSuccess(r: Reservation) {
                successCalled = true
                capturedReservation = r
            }
            override fun onFailure(e: Exception) {}
        })

        verify(mockRepository).getReservation(eq("res1"), getCaptor.capture())
        getCaptor.value.onSuccess(reservation)

        // Cancelling quantity=2 must restore seats → delta = +2
        verify(mockRepository).transaction(eq("event1"), eq(2), txCaptor.capture())
        txCaptor.value.onResult(true)

        verify(mockRepository).deleteReservation(eq("res1"), deleteCaptor.capture())
        deleteCaptor.value.onSuccess()

        // Simulate getUser callback
        verify(mockRepository).getUser(eq("user1"), getUserCaptor.capture())
        val mockUser = com.example.soen345_project.domain.models.User()
        mockUser.setEmail("user1@test.com")
        getUserCaptor.value.onSuccess(mockUser)
        println("interactions: ${mockingDetails(mockRepository).invocations}")

        // Simulate getEvent callback
        verify(mockRepository).getEvent(eq("event1"), getEventCaptor.capture())
        val mockEvent = com.example.soen345_project.domain.models.Event(
            "Test Event", java.util.Date(), "Montreal", "Music", 10
        )
        getEventCaptor.value.onSuccess(mockEvent)

        assertEquals(true, successCalled)
        assertEquals(Reservation.Status.CANCELLED, capturedReservation?.status)
        verify(mockNotifService).sendCancellationMsg(eq("user1@test.com"), anyString())
    }

    @Test
    fun cancelReservation_reservationNotFound_propagatesFailure() {
        val getCaptor = ArgumentCaptor.forClass(GetReservationCallback::class.java)
        var failureCalled = false

        service.cancelReservation("user1", "res1", object : ReservationService.ReservationCallback {
            override fun onSuccess(reservation: Reservation) {}
            override fun onFailure(e: Exception) { failureCalled = true }
        })

        verify(mockRepository).getReservation(eq("res1"), getCaptor.capture())
        getCaptor.value.onFailure(Exception("Not found"))

        assertEquals(true, failureCalled)
        verify(mockRepository, never()).transaction(anyString(), anyInt(), any())
        verify(mockRepository, never()).deleteReservation(anyString(), any())
    }

    @Test
    fun cancelReservation_deleteFails_propagatesFailure() {
        val getCaptor = ArgumentCaptor.forClass(GetReservationCallback::class.java)
        val txCaptor = ArgumentCaptor.forClass(TransactionCallback::class.java)
        val deleteCaptor = ArgumentCaptor.forClass(SimpleCallback::class.java)

        val reservation = Reservation("event1", "user1", 1)
        reservation.id = "res3"

        var failureCalled = false

        service.cancelReservation("user1", "res3", object : ReservationService.ReservationCallback {
            override fun onSuccess(r: Reservation) {}
            override fun onFailure(e: Exception) { failureCalled = true }
        })

        verify(mockRepository).getReservation(eq("res3"), getCaptor.capture())
        getCaptor.value.onSuccess(reservation)

        verify(mockRepository).transaction(eq("event1"), eq(1), txCaptor.capture())
        txCaptor.value.onResult(true)

        verify(mockRepository).deleteReservation(eq("res3"), deleteCaptor.capture())
        deleteCaptor.value.onFailure(Exception("DB delete error"))

        assertEquals(true, failureCalled)
        verify(mockNotifService, never()).sendCancellationMsg(anyString(), anyString())
    }

    @Test
    fun cancelReservation_statusSetToCancelled() {
        val getCaptor = ArgumentCaptor.forClass(GetReservationCallback::class.java)
        val txCaptor = ArgumentCaptor.forClass(TransactionCallback::class.java)
        val deleteCaptor = ArgumentCaptor.forClass(SimpleCallback::class.java)

        val reservation = Reservation("event1", "user1", 3)
        reservation.id = "res2"

        service.cancelReservation("user1", "res2", object : ReservationService.ReservationCallback {
            override fun onSuccess(r: Reservation) {}
            override fun onFailure(e: Exception) {}
        })

        verify(mockRepository).getReservation(eq("res2"), getCaptor.capture())
        getCaptor.value.onSuccess(reservation)

        verify(mockRepository).transaction(eq("event1"), eq(3), txCaptor.capture())
        txCaptor.value.onResult(true)

        verify(mockRepository).deleteReservation(eq("res2"), deleteCaptor.capture())
        deleteCaptor.value.onSuccess()

        assertEquals(Reservation.Status.CANCELLED, reservation.status)
    }
}