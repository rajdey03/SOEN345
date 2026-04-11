package com.example.ticketreservation.repository;

import com.example.ticketreservation.model.User;
import com.example.ticketreservation.model.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Component test for UserRepository.
 * Uses @DataJpaTest to load only the JPA layer with an in-memory H2 database.
 * Tests custom query methods, entity persistence, and constraint enforcement.
 */
@DataJpaTest
class UserRepositoryComponentTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User("John", "Doe", "john@example.com", "5141234567", "hashedpass123");
        testUser.setRole(UserRole.CUSTOMER);
        entityManager.persistAndFlush(testUser);
    }

    @Test
    void findByEmail_returnsUserWhenExists() {
        Optional<User> found = userRepository.findByEmail("john@example.com");

        assertThat(found).isPresent();
        assertThat(found.get().getFirstName()).isEqualTo("John");
        assertThat(found.get().getLastName()).isEqualTo("Doe");
        assertThat(found.get().getEmail()).isEqualTo("john@example.com");
    }

    @Test
    void findByEmail_returnsEmptyWhenNotExists() {
        Optional<User> found = userRepository.findByEmail("nonexistent@example.com");

        assertThat(found).isEmpty();
    }

    @Test
    void findByPhoneNumber_returnsUserWhenExists() {
        Optional<User> found = userRepository.findByPhoneNumber("5141234567");

        assertThat(found).isPresent();
        assertThat(found.get().getFirstName()).isEqualTo("John");
    }

    @Test
    void findByPhoneNumber_returnsEmptyWhenNotExists() {
        Optional<User> found = userRepository.findByPhoneNumber("0000000000");

        assertThat(found).isEmpty();
    }

    @Test
    void existsByEmail_returnsTrueWhenExists() {
        assertThat(userRepository.existsByEmail("john@example.com")).isTrue();
    }

    @Test
    void existsByEmail_returnsFalseWhenNotExists() {
        assertThat(userRepository.existsByEmail("nonexistent@example.com")).isFalse();
    }

    @Test
    void existsByPhoneNumber_returnsTrueWhenExists() {
        assertThat(userRepository.existsByPhoneNumber("5141234567")).isTrue();
    }

    @Test
    void existsByPhoneNumber_returnsFalseWhenNotExists() {
        assertThat(userRepository.existsByPhoneNumber("0000000000")).isFalse();
    }

    @Test
    void save_persistsNewUser() {
        User newUser = new User("Jane", "Smith", "jane@example.com", "5149876543", "hashedpass456");
        newUser.setRole(UserRole.CUSTOMER);

        User saved = userRepository.save(newUser);

        assertThat(saved.getUserId()).isNotNull();
        assertThat(saved.getFirstName()).isEqualTo("Jane");
        assertThat(saved.getEmail()).isEqualTo("jane@example.com");
    }

    @Test
    void save_persistsAdminUser() {
        User admin = new User("Admin", "User", "admin@example.com", null, "adminpass");
        admin.setRole(UserRole.ADMIN);

        User saved = userRepository.save(admin);

        assertThat(saved.getRole()).isEqualTo(UserRole.ADMIN);
    }

    @Test
    void findById_returnsPersistedUser() {
        Optional<User> found = userRepository.findById(testUser.getUserId());

        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("john@example.com");
        assertThat(found.get().getPasswordHash()).isEqualTo("hashedpass123");
    }

    @Test
    void save_updatesExistingUser() {
        testUser.setFirstName("Jonathan");
        userRepository.save(testUser);
        entityManager.flush();

        Optional<User> found = userRepository.findById(testUser.getUserId());
        assertThat(found).isPresent();
        assertThat(found.get().getFirstName()).isEqualTo("Jonathan");
    }

    @Test
    void findByEmail_isCaseSensitive() {
        Optional<User> found = userRepository.findByEmail("JOHN@EXAMPLE.COM");

        assertThat(found).isEmpty();
    }

    @Test
    void save_setsCreatedAtAutomatically() {
        User newUser = new User("Test", "User", "test@example.com", null, "pass");
        User saved = userRepository.saveAndFlush(newUser);

        assertThat(saved.getCreatedAt()).isNotNull();
    }
}
