package com.example.soen345_project.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.soen345_project.R
import com.example.soen345_project.domain.models.Event
import java.text.SimpleDateFormat
import java.util.Locale

class FirebaseEventAdapter(
    private val events: List<Event>,
    // reservedEventIds: set of eventIds the current user has already reserved
    private val reservedEventIds: Map<String, String>, // eventId -> reservationId
    private val onReserveClick: (Event) -> Unit,
    private val onCancelClick: (Event, String) -> Unit  // event, reservationId
) : RecyclerView.Adapter<FirebaseEventAdapter.EventViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_event, parent, false)
        return EventViewHolder(v)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = events[position]
        holder.tvTitle.text = event.title
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        holder.tvDate.text = event.date?.let { dateFormat.format(it) } ?: ""
        holder.tvLocation.text = event.location ?: ""
        holder.tvCategory.text = event.category ?: ""
        holder.tvSeats.text = "Seats available: ${event.openSeats}"

        val reservationId = reservedEventIds[event.id]
        val isReserved = reservationId != null

        if (isReserved) {
            holder.btnAction.text = "Cancel"
            holder.btnAction.isEnabled = true
            holder.btnAction.setBackgroundColor(0xFFE53935.toInt()) // red
            holder.btnAction.setOnClickListener {
                onCancelClick(event, reservationId!!)
            }
        } else {
            val hasSeats = event.openSeats > 0 && event.isActive
            holder.btnAction.text = if (hasSeats) "Reserve" else "Full"
            holder.btnAction.isEnabled = hasSeats
            holder.btnAction.setBackgroundColor(0xFF6200EE.toInt()) // purple
            holder.btnAction.setOnClickListener {
                onReserveClick(event)
            }
        }
    }

    override fun getItemCount(): Int = events.size

    class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.tvEventTitle)
        val tvDate: TextView = itemView.findViewById(R.id.tvEventDate)
        val tvLocation: TextView = itemView.findViewById(R.id.tvEventLocation)
        val tvCategory: TextView = itemView.findViewById(R.id.tvEventCategory)
        val tvSeats: TextView = itemView.findViewById(R.id.tvEventSeats)
        val btnAction: Button = itemView.findViewById(R.id.btnReserveEvent)
    }
}