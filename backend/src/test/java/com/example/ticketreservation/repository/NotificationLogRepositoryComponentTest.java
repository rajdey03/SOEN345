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
 * Component test for NotificationLogRepository.
 * Uses @DataJpaTest to load only the JPA layer with an in-memory H2 database.
 * Tests entity persistence and relationships.
 */
@DataJpaTest
class NotificationLogRepositoryComponentTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private NotificationLogRepository notificationLogRepository;

    private User user;
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

        user = new User("John", "Doe", "john@test.com", "5141234567", "hashedpass");
        user.setRole(UserRole.CUSTOMER);
        entityManager.persistAndFlush(user);

        Event event = new Event();
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
        reservation.setUser(user);
        reservation.setEvent(event);
        reservation.setQuantity(2);
        reservation.setTotalPrice(new BigDecimal("99.98"));
        reservation.setStatus(ReservationStatus.CONFIRMED);
        entityManager.persistAndFlush(reservation);
    }

    @Test
    void save_persistsRegistrationConfirmationLog() {
        NotificationLog log = new NotificationLog();
        log.setUser(user);
        log.setChannel(NotificationChannel.EMAIL);
        log.setMessageType(NotificationMessageType.REGISTRATION_CONFIRMATION);
        log.setStatus(NotificationStatus.SENT);

        NotificationLog saved = notificationLogRepository.saveAndFlush(log);

        assertThat(saved.getNotificationId()).isNotNull();
        assertThat(saved.getChannel()).isEqualTo(NotificationChannel.EMAIL);
        assertThat(saved.getMessageType()).isEqualTo(NotificationMessageType.REGISTRATION_CONFIRMATION);
        assertThat(saved.getStatus()).isEqualTo(NotificationStatus.SENT);
    }

    @Test
    void save_persistsBookingConfirmationWithReservation() {
        NotificationLog log = new NotificationLog();
        log.setUser(user);
        log.setReservation(reservation);
        log.setChannel(NotificationChannel.EMAIL);
        log.setMessageType(NotificationMessageType.BOOKING_CONFIRMATION);
        log.setStatus(NotificationStatus.SENT);

        NotificationLog saved = notificationLogRepository.saveAndFlush(log);

        assertThat(saved.getReservation().getReservationId())
                .isEqualTo(reservation.getReservationId());
    }

    @Test
    void save_persistsCancellationNotice() {
        NotificationLog log = new NotificationLog();
        log.setUser(user);
        log.setReservation(reservation);
        log.setChannel(NotificationChannel.EMAIL);
        log.setMessageType(NotificationMessageType.CANCELLATION_NOTICE);
        log.setStatus(NotificationStatus.PENDING);

        NotificationLog saved = notificationLogRepository.saveAndFlush(log);

        assertThat(saved.getMessageType()).isEqualTo(NotificationMessageType.CANCELLATION_NOTICE);
        assertThat(saved.getStatus()).isEqualTo(NotificationStatus.PENDING);
    }

    @Test
    void save_updatesNotificationStatus() {
        NotificationLog log = new NotificationLog();
        log.setUser(user);
        log.setChannel(NotificationChannel.EMAIL);
        log.setMessageType(NotificationMessageType.REGISTRATION_CONFIRMATION);
        log.setStatus(NotificationStatus.PENDING);
        notificationLogRepository.saveAndFlush(log);

        log.setStatus(NotificationStatus.SENT);
        notificationLogRepository.saveAndFlush(log);

        Optional<NotificationLog> found = notificationLogRepository.findById(log.getNotificationId());
        assertThat(found).isPresent();
        assertThat(found.get().getStatus()).isEqualTo(NotificationStatus.SENT);
    }

    @Test
    void save_persistsSmsNotification() {
        NotificationLog log = new NotificationLog();
        log.setUser(user);
        log.setChannel(NotificationChannel.SMS);
        log.setMessageType(NotificationMessageType.REGISTRATION_CONFIRMATION);
        log.setStatus(NotificationStatus.SENT);

        NotificationLog saved = notificationLogRepository.saveAndFlush(log);

        assertThat(saved.getChannel()).isEqualTo(NotificationChannel.SMS);
    }

    @Test
    void save_persistsFailedNotification() {
        NotificationLog log = new NotificationLog();
        log.setUser(user);
        log.setChannel(NotificationChannel.EMAIL);
        log.setMessageType(NotificationMessageType.BOOKING_CONFIRMATION);
        log.setStatus(NotificationStatus.FAILED);

        NotificationLog saved = notificationLogRepository.saveAndFlush(log);

        assertThat(saved.getStatus()).isEqualTo(NotificationStatus.FAILED);
    }

    @Test
    void findAll_returnsMultipleNotifications() {
        NotificationLog log1 = new NotificationLog();
        log1.setUser(user);
        log1.setChannel(NotificationChannel.EMAIL);
        log1.setMessageType(NotificationMessageType.REGISTRATION_CONFIRMATION);
        log1.setStatus(NotificationStatus.SENT);

        NotificationLog log2 = new NotificationLog();
        log2.setUser(user);
        log2.setReservation(reservation);
        log2.setChannel(NotificationChannel.EMAIL);
        log2.setMessageType(NotificationMessageType.BOOKING_CONFIRMATION);
        log2.setStatus(NotificationStatus.SENT);

        notificationLogRepository.saveAndFlush(log1);
        notificationLogRepository.saveAndFlush(log2);

        assertThat(notificationLogRepository.findAll()).hasSizeGreaterThanOrEqualTo(2);
    }
}
