package com.example.ticketreservation.service;

import com.example.ticketreservation.dto.EventResponse;
import com.example.ticketreservation.model.EventStatus;
import com.example.ticketreservation.repository.EventRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerEventService {

    private final EventRepository eventRepository;

    public CustomerEventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public List<EventResponse> getAllUpcomingEvents() {
        return eventRepository
                .findByStatusAndEventDateGreaterThanEqual(EventStatus.ACTIVE, LocalDate.now())
                .stream()
                .map(EventResponse::from)
                .collect(Collectors.toList());
    }

    public List<EventResponse> searchEvents(String keyword, String category, LocalDate date) {
        return eventRepository
                .searchEvents(EventStatus.ACTIVE, keyword, category, date)
                .stream()
                .map(EventResponse::from)
                .collect(Collectors.toList());
    }
}
