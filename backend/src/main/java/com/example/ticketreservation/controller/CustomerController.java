package com.example.ticketreservation.controller;

import com.example.ticketreservation.dto.*;
import com.example.ticketreservation.service.CustomerEventService;
import com.example.ticketreservation.service.CustomerReservationService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerEventService customerEventService;
    private final CustomerReservationService customerReservationService;

    public CustomerController(CustomerEventService customerEventService,
                              CustomerReservationService customerReservationService) {
        this.customerEventService = customerEventService;
        this.customerReservationService = customerReservationService;
    }

    // ---------------------------------------------------------------
    // GET /api/customers/events — Browse all upcoming events
    // ---------------------------------------------------------------
    @GetMapping("/events")
    public ResponseEntity<List<EventResponse>> getEvents() {
        List<EventResponse> events = customerEventService.getAllUpcomingEvents();
        return ResponseEntity.ok(events);
    }

    // ---------------------------------------------------------------
    // GET /api/customers/events/search — Search / filter events
    //   Query params: keyword, category, date
    // ---------------------------------------------------------------
    @GetMapping("/events/search")
    public ResponseEntity<List<EventResponse>> searchEvents(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String date) {

        LocalDate parsedDate = (date != null && !date.isBlank()) ? LocalDate.parse(date) : null;
        List<EventResponse> results = customerEventService.searchEvents(keyword, category, parsedDate);
        return ResponseEntity.ok(results);
    }

    // ---------------------------------------------------------------
    // POST /api/customers/reservations — Create a ticket reservation
    // ---------------------------------------------------------------
    @PostMapping("/reservations")
    public ResponseEntity<ReservationResponse> createReservation(
            @Valid @RequestBody ReservationRequest request) {
        ReservationResponse response = customerReservationService.createReservation(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ---------------------------------------------------------------
    // GET /api/customers/reservations?userId=... — List user's reservations
    // ---------------------------------------------------------------
    @GetMapping("/reservations")
    public ResponseEntity<List<ReservationResponse>> getUserReservations(@RequestParam UUID userId) {
        List<ReservationResponse> reservations = customerReservationService.getReservationsForUser(userId);
        return ResponseEntity.ok(reservations);
    }

    // ---------------------------------------------------------------
    // DELETE /api/customers/reservations/{reservationId}?userId=... — Cancel reservation
    // ---------------------------------------------------------------
    @DeleteMapping("/reservations/{reservationId}")
    public ResponseEntity<?> cancelReservation(@PathVariable UUID reservationId, @RequestParam UUID userId) {
        boolean cancelled = customerReservationService.cancelReservation(reservationId, userId);
        if (cancelled) {
            return ResponseEntity.ok().body("Reservation cancelled.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Reservation not found or not yours.");
        }
    }
}
