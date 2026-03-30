package com.example.soen345_project.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.soen345_project.R
import com.example.soen345_project.data.FirebaseRepository
import com.example.soen345_project.domain.models.Event
import com.example.soen345_project.domain.models.FilterCriteria
import com.example.soen345_project.domain.services.EventService
import com.example.soen345_project.ui.adapters.FirebaseEventAdapter
import com.google.firebase.auth.FirebaseAuth
import android.content.Intent

class EventListActivity : AppCompatActivity() {

    private lateinit var viewModel: EventViewModel
    private lateinit var adapter: FirebaseEventAdapter
    private val eventList = mutableListOf<Event>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_list)

        viewModel = EventViewModel(EventService(FirebaseRepository()))

        val rvEvents = findViewById<RecyclerView>(R.id.rvEvents)
        rvEvents.layoutManager = LinearLayoutManager(this)

        adapter = FirebaseEventAdapter(eventList) { event ->
            // handle click 
        }
        rvEvents.adapter = adapter

        viewModel.events.observe(this) { events ->
            eventList.clear()
            eventList.addAll(events)
            adapter.notifyDataSetChanged()
        }

        val etSearch = findViewById<EditText>(R.id.etSearch)
        val etFilterDate = findViewById<EditText>(R.id.etFilterDate)
        val etFilterLocation = findViewById<EditText>(R.id.etFilterLocation)
        val etFilterCategory = findViewById<EditText>(R.id.etFilterCategory)
        val btnApplyFilter = findViewById<Button>(R.id.btnApplyFilter)

        etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val keyword = s.toString()
                if (keyword.isNotEmpty()) {
                    viewModel.searchEvents(mapOf("keyword" to keyword))
                } else {
                    viewModel.loadEvents()
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        btnApplyFilter.setOnClickListener {
            val date = etFilterDate.text.toString().takeIf { it.isNotEmpty() }
            val location = etFilterLocation.text.toString().takeIf { it.isNotEmpty() }
            val category = etFilterCategory.text.toString().takeIf { it.isNotEmpty() }
            
            val criteria = FilterCriteria(date, location, category)
            viewModel.applyFilters(criteria)
        }

        val btnLogout = findViewById<Button>(R.id.btnLogout)
        btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        viewModel.loadEvents()
    }
}
