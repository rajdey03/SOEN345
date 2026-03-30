package com.example.ticketreservation.controller;

import com.example.ticketreservation.config.SecurityConfig;
import com.example.ticketreservation.dto.EventResponse;
import com.example.ticketreservation.dto.ReservationRequest;
import com.example.ticketreservation.dto.ReservationResponse;
import com.example.ticketreservation.exception.GlobalExceptionHandler;
import com.example.ticketreservation.exception.InsufficientCapacityException;
import com.example.ticketreservation.exception.ResourceNotFoundException;
import com.example.ticketreservation.model.EventStatus;
import com.example.ticketreservation.model.ReservationStatus;
import com.example.ticketreservation.service.CustomerEventService;
import com.example.ticketreservation.service.CustomerReservationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerController.class)
@Import({SecurityConfig.class, GlobalExceptionHandler.class})
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CustomerEventService customerEventService;

    @MockBean
    private CustomerReservationService customerReservationService;

    // ---------------------------------------------------------------
    // GET /api/customers/events — Browse events
    // ---------------------------------------------------------------

    @Test
    void getEvents_returnsListOfEvents() throws Exception {
        EventResponse event = buildEventResponse("Jazz Night", "CONCERT", "Blue Note Club");

        when(customerEventService.getAllUpcomingEvents()).thenReturn(List.of(event));

        mockMvc.perform(get("/api/customers/events"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title", is("Jazz Night")))
                .andExpect(jsonPath("$[0].category", is("CONCERT")))
                .andExpect(jsonPath("$[0].location", is("Blue Note Club")))
                .andExpect(jsonPath("$[0].availableCapacity", is(80)));
    }

    @Test
    void getEvents_returnsEmptyListWhenNoEvents() throws Exception {
        when(customerEventService.getAllUpcomingEvents()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/customers/events"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void getEvents_returnsMultipleEvents() throws Exception {
        EventResponse event1 = buildEventResponse("Jazz Night", "CONCERT", "Blue Note Club");
        EventResponse event2 = buildEventResponse("Rock Show", "CONCERT", "Olympic Stadium");

        when(customerEventService.getAllUpcomingEvents()).thenReturn(List.of(event1, event2));

        mockMvc.perform(get("/api/customers/events"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].title", is("Jazz Night")))
                .andExpect(jsonPath("$[1].title", is("Rock Show")));
    }

    // ---------------------------------------------------------------
    // GET /api/customers/events/search — Search / filter events
    // ---------------------------------------------------------------

    @Test
    void searchEvents_filtersByKeyword() throws Exception {
        EventResponse event = buildEventResponse("Jazz Night", "CONCERT", "Blue Note Club");

        when(customerEventService.searchEvents(eq("jazz"), eq(null), eq(null)))
                .thenReturn(List.of(event));

        mockMvc.perform(get("/api/customers/events/search")
                        .param("keyword", "jazz"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title", is("Jazz Night")));
    }

    @Test
    void searchEvents_filtersByCategory() throws Exception {
        EventResponse event = buildEventResponse("Jazz Night", "CONCERT", "Blue Note Club");

        when(customerEventService.searchEvents(eq(null), eq("CONCERT"), eq(null)))
                .thenReturn(List.of(event));

        mockMvc.perform(get("/api/customers/events/search")
                        .param("category", "CONCERT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].category", is("CONCERT")));
    }

    @Test
    void searchEvents_filtersByDate() throws Exception {
        LocalDate targetDate = LocalDate.of(2026, 7, 15);
        EventResponse event = buildEventResponse("Jazz Night", "CONCERT", "Blue Note Club");

        when(customerEventService.searchEvents(eq(null), eq(null), eq(targetDate)))
                .thenReturn(List.of(event));

        mockMvc.perform(get("/api/customers/events/search")
                        .param("date", "2026-07-15"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void searchEvents_returnsEmptyForNoMatch() throws Exception {
        when(customerEventService.searchEvents(eq("nonexistent"), eq(null), eq(null)))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/customers/events/search")
                        .param("keyword", "nonexistent"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void searchEvents_noParamsReturnsAll() throws Exception {
        EventResponse event = buildEventResponse("Jazz Night", "CONCERT", "Blue Note Club");

        when(customerEventService.searchEvents(eq(null), eq(null), eq(null)))
                .thenReturn(List.of(event));

        mockMvc.perform(get("/api/customers/events/search"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    // ---------------------------------------------------------------
    // POST /api/customers/reservations — Create a reservation
    // ---------------------------------------------------------------

    @Test
    void createReservation_returnsCreatedReservation() throws Exception {
        UUID customerId = UUID.randomUUID();
        UUID eventId = UUID.randomUUID();

        ReservationRequest request = new ReservationRequest();
        request.setCustomerId(customerId);
        request.setEventId(eventId);
        request.setNumberOfTickets(2);

        ReservationResponse response = new ReservationResponse();
        response.setReservationId(UUID.randomUUID());
        response.setCustomerId(customerId);
        response.setEventId(eventId);
        response.setEventTitle("Jazz Night");
        response.setQuantity(2);
        response.setTotalPrice(new BigDecimal("99.98"));
        response.setStatus(ReservationStatus.CONFIRMED);
        response.setReservationDate(LocalDateTime.now());

        when(customerReservationService.createReservation(any(ReservationRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/customers/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.reservationId", notNullValue()))
                .andExpect(jsonPath("$.customerId", is(customerId.toString())))
                .andExpect(jsonPath("$.eventId", is(eventId.toString())))
                .andExpect(jsonPath("$.quantity", is(2)))
                .andExpect(jsonPath("$.status", is("CONFIRMED")));
    }

    @Test
    void createReservation_returns404WhenCustomerNotFound() throws Exception {
        ReservationRequest request = new ReservationRequest();
        request.setCustomerId(UUID.randomUUID());
        request.setEventId(UUID.randomUUID());
        request.setNumberOfTickets(1);

        when(customerReservationService.createReservation(any(ReservationRequest.class)))
                .thenThrow(new ResourceNotFoundException("Customer not found"));

        mockMvc.perform(post("/api/customers/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", containsString("Customer not found")));
    }

    @Test
    void createReservation_returns404WhenEventNotFound() throws Exception {
        ReservationRequest request = new ReservationRequest();
        request.setCustomerId(UUID.randomUUID());
        request.setEventId(UUID.randomUUID());
        request.setNumberOfTickets(1);

        when(customerReservationService.createReservation(any(ReservationRequest.class)))
                .thenThrow(new ResourceNotFoundException("Event not found"));

        mockMvc.perform(post("/api/customers/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", containsString("Event not found")));
    }

    @Test
    void createReservation_returns409WhenInsufficientCapacity() throws Exception {
        ReservationRequest request = new ReservationRequest();
        request.setCustomerId(UUID.randomUUID());
        request.setEventId(UUID.randomUUID());
        request.setNumberOfTickets(100);

        when(customerReservationService.createReservation(any(ReservationRequest.class)))
                .thenThrow(new InsufficientCapacityException("Not enough tickets available"));

        mockMvc.perform(post("/api/customers/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", containsString("Not enough tickets")));
    }

    @Test
    void createReservation_returns400WhenMissingCustomerId() throws Exception {
        String body = "{\"eventId\":\"" + UUID.randomUUID() + "\",\"numberOfTickets\":1}";

        mockMvc.perform(post("/api/customers/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createReservation_returns400WhenMissingEventId() throws Exception {
        String body = "{\"customerId\":\"" + UUID.randomUUID() + "\",\"numberOfTickets\":1}";

        mockMvc.perform(post("/api/customers/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    // --- Helper methods ---

    private EventResponse buildEventResponse(String title, String category, String location) {
        EventResponse event = new EventResponse();
        event.setEventId(UUID.randomUUID());
        event.setTitle(title);
        event.setDescription("Event description");
        event.setCategory(category);
        event.setLocation(location);
        event.setEventDate(LocalDate.now().plusDays(10));
        event.setStartTime(LocalTime.of(20, 0));
        event.setEndTime(LocalTime.of(23, 0));
        event.setTotalCapacity(100);
        event.setAvailableCapacity(80);
        event.setStatus(EventStatus.ACTIVE);
        event.setOrganizerName("Test Org");
        return event;
    }
}
