# Email Service Implementation Guide

This document provides detailed information about the email service implementation in the Hotel Management System.

## Overview

The email service is implemented using SendGrid as the email delivery provider. It provides automated email notifications for key hotel operations including reservations, check-ins, and billing.

## Implementation Details

### Dependencies

The email service uses the SendGrid Java library, which is included in the `pom.xml`:

```xml
<dependency>
    <groupId>com.sendgrid</groupId>
    <artifactId>sendgrid-java</artifactId>
    <version>4.9.3</version>
</dependency>
```

### Configuration

The email service is configured through `application.properties`:

```properties
# SendGrid Configuration
sendgrid.api.key=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
sendgrid.from.email=sathishdhuda25@gmail.com
```

### Service Class

The `EmailService` class (`src/main/java/com/hotelworks/service/EmailService.java`) provides the following methods:

1. `sendEmail(String toEmail, String subject, String content)` - Generic email sending method
2. `sendReservationConfirmation(String toEmail, String guestName, String reservationNo)` - Sends reservation confirmation
3. `sendCheckInConfirmation(String toEmail, String guestName, String folioNo)` - Sends check-in confirmation
4. `sendBillConfirmation(String toEmail, String guestName, String billNo, String amount)` - Sends bill confirmation

## Integration Points

The email service is integrated into the following services:

### ReservationService

When a new reservation is created, if an email address is provided, a reservation confirmation email is automatically sent:

```java
// Send email confirmation if email is provided
if (savedReservation.getEmailId() != null && !savedReservation.getEmailId().isEmpty()) {
    emailService.sendReservationConfirmation(
        savedReservation.getEmailId(),
        savedReservation.getGuestName(),
        savedReservation.getReservationNo()
    );
}
```

### CheckInService

When a guest is checked in, if an email address is provided, a check-in confirmation email is automatically sent:

```java
// Send email confirmation if email is provided
if (savedCheckIn.getEmailId() != null && !savedCheckIn.getEmailId().isEmpty()) {
    emailService.sendCheckInConfirmation(
        savedCheckIn.getEmailId(),
        savedCheckIn.getGuestName(),
        savedCheckIn.getFolioNo()
    );
}
```

### BillService

When a bill is generated, if an email address is provided, a bill confirmation email is automatically sent:

```java
// Send email confirmation if email is provided
if (checkIn.getEmailId() != null && !checkIn.getEmailId().isEmpty()) {
    emailService.sendBillConfirmation(
        checkIn.getEmailId(),
        checkIn.getGuestName(),
        savedBill.getBillNo(),
        savedBill.getTotalAmount().toString()
    );
}
```

## Testing the Email Service

### Using the Test Controller

A test controller has been added at `src/main/java/com/hotelworks/controller/EmailTestController.java` with the following endpoints:

1. `POST /api/test/send-test-email?toEmail={email}` - Sends a generic test email
2. `POST /api/test/send-reservation-confirmation?toEmail={email}&guestName={name}&reservationNo={no}` - Sends a reservation confirmation
3. `POST /api/test/send-checkin-confirmation?toEmail={email}&guestName={name}&folioNo={no}` - Sends a check-in confirmation
4. `POST /api/test/send-bill-confirmation?toEmail={email}&guestName={name}&billNo={no}&amount={amount}` - Sends a bill confirmation

### Example cURL Commands

```bash
# Send test email
curl -X POST "http://localhost:8080/api/test/send-test-email?toEmail=test@example.com" \
  -H "Authorization: Basic YWRtaW46YWRtaW4xMjM="

# Send reservation confirmation
curl -X POST "http://localhost:8080/api/test/send-reservation-confirmation?toEmail=test@example.com&guestName=John%20Doe&reservationNo=RES-001" \
  -H "Authorization: Basic YWRtaW46YWRtaW4xMjM="

# Send check-in confirmation
curl -X POST "http://localhost:8080/api/test/send-checkin-confirmation?toEmail=test@example.com&guestName=John%20Doe&folioNo=FOL-001" \
  -H "Authorization: Basic YWRtaW46YWRtaW4xMjM="

# Send bill confirmation
curl -X POST "http://localhost:8080/api/test/send-bill-confirmation?toEmail=test@example.com&guestName=John%20Doe&billNo=BILL-001&amount=1000.00" \
  -H "Authorization: Basic YWRtaW46YWRtaW4xMjM="
```

## Troubleshooting

### Common Issues

1. **Emails not being received**
   - Verify the SendGrid API key is correct in `application.properties`
   - Check that the from email address is verified in SendGrid
   - Ensure the recipient email address is valid
   - Check the application logs for any error messages

2. **SendGrid authentication errors**
   - Confirm the API key format is correct (starts with "SG.")
   - Ensure the API key has the necessary permissions

3. **Email delivery delays**
   - Check SendGrid's delivery status and reputation
   - Verify the recipient's email server is not blocking the emails

### Log Messages

The email service includes detailed logging to help with troubleshooting:

- Successful email sends are logged with "Email sent successfully"
- Failed sends include status codes and response bodies
- Exceptions are logged with full stack traces

## Customization

### Email Templates

To customize the email templates, modify the HTML content in the corresponding methods in `EmailService.java`:

- `sendReservationConfirmation()` - Reservation confirmation template
- `sendCheckInConfirmation()` - Check-in confirmation template
- `sendBillConfirmation()` - Bill confirmation template

### Adding New Email Types

To add new email types:

1. Add a new method in `EmailService.java`
2. Follow the pattern of existing methods
3. Use HTML templates for better formatting
4. Add corresponding endpoint in `EmailTestController.java` for testing

## Security Considerations

1. The SendGrid API key should be kept secure and not exposed in client-side code
2. In production, consider using environment variables for sensitive configuration
3. Ensure only authorized users can trigger email sending through API endpoints
4. Validate all email addresses before sending

## Performance Considerations

1. Email sending is asynchronous and non-blocking
2. Failed emails are logged but don't interrupt the main application flow
3. Consider implementing a queue system for high-volume email sending
4. Monitor SendGrid's rate limits for your account tier

## Future Enhancements

1. Add email template management through the admin interface
2. Implement email scheduling for delayed sends
3. Add support for attachments
4. Implement email tracking and analytics
5. Add support for multiple email providers with fallback