package com.example.ticketreservation.controller;

import com.example.ticketreservation.config.SecurityConfig;
import com.example.ticketreservation.dto.LoginRequest;
import com.example.ticketreservation.dto.LoginResponse;
import com.example.ticketreservation.dto.RegistrationRequest;
import com.example.ticketreservation.dto.RegistrationResponse;
import com.example.ticketreservation.exception.GlobalExceptionHandler;
import com.example.ticketreservation.exception.InvalidRegistrationException;
import com.example.ticketreservation.exception.UserAlreadyExistsException;
import com.example.ticketreservation.service.RegistrationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Component test for RegistrationController.
 * Loads only the web layer (@WebMvcTest) with mocked service dependencies.
 * Tests HTTP request routing, validation, serialization, and error handling.
 */
@WebMvcTest(RegistrationController.class)
@Import({SecurityConfig.class, GlobalExceptionHandler.class})
class RegistrationControllerComponentTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RegistrationService registrationService;

    // ---------------------------------------------------------------
    // POST /api/register — Registration
    // ---------------------------------------------------------------

    @Test
    void register_returnsCreatedOnSuccess() throws Exception {
        UUID userId = UUID.randomUUID();
        RegistrationRequest request = new RegistrationRequest(
                "John", "Doe", "john@example.com", null, "password123");

        when(registrationService.registerUser(any(RegistrationRequest.class)))
                .thenReturn(RegistrationResponse.success(userId, "Registration successful."));

        mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.userId", is(userId.toString())))
                .andExpect(jsonPath("$.message", containsString("Registration successful")));
    }

    @Test
    void register_returns400WhenFirstNameMissing() throws Exception {
        RegistrationRequest request = new RegistrationRequest(
                "", "Doe", "john@example.com", null, "password123");

        mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.firstName", notNullValue()));
    }

    @Test
    void register_returns400WhenLastNameMissing() throws Exception {
        RegistrationRequest request = new RegistrationRequest(
                "John", "", "john@example.com", null, "password123");

        mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.lastName", notNullValue()));
    }

    @Test
    void register_returns400WhenPasswordMissing() throws Exception {
        RegistrationRequest request = new RegistrationRequest(
                "John", "Doe", "john@example.com", null, "");

        mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.password", notNullValue()));
    }

    @Test
    void register_returns400WhenPasswordTooShort() throws Exception {
        RegistrationRequest request = new RegistrationRequest(
                "John", "Doe", "john@example.com", null, "abc");

        mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.password", notNullValue()));
    }

    @Test
    void register_returns400WhenInvalidEmail() throws Exception {
        RegistrationRequest request = new RegistrationRequest(
                "John", "Doe", "not-an-email", null, "password123");

        mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.email", notNullValue()));
    }

    @Test
    void register_returns409WhenEmailAlreadyExists() throws Exception {
        RegistrationRequest request = new RegistrationRequest(
                "John", "Doe", "existing@example.com", null, "password123");

        when(registrationService.registerUser(any(RegistrationRequest.class)))
                .thenThrow(new UserAlreadyExistsException("A user with this email already exists."));

        mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", containsString("email already exists")));
    }

    @Test
    void register_returns400WhenNoContactMethodProvided() throws Exception {
        RegistrationRequest request = new RegistrationRequest(
                "John", "Doe", null, null, "password123");

        when(registrationService.registerUser(any(RegistrationRequest.class)))
                .thenThrow(new InvalidRegistrationException(
                        "At least one of email or phone number must be provided."));

        mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("email or phone")));
    }

    @Test
    void register_acceptsPhoneNumberWithoutEmail() throws Exception {
        UUID userId = UUID.randomUUID();
        RegistrationRequest request = new RegistrationRequest(
                "Jane", "Smith", null, "5141234567", "password123");

        when(registrationService.registerUser(any(RegistrationRequest.class)))
                .thenReturn(RegistrationResponse.success(userId, "Registration successful."));

        mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)));
    }

    // ---------------------------------------------------------------
    // POST /api/login — Login
    // ---------------------------------------------------------------

    @Test
    void login_returnsOkOnSuccess() throws Exception {
        UUID userId = UUID.randomUUID();
        LoginRequest request = new LoginRequest("john@example.com", "password123");

        when(registrationService.loginUser(any(LoginRequest.class)))
                .thenReturn(LoginResponse.success(userId, "Login successful."));

        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.userId", is(userId.toString())))
                .andExpect(jsonPath("$.message", containsString("Login successful")));
    }

    @Test
    void login_returnsUnauthorizedOnFailure() throws Exception {
        LoginRequest request = new LoginRequest("wrong@example.com", "wrongpass");

        when(registrationService.loginUser(any(LoginRequest.class)))
                .thenReturn(LoginResponse.failure("Invalid credentials."));

        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success", is(false)))
                .andExpect(jsonPath("$.message", containsString("Invalid credentials")));
    }

    @Test
    void login_returns400WhenUserIdMissing() throws Exception {
        String body = "{\"password\":\"password123\"}";

        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_returns400WhenPasswordMissing() throws Exception {
        String body = "{\"user_id\":\"john@example.com\"}";

        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    // ---------------------------------------------------------------
    // GET /api/health — Health check
    // ---------------------------------------------------------------

    @Test
    void health_returnsOk() throws Exception {
        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(content().string("OK"));
    }
}
