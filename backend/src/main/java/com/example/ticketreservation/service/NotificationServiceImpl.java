package com.example.ticketreservation.service;

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
            sendEmailConfirmation(user);
        } else {
            logger.info("User has no email; skipping registration email for user: {} {}",
                    user.getFirstName(), user.getLastName());
        }
        // SMS is intentionally not implemented in Phase 1.
    }

    private void sendEmailConfirmation(User user) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromAddress);
            message.setTo(user.getEmail());
            message.setSubject("Booking / Registration Confirmation");
            String body = String.format("Hello %s %s,\n\nThank you for registering. Your account has been created.",
                    user.getFirstName(), user.getLastName());
            message.setText(body);
            mailSender.send(message);
            logger.info("Registration confirmation email sent to: {}", user.getEmail());
        } catch (Exception e) {
            logger.error("Failed to send registration email to {}: {}", user.getEmail(), e.getMessage());
        }
    }
}
