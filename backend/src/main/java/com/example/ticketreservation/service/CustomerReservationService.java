package com.example.ticketreservation.service;

import com.example.ticketreservation.dto.ReservationRequest;
import com.example.ticketreservation.dto.ReservationResponse;
import com.example.ticketreservation.exception.InsufficientCapacityException;
import com.example.ticketreservation.exception.ResourceNotFoundException;
import com.example.ticketreservation.model.*;
import com.example.ticketreservation.repository.EventRepository;
import com.example.ticketreservation.repository.ReservationRepository;
import com.example.ticketreservation.repository.TicketRepository;
import com.example.ticketreservation.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class CustomerReservationService {

    private static final BigDecimal TICKET_PRICE = new BigDecimal("49.99");

    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final ReservationRepository reservationRepository;
    private final TicketRepository ticketRepository;

    public CustomerReservationService(UserRepository userRepository,
                                       EventRepository eventRepository,
                                       ReservationRepository reservationRepository,
                                       TicketRepository ticketRepository) {
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        this.reservationRepository = reservationRepository;
        this.ticketRepository = ticketRepository;
    }

    @Transactional
    public ReservationResponse createReservation(ReservationRequest request) {
        User user = userRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Customer not found with ID: " + request.getCustomerId()));

        Event event = eventRepository.findById(request.getEventId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Event not found with ID: " + request.getEventId()));

        if (event.getStatus() == EventStatus.CANCELLED) {
            throw new InsufficientCapacityException("Cannot reserve tickets for a cancelled event.");
        }

        if (event.getAvailableCapacity() < request.getNumberOfTickets()) {
            throw new InsufficientCapacityException(
                    "Not enough tickets available. Requested: " + request.getNumberOfTickets()
                            + ", Available: " + event.getAvailableCapacity());
        }

        // Create the reservation
        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setEvent(event);
        reservation.setQuantity(request.getNumberOfTickets());
        reservation.setTotalPrice(TICKET_PRICE.multiply(BigDecimal.valueOf(request.getNumberOfTickets())));
        reservation.setStatus(ReservationStatus.CONFIRMED);
        Reservation saved = reservationRepository.save(reservation);

        // Generate individual tickets
        for (int i = 0; i < request.getNumberOfTickets(); i++) {
            Ticket ticket = new Ticket();
            ticket.setReservation(saved);
            ticket.setEvent(event);
            ticket.setTicketCode(UUID.randomUUID().toString());
            ticket.setTicketStatus(TicketStatus.VALID);
            ticketRepository.save(ticket);
        }

        // Decrease available capacity
        event.setAvailableCapacity(event.getAvailableCapacity() - request.getNumberOfTickets());
        if (event.getAvailableCapacity() == 0) {
            event.setStatus(EventStatus.SOLD_OUT);
        }
        eventRepository.save(event);

        return ReservationResponse.from(saved);
    }
}
