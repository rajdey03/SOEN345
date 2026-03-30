package com.example.ticketreservationapp;

import java.util.List;

final class AdminEventListState {

    private AdminEventListState() {
    }

    static void addEvent(List<AdminEvent> eventList, AdminEvent event) {
        eventList.add(0, event);
    }

    static boolean updateEvent(List<AdminEvent> eventList, int position, AdminEvent event) {
        if (position < 0 || position >= eventList.size()) {
            return false;
        }

        eventList.set(position, event);
        return true;
    }

    static boolean markCancelled(List<AdminEvent> eventList, int position) {
        if (position < 0 || position >= eventList.size()) {
            return false;
        }

        eventList.set(position, createCancelledCopy(eventList.get(position)));
        return true;
    }

    static AdminEvent createCancelledCopy(AdminEvent current) {
        return new AdminEvent(
                current.getEventId(),
                current.getOrganizerId(),
                current.getTitle(),
                current.getDescription(),
                current.getCategory(),
                current.getLocation(),
                current.getEventDate(),
                current.getStartTime(),
                current.getEndTime(),
                current.getTotalCapacity(),
                "CANCELLED"
        );
    }
}
