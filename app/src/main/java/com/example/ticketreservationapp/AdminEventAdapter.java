package com.example.ticketreservationapp;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AdminEventAdapter extends RecyclerView.Adapter<AdminEventAdapter.AdminEventViewHolder> {

    interface AdminEventActionListener {
        void onEditRequested(AdminEvent event, int position);
        void onCancelRequested(AdminEvent event, int position);
    }

    private final List<AdminEvent> eventList;
    private final AdminEventActionListener actionListener;

    public AdminEventAdapter(List<AdminEvent> eventList, AdminEventActionListener actionListener) {
        this.eventList = eventList;
        this.actionListener = actionListener;
    }

    @NonNull
    @Override
    public AdminEventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_admin_event, parent, false);
        return new AdminEventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminEventViewHolder holder, int position) {
        AdminEvent event = eventList.get(position);

        holder.tvTitle.setText(event.getTitle());
        holder.tvCategory.setText(event.getCategory());
        holder.tvDateTime.setText(event.getEventDate() + " - " + event.getStartTime() + " to " + event.getEndTime());
        holder.tvLocation.setText(event.getLocation());
        holder.tvCapacity.setText("Capacity: " + event.getTotalCapacity());
        holder.tvStatus.setText("Status: " + event.getStatus());

        holder.btnEdit.setOnClickListener(v -> actionListener.onEditRequested(event, holder.getAdapterPosition()));

        holder.btnCancel.setOnClickListener(v -> actionListener.onCancelRequested(event, holder.getAdapterPosition()));
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public void addEvent(AdminEvent event) {
        AdminEventListState.addEvent(eventList, event);
        notifyItemInserted(0);
    }

    public void updateEvent(int position, AdminEvent event) {
        if (AdminEventListState.updateEvent(eventList, position, event)) {
            notifyItemChanged(position);
        }
    }

    public void markCancelled(int position) {
        if (AdminEventListState.markCancelled(eventList, position)) {
            notifyItemChanged(position);
        }
    }

    static class AdminEventViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        TextView tvCategory;
        TextView tvDateTime;
        TextView tvLocation;
        TextView tvCapacity;
        TextView tvStatus;
        Button btnEdit;
        Button btnCancel;

        AdminEventViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvAdminEventTitle);
            tvCategory = itemView.findViewById(R.id.tvAdminEventCategory);
            tvDateTime = itemView.findViewById(R.id.tvAdminEventDateTime);
            tvLocation = itemView.findViewById(R.id.tvAdminEventLocation);
            tvCapacity = itemView.findViewById(R.id.tvAdminEventCapacity);
            tvStatus = itemView.findViewById(R.id.tvAdminEventStatus);
            btnEdit = itemView.findViewById(R.id.btnEditAdminEvent);
            btnCancel = itemView.findViewById(R.id.btnCancelAdminEvent);
        }
    }
}
