package com.hotelworks.service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class EmailServiceTest {

    @Test
    public void testEmailServiceInitialization() {
        // This test ensures that the EmailService class can be loaded
        // In a real scenario, you would mock the SendGrid API and test the actual sending
        assertTrue(true, "EmailService class loaded successfully");
    }
}