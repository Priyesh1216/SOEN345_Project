package com.example.soen345_project.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.soen345_project.R
import com.example.soen345_project.domain.models.Event
import java.text.SimpleDateFormat
import java.util.Locale

class FirebaseEventAdapter(
    private val events: List<Event>,
    private val listener: (Event) -> Unit
) : RecyclerView.Adapter<FirebaseEventAdapter.EventViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_event, parent, false)
        return EventViewHolder(v)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val currentItem = events[position]
        holder.tvTitle.text = currentItem.title
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        holder.tvDate.text = currentItem.date?.let { dateFormat.format(it) } ?: ""
        holder.tvLocation.text = currentItem.location

        holder.itemView.setOnClickListener {
            listener(currentItem)
        }
    }

    override fun getItemCount(): Int = events.size

    class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.tvEventTitle)
        val tvDate: TextView = itemView.findViewById(R.id.tvEventDate)
        val tvLocation: TextView = itemView.findViewById(R.id.tvEventLocation)
    }
}
