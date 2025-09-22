# Advance Payment API Documentation with Card Payment Details

## Overview
This document provides detailed information about the Advance Payment APIs with card payment support, including request/response formats and example JSON payloads.

## Base URL
```
http://localhost:8080/api/advances
```

---

## 1. Create Advance for Reservation

### Endpoint
```
POST /api/advances/reservation
```

### Description
Creates an advance payment for a reservation with card payment details support.

### Request Body
```json
{
  "reservationNo": "RES001",
  "guestName": "John Doe",
  "date": "2024-01-15",
  "arrivalDate": "2024-01-20",
  "modeOfPaymentId": "CARD",
  "amount": 5000.00,
  "creditCardCompany": "Visa",
  "cardNumber": "**** **** **** 1234",
  "details": "Card payment for advance booking",
  "narration": "Advance payment via credit card"
}
```

### Response (201 Created)
```json
{
  "success": true,
  "message": "Advance created successfully for reservation",
  "data": {
    "receiptNo": "ADV001",
    "folioNo": null,
    "reservationNo": "RES001",
    "billNo": null,
    "guestName": "John Doe",
    "date": "2024-01-15",
    "arrivalDate": "2024-01-20",
    "auditDate": "2024-01-15",
    "shiftDate": "2024-01-15",
    "shiftNo": "SHIFT001",
    "modeOfPaymentId": "CARD",
    "modeOfPaymentName": "Credit Card",
    "amount": 5000.00,
    "creditCardCompany": "Visa",
    "cardNumber": "**** **** **** 1234",
    "onlineCompanyName": null,
    "details": "Card payment for advance booking",
    "narration": "Advance payment via credit card"
  }
}
```

---

## 2. Create Advance for In-House Guest

### Endpoint
```
POST /api/advances/inhouse
```

### Description
Creates an advance payment for an in-house guest with card payment details.

### Request Body
```json
{
  "folioNo": "FOL001",
  "guestName": "Jane Smith",
  "date": "2024-01-15",
  "modeOfPaymentId": "DEBIT",
  "amount": 2500.00,
  "creditCardCompany": "MasterCard",
  "cardNumber": "**** **** **** 5678",
  "details": "Debit card payment for room services",
  "narration": "In-house advance payment"
}
```

### Response (201 Created)
```json
{
  "success": true,
  "message": "Advance created successfully for in-house guest",
  "data": {
    "receiptNo": "ADV002",
    "folioNo": "FOL001",
    "reservationNo": null,
    "billNo": null,
    "guestName": "Jane Smith",
    "date": "2024-01-15",
    "arrivalDate": null,
    "auditDate": "2024-01-15",
    "shiftDate": "2024-01-15",
    "shiftNo": "SHIFT001",
    "modeOfPaymentId": "DEBIT",
    "modeOfPaymentName": "Debit Card",
    "amount": 2500.00,
    "creditCardCompany": "MasterCard",
    "cardNumber": "**** **** **** 5678",
    "onlineCompanyName": null,
    "details": "Debit card payment for room services",
    "narration": "In-house advance payment"
  }
}
```

---

## 3. Create Advance for Checkout Guest

### Endpoint
```
POST /api/advances/checkout
```

### Description
Creates an advance payment for a checkout guest with online payment support.

### Request Body
```json
{
  "billNo": "BILL001",
  "guestName": "Robert Johnson",
  "date": "2024-01-15",
  "modeOfPaymentId": "ONLINE",
  "amount": 7500.00,
  "onlineCompanyName": "PayPal",
  "details": "Online payment via PayPal",
  "narration": "Checkout advance payment"
}
```

