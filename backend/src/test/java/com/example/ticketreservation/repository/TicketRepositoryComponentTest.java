package com.example.ticketreservation.repository;

import com.example.ticketreservation.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Component test for TicketRepository.
 * Uses @DataJpaTest to load only the JPA layer with an in-memory H2 database.
 * Tests ticket persistence and relationships with Reservation and Event.
 */
@DataJpaTest
class TicketRepositoryComponentTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TicketRepository ticketRepository;

    private Event event;
    private Reservation reservation;

    @BeforeEach
    void setUp() {
        User adminUser = new User("Admin", "User", "admin@test.com", null, "hashedpass");
        adminUser.setRole(UserRole.ADMIN);
        entityManager.persistAndFlush(adminUser);

        Organizer organizer = new Organizer();
        organizer.setUser(adminUser);
        organizer.setOrganizationName("Test Org");
        organizer.setContactEmail("org@test.com");
        entityManager.persistAndFlush(organizer);

        User customer = new User("John", "Doe", "john@test.com", "5141234567", "hashedpass");
        customer.setRole(UserRole.CUSTOMER);
        entityManager.persistAndFlush(customer);

        event = new Event();
        event.setOrganizer(organizer);
        event.setTitle("Jazz Night");
        event.setDescription("Live jazz");
        event.setCategory("CONCERT");
        event.setLocation("Blue Note Club");
        event.setEventDate(LocalDate.now().plusDays(10));
        event.setStartTime(LocalTime.of(20, 0));
        event.setEndTime(LocalTime.of(23, 0));
        event.setTotalCapacity(100);
        event.setAvailableCapacity(100);
        event.setStatus(EventStatus.ACTIVE);
        entityManager.persistAndFlush(event);

        reservation = new Reservation();
        reservation.setUser(customer);
        reservation.setEvent(event);
        reservation.setQuantity(2);
        reservation.setTotalPrice(new BigDecimal("99.98"));
        reservation.setStatus(ReservationStatus.CONFIRMED);
        entityManager.persistAndFlush(reservation);
    }

    @Test
    void save_persistsTicketWithAllFields() {
        Ticket ticket = new Ticket();
        ticket.setReservation(reservation);
        ticket.setEvent(event);
        ticket.setTicketCode("TICKET-001");
        ticket.setTicketStatus(TicketStatus.VALID);

        Ticket saved = ticketRepository.saveAndFlush(ticket);

        assertThat(saved.getTicketId()).isNotNull();
        assertThat(saved.getTicketCode()).isEqualTo("TICKET-001");
        assertThat(saved.getTicketStatus()).isEqualTo(TicketStatus.VALID);
        assertThat(saved.getIssuedAt()).isNotNull();
    }

    @Test
    void save_persistsMultipleTicketsForSameReservation() {
        Ticket ticket1 = createTicket("TICKET-001");
        Ticket ticket2 = createTicket("TICKET-002");

        ticketRepository.saveAndFlush(ticket1);
        ticketRepository.saveAndFlush(ticket2);

        assertThat(ticketRepository.findAll()).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    void findById_returnsTicketWithRelationships() {
        Ticket ticket = createTicket("TICKET-003");
        ticketRepository.saveAndFlush(ticket);

        Optional<Ticket> found = ticketRepository.findById(ticket.getTicketId());

        assertThat(found).isPresent();
        assertThat(found.get().getReservation().getReservationId())
                .isEqualTo(reservation.getReservationId());
        assertThat(found.get().getEvent().getTitle()).isEqualTo("Jazz Night");
    }

    @Test
    void save_updatesTicketStatus() {
        Ticket ticket = createTicket("TICKET-004");
        ticketRepository.saveAndFlush(ticket);

        ticket.setTicketStatus(TicketStatus.CANCELLED);
        ticketRepository.saveAndFlush(ticket);

        Ticket found = entityManager.find(Ticket.class, ticket.getTicketId());
        assertThat(found.getTicketStatus()).isEqualTo(TicketStatus.CANCELLED);
    }

    @Test
    void save_setsIssuedAtAutomatically() {
        Ticket ticket = createTicket("TICKET-005");

        Ticket saved = ticketRepository.saveAndFlush(ticket);

        assertThat(saved.getIssuedAt()).isNotNull();
    }

    @Test
    void save_persistsTicketWithSeatNumber() {
        Ticket ticket = createTicket("TICKET-006");
        ticket.setSeatNumber("A-12");

        Ticket saved = ticketRepository.saveAndFlush(ticket);

        assertThat(saved.getSeatNumber()).isEqualTo("A-12");
    }

    private Ticket createTicket(String ticketCode) {
        Ticket ticket = new Ticket();
        ticket.setReservation(reservation);
        ticket.setEvent(event);
        ticket.setTicketCode(ticketCode);
        ticket.setTicketStatus(TicketStatus.VALID);
        return ticket;
    }
}
