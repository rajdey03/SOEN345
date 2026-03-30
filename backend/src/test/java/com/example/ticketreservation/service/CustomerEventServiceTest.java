package com.example.ticketreservation.service;

import com.example.ticketreservation.dto.EventResponse;
import com.example.ticketreservation.model.*;
import com.example.ticketreservation.repository.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerEventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private CustomerEventService customerEventService;

    private Event sampleEvent;

    @BeforeEach
    void setUp() {
        Organizer organizer = new Organizer();
        organizer.setOrganizerId(UUID.randomUUID());
        organizer.setOrganizationName("Test Org");

        sampleEvent = new Event();
        sampleEvent.setEventId(UUID.randomUUID());
        sampleEvent.setOrganizer(organizer);
        sampleEvent.setTitle("Jazz Night");
        sampleEvent.setDescription("A smooth jazz evening");
        sampleEvent.setCategory("CONCERT");
        sampleEvent.setLocation("Blue Note Club");
        sampleEvent.setEventDate(LocalDate.now().plusDays(10));
        sampleEvent.setStartTime(LocalTime.of(20, 0));
        sampleEvent.setEndTime(LocalTime.of(23, 0));
        sampleEvent.setTotalCapacity(100);
        sampleEvent.setAvailableCapacity(80);
        sampleEvent.setStatus(EventStatus.ACTIVE);
    }

    // --- Browse events tests ---

    @Test
    void getAllUpcomingEvents_returnsActiveEvents() {
        when(eventRepository.findByStatusAndEventDateGreaterThanEqual(
                eq(EventStatus.ACTIVE), any(LocalDate.class)))
                .thenReturn(List.of(sampleEvent));

        List<EventResponse> result = customerEventService.getAllUpcomingEvents();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Jazz Night");
        assertThat(result.get(0).getOrganizerName()).isEqualTo("Test Org");
        assertThat(result.get(0).getAvailableCapacity()).isEqualTo(80);
    }

    @Test
    void getAllUpcomingEvents_returnsEmptyWhenNoEvents() {
        when(eventRepository.findByStatusAndEventDateGreaterThanEqual(
                eq(EventStatus.ACTIVE), any(LocalDate.class)))
                .thenReturn(Collections.emptyList());

        List<EventResponse> result = customerEventService.getAllUpcomingEvents();

        assertThat(result).isEmpty();
    }

    @Test
    void getAllUpcomingEvents_returnsMultipleEvents() {
        Event secondEvent = new Event();
        secondEvent.setEventId(UUID.randomUUID());
        secondEvent.setOrganizer(sampleEvent.getOrganizer());
        secondEvent.setTitle("Rock Festival");
        secondEvent.setDescription("Outdoor rock festival");
        secondEvent.setCategory("CONCERT");
        secondEvent.setLocation("Olympic Stadium");
        secondEvent.setEventDate(LocalDate.now().plusDays(20));
        secondEvent.setStartTime(LocalTime.of(14, 0));
        secondEvent.setEndTime(LocalTime.of(22, 0));
        secondEvent.setTotalCapacity(5000);
        secondEvent.setAvailableCapacity(3000);
        secondEvent.setStatus(EventStatus.ACTIVE);

        when(eventRepository.findByStatusAndEventDateGreaterThanEqual(
                eq(EventStatus.ACTIVE), any(LocalDate.class)))
                .thenReturn(List.of(sampleEvent, secondEvent));

        List<EventResponse> result = customerEventService.getAllUpcomingEvents();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getTitle()).isEqualTo("Jazz Night");
        assertThat(result.get(1).getTitle()).isEqualTo("Rock Festival");
    }

    // --- Search events tests ---

    @Test
    void searchEvents_filtersByKeyword() {
        when(eventRepository.searchEvents(eq(EventStatus.ACTIVE), eq("jazz"), any(), any()))
                .thenReturn(List.of(sampleEvent));

        List<EventResponse> result = customerEventService.searchEvents("jazz", null, null);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Jazz Night");
        verify(eventRepository).searchEvents(EventStatus.ACTIVE, "jazz", null, null);
    }

    @Test
    void searchEvents_filtersByCategory() {
        when(eventRepository.searchEvents(eq(EventStatus.ACTIVE), any(), eq("CONCERT"), any()))
                .thenReturn(List.of(sampleEvent));

        List<EventResponse> result = customerEventService.searchEvents(null, "CONCERT", null);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCategory()).isEqualTo("CONCERT");
    }

    @Test
    void searchEvents_filtersByDate() {
        LocalDate targetDate = LocalDate.now().plusDays(10);
        when(eventRepository.searchEvents(eq(EventStatus.ACTIVE), any(), any(), eq(targetDate)))
                .thenReturn(List.of(sampleEvent));

        List<EventResponse> result = customerEventService.searchEvents(null, null, targetDate);

        assertThat(result).hasSize(1);
    }

    @Test
    void searchEvents_filtersByCombinedParams() {
        LocalDate targetDate = sampleEvent.getEventDate();
        when(eventRepository.searchEvents(EventStatus.ACTIVE, "jazz", "CONCERT", targetDate))
                .thenReturn(List.of(sampleEvent));

        List<EventResponse> result = customerEventService.searchEvents("jazz", "CONCERT", targetDate);

        assertThat(result).hasSize(1);
        verify(eventRepository).searchEvents(EventStatus.ACTIVE, "jazz", "CONCERT", targetDate);
    }

    @Test
    void searchEvents_returnsEmptyForNoMatch() {
        when(eventRepository.searchEvents(eq(EventStatus.ACTIVE), eq("nonexistent"), any(), any()))
                .thenReturn(Collections.emptyList());

        List<EventResponse> result = customerEventService.searchEvents("nonexistent", null, null);

        assertThat(result).isEmpty();
    }

    @Test
    void searchEvents_allNullParamsReturnsAll() {
        when(eventRepository.searchEvents(eq(EventStatus.ACTIVE), eq(null), eq(null), eq(null)))
                .thenReturn(List.of(sampleEvent));

        List<EventResponse> result = customerEventService.searchEvents(null, null, null);

        assertThat(result).hasSize(1);
    }
}
