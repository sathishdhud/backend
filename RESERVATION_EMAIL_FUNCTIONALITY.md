# Reservation Email Functionality Documentation

## Overview
This document describes the enhanced email functionality for reservation confirmations in the Hotel Management System. The system now sends detailed reservation confirmation emails with PDF attachments containing all reservation information.

## Features

### 1. Detailed Reservation Confirmation Email
- Sends comprehensive email with all reservation details
- Includes guest information, stay dates, room details, and pricing
- Provides additional information such as company, plan, settlement type, etc.
- Responsive HTML email template for better user experience

### 2. PDF Attachment
- Automatically generates a PDF file attachment with all reservation details
- Contains all information from the reservation in structured format
- Named with reservation number for easy identification
- Includes hotel information (name, address, phone, email)

### 3. API Endpoint
- New endpoint to manually trigger reservation confirmation emails
- Useful for resending emails or sending confirmations for existing reservations

## Implementation Details

### EmailService Enhancements
The [EmailService](file:///d%3A/ashward/hotelmanager/hotelworks/src/main/java/com/hotelworks/service/EmailService.java#L15-L268) class has been enhanced with:

1. `sendEmailWithAttachment()` method for sending emails with file attachments
2. `sendDetailedReservationConfirmation()` method for sending comprehensive reservation emails
3. `generateReservationPDFAttachment()` helper method for creating PDF content
4. Hotel information configuration properties

### ReservationService Integration
The [ReservationService](file:///d%3A/ashward/hotelmanager/hotelworks/src/main/java/com/hotelworks/service/ReservationService.java#L36-L596) class now:

1. Automatically sends detailed confirmation emails when creating new reservations
2. Includes `sendReservationConfirmationEmail()` method for manual email sending

### ReservationController Endpoint
A new endpoint has been added to [ReservationController](file:///d%3A/ashward/hotelmanager/hotelworks/src/main/java/com/hotelworks/controller/ReservationController.java#L22-L230):

```
POST /api/reservations/{reservationNo}/send-confirmation
```

## Usage

### Automatic Email Sending
When a new reservation is created via the API, a detailed confirmation email with attachment is automatically sent to the guest's email address (if provided).

### Manual Email Sending
To manually send a reservation confirmation email:

```bash
curl -X POST http://localhost:8080/api/reservations/RES123456/send-confirmation
```

## Email Content

### Subject
`Reservation Confirmation - {reservationNumber}`

### Body
The email body contains a responsive HTML template with:
- Guest name and reservation number
- Stay dates (arrival and departure)
- Number of days, persons, and rooms
- Rate and GST information
- Contact details
- Additional information (company, plan, room type, etc.)

### Attachment
A PDF file named `Reservation_{reservationNumber}.pdf` containing all reservation details in structured format, including hotel information.

## Configuration
The email functionality uses SendGrid for email delivery and requires the following configuration in `application.properties`:

```properties
sendgrid.api.key=your_sendgrid_api_key
sendgrid.from.email=your_from_email@example.com
hotel.name=Your Hotel Name
hotel.address=Your Hotel Address
hotel.phone=Your Hotel Phone
hotel.email=Your Hotel Email
```

## Testing
Unit tests have been added to verify the email functionality:
- `EmailServiceReservationTest.java` - Tests for email service methods
- `ReservationServiceEmailTest.java` - Integration tests for reservation service email functionality

## Error Handling
The email service includes comprehensive error handling:
- Validates email addresses and content before sending
- Logs errors and exceptions for debugging
- Returns appropriate error responses to API clients
- Falls back to text format if PDF generation fails