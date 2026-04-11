package com.example.ticketreservation.repository;

import com.example.ticketreservation.model.Organizer;
import com.example.ticketreservation.model.User;
import com.example.ticketreservation.model.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Component test for OrganizerRepository.
 * Uses @DataJpaTest to load only the JPA layer with an in-memory H2 database.
 * Tests custom query methods and entity persistence.
 */
@DataJpaTest
class OrganizerRepositoryComponentTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private OrganizerRepository organizerRepository;

    private User user;
    private Organizer organizer;

    @BeforeEach
    void setUp() {
        user = new User("Org", "Admin", "orgadmin@test.com", null, "hashedpass");
        user.setRole(UserRole.ORGANIZER);
        entityManager.persistAndFlush(user);

        organizer = new Organizer();
        organizer.setUser(user);
        organizer.setOrganizationName("Jazz Productions");
        organizer.setContactEmail("jazz@productions.com");
        organizer.setContactPhone("5141234567");
        entityManager.persistAndFlush(organizer);
    }

    @Test
    void findByUserUserId_returnsOrganizerWhenExists() {
        Optional<Organizer> found = organizerRepository.findByUserUserId(user.getUserId());

        assertThat(found).isPresent();
        assertThat(found.get().getOrganizationName()).isEqualTo("Jazz Productions");
        assertThat(found.get().getContactEmail()).isEqualTo("jazz@productions.com");
    }

    @Test
    void findByUserUserId_returnsEmptyWhenNotExists() {
        Optional<Organizer> found = organizerRepository.findByUserUserId(UUID.randomUUID());

        assertThat(found).isEmpty();
    }

    @Test
    void findById_returnsOrganizerWithUserRelationship() {
        Optional<Organizer> found = organizerRepository.findById(organizer.getOrganizerId());

        assertThat(found).isPresent();
        assertThat(found.get().getUser().getEmail()).isEqualTo("orgadmin@test.com");
    }

    @Test
    void save_persistsNewOrganizer() {
        User newUser = new User("New", "Organizer", "new@org.com", null, "hashedpass");
        newUser.setRole(UserRole.ORGANIZER);
        entityManager.persistAndFlush(newUser);

        Organizer newOrg = new Organizer();
        newOrg.setUser(newUser);
        newOrg.setOrganizationName("Rock Events Inc");
        newOrg.setContactEmail("rock@events.com");

        Organizer saved = organizerRepository.saveAndFlush(newOrg);

        assertThat(saved.getOrganizerId()).isNotNull();
        assertThat(saved.getOrganizationName()).isEqualTo("Rock Events Inc");
    }

    @Test
    void save_updatesOrganizerDetails() {
        organizer.setOrganizationName("Updated Jazz Productions");
        organizerRepository.saveAndFlush(organizer);

        Organizer found = entityManager.find(Organizer.class, organizer.getOrganizerId());
        assertThat(found.getOrganizationName()).isEqualTo("Updated Jazz Productions");
    }

    @Test
    void findAll_returnsAllOrganizers() {
        assertThat(organizerRepository.findAll()).hasSize(1);

        User newUser = new User("Another", "Org", "another@org.com", null, "hashedpass");
        newUser.setRole(UserRole.ORGANIZER);
        entityManager.persistAndFlush(newUser);

        Organizer newOrg = new Organizer();
        newOrg.setUser(newUser);
        newOrg.setOrganizationName("Another Org");
        newOrg.setContactEmail("another@org.com");
        organizerRepository.saveAndFlush(newOrg);

        assertThat(organizerRepository.findAll()).hasSize(2);
    }
}
