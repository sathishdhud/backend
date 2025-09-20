# ID Proof Implementation for Reservations

## Overview
This document describes the implementation of ID proof functionality for hotel reservations. Three ID proof fields have been added to the reservation system to store guest identification information.

## API Endpoints

### Create Reservation with ID Proofs
```http
POST /api/reservations
Authorization: Bearer <token>
Content-Type: application/json
```

**Request Body:**
```json
{
  "guestName": "John Doe",
  "arrivalDate": "2025-09-25",
  "departureDate": "2025-09-30",
  "noOfDays": 5,
  "noOfPersons": 2,
  "noOfRooms": 1,
  "mobileNumber": "+1234567890",
  "emailId": "john.doe@example.com",
  "rate": 5000.00,
  "includingGst": "N",
  "remarks": "Early check-in requested",
  "idProof1": "Passport: P12345678",
  "idProof2": "Driver License: DL987654321",
  "idProof3": "Aadhar Card: 1234-5678-9012",
  "settlementTypeId": "CASH",
  "arrivalModeId": "CAR",
  "nationalityId": "IND",
  "resvSourceId": "DIRECT"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Reservation created successfully",
  "data": {
    "reservationNo": "1/25-26",
    "guestName": "John Doe",
    "arrivalDate": "2025-09-25",
    "departureDate": "2025-09-30",
    "noOfDays": 5,
    "noOfPersons": 2,
    "noOfRooms": 1,
    "mobileNumber": "+1234567890",
    "emailId": "john.doe@example.com",
    "rate": 5000.00,
    "includingGst": "N",
    "remarks": "Early check-in requested",
    "idProof1": "Passport: P12345678",
    "idProof2": "Driver License: DL987654321",
    "idProof3": "Aadhar Card: 1234-5678-9012",
    "roomsCheckedIn": 0,
    "createdAt": "2025-09-20T14:30:00",
    "updatedAt": "2025-09-20T14:30:00",
    "settlementTypeId": "CASH",
    "settlementTypeName": "Cash Payment",
    "arrivalModeId": "CAR",
    "arrivalModeName": "By Car",
    "nationalityId": "IND",
    "nationalityName": "Indian",
    "resvSourceId": "DIRECT",
    "resvSourceName": "Direct Booking"
  },
  "timestamp": "2025-09-20T14:30:00"
}
```

### Update Reservation with ID Proofs
```http
PUT /api/reservations/{reservationNo}
Authorization: Bearer <token>
Content-Type: application/json
```

**Request Body:**
```json
{
  "guestName": "John Doe",
  "arrivalDate": "2025-09-25",
  "departureDate": "2025-09-30",
  "noOfDays": 5,
  "noOfPersons": 2,
  "noOfRooms": 1,
  "mobileNumber": "+1234567890",
  "emailId": "john.doe@example.com",
  "rate": 5000.00,
  "includingGst": "N",
  "remarks": "Early check-in requested",
  "idProof1": "Passport: P12345678 (Updated)",
  "idProof2": "Driver License: DL987654321 (Updated)",
  "idProof3": "Aadhar Card: 1234-5678-9012 (Updated)",
  "settlementTypeId": "CASH",
  "arrivalModeId": "CAR",
  "nationalityId": "IND",
  "resvSourceId": "DIRECT"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Reservation updated successfully",
  "data": {
    "reservationNo": "1/25-26",
    "guestName": "John Doe",
    "arrivalDate": "2025-09-25",
    "departureDate": "2025-09-30",
    "noOfDays": 5,
    "noOfPersons": 2,
    "noOfRooms": 1,
    "mobileNumber": "+1234567890",
    "emailId": "john.doe@example.com",
    "rate": 5000.00,
    "includingGst": "N",
    "remarks": "Early check-in requested",
    "idProof1": "Passport: P12345678 (Updated)",
    "idProof2": "Driver License: DL987654321 (Updated)",
    "idProof3": "Aadhar Card: 1234-5678-9012 (Updated)",
    "roomsCheckedIn": 0,
    "createdAt": "2025-09-20T14:30:00",
    "updatedAt": "2025-09-20T15:45:00",
    "settlementTypeId": "CASH",
    "settlementTypeName": "Cash Payment",
    "arrivalModeId": "CAR",
    "arrivalModeName": "By Car",
    "nationalityId": "IND",
    "nationalityName": "Indian",
    "resvSourceId": "DIRECT",
    "resvSourceName": "Direct Booking"
  },
  "timestamp": "2025-09-20T15:45:00"
}
```

