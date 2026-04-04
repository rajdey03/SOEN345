package com.example.ticketreservation.service;

import com.example.ticketreservation.model.Reservation;
import com.example.ticketreservation.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class NotificationServiceImpl implements NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationServiceImpl.class);

    private final JavaMailSender mailSender;

    private final String fromAddress;

    public NotificationServiceImpl(JavaMailSender mailSender,
                                   @Value("${spring.mail.from:noreply@localhost}") String fromAddress) {
        this.mailSender = mailSender;
        this.fromAddress = fromAddress;
    }

    @Override
    public void sendRegistrationConfirmation(User user) {
        if (user.getEmail() != null && !user.getEmail().isBlank()) {
            sendRegistrationEmail(user);
        } else {
            logger.info("User has no email; skipping registration email for user: {} {}",
                    user.getFirstName(), user.getLastName());
        }
        // SMS is intentionally not implemented in Phase 1.
    }

    @Override
    public void sendReservationConfirmation(Reservation reservation) {
        User user = reservation.getUser();
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            logger.info("User has no email; skipping reservation confirmation for reservation {}",
                    reservation.getReservationId());
            return;
        }

        String subject = "Ticket Reservation Confirmation";
        String body = String.format(
                "Hello %s %s,\n\nYour reservation for %s has been confirmed.\n" +
                        "Reservation ID: %s\nTickets: %d\nTotal Price: %s",
                user.getFirstName(),
                user.getLastName(),
                reservation.getEvent().getTitle(),
                reservation.getReservationId(),
                reservation.getQuantity(),
                reservation.getTotalPrice()
        );
        sendEmail(user.getEmail(), subject, body, "reservation confirmation");
    }

    @Override
    public void sendCancellationConfirmation(Reservation reservation) {
        User user = reservation.getUser();
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            logger.info("User has no email; skipping cancellation confirmation for reservation {}",
                    reservation.getReservationId());
            return;
        }

        String subject = "Ticket Reservation Cancellation";
        String body = String.format(
                "Hello %s %s,\n\nYour reservation for %s has been cancelled.\n" +
                        "Reservation ID: %s\nCancelled Tickets: %d",
                user.getFirstName(),
                user.getLastName(),
                reservation.getEvent().getTitle(),
                reservation.getReservationId(),
                reservation.getQuantity()
        );
        sendEmail(user.getEmail(), subject, body, "cancellation confirmation");
    }

    private void sendRegistrationEmail(User user) {
        String subject = "Booking / Registration Confirmation";
        String body = String.format("Hello %s %s,\n\nThank you for registering. Your account has been created.",
                user.getFirstName(), user.getLastName());
        sendEmail(user.getEmail(), subject, body, "registration email");
    }

    private void sendEmail(String recipient, String subject, String body, String logContext) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromAddress);
            message.setTo(recipient);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
            logger.info("{} sent to: {}", logContext, recipient);
        } catch (Exception e) {
            logger.error("Failed to send {} to {}: {}", logContext, recipient, e.getMessage());
        }
    }
}
