package com.example.ticketreservation.service;

import com.example.ticketreservation.dto.LoginRequest;
import com.example.ticketreservation.dto.LoginResponse;
import com.example.ticketreservation.dto.RegistrationRequest;
import com.example.ticketreservation.dto.RegistrationResponse;
import com.example.ticketreservation.exception.InvalidRegistrationException;
import com.example.ticketreservation.exception.UserAlreadyExistsException;
import com.example.ticketreservation.model.User;
import com.example.ticketreservation.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegistrationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private RegistrationService registrationService;

    @Test
    void registerUser_succeedsWithEmail() {
        RegistrationRequest request = new RegistrationRequest(
                "Sam",
                "Davis",
                "sam@example.com",
                null,
                "password123"
        );

        when(userRepository.existsByEmail("sam@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("hashed");

        User savedUser = new User("Sam", "Davis", "sam@example.com", null, "hashed");
        UUID userId = UUID.randomUUID();
        savedUser.setUserId(userId);

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        RegistrationResponse response = registrationService.registerUser(request);

        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getUserId()).isEqualTo(userId);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertThat(userCaptor.getValue().getPasswordHash()).isEqualTo("hashed");
        verify(notificationService).sendRegistrationConfirmation(savedUser);
    }

    @Test
    void registerUser_requiresEmailOrPhone() {
        RegistrationRequest request = new RegistrationRequest(
                "Sam",
                "Davis",
                null,
                null,
                "password123"
        );

        assertThatThrownBy(() -> registrationService.registerUser(request))
                .isInstanceOf(InvalidRegistrationException.class)
                .hasMessageContaining("email or phone number");
    }

    @Test
    void registerUser_rejectsDuplicateEmail() {
        RegistrationRequest request = new RegistrationRequest(
                "Sam",
                "Davis",
                "sam@example.com",
                null,
                "password123"
        );

        when(userRepository.existsByEmail("sam@example.com")).thenReturn(true);

        assertThatThrownBy(() -> registrationService.registerUser(request))
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessageContaining("email");
    }

        @Test
        void registerUser_rejectsDuplicatePhone() {
        RegistrationRequest request = new RegistrationRequest(
            "Sam",
            "Davis",
            null,
            "+15145550000",
            "password123"
        );

        when(userRepository.existsByPhoneNumber("+15145550000")).thenReturn(true);

        assertThatThrownBy(() -> registrationService.registerUser(request))
            .isInstanceOf(UserAlreadyExistsException.class)
            .hasMessageContaining("phone");
        }

    @Test
    void loginUser_succeedsWithEmail() {
        LoginRequest request = new LoginRequest("sam@example.com", "password123");
        User user = new User("Sam", "Davis", "sam@example.com", null, "hashed");
        UUID userId = UUID.randomUUID();
        user.setUserId(userId);

        when(userRepository.findByEmail("sam@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", "hashed")).thenReturn(true);

        LoginResponse response = registrationService.loginUser(request);

        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getUserId()).isEqualTo(userId);
    }

    @Test
    void loginUser_failsWithInvalidPassword() {
        LoginRequest request = new LoginRequest("sam@example.com", "wrong");
        User user = new User("Sam", "Davis", "sam@example.com", null, "hashed");

        when(userRepository.findByEmail("sam@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "hashed")).thenReturn(false);

        LoginResponse response = registrationService.loginUser(request);

        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getMessage()).contains("Invalid credentials");
    }

    @Test
    void loginUser_failsWhenUserMissing() {
        LoginRequest request = new LoginRequest("sam@example.com", "password123");

        when(userRepository.findByEmail("sam@example.com")).thenReturn(Optional.empty());
        when(userRepository.findByPhoneNumber("sam@example.com")).thenReturn(Optional.empty());

        LoginResponse response = registrationService.loginUser(request);

        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getMessage()).contains("Invalid credentials");
    }

    @Test
    void loginUser_succeedsWithPhoneNumber() {
        LoginRequest request = new LoginRequest("+15145550000", "password123");
        User user = new User("Sam", "Davis", null, "+15145550000", "hashed");
        UUID userId = UUID.randomUUID();
        user.setUserId(userId);

        when(userRepository.findByEmail("+15145550000")).thenReturn(Optional.empty());
        when(userRepository.findByPhoneNumber("+15145550000")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", "hashed")).thenReturn(true);

        LoginResponse response = registrationService.loginUser(request);

        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getUserId()).isEqualTo(userId);
    }
}
