package com.example.ticketreservation;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("h2")
class TicketReservationApplicationTests {

    @MockBean
    private JavaMailSender mailSender;

    @Test
    void contextLoads() {
    }
}
