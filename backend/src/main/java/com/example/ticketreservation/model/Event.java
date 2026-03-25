package com.example.ticketreservation.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Plain Java model representing an event (movie, concert, travel, sports, etc.).
 * No persistence annotations — this is purely an in-memory representation.
 */
public class Event {

    private UUID eventId;
    private String title;
    private String description;
    private String category;      // e.g. "MOVIE", "CONCERT", "TRAVEL", "SPORTS"
    private String venue;
    private LocalDateTime dateTime;
    private BigDecimal price;
    private int totalSeats;
    private int availableSeats;

    public Event() {
    }

    public Event(UUID eventId, String title, String description, String category,
                 String venue, LocalDateTime dateTime, BigDecimal price,
                 int totalSeats, int availableSeats) {
        this.eventId = eventId;
        this.title = title;
        this.description = description;
        this.category = category;
        this.venue = venue;
        this.dateTime = dateTime;
        this.price = price;
        this.totalSeats = totalSeats;
        this.availableSeats = availableSeats;
    }

    public UUID getEventId() {
        return eventId;
    }

    public void setEventId(UUID eventId) {
        this.eventId = eventId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats(int totalSeats) {
        this.totalSeats = totalSeats;
    }

    public int getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(int availableSeats) {
        this.availableSeats = availableSeats;
    }
}
