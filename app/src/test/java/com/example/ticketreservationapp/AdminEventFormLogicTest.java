package com.example.ticketreservationapp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class AdminEventFormLogicTest {

    @Test
    public void validate_returnsFirstMissingFieldMessage() {
        AdminEventDraft draft = new AdminEventDraft(
                "",
                "Jazz Night",
                "Live jazz downtown",
                "CONCERT",
                "Place des Arts",
                "2026-06-14",
                "19:00",
                "22:00",
                180,
                "ACTIVE"
        );

        assertEquals("Organizer ID is required", AdminEventFormLogic.validate(draft));
    }

    @Test
    public void validate_acceptsCompleteDraft() {
        assertNull(AdminEventFormLogic.validate(buildValidDraft()));
    }

    @Test
    public void parseErrorMessage_extractsJsonMessageWhenPresent() {
        String body = "{\"message\":\"Admin user not found.\"}";

        assertEquals("Admin user not found.", AdminEventFormLogic.parseErrorMessage(body));
    }

    @Test
    public void parseErrorMessage_fallsBackToDefaultWhenBodyEmpty() {
        assertEquals("Request failed.", AdminEventFormLogic.parseErrorMessage(""));
    }

    @Test
    public void buildEventFromResponse_usesApiValuesWhenJsonIsValid() {
        String response = "{\"event_id\":\"2d8db14c-6f52-4c9c-bf1d-2fb6ca6c871a\",\"organizer_id\":\"8b45b542-c98d-4958-9cd4-a030e3ef1c68\",\"title\":\"Food Expo\",\"description\":\"Chef demos\",\"category\":\"EXPO\",\"location\":\"Montreal\",\"event_date\":\"2026-09-12\",\"start_time\":\"10:00\",\"end_time\":\"16:00\",\"total_capacity\":300,\"status\":\"ACTIVE\"}";

        AdminEvent event = AdminEventFormLogic.buildEventFromResponse(response, "fallback-id", buildValidDraft());

        assertEquals("2d8db14c-6f52-4c9c-bf1d-2fb6ca6c871a", event.getEventId());
        assertEquals("Food Expo", event.getTitle());
        assertEquals(300, event.getTotalCapacity());
        assertEquals("ACTIVE", event.getStatus());
    }

    @Test
    public void buildEventFromResponse_fallsBackToDraftWhenBodyIsInvalid() {
        AdminEventDraft fallbackDraft = buildValidDraft();

        AdminEvent event = AdminEventFormLogic.buildEventFromResponse("not-json", "fallback-id", fallbackDraft);

        assertEquals("fallback-id", event.getEventId());
        assertEquals(fallbackDraft.getOrganizerId(), event.getOrganizerId());
        assertEquals(fallbackDraft.getTitle(), event.getTitle());
        assertEquals(fallbackDraft.getTotalCapacity(), event.getTotalCapacity());
    }

    private AdminEventDraft buildValidDraft() {
        return new AdminEventDraft(
                "3c18df78-40ae-4a91-887a-ecf41bf1c123",
                "Jazz Night",
                "Live jazz downtown",
                "CONCERT",
                "Place des Arts",
                "2026-06-14",
                "19:00",
                "22:00",
                180,
                "ACTIVE"
        );
    }
}
