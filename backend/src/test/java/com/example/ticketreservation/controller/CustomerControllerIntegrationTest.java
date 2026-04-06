package com.example.ticketreservation.controller;

import com.example.ticketreservation.dto.ReservationRequest;
import com.example.ticketreservation.model.*;
import com.example.ticketreservation.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("h2")
class CustomerControllerIntegrationTest {

    @MockBean
    private JavaMailSender mailSender;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrganizerRepository organizerRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private TicketRepository ticketRepository;

    private UUID customerId;
    private UUID eventId;
    private UUID organizerId;

    @BeforeEach
    void setUp() {
        ticketRepository.deleteAll();
        reservationRepository.deleteAll();
        eventRepository.deleteAll();
        organizerRepository.deleteAll();
        userRepository.deleteAll();

        // Create a customer
        User customer = new User("Jane", "Doe", "jane@example.com", null, "hashed");
        customer.setRole(UserRole.CUSTOMER);
        customerId = userRepository.save(customer).getUserId();

        // Create an organizer
        User orgUser = new User("Org", "Admin", "org@example.com", null, "hashed");
        orgUser.setRole(UserRole.ORGANIZER);
        orgUser = userRepository.save(orgUser);

        Organizer organizer = new Organizer();
        organizer.setUser(orgUser);
        organizer.setOrganizationName("Montreal Events");
        organizer.setContactEmail("contact@montrealevents.example");
        organizerId = organizerRepository.save(organizer).getOrganizerId();

        // Create events
        eventId = createEvent("Jazz Night", "A smooth jazz evening", "CONCERT",
                "Blue Note Club", LocalDate.now().plusDays(10), 100, 80).getEventId();

        createEvent("Rock Festival", "Outdoor rock music", "CONCERT",
                "Olympic Stadium", LocalDate.now().plusDays(20), 5000, 3000);

        createEvent("Avengers Movie", "Marvel blockbuster", "MOVIE",
                "AMC Cinema", LocalDate.now().plusDays(5), 200, 150);

        // Cancelled event — should NOT appear in browse/search
        Event cancelled = createEvent("Old Show", "Cancelled event", "CONCERT",
                "Nowhere", LocalDate.now().plusDays(30), 100, 100);
        cancelled.setStatus(EventStatus.CANCELLED);
        eventRepository.save(cancelled);

        // Past event — should NOT appear in browse
        createEvent("Past Concert", "Already happened", "CONCERT",
                "Old Venue", LocalDate.now().minusDays(5), 100, 50);
    }

    // =======================================================================
    // GET /api/customers/events — Browsing
    // =======================================================================

