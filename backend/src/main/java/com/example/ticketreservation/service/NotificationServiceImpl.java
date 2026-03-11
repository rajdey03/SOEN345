package com.example.ticketreservation.service;

import com.example.ticketreservation.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Notification service implementation.
 * Currently logs confirmations. In production, this would use
 * JavaMailSender for email and an SMS gateway (e.g., Twilio) for SMS.
 */
@Service
public class NotificationServiceImpl implements NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationServiceImpl.class);

    @Override
    public void sendRegistrationConfirmation(User user) {
        if (user.getEmail() != null && !user.getEmail().isBlank()) {
            sendEmailConfirmation(user);
        }
        if (user.getPhoneNumber() != null && !user.getPhoneNumber().isBlank()) {
            sendSmsConfirmation(user);
        }
    }

    private void sendEmailConfirmation(User user) {
        // In production: use JavaMailSender to send actual email
        logger.info("Registration confirmation email sent to: {}", user.getEmail());
    }

    private void sendSmsConfirmation(User user) {
        // In production: use Twilio or similar SMS API
        logger.info("Registration confirmation SMS sent to: {}", user.getPhoneNumber());
    }
}
