package com.example.ticketreservation.dto;

import java.util.UUID;

public class RegistrationResponse {

    private UUID userId;
    private String message;
    private boolean success;

    public RegistrationResponse() {
    }

    public RegistrationResponse(UUID userId, String message, boolean success) {
        this.userId = userId;
        this.message = message;
        this.success = success;
    }

    public static RegistrationResponse success(UUID userId, String message) {
        return new RegistrationResponse(userId, message, true);
    }

    public static RegistrationResponse failure(String message) {
        return new RegistrationResponse(null, message, false);
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
