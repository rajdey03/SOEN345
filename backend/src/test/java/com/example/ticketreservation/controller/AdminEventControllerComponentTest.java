package com.example.ticketreservation.controller;

import com.example.ticketreservation.config.SecurityConfig;
import com.example.ticketreservation.dto.AdminEventRequest;
import com.example.ticketreservation.dto.AdminEventResponse;
import com.example.ticketreservation.exception.GlobalExceptionHandler;
import com.example.ticketreservation.exception.InvalidEventOperationException;
import com.example.ticketreservation.exception.ResourceNotFoundException;
import com.example.ticketreservation.exception.UnauthorizedAdminActionException;
import com.example.ticketreservation.model.EventStatus;
import com.example.ticketreservation.service.AdminEventService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Component test for AdminEventController.
 * Loads only the web layer (@WebMvcTest) with mocked service dependencies.
 * Tests HTTP request routing, header validation, request body validation,
 * serialization, and error handling.
 */
@WebMvcTest(AdminEventController.class)
@Import({SecurityConfig.class, GlobalExceptionHandler.class})
class AdminEventControllerComponentTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AdminEventService adminEventService;

    private static final UUID ADMIN_USER_ID = UUID.randomUUID();

    // ---------------------------------------------------------------
    // GET /api/admin/events — List events
    // ---------------------------------------------------------------

    @Test
    void getEvents_returnsListOfEvents() throws Exception {
        AdminEventResponse event = buildAdminEventResponse("Jazz Night", "CONCERT");

        when(adminEventService.getEvents(ADMIN_USER_ID)).thenReturn(List.of(event));

        mockMvc.perform(get("/api/admin/events")
                        .header("X-Admin-User-Id", ADMIN_USER_ID.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title", is("Jazz Night")))
                .andExpect(jsonPath("$[0].category", is("CONCERT")));
    }

    @Test
    void getEvents_returnsEmptyListWhenNoEvents() throws Exception {
        when(adminEventService.getEvents(ADMIN_USER_ID)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/admin/events")
                        .header("X-Admin-User-Id", ADMIN_USER_ID.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void getEvents_returns400WhenAdminHeaderMissing() throws Exception {
        mockMvc.perform(get("/api/admin/events"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getEvents_returns404WhenAdminNotFound() throws Exception {
        when(adminEventService.getEvents(ADMIN_USER_ID))
                .thenThrow(new ResourceNotFoundException("Admin user not found."));

        mockMvc.perform(get("/api/admin/events")
                        .header("X-Admin-User-Id", ADMIN_USER_ID.toString()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", containsString("Admin user not found")));
    }

    @Test
    void getEvents_returns403WhenUserNotAdmin() throws Exception {
        when(adminEventService.getEvents(ADMIN_USER_ID))
                .thenThrow(new UnauthorizedAdminActionException("User is not authorized to manage events."));

        mockMvc.perform(get("/api/admin/events")
                        .header("X-Admin-User-Id", ADMIN_USER_ID.toString()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message", containsString("not authorized")));
    }

    // ---------------------------------------------------------------
    // POST /api/admin/events — Create event
    // ---------------------------------------------------------------

    @Test
    void createEvent_returnsCreatedOnSuccess() throws Exception {
        AdminEventRequest request = buildAdminEventRequest();
        AdminEventResponse response = buildAdminEventResponse("Jazz Night", "CONCERT");

        when(adminEventService.createEvent(eq(ADMIN_USER_ID), any(AdminEventRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/admin/events")
                        .header("X-Admin-User-Id", ADMIN_USER_ID.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title", is("Jazz Night")))
                .andExpect(jsonPath("$.category", is("CONCERT")))
                .andExpect(jsonPath("$.status", is("ACTIVE")));
    }

    @Test
    void createEvent_returns400WhenTitleMissing() throws Exception {
        AdminEventRequest request = buildAdminEventRequest();
        request.setTitle("");

        mockMvc.perform(post("/api/admin/events")
                        .header("X-Admin-User-Id", ADMIN_USER_ID.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createEvent_returns400WhenOrganizerIdMissing() throws Exception {
        AdminEventRequest request = buildAdminEventRequest();
        request.setOrganizerId(null);

        mockMvc.perform(post("/api/admin/events")
                        .header("X-Admin-User-Id", ADMIN_USER_ID.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createEvent_returns400WhenCapacityLessThanOne() throws Exception {
        AdminEventRequest request = buildAdminEventRequest();
        request.setTotalCapacity(0);

        mockMvc.perform(post("/api/admin/events")
                        .header("X-Admin-User-Id", ADMIN_USER_ID.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createEvent_returns400WhenEventDateMissing() throws Exception {
        AdminEventRequest request = buildAdminEventRequest();
        request.setEventDate(null);

        mockMvc.perform(post("/api/admin/events")
                        .header("X-Admin-User-Id", ADMIN_USER_ID.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createEvent_returns404WhenOrganizerNotFound() throws Exception {
        AdminEventRequest request = buildAdminEventRequest();

        when(adminEventService.createEvent(eq(ADMIN_USER_ID), any(AdminEventRequest.class)))
                .thenThrow(new ResourceNotFoundException("Organizer not found."));

        mockMvc.perform(post("/api/admin/events")
                        .header("X-Admin-User-Id", ADMIN_USER_ID.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", containsString("Organizer not found")));
    }

    @Test
    void createEvent_returns400WhenEndTimeBeforeStartTime() throws Exception {
        AdminEventRequest request = buildAdminEventRequest();

        when(adminEventService.createEvent(eq(ADMIN_USER_ID), any(AdminEventRequest.class)))
                .thenThrow(new InvalidEventOperationException("End time must be after start time."));

        mockMvc.perform(post("/api/admin/events")
                        .header("X-Admin-User-Id", ADMIN_USER_ID.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("End time must be after start time")));
    }

    // ---------------------------------------------------------------
    // PUT /api/admin/events/{eventId} — Update event
    // ---------------------------------------------------------------

    @Test
    void updateEvent_returnsOkOnSuccess() throws Exception {
        UUID eventId = UUID.randomUUID();
        AdminEventRequest request = buildAdminEventRequest();
        AdminEventResponse response = buildAdminEventResponse("Updated Jazz Night", "CONCERT");

        when(adminEventService.updateEvent(eq(ADMIN_USER_ID), eq(eventId), any(AdminEventRequest.class)))
                .thenReturn(response);

        mockMvc.perform(put("/api/admin/events/{eventId}", eventId)
                        .header("X-Admin-User-Id", ADMIN_USER_ID.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Updated Jazz Night")));
    }

    @Test
    void updateEvent_returns404WhenEventNotFound() throws Exception {
        UUID eventId = UUID.randomUUID();
        AdminEventRequest request = buildAdminEventRequest();

        when(adminEventService.updateEvent(eq(ADMIN_USER_ID), eq(eventId), any(AdminEventRequest.class)))
                .thenThrow(new ResourceNotFoundException("Event not found."));

        mockMvc.perform(put("/api/admin/events/{eventId}", eventId)
                        .header("X-Admin-User-Id", ADMIN_USER_ID.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", containsString("Event not found")));
    }

    @Test
    void updateEvent_returns400WhenCapacityBelowReserved() throws Exception {
        UUID eventId = UUID.randomUUID();
        AdminEventRequest request = buildAdminEventRequest();

        when(adminEventService.updateEvent(eq(ADMIN_USER_ID), eq(eventId), any(AdminEventRequest.class)))
                .thenThrow(new InvalidEventOperationException(
                        "Total capacity cannot be lower than the number of reserved tickets."));

        mockMvc.perform(put("/api/admin/events/{eventId}", eventId)
                        .header("X-Admin-User-Id", ADMIN_USER_ID.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("reserved tickets")));
    }

    // ---------------------------------------------------------------
    // PATCH /api/admin/events/{eventId}/cancel — Cancel event
    // ---------------------------------------------------------------

    @Test
    void cancelEvent_returnsOkOnSuccess() throws Exception {
        UUID eventId = UUID.randomUUID();
        AdminEventResponse response = buildAdminEventResponse("Jazz Night", "CONCERT");

        when(adminEventService.cancelEvent(ADMIN_USER_ID, eventId)).thenReturn(response);

        mockMvc.perform(patch("/api/admin/events/{eventId}/cancel", eventId)
                        .header("X-Admin-User-Id", ADMIN_USER_ID.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Jazz Night")));
    }

    @Test
    void cancelEvent_returns404WhenEventNotFound() throws Exception {
        UUID eventId = UUID.randomUUID();

        when(adminEventService.cancelEvent(ADMIN_USER_ID, eventId))
                .thenThrow(new ResourceNotFoundException("Event not found."));

        mockMvc.perform(patch("/api/admin/events/{eventId}/cancel", eventId)
                        .header("X-Admin-User-Id", ADMIN_USER_ID.toString()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", containsString("Event not found")));
    }

    @Test
    void cancelEvent_returns403WhenUserNotAdmin() throws Exception {
        UUID eventId = UUID.randomUUID();

        when(adminEventService.cancelEvent(ADMIN_USER_ID, eventId))
                .thenThrow(new UnauthorizedAdminActionException("User is not authorized to manage events."));

        mockMvc.perform(patch("/api/admin/events/{eventId}/cancel", eventId)
                        .header("X-Admin-User-Id", ADMIN_USER_ID.toString()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message", containsString("not authorized")));
    }

    // --- Helper methods ---

    private AdminEventRequest buildAdminEventRequest() {
        AdminEventRequest request = new AdminEventRequest();
        request.setOrganizerId(UUID.randomUUID());
        request.setTitle("Jazz Night");
        request.setDescription("Live jazz performance downtown");
        request.setCategory("CONCERT");
        request.setLocation("Blue Note Club");
        request.setEventDate(LocalDate.now().plusDays(30));
        request.setStartTime(LocalTime.of(20, 0));
        request.setEndTime(LocalTime.of(23, 0));
        request.setTotalCapacity(200);
        return request;
    }

    private AdminEventResponse buildAdminEventResponse(String title, String category) {
        // AdminEventResponse uses static from(Event), so we build via reflection-free approach
        // We'll create a minimal Event and use the from() method, or set fields directly
        AdminEventResponse response = new AdminEventResponse();
        // AdminEventResponse only has getters (no setters) — it's built via from(Event)
        // We need to use a different approach. Let's mock the service to return a proper response.
        // Actually, looking at the code, AdminEventResponse does NOT have setters.
        // The from(Event) method sets fields directly. We need to create it via from().
        // For @WebMvcTest we mock the service, so we need to construct a valid response.
        // Let's build an Event entity and use AdminEventResponse.from()
        com.example.ticketreservation.model.Organizer organizer = new com.example.ticketreservation.model.Organizer();
        organizer.setOrganizerId(UUID.randomUUID());

        com.example.ticketreservation.model.Event event = new com.example.ticketreservation.model.Event();
        event.setEventId(UUID.randomUUID());
        event.setOrganizer(organizer);
        event.setTitle(title);
        event.setDescription("Event description");
        event.setCategory(category);
        event.setLocation("Blue Note Club");
        event.setEventDate(LocalDate.now().plusDays(30));
        event.setStartTime(LocalTime.of(20, 0));
        event.setEndTime(LocalTime.of(23, 0));
        event.setTotalCapacity(200);
        event.setAvailableCapacity(200);
        event.setStatus(EventStatus.ACTIVE);

        return AdminEventResponse.from(event);
    }
}
