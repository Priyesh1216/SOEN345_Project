package com.example.soen345_project.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.soen345_project.R;
import com.example.soen345_project.domain.models.MockTicket;

import java.util.List;

public class TicketAdapter extends RecyclerView.Adapter<TicketAdapter.TicketViewHolder> {

    private List<MockTicket> tickets;
    private OnTicketCancelListener cancelListener;

    public interface OnTicketCancelListener {
        void onCancel(MockTicket ticket, int position);
    }

    public TicketAdapter(List<MockTicket> tickets, OnTicketCancelListener cancelListener) {
        this.tickets = tickets;
        this.cancelListener = cancelListener;
    }

    @NonNull
    @Override
    public TicketViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ticket, parent, false);
        return new TicketViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull TicketViewHolder holder, int position) {
        MockTicket currentItem = tickets.get(position);
        holder.tvEventName.setText(currentItem.getEvent().getTitle());
        holder.tvDate.setText(currentItem.getEvent().getDate());
        holder.tvQuantity.setText("Tickets: " + currentItem.getQuantity());

        holder.btnCancel.setOnClickListener(v -> {
            if (cancelListener != null) {
                cancelListener.onCancel(currentItem, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return tickets.size();
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
