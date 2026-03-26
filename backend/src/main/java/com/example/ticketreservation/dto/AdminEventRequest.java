package com.example.ticketreservation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public class AdminEventRequest {

    @NotNull(message = "Organizer ID is required")
    @JsonProperty("organizer_id")
    private UUID organizerId;

    @NotBlank(message = "Title is required")
    @JsonProperty("title")
    private String title;

    @NotBlank(message = "Description is required")
    @JsonProperty("description")
    private String description;

    @NotBlank(message = "Category is required")
    @JsonProperty("category")
    private String category;

    @NotBlank(message = "Location is required")
    @JsonProperty("location")
    private String location;

    @NotNull(message = "Event date is required")
    @FutureOrPresent(message = "Event date must be today or later")
    @JsonProperty("event_date")
    private LocalDate eventDate;

    @NotNull(message = "Start time is required")
    @JsonProperty("start_time")
    private LocalTime startTime;

    @NotNull(message = "End time is required")
    @JsonProperty("end_time")
    private LocalTime endTime;

    @NotNull(message = "Total capacity is required")
    @Min(value = 1, message = "Total capacity must be at least 1")
    @JsonProperty("total_capacity")
    private Integer totalCapacity;

    public UUID getOrganizerId() {
        return organizerId;
    }

    public void setOrganizerId(UUID organizerId) {
        this.organizerId = organizerId;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public LocalDate getEventDate() {
        return eventDate;
    }

    public void setEventDate(LocalDate eventDate) {
        this.eventDate = eventDate;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public Integer getTotalCapacity() {
        return totalCapacity;
    }

    public void setTotalCapacity(Integer totalCapacity) {
        this.totalCapacity = totalCapacity;
    }
}
