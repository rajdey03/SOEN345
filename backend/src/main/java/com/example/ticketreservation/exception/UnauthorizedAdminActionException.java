package com.example.ticketreservation.exception;

public class UnauthorizedAdminActionException extends RuntimeException {

    public UnauthorizedAdminActionException(String message) {
        super(message);
    }
}
