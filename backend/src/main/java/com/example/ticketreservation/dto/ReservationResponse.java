package com.example.ticketreservation.dto;

import com.example.ticketreservation.model.Reservation;
import com.example.ticketreservation.model.ReservationStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class ReservationResponse {

    private UUID reservationId;
    private UUID customerId;
    private UUID eventId;
    private String eventTitle;
    private int quantity;
    private BigDecimal totalPrice;
    private ReservationStatus status;
    private LocalDateTime reservationDate;

    public static ReservationResponse from(Reservation reservation) {
        ReservationResponse r = new ReservationResponse();
        r.reservationId = reservation.getReservationId();
        r.customerId = reservation.getUser().getUserId();
        r.eventId = reservation.getEvent().getEventId();
        r.eventTitle = reservation.getEvent().getTitle();
        r.quantity = reservation.getQuantity();
        r.totalPrice = reservation.getTotalPrice();
        r.status = reservation.getStatus();
        r.reservationDate = reservation.getReservationDate();
        return r;
    }

    public UUID getReservationId() { return reservationId; }
    public void setReservationId(UUID reservationId) { this.reservationId = reservationId; }
    public UUID getCustomerId() { return customerId; }
    public void setCustomerId(UUID customerId) { this.customerId = customerId; }
    public UUID getEventId() { return eventId; }
    public void setEventId(UUID eventId) { this.eventId = eventId; }
    public String getEventTitle() { return eventTitle; }
    public void setEventTitle(String eventTitle) { this.eventTitle = eventTitle; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public BigDecimal getTotalPrice() { return totalPrice; }
    public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }
    public ReservationStatus getStatus() { return status; }
    public void setStatus(ReservationStatus status) { this.status = status; }
    public LocalDateTime getReservationDate() { return reservationDate; }
    public void setReservationDate(LocalDateTime reservationDate) { this.reservationDate = reservationDate; }
}
