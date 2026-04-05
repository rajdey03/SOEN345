package com.example.ticketreservation.repository;

import com.example.ticketreservation.model.Organizer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrganizerRepository extends JpaRepository<Organizer, UUID> {
    Optional<Organizer> findByUserUserId(UUID userId);
}
