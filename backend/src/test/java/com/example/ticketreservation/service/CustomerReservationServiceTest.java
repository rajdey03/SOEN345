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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerReservationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private TicketRepository ticketRepository;

    @InjectMocks
    private CustomerReservationService customerReservationService;

    private User sampleUser;
    private Event sampleEvent;
    private UUID userId;
    private UUID eventId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        eventId = UUID.randomUUID();

        sampleUser = new User();
        sampleUser.setUserId(userId);
        sampleUser.setFirstName("Jane");
        sampleUser.setLastName("Doe");
        sampleUser.setEmail("jane@example.com");
        sampleUser.setRole(UserRole.CUSTOMER);

        Organizer organizer = new Organizer();
        organizer.setOrganizerId(UUID.randomUUID());
        organizer.setOrganizationName("Test Org");

        sampleEvent = new Event();
        sampleEvent.setEventId(eventId);
        sampleEvent.setOrganizer(organizer);
        sampleEvent.setTitle("Jazz Night");
        sampleEvent.setDescription("A smooth jazz evening");
        sampleEvent.setCategory("CONCERT");
        sampleEvent.setLocation("Blue Note Club");
        sampleEvent.setEventDate(LocalDate.now().plusDays(10));
        sampleEvent.setStartTime(LocalTime.of(20, 0));
        sampleEvent.setEndTime(LocalTime.of(23, 0));
        sampleEvent.setTotalCapacity(100);
        sampleEvent.setAvailableCapacity(50);
        sampleEvent.setStatus(EventStatus.ACTIVE);
    }

    // --- Successful reservation tests ---

    @Test
    void createReservation_successfullyCreatesReservation() {
        ReservationRequest request = new ReservationRequest();
        request.setCustomerId(userId);
        request.setEventId(eventId);
        request.setNumberOfTickets(2);

        when(userRepository.findById(userId)).thenReturn(Optional.of(sampleUser));
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(sampleEvent));
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(invocation -> {
            Reservation r = invocation.getArgument(0);
            r.setReservationId(UUID.randomUUID());
            return r;
        });
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ReservationResponse response = customerReservationService.createReservation(request);

        assertThat(response).isNotNull();
        assertThat(response.getReservationId()).isNotNull();
        assertThat(response.getCustomerId()).isEqualTo(userId);
        assertThat(response.getEventId()).isEqualTo(eventId);
        assertThat(response.getQuantity()).isEqualTo(2);
        assertThat(response.getTotalPrice()).isEqualByComparingTo(new BigDecimal("99.98"));
        assertThat(response.getStatus()).isEqualTo(ReservationStatus.CONFIRMED);
    }

    @Test
    void createReservation_decreasesAvailableCapacity() {
        ReservationRequest request = new ReservationRequest();
        request.setCustomerId(userId);
        request.setEventId(eventId);
        request.setNumberOfTickets(3);

        when(userRepository.findById(userId)).thenReturn(Optional.of(sampleUser));
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(sampleEvent));
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(invocation -> {
            Reservation r = invocation.getArgument(0);
            r.setReservationId(UUID.randomUUID());
            return r;
        });
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(invocation -> invocation.getArgument(0));

        customerReservationService.createReservation(request);

        ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);
        verify(eventRepository).save(eventCaptor.capture());
        assertThat(eventCaptor.getValue().getAvailableCapacity()).isEqualTo(47);
    }

    @Test
    void createReservation_generatesCorrectNumberOfTickets() {
        ReservationRequest request = new ReservationRequest();
        request.setCustomerId(userId);
        request.setEventId(eventId);
        request.setNumberOfTickets(4);

        when(userRepository.findById(userId)).thenReturn(Optional.of(sampleUser));
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(sampleEvent));
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(invocation -> {
            Reservation r = invocation.getArgument(0);
            r.setReservationId(UUID.randomUUID());
            return r;
        });
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(invocation -> invocation.getArgument(0));

        customerReservationService.createReservation(request);

        verify(ticketRepository, times(4)).save(any(Ticket.class));
    }

    @Test
    void createReservation_setsEventToSoldOutWhenCapacityReachesZero() {
        sampleEvent.setAvailableCapacity(2);

        ReservationRequest request = new ReservationRequest();
        request.setCustomerId(userId);
        request.setEventId(eventId);
        request.setNumberOfTickets(2);

        when(userRepository.findById(userId)).thenReturn(Optional.of(sampleUser));
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(sampleEvent));
        when(reservationRepository.save(any(Reservation.class))).thenAnswer(invocation -> {
            Reservation r = invocation.getArgument(0);
            r.setReservationId(UUID.randomUUID());
            return r;
        });
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(invocation -> invocation.getArgument(0));

        customerReservationService.createReservation(request);

        ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);
        verify(eventRepository).save(eventCaptor.capture());
        assertThat(eventCaptor.getValue().getStatus()).isEqualTo(EventStatus.SOLD_OUT);
        assertThat(eventCaptor.getValue().getAvailableCapacity()).isEqualTo(0);
    }

    // --- Error case tests ---

    @Test
    void createReservation_throwsWhenCustomerNotFound() {
        ReservationRequest request = new ReservationRequest();
        request.setCustomerId(UUID.randomUUID());
        request.setEventId(eventId);
        request.setNumberOfTickets(1);

        when(userRepository.findById(request.getCustomerId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customerReservationService.createReservation(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Customer not found");
    }

    @Test
    void createReservation_throwsWhenEventNotFound() {
        ReservationRequest request = new ReservationRequest();
        request.setCustomerId(userId);
        request.setEventId(UUID.randomUUID());
        request.setNumberOfTickets(1);

        when(userRepository.findById(userId)).thenReturn(Optional.of(sampleUser));
        when(eventRepository.findById(request.getEventId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customerReservationService.createReservation(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Event not found");
    }

    @Test
    void createReservation_throwsWhenEventIsCancelled() {
        sampleEvent.setStatus(EventStatus.CANCELLED);

        ReservationRequest request = new ReservationRequest();
        request.setCustomerId(userId);
        request.setEventId(eventId);
        request.setNumberOfTickets(1);

        when(userRepository.findById(userId)).thenReturn(Optional.of(sampleUser));
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(sampleEvent));

        assertThatThrownBy(() -> customerReservationService.createReservation(request))
                .isInstanceOf(InsufficientCapacityException.class)
                .hasMessageContaining("cancelled");
    }

    @Test
    void createReservation_throwsWhenNotEnoughCapacity() {
        sampleEvent.setAvailableCapacity(1);

        ReservationRequest request = new ReservationRequest();
        request.setCustomerId(userId);
        request.setEventId(eventId);
        request.setNumberOfTickets(5);

        when(userRepository.findById(userId)).thenReturn(Optional.of(sampleUser));
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(sampleEvent));

        assertThatThrownBy(() -> customerReservationService.createReservation(request))
                .isInstanceOf(InsufficientCapacityException.class)
                .hasMessageContaining("Not enough tickets available");
    }

    @Test
    void createReservation_throwsWhenZeroCapacity() {
        sampleEvent.setAvailableCapacity(0);

        ReservationRequest request = new ReservationRequest();
        request.setCustomerId(userId);
        request.setEventId(eventId);
        request.setNumberOfTickets(1);

        when(userRepository.findById(userId)).thenReturn(Optional.of(sampleUser));
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(sampleEvent));

        assertThatThrownBy(() -> customerReservationService.createReservation(request))
                .isInstanceOf(InsufficientCapacityException.class)
                .hasMessageContaining("Not enough tickets");
    }
}
