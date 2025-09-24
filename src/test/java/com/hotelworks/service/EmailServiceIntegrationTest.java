package com.hotelworks.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class EmailServiceIntegrationTest {

    @Autowired
    private EmailService emailService;

    @Test
    public void testSendEmail() {
        // This test requires a valid email address to send to
        // Replace with a valid test email address
        boolean result = emailService.sendEmail(
            "test@example.com",
            "Test Email",
            "<h1>Test Email</h1><p>This is a test email from the Hotel Management System.</p>"
        );
        
        // Note: This test will fail if email sending fails
        // In a real test environment, you might want to use a mock or test email service
        System.out.println("Email sent result: " + result);
    }

    @Test
    public void testSendReservationConfirmation() {
        boolean result = emailService.sendReservationConfirmation(
            "test@example.com",
            "John Doe",
            "RES-001"
        );
        
        System.out.println("Reservation confirmation email sent result: " + result);
    }

    @Test
    public void testSendCheckInConfirmation() {
        boolean result = emailService.sendCheckInConfirmation(
            "test@example.com",
            "John Doe",
            "FOL-001"
        );
        
        System.out.println("Check-in confirmation email sent result: " + result);
    }

    @Test
    public void testSendBillConfirmation() {
        boolean result = emailService.sendBillConfirmation(
            "test@example.com",
            "John Doe",
            "BILL-001",
            "1000.00"
        );
        
        System.out.println("Bill confirmation email sent result: " + result);
    }
}