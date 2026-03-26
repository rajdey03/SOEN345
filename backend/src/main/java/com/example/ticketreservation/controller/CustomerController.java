package com.example.ticketreservation.controller;

import com.example.ticketreservation.dto.*;
import com.example.ticketreservation.model.Event;
import com.example.ticketreservation.model.EventStatus;
import com.example.ticketreservation.model.Organizer;
import com.example.ticketreservation.model.Reservation;
import com.example.ticketreservation.model.ReservationStatus;
import com.example.ticketreservation.model.User;
import com.example.ticketreservation.model.UserRole;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

/**
 * REST controller for all customer-facing endpoints.
 * Contains method stubs with placeholder responses — no real business logic yet.
 * Will be connected to service and persistence layers in a future sprint.
 */
@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    // ---------------------------------------------------------------
    // POST /api/customers/register — Register a new customer account
    // ---------------------------------------------------------------
    @PostMapping("/register")
    public ResponseEntity<RegisterCustomerResponse> registerCustomer(
            @Valid @RequestBody RegisterCustomerRequest request) {

        // TODO: delegate to CustomerService.register(request)
        RegisterCustomerResponse response = new RegisterCustomerResponse(
                UUID.randomUUID(),
                "Customer registered successfully (placeholder)",
                true
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ---------------------------------------------------------------
    // GET /api/customers/events — Browse all upcoming events
    // ---------------------------------------------------------------
    @GetMapping("/events")
    public ResponseEntity<List<Event>> getEvents() {

        // TODO: delegate to EventService.getAllUpcomingEvents()
        List<Event> placeholderEvents = List.of(
                buildPlaceholderEvent(
                        "Summer Concert 2026",
                        "Live outdoor concert",
                        "CONCERT",
                        "Olympic Stadium",
                        LocalDate.of(2026, 7, 15),
                        LocalTime.of(19, 0),
                        LocalTime.of(22, 0),
                        500,
                        350
                ),
                buildPlaceholderEvent(
                        "Avengers: Secret Wars",
                        "Marvel blockbuster movie",
                        "MOVIE",
                        "AMC Cinema",
                        LocalDate.of(2026, 5, 1),
                        LocalTime.of(20, 30),
                        LocalTime.of(23, 0),
                        200,
                        120
                )
        );
        return ResponseEntity.ok(placeholderEvents);
    }

    // ---------------------------------------------------------------
    // GET /api/customers/events/search — Search / filter events
    //   Query params: keyword, category, date
    // ---------------------------------------------------------------
    @GetMapping("/events/search")
    public ResponseEntity<List<Event>> searchEvents(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String date) {

        // TODO: delegate to EventService.search(keyword, category, date)
        List<Event> placeholderResults = List.of(
                buildPlaceholderEvent(
                        "Jazz Night",
                        "Smooth jazz evening",
                        "CONCERT",
                        "Blue Note Club",
                        LocalDate.of(2026, 6, 10),
                        LocalTime.of(21, 0),
                        LocalTime.of(23, 0),
                        100,
                        60
                )
        );
        return ResponseEntity.ok(placeholderResults);
    }

    // ---------------------------------------------------------------
    // POST /api/customers/reservations — Create a ticket reservation
    // ---------------------------------------------------------------
    @PostMapping("/reservations")
    public ResponseEntity<Reservation> createReservation(
            @Valid @RequestBody ReservationRequest request) {

        // TODO: delegate to ReservationService.create(request)
        Reservation placeholder = new Reservation();
        placeholder.setReservationId(UUID.randomUUID());
        placeholder.setUser(buildPlaceholderUser(request.getCustomerId()));
        placeholder.setEvent(buildPlaceholderEvent(
                "Placeholder Event",
                "Placeholder description",
                "CONCERT",
                "Placeholder Venue",
                LocalDate.now().plusDays(7),
                LocalTime.of(19, 0),
                LocalTime.of(21, 0),
                100,
                100 - request.getNumberOfTickets()
        ));
        placeholder.setQuantity(request.getNumberOfTickets());
        placeholder.setTotalPrice(new BigDecimal("49.99")
                .multiply(BigDecimal.valueOf(request.getNumberOfTickets())));
        placeholder.setStatus(ReservationStatus.CONFIRMED);
        placeholder.setReservationDate(LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.CREATED).body(placeholder);
    }

    // ---------------------------------------------------------------
    // DELETE /api/customers/reservations/{reservationId} — Cancel a reservation
    // ---------------------------------------------------------------
    @DeleteMapping("/reservations/{reservationId}")
    public ResponseEntity<ErrorResponse> cancelReservation(
            @PathVariable UUID reservationId) {

        // TODO: delegate to ReservationService.cancel(reservationId)
        // For now, return a placeholder success message
        ErrorResponse response = new ErrorResponse(200,
                "Reservation " + reservationId + " cancelled successfully (placeholder)");
        return ResponseEntity.ok(response);
    }

    // ---------------------------------------------------------------
    // GET /api/customers/reservations/{reservationId}/confirmation
    //   — Retrieve confirmation details for a reservation
    // ---------------------------------------------------------------
    @GetMapping("/reservations/{reservationId}/confirmation")
    public ResponseEntity<ConfirmationResponse> getConfirmation(
            @PathVariable UUID reservationId) {

        // TODO: delegate to ReservationService.getConfirmation(reservationId)
        ConfirmationResponse placeholder = new ConfirmationResponse();
        placeholder.setReservationId(reservationId);
        placeholder.setCustomerName("Jane Doe");
        placeholder.setEventTitle("Summer Concert 2026");
        placeholder.setVenue("Olympic Stadium");
        placeholder.setEventDateTime(LocalDateTime.of(2026, 7, 15, 19, 0));
        placeholder.setNumberOfTickets(2);
        placeholder.setTotalPrice(new BigDecimal("99.98"));
        placeholder.setStatus("CONFIRMED");
        placeholder.setReservedAt(LocalDateTime.now());

        return ResponseEntity.ok(placeholder);
    }

    private Event buildPlaceholderEvent(String title,
                                        String description,
                                        String category,
                                        String location,
                                        LocalDate eventDate,
                                        LocalTime startTime,
                                        LocalTime endTime,
                                        int totalCapacity,
                                        int availableCapacity) {
        Event event = new Event();
        event.setEventId(UUID.randomUUID());
        event.setOrganizer(buildPlaceholderOrganizer());
        event.setTitle(title);
        event.setDescription(description);
        event.setCategory(category);
        event.setLocation(location);
        event.setEventDate(eventDate);
        event.setStartTime(startTime);
        event.setEndTime(endTime);
        event.setTotalCapacity(totalCapacity);
        event.setAvailableCapacity(availableCapacity);
        event.setStatus(EventStatus.ACTIVE);
        return event;
    }

    private Organizer buildPlaceholderOrganizer() {
        Organizer organizer = new Organizer();
        organizer.setOrganizerId(UUID.randomUUID());
        organizer.setUser(buildPlaceholderUser(UUID.randomUUID()));
        organizer.setOrganizationName("Placeholder Organizer");
        organizer.setContactEmail("organizer@example.com");
        organizer.setContactPhone("+10000000000");
        return organizer;
    }

    private User buildPlaceholderUser(UUID userId) {
        User user = new User();
        user.setUserId(userId);
        user.setFirstName("Placeholder");
        user.setLastName("User");
        user.setEmail("placeholder@example.com");
        user.setPasswordHash("hashed");
        user.setRole(UserRole.CUSTOMER);
        user.setVerified(true);
        return user;
    }
}