### Response (201 Created)
```json
{
  "success": true,
  "message": "Advance created successfully for checkout guest",
  "data": {
    "receiptNo": "ADV003",
    "folioNo": null,
    "reservationNo": null,
    "billNo": "BILL001",
    "guestName": "Robert Johnson",
    "date": "2024-01-15",
    "arrivalDate": null,
    "auditDate": "2024-01-15",
    "shiftDate": "2024-01-15",
    "shiftNo": "SHIFT001",
    "modeOfPaymentId": "ONLINE",
    "modeOfPaymentName": "Online Payment",
    "amount": 7500.00,
    "creditCardCompany": null,
    "cardNumber": null,
    "onlineCompanyName": "PayPal",
    "details": "Online payment via PayPal",
    "narration": "Checkout advance payment"
  }
}
```

---

## 4. Create Advance for Reservation with Bill

### Endpoint
```
POST /api/advances/reservation/{reservationNo}/bill/{billNo}
```

### Description
Creates an advance payment for a reservation associated with a specific bill.

### Path Parameters
- `reservationNo` (string): The reservation number
- `billNo` (string): The bill number

### Request Body
```json
{
  "guestName": "Alice Brown",
  "date": "2024-01-15",
  "modeOfPaymentId": "CARD",
  "amount": 3000.00,
  "creditCardCompany": "American Express",
  "cardNumber": "**** **** **** 9012",
  "details": "Amex card payment for reservation bill",
  "narration": "Advance payment for reservation with bill"
}
```

### Response (201 Created)
```json
{
  "success": true,
  "message": "Advance created successfully for reservation with bill",
  "data": {
    "receiptNo": "ADV004",
    "folioNo": null,
    "reservationNo": "RES002",
    "billNo": "BILL002",
    "guestName": "Alice Brown",
    "date": "2024-01-15",
    "arrivalDate": null,
    "auditDate": "2024-01-15",
    "shiftDate": "2024-01-15",
    "shiftNo": "SHIFT001",
    "modeOfPaymentId": "CARD",
    "modeOfPaymentName": "Credit Card",
    "amount": 3000.00,
    "creditCardCompany": "American Express",
    "cardNumber": "**** **** **** 9012",
    "onlineCompanyName": null,
    "details": "Amex card payment for reservation bill",
    "narration": "Advance payment for reservation with bill"
  }
}
```

---

## 5. Get Advances by Reservation

### Endpoint
```
GET /api/advances/reservation/{reservationNo}
```

### Path Parameters
- `reservationNo` (string): The reservation number

### Response (200 OK)
```json
{
  "success": true,
  "message": null,
  "data": [
    {
      "receiptNo": "ADV001",
      "folioNo": null,
      "reservationNo": "RES001",
      "billNo": null,
      "guestName": "John Doe",
      "date": "2024-01-15",
      "arrivalDate": "2024-01-20",
      "auditDate": "2024-01-15",
      "shiftDate": "2024-01-15",
      "shiftNo": "SHIFT001",
      "modeOfPaymentId": "CARD",
      "modeOfPaymentName": "Credit Card",
      "amount": 5000.00,
      "creditCardCompany": "Visa",
      "cardNumber": "**** **** **** 1234",
      "onlineCompanyName": null,
      "details": "Card payment for advance booking",
      "narration": "Advance payment via credit card"
    }
  ]
}
```

---

## 6. Get Advances by Folio

### Endpoint
```
GET /api/advances/folio/{folioNo}
```

### Path Parameters
- `folioNo` (string): The folio number

### Response (200 OK)
```json
{
  "success": true,
  "message": null,
  "data": [
    {
      "receiptNo": "ADV002",
      "folioNo": "FOL001",
      "reservationNo": null,
      "billNo": null,
      "guestName": "Jane Smith",
      "date": "2024-01-15",
      "arrivalDate": null,
      "auditDate": "2024-01-15",
      "shiftDate": "2024-01-15",
      "shiftNo": "SHIFT001",
      "modeOfPaymentId": "DEBIT",
      "modeOfPaymentName": "Debit Card",
      "amount": 2500.00,
      "creditCardCompany": "MasterCard",
      "cardNumber": "**** **** **** 5678",
      "onlineCompanyName": null,
      "details": "Debit card payment for room services",
      "narration": "In-house advance payment"
    }
  ]
}
```

