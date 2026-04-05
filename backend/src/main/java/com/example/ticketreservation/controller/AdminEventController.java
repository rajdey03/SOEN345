package com.example.ticketreservation.controller;

import com.example.ticketreservation.dto.AdminEventRequest;
import com.example.ticketreservation.dto.AdminEventResponse;
import com.example.ticketreservation.service.AdminEventService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/events")
public class AdminEventController {

    private final AdminEventService adminEventService;

    public AdminEventController(AdminEventService adminEventService) {
        this.adminEventService = adminEventService;
    }

    @GetMapping
    public ResponseEntity<List<AdminEventResponse>> getEvents(
            @RequestHeader("X-Admin-User-Id") UUID adminUserId) {
        return ResponseEntity.ok(adminEventService.getEvents(adminUserId));
    }

    @PostMapping
    public ResponseEntity<AdminEventResponse> createEvent(
            @RequestHeader("X-Admin-User-Id") UUID adminUserId,
            @Valid @RequestBody AdminEventRequest request) {
        AdminEventResponse response = adminEventService.createEvent(adminUserId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{eventId}")
    public ResponseEntity<AdminEventResponse> updateEvent(
            @RequestHeader("X-Admin-User-Id") UUID adminUserId,
            @PathVariable UUID eventId,
            @Valid @RequestBody AdminEventRequest request) {
        return ResponseEntity.ok(adminEventService.updateEvent(adminUserId, eventId, request));
    }

    @PatchMapping("/{eventId}/cancel")
    public ResponseEntity<AdminEventResponse> cancelEvent(
            @RequestHeader("X-Admin-User-Id") UUID adminUserId,
            @PathVariable UUID eventId) {
        return ResponseEntity.ok(adminEventService.cancelEvent(adminUserId, eventId));
    }
}
