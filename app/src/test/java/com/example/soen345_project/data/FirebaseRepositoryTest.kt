package com.example.soen345_project.data

import com.example.soen345_project.domain.models.Event
import com.example.soen345_project.domain.services.EventService
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.MockedStatic
import org.mockito.Mockito.mock
import org.mockito.Mockito.mockStatic
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import java.util.Date

class FirebaseRepositoryTest {

    private lateinit var firebaseDatabaseMockStatic: MockedStatic<FirebaseDatabase>
    private lateinit var mockDatabase: FirebaseDatabase
    private lateinit var mockReference: DatabaseReference
    private lateinit var mockEventsReference: DatabaseReference

    @Before
    fun setUp() {
        mockDatabase = mock(FirebaseDatabase::class.java)
        mockReference = mock(DatabaseReference::class.java)
        mockEventsReference = mock(DatabaseReference::class.java)

        firebaseDatabaseMockStatic = mockStatic(FirebaseDatabase::class.java)
        firebaseDatabaseMockStatic.`when`<FirebaseDatabase> { FirebaseDatabase.getInstance() }.thenReturn(mockDatabase)

        `when`(mockDatabase.reference).thenReturn(mockReference)
        `when`(mockReference.child("users")).thenReturn(mock(DatabaseReference::class.java))
        `when`(mockReference.child("events")).thenReturn(mockEventsReference)
        `when`(mockReference.child("reservations")).thenReturn(mock(DatabaseReference::class.java))
    }

    @After
    fun tearDown() {
        firebaseDatabaseMockStatic.close()
    }

    @Test
    fun getFilteredEvents_withKeyword_filtersSuccessfully() {
        val repository = FirebaseRepository()

        val mockSnapshot = mock(DataSnapshot::class.java)
        val mockChild1 = mock(DataSnapshot::class.java)
        val mockChild2 = mock(DataSnapshot::class.java)
        val mockChild3 = mock(DataSnapshot::class.java)

        val ev1 = Event("Rock Concert", Date(), "Montreal", "Music", 100)
        val ev2 = Event("Jazz Festival", Date(), "Montreal", "Music", 100)
        val ev3 = Event("Tech Talk", Date(), "Toronto", "Technology", 100)

        // Adding an event where only category matches keyword
        val ev4 = Event("Indie Show", Date(), "Montreal", "Indie Rock", 100)
        val mockChild4 = mock(DataSnapshot::class.java)

        `when`(mockChild1.getValue(Event::class.java)).thenReturn(ev1)
        `when`(mockChild1.key).thenReturn("ev1")

        `when`(mockChild2.getValue(Event::class.java)).thenReturn(ev2)
        `when`(mockChild2.key).thenReturn("ev2")

        `when`(mockChild3.getValue(Event::class.java)).thenReturn(ev3)
        `when`(mockChild3.key).thenReturn("ev3")

        `when`(mockChild4.getValue(Event::class.java)).thenReturn(ev4)
        `when`(mockChild4.key).thenReturn("ev4")

        `when`(mockSnapshot.children).thenReturn(listOf(mockChild1, mockChild2, mockChild3, mockChild4))

        val filters = mutableMapOf<String, String>()
        filters["keyword"] = "rock"

        var returnedList: List<Event>? = null

        repository.getFilteredEvents(filters, object : EventService.EventListCallback {
            override fun onSuccess(events: List<Event>) {
                returnedList = events
            }
            override fun onFailure(e: Exception) {}
        })

        // Capture the listener
        val captor = ArgumentCaptor.forClass(ValueEventListener::class.java)
        verify(mockEventsReference).addListenerForSingleValueEvent(captor.capture())

        // Trigger the callback manually
        captor.value.onDataChange(mockSnapshot)

        // "rock" should match ev1 Title ("Rock Concert") and ev4 Category ("Indie Rock")
        assertEquals(2, returnedList!!.size)
        assertEquals("ev1", returnedList!![0].id)
        assertEquals("ev4", returnedList!![1].id)
    }

    @Test
    fun getFilteredEvents_keywordCaseNull() {
        // Just to ensure all branches (null checking) are covered
        val repository = FirebaseRepository()

        val mockSnapshot = mock(DataSnapshot::class.java)
        val mockChild1 = mock(DataSnapshot::class.java)

        // Event with null title and category
        val evNull = Event()

        `when`(mockChild1.getValue(Event::class.java)).thenReturn(evNull)
        `when`(mockChild1.key).thenReturn("evNull")
        `when`(mockSnapshot.children).thenReturn(listOf(mockChild1))

        val filters = mutableMapOf<String, String>()
        filters["keyword"] = "rock"

        var returnedList: List<Event>? = null
        repository.getFilteredEvents(filters, object : EventService.EventListCallback {
            override fun onSuccess(events: List<Event>) { returnedList = events }
            override fun onFailure(e: Exception) {}
        })

        val captor = ArgumentCaptor.forClass(ValueEventListener::class.java)
        verify(mockEventsReference).addListenerForSingleValueEvent(captor.capture())
        captor.value.onDataChange(mockSnapshot)

        assertEquals(0, returnedList!!.size)
    }
}
