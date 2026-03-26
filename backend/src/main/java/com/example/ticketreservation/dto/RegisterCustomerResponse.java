package com.example.ticketreservation.dto;

import java.util.UUID;

/**
 * Response DTO returned after a customer registration attempt.
 */
public class RegisterCustomerResponse {

    private UUID customerId;
    private String message;
    private boolean success;

    public RegisterCustomerResponse() {
    }

    public RegisterCustomerResponse(UUID customerId, String message, boolean success) {
        this.customerId = customerId;
        this.message = message;
        this.success = success;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public void setCustomerId(UUID customerId) {
        this.customerId = customerId;
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
