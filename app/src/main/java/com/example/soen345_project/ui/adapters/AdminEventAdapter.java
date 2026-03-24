package com.example.soen345_project.ui.adapters;

import androidx.recyclerview.widget.RecyclerView;
import com.example.soen345_project.R;
import com.example.soen345_project.domain.models.Event;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class AdminEventAdapter extends RecyclerView.Adapter<AdminEventAdapter.ViewHolder> {

    public interface AdminEventListener {
        void onEdit(Event event);
        void onCancel(Event event);
    }

    private final List<Event> events;
    private final AdminEventListener listener;

    public AdminEventAdapter(List<Event> events, AdminEventListener listener) {
        this.events = events;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_event, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Event event = events.get(position);
        holder.tvTitle.setText(event.getTitle());
        holder.tvDetails.setText(
                "Category: " + event.getCategory() + "\n" +
                        "Location: " + event.getLocation() + "\n" +
                        "Date: " + new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(event.getDate())
        );
        holder.btnEdit.setOnClickListener(v -> listener.onEdit(event));
        holder.btnCancel.setOnClickListener(v -> listener.onCancel(event));
    }

    @Override
    public int getItemCount() { return events.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDetails;
        Button btnEdit, btnCancel;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle   = itemView.findViewById(R.id.tvAdminEventTitle);
            tvDetails = itemView.findViewById(R.id.tvAdminEventDetails);
            btnEdit   = itemView.findViewById(R.id.btnEditEvent);
            btnCancel = itemView.findViewById(R.id.btnCancelEvent);
        }
    }
}