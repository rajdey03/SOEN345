package com.example.ticketreservationapp;

import org.json.JSONObject;

public class Reservation {
    private String reservationId;
    private String eventTitle;
    // Add other fields as needed

    public Reservation(String reservationId, String eventTitle) {
        this.reservationId = reservationId;
        this.eventTitle = eventTitle;
    }

    public String getReservationId() { return reservationId; }
    public String getEventTitle() { return eventTitle; }

    public static Reservation fromJson(JSONObject obj) {
        String reservationId = obj.optString("reservationId");
        String eventTitle = obj.optString("eventTitle");
        return new Reservation(reservationId, eventTitle);
    }
}
