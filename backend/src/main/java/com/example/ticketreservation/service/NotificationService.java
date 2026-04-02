package com.example.ticketreservation.service;

import com.example.ticketreservation.model.User;
import com.example.ticketreservation.model.Reservation;

public interface NotificationService {

    void sendRegistrationConfirmation(User user);

    void sendReservationConfirmation(Reservation reservation);

    void sendCancellationConfirmation(Reservation reservation);
}
