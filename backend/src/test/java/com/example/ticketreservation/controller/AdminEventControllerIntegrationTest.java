package com.example.ticketreservation.controller;

import com.example.ticketreservation.dto.AdminEventRequest;
import com.example.ticketreservation.model.Event;
import com.example.ticketreservation.model.EventStatus;
import com.example.ticketreservation.model.Organizer;
import com.example.ticketreservation.model.User;
import com.example.ticketreservation.model.UserRole;
import com.example.ticketreservation.repository.EventRepository;
import com.example.ticketreservation.repository.OrganizerRepository;
import com.example.ticketreservation.repository.UserRepository;
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

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("h2")
class AdminEventControllerIntegrationTest {

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

    private UUID adminUserId;
    private UUID organizerId;
    private UUID customerUserId;

    @BeforeEach
    void setUp() {
        eventRepository.deleteAll();
        organizerRepository.deleteAll();
        userRepository.deleteAll();

        User admin = new User("Admin", "User", "admin@example.com", null, "hashed");
        admin.setRole(UserRole.ADMIN);
        adminUserId = userRepository.save(admin).getUserId();

        User organizerUser = new User("Olivia", "Org", "organizer@example.com", null, "hashed");
        organizerUser.setRole(UserRole.ORGANIZER);
        organizerUser = userRepository.save(organizerUser);

        Organizer organizer = new Organizer();
        organizer.setUser(organizerUser);
        organizer.setOrganizationName("Montreal Events");
        organizer.setContactEmail("contact@montrealevents.example");
        organizer.setContactPhone("+15145550111");
        organizerId = organizerRepository.save(organizer).getOrganizerId();

        User customer = new User("Chris", "Guest", "guest@example.com", null, "hashed");
        customer.setRole(UserRole.CUSTOMER);
        customerUserId = userRepository.save(customer).getUserId();
    }

    @Test
    void createEvent_createsEventForAdmin() throws Exception {
        AdminEventRequest request = buildRequest("Jazz Night", 120);

        mockMvc.perform(post("/api/admin/events")
                        .header("X-Admin-User-Id", adminUserId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.event_id", notNullValue()))
                .andExpect(jsonPath("$.organizer_id", is(organizerId.toString())))
                .andExpect(jsonPath("$.title", is("Jazz Night")))
                .andExpect(jsonPath("$.status", is(EventStatus.ACTIVE.name())))
                .andExpect(jsonPath("$.available_capacity", is(120)));
    }

    @Test
    void createEvent_rejectsNonAdminUser() throws Exception {
        AdminEventRequest request = buildRequest("Jazz Night", 120);

        mockMvc.perform(post("/api/admin/events")
                        .header("X-Admin-User-Id", customerUserId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message", is("User is not authorized to manage events.")));
    }

    @Test
    void getEvents_returnsAdminManagedEvents() throws Exception {
        Event first = new Event();
        first.setOrganizer(organizerRepository.findById(organizerId).orElseThrow());
        first.setTitle("Admin Event One");
        first.setDescription("First admin event");
        first.setCategory("Concert");
        first.setLocation("Hall A");
        first.setEventDate(LocalDate.now().plusDays(3));
        first.setStartTime(LocalTime.of(18, 0));
        first.setEndTime(LocalTime.of(20, 0));
        first.setTotalCapacity(50);
        first.setAvailableCapacity(50);
        first.setStatus(EventStatus.ACTIVE);
        eventRepository.save(first);

        Event second = new Event();
        second.setOrganizer(organizerRepository.findById(organizerId).orElseThrow());
        second.setTitle("Admin Event Two");
        second.setDescription("Second admin event");
        second.setCategory("Expo");
        second.setLocation("Hall B");
        second.setEventDate(LocalDate.now().plusDays(6));
        second.setStartTime(LocalTime.of(10, 0));
        second.setEndTime(LocalTime.of(16, 0));
        second.setTotalCapacity(80);
        second.setAvailableCapacity(80);
        second.setStatus(EventStatus.CANCELLED);
        eventRepository.save(second);

        mockMvc.perform(get("/api/admin/events")
                        .header("X-Admin-User-Id", adminUserId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].title", is("Admin Event Two")))
                .andExpect(jsonPath("$[0].status", is(EventStatus.CANCELLED.name())))
                .andExpect(jsonPath("$[1].title", is("Admin Event One")));
    }

    @Test
    void updateEvent_updatesExistingEvent() throws Exception {
        Event event = new Event();
        event.setOrganizer(organizerRepository.findById(organizerId).orElseThrow());
        event.setTitle("Old Title");
        event.setDescription("Old Description");
        event.setCategory("Concert");
        event.setLocation("Hall A");
        event.setEventDate(LocalDate.now().plusDays(10));
        event.setStartTime(LocalTime.of(18, 0));
        event.setEndTime(LocalTime.of(20, 0));
        event.setTotalCapacity(100);
        event.setAvailableCapacity(100);
        event.setStatus(EventStatus.ACTIVE);
        UUID eventId = eventRepository.save(event).getEventId();

        AdminEventRequest updateRequest = buildRequest("Updated Title", 150);
        updateRequest.setDescription("Updated Description");
        updateRequest.setLocation("Hall B");

        mockMvc.perform(put("/api/admin/events/{eventId}", eventId)
                        .header("X-Admin-User-Id", adminUserId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Updated Title")))
                .andExpect(jsonPath("$.description", is("Updated Description")))
                .andExpect(jsonPath("$.location", is("Hall B")))
                .andExpect(jsonPath("$.total_capacity", is(150)))
                .andExpect(jsonPath("$.available_capacity", is(150)));
    }

    @Test
    void cancelEvent_marksEventAsCancelled() throws Exception {
        Event event = new Event();
        event.setOrganizer(organizerRepository.findById(organizerId).orElseThrow());
        event.setTitle("Tech Expo");
        event.setDescription("Expo description");
        event.setCategory("Expo");
        event.setLocation("Convention Center");
        event.setEventDate(LocalDate.now().plusDays(14));
        event.setStartTime(LocalTime.of(9, 0));
        event.setEndTime(LocalTime.of(17, 0));
        event.setTotalCapacity(300);
        event.setAvailableCapacity(300);
        event.setStatus(EventStatus.ACTIVE);
        UUID eventId = eventRepository.save(event).getEventId();

        mockMvc.perform(patch("/api/admin/events/{eventId}/cancel", eventId)
                        .header("X-Admin-User-Id", adminUserId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.event_id", is(eventId.toString())))
                .andExpect(jsonPath("$.status", is(EventStatus.CANCELLED.name())));
    }

    private AdminEventRequest buildRequest(String title, int totalCapacity) {
        AdminEventRequest request = new AdminEventRequest();
        request.setOrganizerId(organizerId);
        request.setTitle(title);
        request.setDescription("A live event in Montreal.");
        request.setCategory("Concert");
        request.setLocation("Place des Arts");
        request.setEventDate(LocalDate.now().plusDays(7));
        request.setStartTime(LocalTime.of(19, 0));
        request.setEndTime(LocalTime.of(22, 0));
        request.setTotalCapacity(totalCapacity);
        return request;
    }
}