    @Test
    void browseEvents_returnsOnlyActiveUpcomingEvents() throws Exception {
        mockMvc.perform(get("/api/customers/events"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[*].title", hasItems("Jazz Night", "Rock Festival", "Avengers Movie")))
                .andExpect(jsonPath("$[*].title", not(hasItem("Old Show"))))
                .andExpect(jsonPath("$[*].title", not(hasItem("Past Concert"))));
    }

    @Test
    void browseEvents_containsExpectedFields() throws Exception {
        mockMvc.perform(get("/api/customers/events"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].eventId", notNullValue()))
                .andExpect(jsonPath("$[0].title", notNullValue()))
                .andExpect(jsonPath("$[0].category", notNullValue()))
                .andExpect(jsonPath("$[0].location", notNullValue()))
                .andExpect(jsonPath("$[0].availableCapacity", notNullValue()))
                .andExpect(jsonPath("$[0].organizerName", is("Montreal Events")));
    }

    // =======================================================================
    // GET /api/customers/events/search — Filtering
    // =======================================================================

    @Test
    void searchEvents_filterByKeyword() throws Exception {
        mockMvc.perform(get("/api/customers/events/search")
                        .param("keyword", "jazz"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title", is("Jazz Night")));
    }

    @Test
    void searchEvents_filterByCategory() throws Exception {
        mockMvc.perform(get("/api/customers/events/search")
                        .param("category", "MOVIE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title", is("Avengers Movie")));
    }

    @Test
    void searchEvents_filterByDate() throws Exception {
        String targetDate = LocalDate.now().plusDays(10).toString();

        mockMvc.perform(get("/api/customers/events/search")
                        .param("date", targetDate))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title", is("Jazz Night")));
    }

    @Test
    void searchEvents_filterByCombinedParams() throws Exception {
        mockMvc.perform(get("/api/customers/events/search")
                        .param("keyword", "rock")
                        .param("category", "CONCERT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title", is("Rock Festival")));
    }

    @Test
    void searchEvents_noMatchReturnsEmpty() throws Exception {
        mockMvc.perform(get("/api/customers/events/search")
                        .param("keyword", "nonexistent"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void searchEvents_excludesCancelledEvents() throws Exception {
        mockMvc.perform(get("/api/customers/events/search")
                        .param("keyword", "Old Show"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void searchEvents_keywordMatchesLocation() throws Exception {
        mockMvc.perform(get("/api/customers/events/search")
                        .param("keyword", "Olympic"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title", is("Rock Festival")));
    }

    // =======================================================================
    // POST /api/customers/reservations — Reserving
    // =======================================================================

    @Test
    void createReservation_successfullyReservesTickets() throws Exception {
        ReservationRequest request = new ReservationRequest();
        request.setCustomerId(customerId);
        request.setEventId(eventId);
        request.setNumberOfTickets(2);

        mockMvc.perform(post("/api/customers/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.reservationId", notNullValue()))
                .andExpect(jsonPath("$.customerId", is(customerId.toString())))
                .andExpect(jsonPath("$.eventId", is(eventId.toString())))
                .andExpect(jsonPath("$.eventTitle", is("Jazz Night")))
                .andExpect(jsonPath("$.quantity", is(2)))
                .andExpect(jsonPath("$.totalPrice", is(99.98)))
                .andExpect(jsonPath("$.status", is("CONFIRMED")));
    }

    @Test
    void createReservation_decreasesAvailableCapacity() throws Exception {
        ReservationRequest request = new ReservationRequest();
        request.setCustomerId(customerId);
        request.setEventId(eventId);
        request.setNumberOfTickets(5);

        mockMvc.perform(post("/api/customers/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        // Verify capacity decreased: was 80, now should be 75
        mockMvc.perform(get("/api/customers/events/search")
                        .param("keyword", "Jazz Night"))
                .andExpect(jsonPath("$[0].availableCapacity", is(75)));
    }

    @Test
    void createReservation_failsWhenNotEnoughCapacity() throws Exception {
        ReservationRequest request = new ReservationRequest();
        request.setCustomerId(customerId);
        request.setEventId(eventId);
        request.setNumberOfTickets(999);

        mockMvc.perform(post("/api/customers/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", containsString("Not enough tickets")));
    }

    @Test
    void createReservation_failsWithInvalidCustomerId() throws Exception {
        ReservationRequest request = new ReservationRequest();
        request.setCustomerId(UUID.randomUUID());
        request.setEventId(eventId);
        request.setNumberOfTickets(1);

        mockMvc.perform(post("/api/customers/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", containsString("Customer not found")));
    }

    @Test
    void createReservation_failsWithInvalidEventId() throws Exception {
        ReservationRequest request = new ReservationRequest();
        request.setCustomerId(customerId);
        request.setEventId(UUID.randomUUID());
        request.setNumberOfTickets(1);

        mockMvc.perform(post("/api/customers/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", containsString("Event not found")));
    }

    @Test
    void createReservation_failsForCancelledEvent() throws Exception {
        Event cancelledEvent = createEvent("Cancelled Gig", "Nope", "CONCERT",
                "Nowhere", LocalDate.now().plusDays(15), 100, 100);
        cancelledEvent.setStatus(EventStatus.CANCELLED);
        eventRepository.save(cancelledEvent);

        ReservationRequest request = new ReservationRequest();
        request.setCustomerId(customerId);
        request.setEventId(cancelledEvent.getEventId());
        request.setNumberOfTickets(1);

        mockMvc.perform(post("/api/customers/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", containsString("cancelled")));
    }

        // =======================================================================
        // DELETE /api/customers/reservations/{reservationId} — Cancellation
        // =======================================================================

        @Test
        void cancelReservation_successfullyCancelsAndRestoresCapacity() throws Exception {
                Event event = eventRepository.findById(eventId).orElseThrow();
                event.setAvailableCapacity(77);
                eventRepository.save(event);

                Reservation reservation = new Reservation();
                reservation.setUser(userRepository.findById(customerId).orElseThrow());
                reservation.setEvent(event);
                reservation.setQuantity(3);
                reservation.setTotalPrice(java.math.BigDecimal.valueOf(149.97));
                reservation.setStatus(ReservationStatus.CONFIRMED);
                reservation = reservationRepository.save(reservation);

                mockMvc.perform(delete("/api/customers/reservations/{reservationId}", reservation.getReservationId())
                                                .param("userId", customerId.toString()))
                                .andExpect(status().isOk())
                                .andExpect(content().string("Reservation cancelled."));

                Reservation persistedReservation = reservationRepository.findById(reservation.getReservationId()).orElseThrow();
                Event persistedEvent = eventRepository.findById(eventId).orElseThrow();

                org.junit.jupiter.api.Assertions.assertEquals(ReservationStatus.CANCELLED, persistedReservation.getStatus());
                org.junit.jupiter.api.Assertions.assertEquals(80, persistedEvent.getAvailableCapacity());
        }

        @Test
        void cancelReservation_returnsNotFoundWhenReservationDoesNotBelongToUser() throws Exception {
                User otherCustomer = new User("Other", "User", "other@example.com", null, "hashed");
                otherCustomer.setRole(UserRole.CUSTOMER);
                UUID otherCustomerId = userRepository.save(otherCustomer).getUserId();

                Event event = eventRepository.findById(eventId).orElseThrow();
                Reservation reservation = new Reservation();
                reservation.setUser(userRepository.findById(customerId).orElseThrow());
                reservation.setEvent(event);
                reservation.setQuantity(1);
                reservation.setTotalPrice(java.math.BigDecimal.valueOf(49.99));
                reservation.setStatus(ReservationStatus.CONFIRMED);
                reservation = reservationRepository.save(reservation);

                mockMvc.perform(delete("/api/customers/reservations/{reservationId}", reservation.getReservationId())
                                                .param("userId", otherCustomerId.toString()))
                                .andExpect(status().isNotFound())
                                .andExpect(content().string("Reservation not found or not yours."));

                Reservation persistedReservation = reservationRepository.findById(reservation.getReservationId()).orElseThrow();
                org.junit.jupiter.api.Assertions.assertEquals(ReservationStatus.CONFIRMED, persistedReservation.getStatus());
        }

        @Test
        void cancelReservation_returnsNotFoundWhenReservationDoesNotExist() throws Exception {
                mockMvc.perform(delete("/api/customers/reservations/{reservationId}", UUID.randomUUID())
                                                .param("userId", customerId.toString()))
                                .andExpect(status().isNotFound())
                                .andExpect(content().string("Reservation not found or not yours."));
        }

    // --- Helper ---

    private Event createEvent(String title, String description, String category,
                               String location, LocalDate date, int total, int available) {
        Event event = new Event();
        event.setOrganizer(organizerRepository.findById(organizerId).orElseThrow());
        event.setTitle(title);
        event.setDescription(description);
        event.setCategory(category);
        event.setLocation(location);
        event.setEventDate(date);
        event.setStartTime(LocalTime.of(19, 0));
        event.setEndTime(LocalTime.of(22, 0));
        event.setTotalCapacity(total);
        event.setAvailableCapacity(available);
        event.setStatus(EventStatus.ACTIVE);
        return eventRepository.save(event);
    }
}
