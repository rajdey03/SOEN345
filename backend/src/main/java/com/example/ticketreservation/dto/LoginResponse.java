package com.example.ticketreservation.dto;

import java.util.UUID;

public class LoginResponse {

    private UUID userId;
    private String message;
    private boolean success;

    public LoginResponse() {
    }

    public LoginResponse(UUID userId, String message, boolean success) {
        this.userId = userId;
        this.message = message;
        this.success = success;
    }

    public static LoginResponse success(UUID userId, String message) {
        return new LoginResponse(userId, message, true);
    }

    public static LoginResponse failure(String message) {
        return new LoginResponse(null, message, false);
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
