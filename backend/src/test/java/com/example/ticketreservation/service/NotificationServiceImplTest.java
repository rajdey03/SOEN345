package com.example.ticketreservation.service;

import com.example.ticketreservation.model.User;
import com.example.ticketreservation.model.Event;
import com.example.ticketreservation.model.Reservation;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    @Test
    void sendsEmailOnRegistration() {
        User user = new User("Test", "User", "test@example.com", null, "hash");

        notificationService.sendRegistrationConfirmation(user);

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());

        SimpleMailMessage sent = captor.getValue();
        assertThat(sent.getTo()).contains("test@example.com");
        assertThat(sent.getSubject()).contains("Confirmation");
        assertThat(sent.getText()).contains("Thank you for registering");
    }

    @Test
    void sendsEmailOnReservationConfirmation() {
        Reservation reservation = buildReservation("Jazz Night", 2);

        notificationService.sendReservationConfirmation(reservation);

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());

        SimpleMailMessage sent = captor.getValue();
        assertThat(sent.getTo()).contains("test@example.com");
        assertThat(sent.getSubject()).contains("Reservation Confirmation");
        assertThat(sent.getText()).contains("Jazz Night");
        assertThat(sent.getText()).contains("Tickets: 2");
    }

    @Test
    void sendsEmailOnCancellationConfirmation() {
        Reservation reservation = buildReservation("Jazz Night", 3);

        notificationService.sendCancellationConfirmation(reservation);

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());

        SimpleMailMessage sent = captor.getValue();
        assertThat(sent.getTo()).contains("test@example.com");
        assertThat(sent.getSubject()).contains("Cancellation");
        assertThat(sent.getText()).contains("Jazz Night");
        assertThat(sent.getText()).contains("Cancelled Tickets: 3");
    }

    private Reservation buildReservation(String eventTitle, int quantity) {
        User user = new User("Test", "User", "test@example.com", null, "hash");

        Event event = new Event();
        event.setTitle(eventTitle);

        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setEvent(event);
        reservation.setQuantity(quantity);
        reservation.setReservationId(java.util.UUID.randomUUID());
        reservation.setTotalPrice(new java.math.BigDecimal("99.98"));
        return reservation;
    }
}