### Get Reservation Details (with ID Proofs)
```http
GET /api/reservations/{reservationNo}
Authorization: Bearer <token>
```

**Response:**
```json
{
  "success": true,
  "data": {
    "reservationNo": "1/25-26",
    "guestName": "John Doe",
    "arrivalDate": "2025-09-25",
    "departureDate": "2025-09-30",
    "noOfDays": 5,
    "noOfPersons": 2,
    "noOfRooms": 1,
    "mobileNumber": "+1234567890",
    "emailId": "john.doe@example.com",
    "rate": 5000.00,
    "includingGst": "N",
    "remarks": "Early check-in requested",
    "idProof1": "Passport: P12345678 (Updated)",
    "idProof2": "Driver License: DL987654321 (Updated)",
    "idProof3": "Aadhar Card: 1234-5678-9012 (Updated)",
    "roomsCheckedIn": 0,
    "createdAt": "2025-09-20T14:30:00",
    "updatedAt": "2025-09-20T15:45:00",
    "settlementTypeId": "CASH",
    "settlementTypeName": "Cash Payment",
    "arrivalModeId": "CAR",
    "arrivalModeName": "By Car",
    "nationalityId": "IND",
    "nationalityName": "Indian",
    "resvSourceId": "DIRECT",
    "resvSourceName": "Direct Booking"
  },
  "timestamp": "2025-09-20T16:00:00"
}
```

## Database Schema Changes

### Reservation Table
The following columns have been added to the `reservations` table:

| Column Name | Data Type | Nullable | Description |
|-------------|-----------|----------|-------------|
| id_proof1 | VARCHAR(255) | YES | First ID proof document details |
| id_proof2 | VARCHAR(255) | YES | Second ID proof document details |
| id_proof3 | VARCHAR(255) | YES | Third ID proof document details |

## Implementation Details

### Entity Changes
- Added `idProof1`, `idProof2`, and `idProof3` fields to the [Reservation](file:///d:/ashward/hotelmanager/hotelworks/src/main/java/com/hotelworks/entity/Reservation.java#L12-L251) entity
- Added corresponding getter and setter methods

### DTO Changes
- Added `idProof1`, `idProof2`, and `idProof3` fields to [ReservationRequest](file:///d:/ashward/hotelmanager/hotelworks/src/main/java/com/hotelworks/dto/request/ReservationRequest.java#L10-L131) DTO
- Added `idProof1`, `idProof2`, and `idProof3` fields to [ReservationResponse](file:///d:/ashward/hotelmanager/hotelworks/src/main/java/com/hotelworks/dto/response/ReservationResponse.java#L8-L158) DTO
- Added corresponding getter and setter methods

### Service Changes
- Updated [ReservationService](file:///d:/ashward/hotelmanager/hotelworks/src/main/java/com/hotelworks/service/ReservationService.java#L25-L505) to handle ID proof fields in create and update operations
- Updated `mapToReservationResponse` method to include ID proof fields in responses

### Field Descriptions
- **idProof1**: Primary identification document (e.g., Passport, National ID)
- **idProof2**: Secondary identification document (e.g., Driver's License, PAN Card)
- **idProof3**: Tertiary identification document (e.g., Aadhar Card, Social Security Card)

Each field can store a string value containing the document type and number, such as "Passport: P12345678" or "Driver License: DL987654321".