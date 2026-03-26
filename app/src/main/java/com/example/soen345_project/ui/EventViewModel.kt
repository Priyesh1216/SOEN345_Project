package com.example.soen345_project.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.soen345_project.domain.models.Event
import com.example.soen345_project.domain.models.FilterCriteria
import com.example.soen345_project.domain.services.EventService

class EventViewModel(private val eventService: EventService) : ViewModel() {

    private val _events = MutableLiveData<List<Event>>()
    val events: LiveData<List<Event>> get() = _events

    fun loadEvents() {
        eventService.listEvents(object : EventService.EventListCallback {
            override fun onSuccess(events: List<Event>) {
                _events.postValue(events)
            }
            override fun onFailure(e: Exception) {
                // handle error
            }
        })
    }

    fun searchEvents(filters: Map<String, String>) {
        eventService.searchEvents(filters, object : EventService.EventListCallback {
            override fun onSuccess(events: List<Event>) {
                _events.postValue(events)
            }
            override fun onFailure(e: Exception) {
                // handle error
            }
        })
    }

    fun applyFilters(filters: FilterCriteria) {
        val filterMap = mutableMapOf<String, String>()
        filters.date?.let { 
            // the repository expects dateFrom/dateTo
            filterMap["dateFrom"] = it
            filterMap["dateTo"] = it
        }
        filters.location?.let { filterMap["location"] = it }
        filters.category?.let { filterMap["category"] = it }
        
        searchEvents(filterMap)
    }
}
