package com.example.ticketreservationapp;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AdminEventAdapter extends RecyclerView.Adapter<AdminEventAdapter.AdminEventViewHolder> {

    private final List<AdminEvent> eventList;

    public AdminEventAdapter(List<AdminEvent> eventList) {
        this.eventList = eventList;
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

        holder.btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), AdminEventFormActivity.class);
            intent.putExtra(AdminEventFormActivity.EXTRA_MODE, AdminEventFormActivity.MODE_EDIT);
            intent.putExtra(AdminEventFormActivity.EXTRA_EVENT_ID, event.getEventId());
            intent.putExtra(AdminEventFormActivity.EXTRA_ORGANIZER_ID, event.getOrganizerId());
            intent.putExtra(AdminEventFormActivity.EXTRA_TITLE, event.getTitle());
            intent.putExtra(AdminEventFormActivity.EXTRA_DESCRIPTION, event.getDescription());
            intent.putExtra(AdminEventFormActivity.EXTRA_CATEGORY, event.getCategory());
            intent.putExtra(AdminEventFormActivity.EXTRA_LOCATION, event.getLocation());
            intent.putExtra(AdminEventFormActivity.EXTRA_EVENT_DATE, event.getEventDate());
            intent.putExtra(AdminEventFormActivity.EXTRA_START_TIME, event.getStartTime());
            intent.putExtra(AdminEventFormActivity.EXTRA_END_TIME, event.getEndTime());
            intent.putExtra(AdminEventFormActivity.EXTRA_TOTAL_CAPACITY, event.getTotalCapacity());
            intent.putExtra(AdminEventFormActivity.EXTRA_STATUS, event.getStatus());
            v.getContext().startActivity(intent);
        });

        holder.btnCancel.setOnClickListener(v -> Toast.makeText(
                v.getContext(),
                "Cancel action will be connected to the admin API next.",
                Toast.LENGTH_SHORT
        ).show());
    }

    @Override
    public int getItemCount() {
        return eventList.size();
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
