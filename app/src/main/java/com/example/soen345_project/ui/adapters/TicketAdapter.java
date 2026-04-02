package com.example.soen345_project.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.soen345_project.R;
import com.example.soen345_project.domain.models.Reservation;
import com.example.soen345_project.domain.models.Event;
import java.util.Map;
import java.util.HashMap;

import java.util.List;

public class TicketAdapter extends RecyclerView.Adapter<TicketAdapter.TicketViewHolder> {

    private List<Reservation> reservations;
    private Map<String, Event> eventMap = new HashMap<>();
    private OnReservationCancelListener cancelListener;

    public interface OnReservationCancelListener {
        void onCancel(Reservation reservation, int position);
    }

    public TicketAdapter(List<Reservation> reservations, OnReservationCancelListener cancelListener) {
        this.reservations = reservations;
        this.cancelListener = cancelListener;
    }

    public void setEventMap(Map<String, Event> eventMap) {
        this.eventMap = eventMap;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public TicketViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ticket, parent, false);
        return new TicketViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull TicketViewHolder holder, int position) {
        Reservation res = reservations.get(position);
        Event event = eventMap.get(res.getEventId());

        if (event != null) {
            holder.tvEventName.setText(event.getTitle());
            holder.tvDate.setText(event.getDate().toString()); 
        } else {
            holder.tvEventName.setText("Loading...");
            holder.tvDate.setText("");
        }
        
        holder.tvQuantity.setText("Tickets: " + res.getQuantity());

        holder.btnCancel.setOnClickListener(v -> {
            if (cancelListener != null) {
                cancelListener.onCancel(res, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return reservations.size();
    }

    public static class TicketViewHolder extends RecyclerView.ViewHolder {
        public TextView tvEventName;
        public TextView tvDate;
        public TextView tvQuantity;
        public Button btnCancel;

        public TicketViewHolder(@NonNull View itemView) {
            super(itemView);
            tvEventName = itemView.findViewById(R.id.tvTicketEventName);
            tvDate = itemView.findViewById(R.id.tvTicketDate);
            tvQuantity = itemView.findViewById(R.id.tvTicketQuantity);
            btnCancel = itemView.findViewById(R.id.btnCancelTicket);
        }
    }
}
