package com.example.ticketreservationapp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class AdminEventListStateTest {

    @Test
    public void addEvent_insertsNewEventAtTopOfList() {
        List<AdminEvent> events = new ArrayList<>();
        events.add(buildEvent("event-1", "ACTIVE"));

        AdminEventListState.addEvent(events, buildEvent("event-2", "ACTIVE"));

        assertEquals(2, events.size());
        assertEquals("event-2", events.get(0).getEventId());
        assertEquals("event-1", events.get(1).getEventId());
    }

    @Test
    public void updateEvent_replacesEventAtGivenPosition() {
        List<AdminEvent> events = new ArrayList<>();
        events.add(buildEvent("event-1", "ACTIVE"));
        events.add(buildEvent("event-2", "ACTIVE"));

        boolean updated = AdminEventListState.updateEvent(events, 1, buildEvent("event-2-updated", "ACTIVE"));

        assertTrue(updated);
        assertEquals("event-2-updated", events.get(1).getEventId());
    }

    @Test
    public void updateEvent_rejectsOutOfBoundsPosition() {
        List<AdminEvent> events = new ArrayList<>();
        events.add(buildEvent("event-1", "ACTIVE"));

        assertFalse(AdminEventListState.updateEvent(events, 5, buildEvent("event-2", "ACTIVE")));
    }

    @Test
    public void markCancelled_changesOnlyStatus() {
        List<AdminEvent> events = new ArrayList<>();
        events.add(buildEvent("event-1", "ACTIVE"));

        boolean changed = AdminEventListState.markCancelled(events, 0);

        assertTrue(changed);
        assertEquals("event-1", events.get(0).getEventId());
        assertEquals("Sample Event", events.get(0).getTitle());
        assertEquals("CANCELLED", events.get(0).getStatus());
    }

    @Test
    public void createCancelledCopy_preservesEventDetails() {
        AdminEvent original = buildEvent("event-1", "ACTIVE");

        AdminEvent cancelled = AdminEventListState.createCancelledCopy(original);

        assertEquals(original.getEventId(), cancelled.getEventId());
        assertEquals(original.getOrganizerId(), cancelled.getOrganizerId());
        assertEquals(original.getLocation(), cancelled.getLocation());
        assertEquals("CANCELLED", cancelled.getStatus());
    }

    private AdminEvent buildEvent(String eventId, String status) {
        return new AdminEvent(
                eventId,
                "org-100",
                "Sample Event",
                "Sample description",
                "CONCERT",
                "Place des Arts",
                "2026-08-01",
                "19:00",
                "22:00",
                200,
                status
        );
    }
}
