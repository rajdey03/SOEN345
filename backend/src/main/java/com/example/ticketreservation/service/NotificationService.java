package com.example.ticketreservation.service;

import com.example.ticketreservation.model.User;

public interface NotificationService {

    void sendRegistrationConfirmation(User user);
}
