package com.example.ticketreservation.dto;

import com.example.ticketreservation.model.Event;
import com.example.ticketreservation.model.EventStatus;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public class EventResponse {

    private UUID eventId;
    private String title;
    private String description;
    private String category;
    private String location;
    private LocalDate eventDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private int totalCapacity;
    private int availableCapacity;
    private EventStatus status;
    private String organizerName;

    public static EventResponse from(Event event) {
        EventResponse r = new EventResponse();
        r.eventId = event.getEventId();
        r.title = event.getTitle();
        r.description = event.getDescription();
        r.category = event.getCategory();
        r.location = event.getLocation();
        r.eventDate = event.getEventDate();
        r.startTime = event.getStartTime();
        r.endTime = event.getEndTime();
        r.totalCapacity = event.getTotalCapacity();
        r.availableCapacity = event.getAvailableCapacity();
        r.status = event.getStatus();
        if (event.getOrganizer() != null) {
            r.organizerName = event.getOrganizer().getOrganizationName();
        }
        return r;
    }

    public UUID getEventId() { return eventId; }
    public void setEventId(UUID eventId) { this.eventId = eventId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public LocalDate getEventDate() { return eventDate; }
    public void setEventDate(LocalDate eventDate) { this.eventDate = eventDate; }
    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }
    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }
    public int getTotalCapacity() { return totalCapacity; }
    public void setTotalCapacity(int totalCapacity) { this.totalCapacity = totalCapacity; }
    public int getAvailableCapacity() { return availableCapacity; }
    public void setAvailableCapacity(int availableCapacity) { this.availableCapacity = availableCapacity; }
    public EventStatus getStatus() { return status; }
    public void setStatus(EventStatus status) { this.status = status; }
    public String getOrganizerName() { return organizerName; }
    public void setOrganizerName(String organizerName) { this.organizerName = organizerName; }
}
