package com.example.soen345_project.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.soen345_project.R;
import com.example.soen345_project.domain.models.MockEvent;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private List<MockEvent> events;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(MockEvent event);
    }

    public EventAdapter(List<MockEvent> events, OnItemClickListener listener) {
        this.events = events;
        this.listener = listener;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event, parent, false);
        return new EventViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        MockEvent currentItem = events.get(position);
        holder.tvTitle.setText(currentItem.getTitle());
        holder.tvDate.setText(currentItem.getDate());
        holder.tvLocation.setText(currentItem.getLocation());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(currentItem);
            }
        });
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        public TextView tvTitle;
        public TextView tvDate;
        public TextView tvLocation;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvEventTitle);
            tvDate = itemView.findViewById(R.id.tvEventDate);
            tvLocation = itemView.findViewById(R.id.tvEventLocation);
        }
    }
}
