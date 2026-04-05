package com.example.ticketreservation.service;

import com.example.ticketreservation.dto.AdminEventRequest;
import com.example.ticketreservation.dto.AdminEventResponse;
import com.example.ticketreservation.exception.InvalidEventOperationException;
import com.example.ticketreservation.exception.ResourceNotFoundException;
import com.example.ticketreservation.exception.UnauthorizedAdminActionException;
import com.example.ticketreservation.model.Event;
import com.example.ticketreservation.model.EventStatus;
import com.example.ticketreservation.model.Organizer;
import com.example.ticketreservation.model.User;
import com.example.ticketreservation.model.UserRole;
import com.example.ticketreservation.repository.EventRepository;
import com.example.ticketreservation.repository.OrganizerRepository;
import com.example.ticketreservation.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AdminEventService {

    private final UserRepository userRepository;
    private final OrganizerRepository organizerRepository;
    private final EventRepository eventRepository;

    public AdminEventService(UserRepository userRepository,
                             OrganizerRepository organizerRepository,
                             EventRepository eventRepository) {
        this.userRepository = userRepository;
        this.organizerRepository = organizerRepository;
        this.eventRepository = eventRepository;
    }

    @Transactional
    public List<AdminEventResponse> getEvents(UUID adminUserId) {
        validateAdmin(adminUserId);
        return eventRepository.findAllByOrderByEventDateDescStartTimeDesc()
                .stream()
                .map(AdminEventResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public AdminEventResponse createEvent(UUID adminUserId, AdminEventRequest request) {
        validateAdmin(adminUserId);
        validateEventTimes(request);

        Organizer organizer = organizerRepository.findById(request.getOrganizerId())
                .orElseThrow(() -> new ResourceNotFoundException("Organizer not found."));

        Event event = new Event();
        applyEventDetails(event, request, organizer);
        event.setAvailableCapacity(request.getTotalCapacity());
        event.setStatus(EventStatus.ACTIVE);

        return AdminEventResponse.from(eventRepository.save(event));
    }

    @Transactional
    public AdminEventResponse updateEvent(UUID adminUserId, UUID eventId, AdminEventRequest request) {
        validateAdmin(adminUserId);
        validateEventTimes(request);

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found."));
        Organizer organizer = organizerRepository.findById(request.getOrganizerId())
                .orElseThrow(() -> new ResourceNotFoundException("Organizer not found."));

        int soldTickets = event.getTotalCapacity() - event.getAvailableCapacity();
        if (request.getTotalCapacity() < soldTickets) {
            throw new InvalidEventOperationException(
                    "Total capacity cannot be lower than the number of reserved tickets."
            );
        }

        applyEventDetails(event, request, organizer);
        event.setAvailableCapacity(request.getTotalCapacity() - soldTickets);

        if (event.getAvailableCapacity() == 0 && event.getStatus() != EventStatus.CANCELLED) {
            event.setStatus(EventStatus.SOLD_OUT);
        } else if (event.getAvailableCapacity() > 0 && event.getStatus() != EventStatus.CANCELLED) {
            event.setStatus(EventStatus.ACTIVE);
        }

        return AdminEventResponse.from(eventRepository.save(event));
    }

    @Transactional
    public AdminEventResponse cancelEvent(UUID adminUserId, UUID eventId) {
        validateAdmin(adminUserId);

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found."));

        event.setStatus(EventStatus.CANCELLED);
        return AdminEventResponse.from(eventRepository.save(event));
    }

    private void validateAdmin(UUID adminUserId) {
        User adminUser = userRepository.findById(adminUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin user not found."));

        if (adminUser.getRole() != UserRole.ADMIN) {
            throw new UnauthorizedAdminActionException("User is not authorized to manage events.");
        }
    }

    private void validateEventTimes(AdminEventRequest request) {
        if (!request.getEndTime().isAfter(request.getStartTime())) {
            throw new InvalidEventOperationException("End time must be after start time.");
        }
    }

    private void applyEventDetails(Event event, AdminEventRequest request, Organizer organizer) {
        event.setOrganizer(organizer);
        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setCategory(request.getCategory());
        event.setLocation(request.getLocation());
        event.setEventDate(request.getEventDate());
        event.setStartTime(request.getStartTime());
        event.setEndTime(request.getEndTime());
        event.setTotalCapacity(request.getTotalCapacity());
    }
}
