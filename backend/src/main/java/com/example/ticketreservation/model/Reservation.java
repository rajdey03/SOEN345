package com.example.ticketreservation.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Plain Java model representing a ticket reservation.
 * No persistence annotations — this is purely an in-memory representation.
 */
public class Reservation {

    private UUID reservationId;
    private UUID customerId;
    private UUID eventId;
    private String eventTitle;
    private int numberOfTickets;
    private BigDecimal totalPrice;
    private String status;         // e.g. "CONFIRMED", "CANCELLED"
    private LocalDateTime createdAt;

    public Reservation() {
    }

    public Reservation(UUID reservationId, UUID customerId, UUID eventId,
                       String eventTitle, int numberOfTickets, BigDecimal totalPrice,
                       String status, LocalDateTime createdAt) {
        this.reservationId = reservationId;
        this.customerId = customerId;
        this.eventId = eventId;
        this.eventTitle = eventTitle;
        this.numberOfTickets = numberOfTickets;
        this.totalPrice = totalPrice;
        this.status = status;
        this.createdAt = createdAt;
    }

    public UUID getReservationId() {
        return reservationId;
    }

    public void setReservationId(UUID reservationId) {
        this.reservationId = reservationId;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public void setCustomerId(UUID customerId) {
        this.customerId = customerId;
    }

    public UUID getEventId() {
        return eventId;
    }

    public void setEventId(UUID eventId) {
        this.eventId = eventId;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public int getNumberOfTickets() {
        return numberOfTickets;
    }

    public void setNumberOfTickets(int numberOfTickets) {
        this.numberOfTickets = numberOfTickets;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
