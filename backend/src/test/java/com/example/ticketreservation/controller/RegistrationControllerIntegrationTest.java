package com.example.ticketreservation.controller;

import com.example.ticketreservation.dto.LoginRequest;
import com.example.ticketreservation.dto.RegistrationRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class RegistrationControllerIntegrationTest {

    @MockBean
    private JavaMailSender mailSender;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void registerAndLogin_roundTrip() throws Exception {
        RegistrationRequest registrationRequest = new RegistrationRequest(
                "Ava",
                "Ng",
                "ava.ng@example.com",
                null,
                "password123"
        );

        mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.userId", notNullValue()));

        LoginRequest loginRequest = new LoginRequest("ava.ng@example.com", "password123");

        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.userId", notNullValue()));
    }

    @Test
    void register_rejectsMissingPassword() throws Exception {
        RegistrationRequest request = new RegistrationRequest(
                "Ava",
                "Ng",
                "ava.missing@example.com",
                null,
                ""
        );

        mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.password", notNullValue()));
    }

    @Test
    void register_rejectsMissingFirstName() throws Exception {
        RegistrationRequest request = new RegistrationRequest(
                "",
                "Ng",
                "ava.no-first@example.com",
                null,
                "password123"
        );

        mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.firstName", notNullValue()));
    }

    @Test
    void register_rejectsMissingLastName() throws Exception {
        RegistrationRequest request = new RegistrationRequest(
                "Ava",
                "",
                "ava.no-last@example.com",
                null,
                "password123"
        );

        mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.lastName", notNullValue()));
    }

    @Test
    void register_rejectsInvalidEmailFormat() throws Exception {
        RegistrationRequest request = new RegistrationRequest(
                "Ava",
                "Ng",
                "invalid-email",
                null,
                "password123"
        );

        mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.email", notNullValue()));
    }

    @Test
    void register_rejectsShortPassword() throws Exception {
        RegistrationRequest request = new RegistrationRequest(
                "Ava",
                "Ng",
                "ava.short-pass@example.com",
                null,
                "123"
        );

        mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.password", notNullValue()));
    }

    @Test
    void register_rejectsDuplicateEmailWithConflict() throws Exception {
        RegistrationRequest request = new RegistrationRequest(
                "Ava",
                "Ng",
                "ava.dup@example.com",
                null,
                "password123"
        );

        mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)));

        mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success", is(false)));
    }

    @Test
    void register_rejectsDuplicatePhoneWithConflict() throws Exception {
        RegistrationRequest request = new RegistrationRequest(
                "Leo",
                "Kim",
                null,
                "+15145550999",
                "password123"
        );

        mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)));

        mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success", is(false)));
    }

    @Test
    void registerAndLogin_roundTripWithPhoneNumber() throws Exception {
        RegistrationRequest registrationRequest = new RegistrationRequest(
                "Leo",
                "Kim",
                null,
                "+15145550123",
                "password123"
        );

        mockMvc.perform(post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registrationRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.userId", notNullValue()));

        LoginRequest loginRequest = new LoginRequest("+15145550123", "password123");

        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.userId", notNullValue()));
    }
}
