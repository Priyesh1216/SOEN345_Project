package com.example.soen345_project.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.soen345_project.domain.models.Event
import com.example.soen345_project.domain.models.FilterCriteria
import com.example.soen345_project.domain.services.EventService
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mockito.doAnswer
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import java.util.Date

class EventViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    private lateinit var eventService: EventService
    private lateinit var viewModel: EventViewModel
    private lateinit var observer: Observer<List<Event>>

    @Before
    fun setup() {
        eventService = mock(EventService::class.java)
        viewModel = EventViewModel(eventService)
        observer = mock(Observer::class.java) as Observer<List<Event>>
        viewModel.events.observeForever(observer)
    }

    @Test
    fun loadEvents_updatesStateCorrectly() {
        val mockEvents = listOf(Event("Title", Date(), "Loc", "Cat", 100))
        
        doAnswer { invocation ->
            val callback = invocation.getArgument<EventService.EventListCallback>(0)
            callback.onSuccess(mockEvents)
            null
        }.`when`(eventService).listEvents(any())

        viewModel.loadEvents()

        verify(observer).onChanged(mockEvents)
        assertEquals(mockEvents, viewModel.events.value)
    }

    @Test
    fun searchEvents_updatesState() {
        val mockEvents = listOf(Event("Title", Date(), "Loc", "Cat", 100))
        val filters = mapOf("keyword" to "Title")

        doAnswer { invocation ->
            val callback = invocation.getArgument<EventService.EventListCallback>(1)
            callback.onSuccess(mockEvents)
            null
        }.`when`(eventService).searchEvents(eq(filters), any())

        viewModel.searchEvents(filters)

        verify(observer).onChanged(mockEvents)
        assertEquals(mockEvents, viewModel.events.value)
    }

    @Test
    fun applyFilters_updatesState() {
        val mockEvents = listOf(Event("Title", Date(), "Loc", "Cat", 100))
        val criteria = FilterCriteria(date = "2026-03-25", location = "Montreal", category = "Music")
        
        val expectedFilters = mapOf(
            "dateFrom" to "2026-03-25",
            "dateTo" to "2026-03-25",
            "location" to "Montreal",
            "category" to "Music"
        )

        doAnswer { invocation ->
            val callback = invocation.getArgument<EventService.EventListCallback>(1)
            callback.onSuccess(mockEvents)
            null
        }.`when`(eventService).searchEvents(eq(expectedFilters), any())

        viewModel.applyFilters(criteria)

        verify(observer).onChanged(mockEvents)
        assertEquals(mockEvents, viewModel.events.value)
    }
}
