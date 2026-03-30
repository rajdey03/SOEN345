package com.example.ticketreservation.repository;

import com.example.ticketreservation.model.Event;
import com.example.ticketreservation.model.EventStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface EventRepository extends JpaRepository<Event, UUID> {

    List<Event> findByStatusAndEventDateGreaterThanEqual(EventStatus status, LocalDate date);

    @Query("SELECT e FROM Event e WHERE e.status = :status " +
           "AND (:keyword IS NULL OR LOWER(e.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "    OR LOWER(e.description) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "    OR LOWER(e.location) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "AND (:category IS NULL OR LOWER(e.category) = LOWER(:category)) " +
           "AND (:date IS NULL OR e.eventDate = :date)")
    List<Event> searchEvents(@Param("status") EventStatus status,
                             @Param("keyword") String keyword,
                             @Param("category") String category,
                             @Param("date") LocalDate date);
}
