package com.example.ticketreservationapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private List<Event> eventList;

    public EventAdapter(List<Event> eventList) {
        this.eventList = eventList;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = eventList.get(position);

        holder.tvTitle.setText(event.getTitle());
        holder.tvCategory.setText(event.getCategory());
        holder.tvDateTime.setText(event.getDateTime());
        holder.tvLocation.setText(event.getLocation());
        holder.tvCapacity.setText("Available: " + event.getAvailableCapacity());

        // Handle the Reserve button click
        holder.btnReserve.setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(v.getContext(), ReservationActivity.class);

            intent.putExtra("EVENT_TITLE", event.getTitle());
            intent.putExtra("EVENT_DATETIME", event.getDateTime());
            intent.putExtra("EVENT_LOCATION", event.getLocation());

            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvCategory, tvDateTime, tvLocation, tvCapacity;
        Button btnReserve;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvEventTitle);
            tvCategory = itemView.findViewById(R.id.tvEventCategory);
            tvDateTime = itemView.findViewById(R.id.tvEventDateTime);
            tvLocation = itemView.findViewById(R.id.tvEventLocation);
            tvCapacity = itemView.findViewById(R.id.tvEventCapacity);
            btnReserve = itemView.findViewById(R.id.btnReserve);
        }
    }
}