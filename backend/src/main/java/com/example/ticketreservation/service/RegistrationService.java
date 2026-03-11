package com.example.ticketreservation.service;

import com.example.ticketreservation.dto.LoginRequest;
import com.example.ticketreservation.dto.LoginResponse;
import com.example.ticketreservation.dto.RegistrationRequest;
import com.example.ticketreservation.dto.RegistrationResponse;
import com.example.ticketreservation.exception.InvalidRegistrationException;
import com.example.ticketreservation.exception.UserAlreadyExistsException;
import com.example.ticketreservation.model.User;
import com.example.ticketreservation.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class RegistrationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final NotificationService notificationService;

    public RegistrationService(UserRepository userRepository,
                                PasswordEncoder passwordEncoder,
                                NotificationService notificationService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.notificationService = notificationService;
    }

    /**
     * Registers a new user.
     *
     * Validates that at least one of email or phone is provided,
     * checks for duplicates, hashes the password, persists the user,
     * and triggers a registration confirmation notification.
     */
    @Transactional
    public RegistrationResponse registerUser(RegistrationRequest request) {
        // Validate that at least one contact method is provided
        boolean hasEmail = request.getEmail() != null && !request.getEmail().isBlank();
        boolean hasPhone = request.getPhoneNumber() != null && !request.getPhoneNumber().isBlank();

        if (!hasEmail && !hasPhone) {
            throw new InvalidRegistrationException(
                    "At least one of email or phone number must be provided.");
        }

        // Check for duplicate email
        if (hasEmail && userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException(
                    "A user with this email already exists.");
        }

        // Check for duplicate phone number
        if (hasPhone && userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new UserAlreadyExistsException(
                    "A user with this phone number already exists.");
        }

        // Hash the password (never store plain text)
        String hashedPassword = passwordEncoder.encode(request.getPassword());

        // Create and persist the user entity
        User user = new User(
                request.getFirstName(),
                request.getLastName(),
                hasEmail ? request.getEmail() : null,
                hasPhone ? request.getPhoneNumber() : null,
                hashedPassword
        );

        User savedUser = userRepository.save(user);

        // Send registration confirmation via email/SMS
        notificationService.sendRegistrationConfirmation(savedUser);

        return RegistrationResponse.success(
                savedUser.getUserId(),
                "Registration successful. Confirmation sent."
        );
    }

    /**
     * Authenticates a user by email or phone number and password.
     */
    public LoginResponse loginUser(LoginRequest request) {
        String identifier = request.getUserId();

        // Try to find user by email first, then by phone number
        Optional<User> userOpt = userRepository.findByEmail(identifier);
        if (userOpt.isEmpty()) {
            userOpt = userRepository.findByPhoneNumber(identifier);
        }

        if (userOpt.isEmpty()) {
            return LoginResponse.failure("Invalid credentials.");
        }

        User user = userOpt.get();

        // Verify password against stored hash
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            return LoginResponse.failure("Invalid credentials.");
        }

        return LoginResponse.success(user.getUserId(), "Login successful.");
    }
}
