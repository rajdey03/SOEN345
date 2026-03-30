package com.example.ticketreservationapp;

class AdminEventDraft {

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

    AdminEventDraft(String organizerId,
                    String title,
                    String description,
                    String category,
                    String location,
                    String eventDate,
                    String startTime,
                    String endTime,
                    int totalCapacity,
                    String status) {
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

    String getOrganizerId() {
        return organizerId;
    }

    String getTitle() {
        return title;
    }

    String getDescription() {
        return description;
    }

    String getCategory() {
        return category;
    }

    String getLocation() {
        return location;
    }

    String getEventDate() {
        return eventDate;
    }

    String getStartTime() {
        return startTime;
    }

    String getEndTime() {
        return endTime;
    }

    int getTotalCapacity() {
        return totalCapacity;
    }

    String getStatus() {
        return status;
    }
}
