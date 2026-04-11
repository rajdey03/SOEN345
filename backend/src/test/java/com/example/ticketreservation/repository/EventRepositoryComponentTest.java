package com.example.ticketreservation.repository;

import com.example.ticketreservation.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Component test for EventRepository.
 * Uses @DataJpaTest to load only the JPA layer with an in-memory H2 database.
 * Tests custom query methods including search filtering and date-based queries.
 */
@DataJpaTest
class EventRepositoryComponentTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private EventRepository eventRepository;

    private Organizer organizer;

    @BeforeEach
    void setUp() {
        User adminUser = new User("Admin", "User", "admin@test.com", null, "hashedpass");
        adminUser.setRole(UserRole.ADMIN);
        entityManager.persistAndFlush(adminUser);

        organizer = new Organizer();
        organizer.setUser(adminUser);
        organizer.setOrganizationName("Test Org");
        organizer.setContactEmail("org@test.com");
        entityManager.persistAndFlush(organizer);
    }

    @Test
    void findByStatusAndEventDateGreaterThanEqual_returnsActiveUpcomingEvents() {
        Event upcoming = createEvent("Upcoming Concert", "CONCERT", "Montreal",
                LocalDate.now().plusDays(10), EventStatus.ACTIVE);
        Event past = createEvent("Past Concert", "CONCERT", "Montreal",
                LocalDate.now().minusDays(5), EventStatus.ACTIVE);
        Event cancelled = createEvent("Cancelled Concert", "CONCERT", "Montreal",
                LocalDate.now().plusDays(10), EventStatus.CANCELLED);
        entityManager.persistAndFlush(upcoming);
        entityManager.persistAndFlush(past);
        entityManager.persistAndFlush(cancelled);

        List<Event> results = eventRepository.findByStatusAndEventDateGreaterThanEqual(
                EventStatus.ACTIVE, LocalDate.now());

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getTitle()).isEqualTo("Upcoming Concert");
    }

    @Test
    void findAllByOrderByEventDateDescStartTimeDesc_returnsOrderedEvents() {
        Event event1 = createEvent("Early Event", "CONCERT", "Montreal",
                LocalDate.now().plusDays(5), EventStatus.ACTIVE);
        event1.setStartTime(LocalTime.of(10, 0));
        Event event2 = createEvent("Late Event", "CONCERT", "Montreal",
                LocalDate.now().plusDays(10), EventStatus.ACTIVE);
        event2.setStartTime(LocalTime.of(20, 0));
        entityManager.persistAndFlush(event1);
        entityManager.persistAndFlush(event2);

        List<Event> results = eventRepository.findAllByOrderByEventDateDescStartTimeDesc();

        assertThat(results).hasSizeGreaterThanOrEqualTo(2);
        // Later date should come first (DESC order)
        assertThat(results.get(0).getTitle()).isEqualTo("Late Event");
    }

    @Test
    void searchEvents_filtersByKeyword() {
        Event jazzEvent = createEvent("Jazz Night", "CONCERT", "Blue Note",
                LocalDate.now().plusDays(10), EventStatus.ACTIVE);
        Event rockEvent = createEvent("Rock Show", "CONCERT", "Stadium",
                LocalDate.now().plusDays(10), EventStatus.ACTIVE);
        entityManager.persistAndFlush(jazzEvent);
        entityManager.persistAndFlush(rockEvent);

        List<Event> results = eventRepository.searchEvents(
                EventStatus.ACTIVE, "jazz", null, null);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getTitle()).isEqualTo("Jazz Night");
    }

    @Test
    void searchEvents_filtersByCategory() {
        Event concert = createEvent("Jazz Night", "CONCERT", "Blue Note",
                LocalDate.now().plusDays(10), EventStatus.ACTIVE);
        Event sports = createEvent("Soccer Match", "SPORTS", "Stadium",
                LocalDate.now().plusDays(10), EventStatus.ACTIVE);
        entityManager.persistAndFlush(concert);
        entityManager.persistAndFlush(sports);

        List<Event> results = eventRepository.searchEvents(
                EventStatus.ACTIVE, null, "SPORTS", null);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getTitle()).isEqualTo("Soccer Match");
    }

    @Test
    void searchEvents_filtersByDate() {
        LocalDate targetDate = LocalDate.now().plusDays(15);
        Event event1 = createEvent("Event on Target", "CONCERT", "Venue",
                targetDate, EventStatus.ACTIVE);
        Event event2 = createEvent("Event Other Day", "CONCERT", "Venue",
                LocalDate.now().plusDays(10), EventStatus.ACTIVE);
        entityManager.persistAndFlush(event1);
        entityManager.persistAndFlush(event2);

        List<Event> results = eventRepository.searchEvents(
                EventStatus.ACTIVE, null, null, targetDate);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getTitle()).isEqualTo("Event on Target");
    }

    @Test
    void searchEvents_filtersByLocation() {
        Event event = createEvent("Jazz Night", "CONCERT", "Blue Note Club",
                LocalDate.now().plusDays(10), EventStatus.ACTIVE);
        entityManager.persistAndFlush(event);

        List<Event> results = eventRepository.searchEvents(
                EventStatus.ACTIVE, "blue note", null, null);

        assertThat(results).hasSize(1);
    }

    @Test
    void searchEvents_returnsEmptyForNoMatch() {
        Event event = createEvent("Jazz Night", "CONCERT", "Blue Note",
                LocalDate.now().plusDays(10), EventStatus.ACTIVE);
        entityManager.persistAndFlush(event);

        List<Event> results = eventRepository.searchEvents(
                EventStatus.ACTIVE, "nonexistent", null, null);

        assertThat(results).isEmpty();
    }

    @Test
    void searchEvents_excludesCancelledEvents() {
        Event cancelled = createEvent("Cancelled Jazz", "CONCERT", "Blue Note",
                LocalDate.now().plusDays(10), EventStatus.CANCELLED);
        entityManager.persistAndFlush(cancelled);

        List<Event> results = eventRepository.searchEvents(
                EventStatus.ACTIVE, "jazz", null, null);

        assertThat(results).isEmpty();
    }

    @Test
    void searchEvents_withAllNullFiltersReturnsAllActiveEvents() {
        Event event1 = createEvent("Jazz Night", "CONCERT", "Montreal",
                LocalDate.now().plusDays(10), EventStatus.ACTIVE);
        Event event2 = createEvent("Rock Show", "CONCERT", "Toronto",
                LocalDate.now().plusDays(10), EventStatus.ACTIVE);
        entityManager.persistAndFlush(event1);
        entityManager.persistAndFlush(event2);

        List<Event> results = eventRepository.searchEvents(
                EventStatus.ACTIVE, null, null, null);

        assertThat(results).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    void save_persistsEventWithAllFields() {
        Event event = createEvent("Full Event", "SPORTS", "Stadium",
                LocalDate.now().plusDays(20), EventStatus.ACTIVE);

        Event saved = eventRepository.saveAndFlush(event);

        assertThat(saved.getEventId()).isNotNull();
        assertThat(saved.getTitle()).isEqualTo("Full Event");
        assertThat(saved.getCategory()).isEqualTo("SPORTS");
        assertThat(saved.getTotalCapacity()).isEqualTo(200);
        assertThat(saved.getAvailableCapacity()).isEqualTo(200);
        assertThat(saved.getCreatedAt()).isNotNull();
    }

    private Event createEvent(String title, String category, String location,
                               LocalDate eventDate, EventStatus status) {
        Event event = new Event();
        event.setOrganizer(organizer);
        event.setTitle(title);
        event.setDescription("Test event description for " + title);
        event.setCategory(category);
        event.setLocation(location);
        event.setEventDate(eventDate);
        event.setStartTime(LocalTime.of(19, 0));
        event.setEndTime(LocalTime.of(22, 0));
        event.setTotalCapacity(200);
        event.setAvailableCapacity(200);
        event.setStatus(status);
        return event;
    }
}
