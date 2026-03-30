package com.example.ticketreservationapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ReservationAdapter extends RecyclerView.Adapter<ReservationAdapter.ViewHolder> {
    public interface OnCancelClickListener {
        void onCancel(Reservation reservation);
    }
    private List<Reservation> reservations;
    private OnCancelClickListener cancelListener;

    public ReservationAdapter(List<Reservation> reservations, OnCancelClickListener cancelListener) {
        this.reservations = reservations;
        this.cancelListener = cancelListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reservation, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Reservation reservation = reservations.get(position);
        holder.tvEventTitle.setText(reservation.getEventTitle());
        holder.btnCancel.setOnClickListener(v -> cancelListener.onCancel(reservation));
    }

    @Override
    public int getItemCount() {
        return reservations.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvEventTitle;
        Button btnCancel;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvEventTitle = itemView.findViewById(R.id.tvEventTitle);
            btnCancel = itemView.findViewById(R.id.btnCancelReservation);
        }
    }
}
