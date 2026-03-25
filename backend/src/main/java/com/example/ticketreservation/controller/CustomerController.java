package com.example.ticketreservation.controller;

import com.example.ticketreservation.dto.*;
import com.example.ticketreservation.model.Event;
import com.example.ticketreservation.model.Reservation;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
                new Event(UUID.randomUUID(), "Summer Concert 2026",
                        "Live outdoor concert", "CONCERT", "Olympic Stadium",
                        LocalDateTime.of(2026, 7, 15, 19, 0),
                        new BigDecimal("49.99"), 500, 350),
                new Event(UUID.randomUUID(), "Avengers: Secret Wars",
                        "Marvel blockbuster movie", "MOVIE", "AMC Cinema",
                        LocalDateTime.of(2026, 5, 1, 20, 30),
                        new BigDecimal("15.00"), 200, 120)
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
                new Event(UUID.randomUUID(), "Jazz Night",
                        "Smooth jazz evening", "CONCERT", "Blue Note Club",
                        LocalDateTime.of(2026, 6, 10, 21, 0),
                        new BigDecimal("35.00"), 100, 60)
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
        Reservation placeholder = new Reservation(
                UUID.randomUUID(),
                request.getCustomerId(),
                request.getEventId(),
                "Placeholder Event",
                request.getNumberOfTickets(),
                new BigDecimal("49.99").multiply(BigDecimal.valueOf(request.getNumberOfTickets())),
                "CONFIRMED",
                LocalDateTime.now()
        );
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
}
