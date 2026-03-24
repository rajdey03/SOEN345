package com.example.ticketreservationapp;

public class Event {
    private String id;
    private String title;
    private String category;
    private String location;
    private String dateTime;
    private int availableCapacity;

    public Event(String id, String title, String category, String location, String dateTime, int availableCapacity) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.location = location;
        this.dateTime = dateTime;
        this.availableCapacity = availableCapacity;
    }

    // Getters
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getCategory() { return category; }
    public String getLocation() { return location; }
    public String getDateTime() { return dateTime; }
    public int getAvailableCapacity() { return availableCapacity; }
}