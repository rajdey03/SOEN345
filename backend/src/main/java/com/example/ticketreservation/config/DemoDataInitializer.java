package com.example.ticketreservation.config;

import com.example.ticketreservation.model.Organizer;
import com.example.ticketreservation.model.User;
import com.example.ticketreservation.model.UserRole;
import com.example.ticketreservation.repository.OrganizerRepository;
import com.example.ticketreservation.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class DemoDataInitializer implements CommandLineRunner {

    public static final UUID DEMO_ADMIN_USER_ID = UUID.fromString("e8fedc08-e40c-40a7-b003-074543dee3f8");
    public static final UUID DEMO_ORGANIZER_USER_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");
    public static final UUID DEMO_ORGANIZER_ID = UUID.fromString("44444444-4444-4444-4444-444444444444");

    private final UserRepository userRepository;
    private final OrganizerRepository organizerRepository;
    private final PasswordEncoder passwordEncoder;

    public DemoDataInitializer(UserRepository userRepository,
                               OrganizerRepository organizerRepository,
                               PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.organizerRepository = organizerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        seedAdminUser();
        seedOrganizer();
    }

    private void seedAdminUser() {
        User adminUser = userRepository.findById(DEMO_ADMIN_USER_ID)
                .orElseGet(() -> {
                    User user = new User(
                            "Demo",
                            "Admin",
                            null,
                            null,
                            passwordEncoder.encode("admin123")
                    );
                    user.setUserId(DEMO_ADMIN_USER_ID);
                    return user;
                });

        adminUser.setRole(UserRole.ADMIN);
        adminUser.setVerified(true);
        userRepository.save(adminUser);
    }

    private void seedOrganizer() {
        User organizerUser = userRepository.findById(DEMO_ORGANIZER_USER_ID)
                .or(() -> userRepository.findByEmail("organizer@example.com"))
                .or(() -> userRepository.findByPhoneNumber("5550000002"))
                .orElseGet(() -> {
                    User user = new User(
                            "Olivia",
                            "Organizer",
                            "organizer@example.com",
                            "5550000002",
                            passwordEncoder.encode("organizer123")
                    );
                    user.setUserId(DEMO_ORGANIZER_USER_ID);
                    user.setRole(UserRole.ORGANIZER);
                    user.setVerified(true);
                    return userRepository.save(user);
                });

        organizerUser.setRole(UserRole.ORGANIZER);
        organizerUser.setVerified(true);
        organizerUser = userRepository.save(organizerUser);

        Organizer organizer = organizerRepository.findById(DEMO_ORGANIZER_ID).orElse(null);
        if (organizer == null) {
            organizer = organizerRepository.findByUserUserId(organizerUser.getUserId()).orElse(null);
        }
        if (organizer == null) {
            organizer = new Organizer();
        }

        if (organizer.getOrganizerId() == null) {
            organizer.setOrganizerId(DEMO_ORGANIZER_ID);
        }
        organizer.setUser(organizerUser);
        organizer.setOrganizationName("Montreal Events Group");
        organizer.setContactEmail("organizer@example.com");
        organizer.setContactPhone("5550000002");
        organizerRepository.save(organizer);
    }
}
