package com.example.soen345_project.domain.services

import com.example.soen345_project.data.FirebaseRepository
import com.example.soen345_project.domain.models.Event
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mockito.doAnswer
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import java.util.Date

class EventServiceEpic3Test {

    private lateinit var repository: FirebaseRepository
    private lateinit var eventService: EventService

    @Before
    fun setup() {
        repository = mock(FirebaseRepository::class.java)
        eventService = EventService(repository)
    }

    @Test
    fun listEvents_returnsCorrectData() {
        val mockEvents = listOf(Event("Title 1", Date(), "Loc 1", "Cat 1", 100))
        var resultEvents: List<Event>? = null

        doAnswer { invocation ->
            val callback = invocation.getArgument<EventService.EventListCallback>(1)
            callback.onSuccess(mockEvents)
            null
        }.`when`(repository).getFilteredEvents(eq(null), any())

        eventService.listEvents(object : EventService.EventListCallback {
            override fun onSuccess(events: List<Event>) {
                resultEvents = events
            }
            override fun onFailure(e: Exception?) {}
        })

        assertEquals(mockEvents, resultEvents)
    }

    @Test
    fun searchEvents_appliesKeywordFilteringCorrectly() {
        val mockEvents = listOf(Event("Title 1", Date(), "Loc 1", "Cat 1", 100))
        var resultEvents: List<Event>? = null
        val filters = mapOf("keyword" to "Title")

        doAnswer { invocation ->
            val callback = invocation.getArgument<EventService.EventListCallback>(1)
            callback.onSuccess(mockEvents)
            null
        }.`when`(repository).getFilteredEvents(eq(filters), any())

        eventService.searchEvents(filters, object : EventService.EventListCallback {
            override fun onSuccess(events: List<Event>) {
                resultEvents = events
            }
            override fun onFailure(e: Exception?) {}
        })

        assertEquals(mockEvents, resultEvents)
    }

    @Test
    fun searchEvents_filterLogic_singleFilter() {
        val filters = mapOf("category" to "Music")
        eventService.searchEvents(filters, mock(EventService.EventListCallback::class.java))
        verify(repository).getFilteredEvents(eq(filters), any())
    }

    @Test
    fun searchEvents_filterLogic_multipleFiltersCombined() {
        val filters = mapOf("category" to "Music", "location" to "Venue")
        eventService.searchEvents(filters, mock(EventService.EventListCallback::class.java))
        verify(repository).getFilteredEvents(eq(filters), any())
    }

    @Test
    fun searchEvents_filterLogic_emptyFilters() {
        val filters = emptyMap<String, String>()
        eventService.searchEvents(filters, mock(EventService.EventListCallback::class.java))
        verify(repository).getFilteredEvents(eq(filters), any())
    }
}
