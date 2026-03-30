package com.example.ticketreservationapp;

public class AdminEvent {
    private final String eventId;
    private final String organizerId;
    private final String title;
    private final String description;
    private final String category;
    private final String location;
    private final String eventDate;
    private final String startTime;
    private final String endTime;
    private final int totalCapacity;
    private final String status;

    public AdminEvent(String eventId,
                      String organizerId,
                      String title,
                      String description,
                      String category,
                      String location,
                      String eventDate,
                      String startTime,
                      String endTime,
                      int totalCapacity,
                      String status) {
        this.eventId = eventId;
        this.organizerId = organizerId;
        this.title = title;
        this.description = description;
        this.category = category;
        this.location = location;
        this.eventDate = eventDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.totalCapacity = totalCapacity;
        this.status = status;
    }

    public String getEventId() {
        return eventId;
    }

    public String getOrganizerId() {
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

    public String getEventDate() {
        return eventDate;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public int getTotalCapacity() {
        return totalCapacity;
    }

    public String getStatus() {
        return status;
    }
}
