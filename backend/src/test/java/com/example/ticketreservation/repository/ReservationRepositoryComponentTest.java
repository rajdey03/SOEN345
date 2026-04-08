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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Component test for ReservationRepository.
 * Uses @DataJpaTest to load only the JPA layer with an in-memory H2 database.
 * Tests custom query methods and entity relationships.
 */
@DataJpaTest
class ReservationRepositoryComponentTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ReservationRepository reservationRepository;

    private User customer;
    private User otherCustomer;
    private Event event;

    @BeforeEach
    void setUp() {
        // Create admin user for organizer
        User adminUser = new User("Admin", "User", "admin@test.com", null, "hashedpass");
        adminUser.setRole(UserRole.ADMIN);
        entityManager.persistAndFlush(adminUser);

        Organizer organizer = new Organizer();
        organizer.setUser(adminUser);
        organizer.setOrganizationName("Test Org");
        organizer.setContactEmail("org@test.com");
        entityManager.persistAndFlush(organizer);

        // Create customers
        customer = new User("John", "Doe", "john@test.com", "5141111111", "hashedpass");
        customer.setRole(UserRole.CUSTOMER);
        entityManager.persistAndFlush(customer);

        otherCustomer = new User("Jane", "Smith", "jane@test.com", "5142222222", "hashedpass");
        otherCustomer.setRole(UserRole.CUSTOMER);
        entityManager.persistAndFlush(otherCustomer);

        // Create event
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
    }

    @Test
    void findByUser_UserId_returnsReservationsForUser() {
        Reservation reservation = createReservation(customer, event, 2);
        entityManager.persistAndFlush(reservation);

        List<Reservation> results = reservationRepository.findByUser_UserId(customer.getUserId());

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getUser().getUserId()).isEqualTo(customer.getUserId());
        assertThat(results.get(0).getQuantity()).isEqualTo(2);
    }

    @Test
    void findByUser_UserId_returnsEmptyForUserWithNoReservations() {
        List<Reservation> results = reservationRepository.findByUser_UserId(otherCustomer.getUserId());

        assertThat(results).isEmpty();
    }

    @Test
    void findByUser_UserId_returnsOnlyReservationsForSpecifiedUser() {
        Reservation res1 = createReservation(customer, event, 2);
        Reservation res2 = createReservation(otherCustomer, event, 3);
        entityManager.persistAndFlush(res1);
        entityManager.persistAndFlush(res2);

        List<Reservation> results = reservationRepository.findByUser_UserId(customer.getUserId());

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getQuantity()).isEqualTo(2);
    }

    @Test
    void findByUser_UserId_returnsMultipleReservationsForSameUser() {
        Reservation res1 = createReservation(customer, event, 1);
        Reservation res2 = createReservation(customer, event, 3);
        entityManager.persistAndFlush(res1);
        entityManager.persistAndFlush(res2);

        List<Reservation> results = reservationRepository.findByUser_UserId(customer.getUserId());

        assertThat(results).hasSize(2);
    }

    @Test
    void save_persistsReservationWithAllFields() {
        Reservation reservation = createReservation(customer, event, 4);

        Reservation saved = reservationRepository.saveAndFlush(reservation);

        assertThat(saved.getReservationId()).isNotNull();
        assertThat(saved.getUser().getUserId()).isEqualTo(customer.getUserId());
        assertThat(saved.getEvent().getEventId()).isEqualTo(event.getEventId());
        assertThat(saved.getQuantity()).isEqualTo(4);
        assertThat(saved.getTotalPrice()).isEqualByComparingTo(new BigDecimal("199.96"));
        assertThat(saved.getStatus()).isEqualTo(ReservationStatus.CONFIRMED);
        assertThat(saved.getReservationDate()).isNotNull();
    }

    @Test
    void save_updatesReservationStatus() {
        Reservation reservation = createReservation(customer, event, 2);
        entityManager.persistAndFlush(reservation);

        reservation.setStatus(ReservationStatus.CANCELLED);
        reservationRepository.saveAndFlush(reservation);

        Reservation found = entityManager.find(Reservation.class, reservation.getReservationId());
        assertThat(found.getStatus()).isEqualTo(ReservationStatus.CANCELLED);
    }

    @Test
    void findById_returnsPersistedReservation() {
        Reservation reservation = createReservation(customer, event, 2);
        entityManager.persistAndFlush(reservation);

        var found = reservationRepository.findById(reservation.getReservationId());

        assertThat(found).isPresent();
        assertThat(found.get().getEvent().getTitle()).isEqualTo("Jazz Night");
    }

    private Reservation createReservation(User user, Event event, int quantity) {
        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setEvent(event);
        reservation.setQuantity(quantity);
        reservation.setTotalPrice(new BigDecimal("49.99").multiply(BigDecimal.valueOf(quantity)));
        reservation.setStatus(ReservationStatus.CONFIRMED);
        return reservation;
    }
}
