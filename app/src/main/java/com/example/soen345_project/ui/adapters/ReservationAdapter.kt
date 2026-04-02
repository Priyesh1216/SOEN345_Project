package com.example.soen345_project.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.soen345_project.R
import com.example.soen345_project.domain.models.Reservation

class ReservationAdapter(private val reservations: List<Reservation>) :
    RecyclerView.Adapter<ReservationAdapter.ReservationViewHolder>() {

    private val userNames = mutableMapOf<String, String>() // userId -> name/email

    fun updateNames(names: Map<String, String>) {
        userNames.putAll(names)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReservationViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_reservation, parent, false)
        return ReservationViewHolder(v)
    }

    override fun onBindViewHolder(holder: ReservationViewHolder, position: Int) {
        val res = reservations[position]
        val displayName = userNames[res.userId] ?: res.userId
        holder.tvUser.text = "User: $displayName"
        holder.tvQuantity.text = "Quantity: ${res.quantity}"
        holder.tvStatus.text = "Status: ${res.status}"
    }

    override fun getItemCount(): Int = reservations.size

    class ReservationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvUser: TextView = itemView.findViewById(R.id.tvResUserId)
        val tvQuantity: TextView = itemView.findViewById(R.id.tvResQuantity)
        val tvStatus: TextView = itemView.findViewById(R.id.tvResStatus)
    }
}
