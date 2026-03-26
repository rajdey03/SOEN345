package com.example.ticketreservation.dto;

import com.example.ticketreservation.model.Event;
import com.example.ticketreservation.model.EventStatus;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

public class AdminEventResponse {

    @JsonProperty("event_id")
    private UUID eventId;

    @JsonProperty("organizer_id")
    private UUID organizerId;

    @JsonProperty("title")
    private String title;

    @JsonProperty("description")
    private String description;

    @JsonProperty("category")
    private String category;

    @JsonProperty("location")
    private String location;

    @JsonProperty("event_date")
    private LocalDate eventDate;

    @JsonProperty("start_time")
    private LocalTime startTime;

    @JsonProperty("end_time")
    private LocalTime endTime;

    @JsonProperty("total_capacity")
    private int totalCapacity;

    @JsonProperty("available_capacity")
    private int availableCapacity;

    @JsonProperty("status")
    private EventStatus status;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;

    public static AdminEventResponse from(Event event) {
        AdminEventResponse response = new AdminEventResponse();
        response.eventId = event.getEventId();
        response.organizerId = event.getOrganizer().getOrganizerId();
        response.title = event.getTitle();
        response.description = event.getDescription();
        response.category = event.getCategory();
        response.location = event.getLocation();
        response.eventDate = event.getEventDate();
        response.startTime = event.getStartTime();
        response.endTime = event.getEndTime();
        response.totalCapacity = event.getTotalCapacity();
        response.availableCapacity = event.getAvailableCapacity();
        response.status = event.getStatus();
        response.createdAt = event.getCreatedAt();
        response.updatedAt = event.getUpdatedAt();
        return response;
    }

    public UUID getEventId() {
        return eventId;
    }

    public UUID getOrganizerId() {
        return organizerId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public String getLocation() {
        return location;
    }

    public LocalDate getEventDate() {
        return eventDate;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public int getTotalCapacity() {
        return totalCapacity;
    }

    public int getAvailableCapacity() {
        return availableCapacity;
    }

    public EventStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
