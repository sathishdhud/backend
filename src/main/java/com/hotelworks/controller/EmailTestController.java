package com.hotelworks.controller;

import com.hotelworks.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test")
public class EmailTestController {

    @Autowired
    private EmailService emailService;

    @PostMapping("/send-test-email")
    public ResponseEntity<String> sendTestEmail(@RequestParam String toEmail) {
        // Log the email service configuration
        System.out.println("Testing email service...");
        
        boolean result = emailService.sendEmail(
            toEmail,
            "Test Email from Hotel Management System",
            "<h1>Test Email</h1><p>This is a test email from the Hotel Management System.</p>" +
            "<p>If you received this email, the email service is working correctly.</p>"
        );
        
        if (result) {
            return ResponseEntity.ok("Test email sent successfully to " + toEmail);
        } else {
            return ResponseEntity.status(500).body("Failed to send test email to " + toEmail);
        }
    }

    @PostMapping("/send-reservation-confirmation")
    public ResponseEntity<String> sendReservationConfirmation(
            @RequestParam String toEmail,
            @RequestParam String guestName,
            @RequestParam String reservationNo) {
        boolean result = emailService.sendReservationConfirmation(toEmail, guestName, reservationNo);
        
        if (result) {
            return ResponseEntity.ok("Reservation confirmation email sent successfully to " + toEmail);
        } else {
            return ResponseEntity.status(500).body("Failed to send reservation confirmation email to " + toEmail);
        }
    }

    @PostMapping("/send-checkin-confirmation")
    public ResponseEntity<String> sendCheckInConfirmation(
            @RequestParam String toEmail,
            @RequestParam String guestName,
            @RequestParam String folioNo) {
        boolean result = emailService.sendCheckInConfirmation(toEmail, guestName, folioNo);
        
        if (result) {
            return ResponseEntity.ok("Check-in confirmation email sent successfully to " + toEmail);
        } else {
            return ResponseEntity.status(500).body("Failed to send check-in confirmation email to " + toEmail);
        }
    }

    @PostMapping("/send-bill-confirmation")
    public ResponseEntity<String> sendBillConfirmation(
            @RequestParam String toEmail,
            @RequestParam String guestName,
            @RequestParam String billNo,
            @RequestParam String amount) {
        boolean result = emailService.sendBillConfirmation(toEmail, guestName, billNo, amount);
        
        if (result) {
            return ResponseEntity.ok("Bill confirmation email sent successfully to " + toEmail);
        } else {
            return ResponseEntity.status(500).body("Failed to send bill confirmation email to " + toEmail);
        }
    }
    
    @GetMapping("/email-config")
    public ResponseEntity<String> getEmailConfig() {
        return ResponseEntity.ok("Email service is configured and ready");
    }
}