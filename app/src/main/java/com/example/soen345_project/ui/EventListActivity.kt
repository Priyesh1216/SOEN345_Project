package com.example.soen345_project.ui

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.NumberPicker
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.soen345_project.R
import com.example.soen345_project.api.ReservationController
import com.example.soen345_project.data.FirebaseRepository
import com.example.soen345_project.domain.models.Event
import com.example.soen345_project.domain.models.FilterCriteria
import com.example.soen345_project.domain.models.Reservation
import com.example.soen345_project.domain.services.EventService
import com.example.soen345_project.domain.services.NotifService
import com.example.soen345_project.domain.services.ReservationService
import com.example.soen345_project.ui.adapters.FirebaseEventAdapter
import com.google.firebase.auth.FirebaseAuth

class EventListActivity : AppCompatActivity() {

    private lateinit var viewModel: EventViewModel
    private lateinit var reservationController: ReservationController
    private lateinit var repository: FirebaseRepository

    private val eventList = mutableListOf<Event>()
    private val reservedEventIds = mutableMapOf<String, String>() // eventId -> reservationId

    private lateinit var adapter: FirebaseEventAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_list)

        repository = FirebaseRepository()
        viewModel = EventViewModel(EventService(repository))
        reservationController = ReservationController(
            ReservationService(repository, NotifService())
        )

        val rvEvents = findViewById<RecyclerView>(R.id.rvEvents)
        rvEvents.layoutManager = LinearLayoutManager(this)

        adapter = FirebaseEventAdapter(
            eventList,
            reservedEventIds,
            isAdmin = false,
            onReserveClick = { event -> showReserveDialog(event) },
            onCancelClick = { event, reservationId -> showCancelDialog(event, reservationId) },
            onEditClick = { event ->
                val intent = Intent(this, AddEditEventActivity::class.java)
                intent.putExtra("adminId", FirebaseAuth.getInstance().currentUser?.uid)
                intent.putExtra("eventId", event.id)
                intent.putExtra("isEdit", true)
                startActivity(intent)
            },
            onAdminCancelClick = { event ->
                showAdminCancelDialog(event)
            },
            onViewReservationsClick = { event ->
                val intent = Intent(this, ViewReservationsActivity::class.java)
                intent.putExtra("eventId", event.id)
                startActivity(intent)
            }
        )
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
                if (keyword.isNotEmpty()) viewModel.searchEvents(mapOf("keyword" to keyword))
                else viewModel.loadEvents()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        btnApplyFilter.setOnClickListener {
            val criteria = FilterCriteria(
                date = etFilterDate.text.toString().takeIf { it.isNotEmpty() },
                location = etFilterLocation.text.toString().takeIf { it.isNotEmpty() },
                category = etFilterCategory.text.toString().takeIf { it.isNotEmpty() }
            )
            viewModel.applyFilters(criteria)
        }

        val bottomNavigation = findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(R.id.bottomNavigation)
        bottomNavigation.selectedItemId = R.id.nav_home
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true
                R.id.nav_tickets -> {
                    startActivity(Intent(this, MyTicketsActivity::class.java))
                    true
                }
                else -> false
            }
        }

        val btnCreateEvent = findViewById<Button>(R.id.btnCreateEvent)
        btnCreateEvent.setOnClickListener {
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            val intent = Intent(this, AddEditEventActivity::class.java)
            intent.putExtra("adminId", userId)
            intent.putExtra("isEdit", false)
            startActivity(intent)
        }

        // Check if user is admin to show Create Event button
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        if (currentUserId != null) {
            repository.getUser(currentUserId, object : FirebaseRepository.GetUserCallback {
                override fun onSuccess(user: com.example.soen345_project.domain.models.User) {
                    if (user.isAdmin) {
                        runOnUiThread {
                            btnCreateEvent.visibility = android.view.View.VISIBLE
                            adapter.isAdmin = true
                            adapter.notifyDataSetChanged()
                        }
                    }
                }
                override fun onFailure(e: Exception) {
                    // Silently fail, button remains GONE
                }
            })
        }

        findViewById<Button>(R.id.btnLogout).setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        loadUserReservations()
        viewModel.loadEvents()
    }

    override fun onResume() {
        super.onResume()
        loadUserReservations()
        viewModel.loadEvents()
    }

    private fun loadUserReservations() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        reservationController.listReservations(userId, object : ReservationService.ReservationListCallback {
            override fun onSuccess(reservations: List<Reservation>) {
                reservedEventIds.clear()
                for (r in reservations) {
                    if (r.status == Reservation.Status.CONFIRMED && r.eventId != null) {
                        reservedEventIds[r.eventId] = r.id
                    }
                }
                runOnUiThread { adapter.notifyDataSetChanged() }
            }
            override fun onFailure(e: Exception) {}
        })
    }

    private fun showReserveDialog(event: Event) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            Toast.makeText(this, "Please log in to reserve tickets.", Toast.LENGTH_SHORT).show()
            return
        }

        val maxSeats = event.openSeats.coerceAtMost(10)

        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_reserve, null)
        dialogView.findViewById<TextView>(R.id.tvDialogEventName).text = event.title

        val numberPicker = dialogView.findViewById<NumberPicker>(R.id.numberPickerSeats)
        numberPicker.minValue = 1
        numberPicker.maxValue = maxSeats
        numberPicker.value = 1

        AlertDialog.Builder(this)
            .setTitle("Reserve Tickets")
            .setView(dialogView)
            .setPositiveButton("Confirm") { _, _ ->
                reserveTicket(userId, event, numberPicker.value)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showCancelDialog(event: Event, reservationId: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        AlertDialog.Builder(this)
            .setTitle("Cancel Reservation")
            .setMessage("Cancel your reservation for \"${event.title}\"?")
            .setPositiveButton("Yes, cancel") { _, _ -> cancelTicket(userId, event, reservationId) }
            .setNegativeButton("Keep it", null)
            .show()
    }

    private fun reserveTicket(userId: String, event: Event, quantity: Int) {
        val eventId = event.id ?: return
        reservationController.reserveTickets(userId, eventId, quantity,
            object : ReservationService.ReservationCallback {
                override fun onSuccess(reservation: Reservation) {
                    runOnUiThread {
                        reservedEventIds[eventId] = reservation.id
                        Toast.makeText(
                            this@EventListActivity,
                            "✓ Reserved $quantity seat(s)!",
                            Toast.LENGTH_LONG
                        ).show()
                        viewModel.loadEvents()
                    }
                }
                override fun onFailure(e: Exception) {
                    runOnUiThread {
                        Toast.makeText(
                            this@EventListActivity,
                            "Reservation failed: ${e.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        )
    }

    private fun cancelTicket(userId: String, event: Event, reservationId: String) {
        reservationController.cancelReservation(userId, reservationId,
            object : ReservationService.ReservationCallback {
                override fun onSuccess(reservation: Reservation) {
                    runOnUiThread {
                        reservedEventIds.remove(event.id)
                        Toast.makeText(this@EventListActivity, "Reservation cancelled.", Toast.LENGTH_SHORT).show()
                        viewModel.loadEvents()
                    }
                }
                override fun onFailure(e: Exception) {
                    runOnUiThread {
                        Toast.makeText(
                            this@EventListActivity,
                            "Cancel failed: ${e.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        )
    }

    private fun showAdminCancelDialog(event: Event) {
        AlertDialog.Builder(this)
            .setTitle("Cancel Event")
            .setMessage("Are you sure you want to cancel the event \"${event.title}\"? This will make the event inactive for all users.")
            .setPositiveButton("Yes, Cancel Event") { _, _ ->
                repository.getEvent(event.id, object : FirebaseRepository.GetEventCallback {
                    override fun onSuccess(fetchedEvent: Event) {
                        fetchedEvent.cancelEvent()
                        repository.saveEvent(fetchedEvent, object : EventService.EventCallback {
                            override fun onSuccess(savedEvent: Event) {
                                runOnUiThread {
                                    Toast.makeText(this@EventListActivity, "Event cancelled successfully", Toast.LENGTH_SHORT).show()
                                    viewModel.loadEvents()
                                }
                            }
                            override fun onFailure(e: Exception) {
                                runOnUiThread {
                                    Toast.makeText(this@EventListActivity, "Failed to cancel event: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                            }
                        })
                    }
                    override fun onFailure(e: Exception) {}
                })
            }
            .setNegativeButton("No", null)
            .show()
    }
}