---

## 7. Get Total Advances by Reservation

### Endpoint
```
GET /api/advances/reservation/{reservationNo}/total
```

### Response (200 OK)
```json
{
  "success": true,
  "message": null,
  "data": 5000.00
}
```

---

## 8. Update Advance Payment

### Endpoint
```
PUT /api/advances/{advanceId}
```

### Path Parameters
- `advanceId` (string): The advance receipt number

### Request Body
```json
{
  "guestName": "John Doe Updated",
  "modeOfPaymentId": "CARD",
  "amount": 5500.00,
  "creditCardCompany": "Visa Platinum",
  "cardNumber": "**** **** **** 1234",
  "details": "Updated card payment details",
  "narration": "Updated advance payment via credit card"
}
```

### Response (200 OK)
```json
{
  "success": true,
  "message": "Advance updated successfully",
  "data": {
    "receiptNo": "ADV001",
    "folioNo": null,
    "reservationNo": "RES001",
    "billNo": null,
    "guestName": "John Doe Updated",
    "date": "2024-01-15",
    "arrivalDate": "2024-01-20",
    "auditDate": "2024-01-15",
    "shiftDate": "2024-01-15",
    "shiftNo": "SHIFT001",
    "modeOfPaymentId": "CARD",
    "modeOfPaymentName": "Credit Card",
    "amount": 5500.00,
    "creditCardCompany": "Visa Platinum",
    "cardNumber": "**** **** **** 1234",
    "onlineCompanyName": null,
    "details": "Updated card payment details",
    "narration": "Updated advance payment via credit card"
  }
}
```

---

## Payment Mode IDs

The following payment mode IDs are commonly used:

| Mode ID | Description |
|---------|-------------|
| CASH | Cash Payment |
| CARD | Credit Card |
| DEBIT | Debit Card |
| ONLINE | Online Payment |
| CHEQUE | Cheque Payment |
| UPI | UPI Payment |
| NEFT | NEFT Transfer |
| RTGS | RTGS Transfer |

---

## Card Payment Fields

When using card payments (`modeOfPaymentId`: "CARD" or "DEBIT"), include these fields:

- `creditCardCompany`: The card company (Visa, MasterCard, American Express, etc.)
- `cardNumber`: Masked card number for security (e.g., "**** **** **** 1234")
- `details`: Additional card payment details
- `narration`: Payment description/notes

## Online Payment Fields

When using online payments (`modeOfPaymentId`: "ONLINE"), include:

- `onlineCompanyName`: The online payment provider (PayPal, Razorpay, Paytm, etc.)
- `details`: Transaction reference or additional details
- `narration`: Payment description/notes

---

## Error Responses

### 400 Bad Request
```json
{
  "success": false,
  "message": "Failed to create advance: Guest name is required",
  "data": null
}
```

### 404 Not Found
```json
{
  "success": false,
  "message": "Advance not found: ADV999",
  "data": null
}
```

### 500 Internal Server Error
```json
{
  "success": false,
  "message": "Internal server error occurred while processing advance payment",
  "data": null
}
```

---

## Security Notes

1. **Card Number Masking**: Always mask card numbers in responses for security
2. **Sensitive Data**: Card details should be handled securely and not logged
3. **Validation**: Validate card company and payment mode combinations
4. **Audit Trail**: All advance payments are tracked with audit dates and shift information

---

## Additional Features

1. **Automatic Receipt Generation**: Each advance payment gets a unique receipt number
2. **Shift Tracking**: Payments are associated with the current shift
3. **Multi-Reference Support**: Advances can be linked to reservations, folios, or bills
4. **Payment Method Flexibility**: Supports cash, card, online, and other payment methods
5. **Guest Name Auto-Population**: Guest names can be auto-populated from reservations or folios