# Email Service Configuration

This document explains how to configure and use the email service in the Hotel Management System.

## SendGrid Configuration

The system uses SendGrid to send emails. The following configuration properties need to be set in `application.properties`:

```properties
# SendGrid Configuration
sendgrid.api.key=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
sendgrid.from.email=sathishdhuda25@gmail.com
```

## Email Service Features

The EmailService class provides the following methods:

1. `sendEmail(String toEmail, String subject, String content)` - Send a generic email
2. `sendReservationConfirmation(String toEmail, String guestName, String reservationNo)` - Send reservation confirmation
3. `sendCheckInConfirmation(String toEmail, String guestName, String folioNo)` - Send check-in confirmation
4. `sendBillConfirmation(String toEmail, String guestName, String billNo, String amount)` - Send bill confirmation

## How Emails Are Sent

Emails are automatically sent in the following scenarios:

1. **Reservation Creation** - When a new reservation is created, a confirmation email is sent to the guest if an email address is provided.

2. **Check-In Processing** - When a guest is checked in, a confirmation email is sent to the guest if an email address is provided.

3. **Bill Generation** - When a bill is generated, a confirmation email is sent to the guest if an email address is provided.

## Customization

To customize the email templates, modify the corresponding methods in the EmailService class:

- `sendReservationConfirmation()`
- `sendCheckInConfirmation()`
- `sendBillConfirmation()`

## Testing

To test the email functionality:

1. Ensure the SendGrid API key is correctly configured
2. Create a reservation with a valid email address
3. Check that a confirmation email is received
4. Process a check-in for that reservation
5. Check that a check-in confirmation email is received
6. Generate a bill for the check-in
7. Check that a bill confirmation email is received

## Troubleshooting

If emails are not being sent:

1. Verify the SendGrid API key is correct
2. Check that the from email address is verified in SendGrid
3. Ensure the recipient email address is valid
4. Check the application logs for any error messages