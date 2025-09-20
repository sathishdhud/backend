# Soft Delete Implementation for Reservations

## Overview
This document describes the implementation of soft delete functionality for hotel reservations. Instead of permanently deleting reservations, they are marked as deleted and hidden from regular queries while still being stored in the database for audit purposes.

## API Endpoints

### Delete Reservation (Soft Delete)
```http
DELETE /api/reservations/{reservationNo}
Authorization: Bearer <token>
```

**Response:**
```json
{
  "success": true,
  "message": "Reservation deleted successfully",
  "timestamp": "2025-09-20T14:30:00"
}
```

### Get Deleted Reservations
```http
GET /api/reservations/deleted
Authorization: Bearer <token>
```

**Response:**
```json
{
  "success": true,
  "data": [
    {
      "reservationNo": "2/25-26",
      "guestName": "Jane Smith",
      "arrivalDate": "2025-09-20",
      "departureDate": "2025-09-25",
      "noOfDays": 5,
      "noOfPersons": 2,
      "noOfRooms": 1,
      "mobileNumber": "+9876543210",
      "emailId": "jane.smith@example.com",
      "rate": 4500.00,
      "includingGst": "N",
      "remarks": "Late checkout requested",
      "idProof1": "Passport: P87654321",
      "idProof2": "Driver License: DL123456789",
      "idProof3": "Aadhar Card: 9876-5432-1098",
      "roomsCheckedIn": 0,
      "createdAt": "2025-09-15T10:30:00",
      "updatedAt": "2025-09-18T16:45:00",
      "deleted": true,
      "deletedAt": "2025-09-20T14:30:00",
      "settlementTypeId": "CREDIT",
      "settlementTypeName": "Credit Card",
      "arrivalModeId": "FLIGHT",
      "arrivalModeName": "By Flight",
      "nationalityId": "USA",
      "nationalityName": "American",
      "resvSourceId": "ONLINE",
      "resvSourceName": "Online Booking"
    }
  ],
  "timestamp": "2025-09-20T15:00:00"
}
```

### Restore Deleted Reservation
```http
PUT /api/reservations/{reservationNo}/restore
Authorization: Bearer <token>
```

**Response:**
```json
{
  "success": true,
  "message": "Reservation restored successfully",
  "data": {
    "reservationNo": "2/25-26",
    "guestName": "Jane Smith",
    "arrivalDate": "2025-09-20",
    "departureDate": "2025-09-25",
    "noOfDays": 5,
    "noOfPersons": 2,
    "noOfRooms": 1,
    "mobileNumber": "+9876543210",
    "emailId": "jane.smith@example.com",
    "rate": 4500.00,
    "includingGst": "N",
    "remarks": "Late checkout requested",
    "idProof1": "Passport: P87654321",
    "idProof2": "Driver License: DL123456789",
    "idProof3": "Aadhar Card: 9876-5432-1098",
    "roomsCheckedIn": 0,
    "createdAt": "2025-09-15T10:30:00",
    "updatedAt": "2025-09-20T15:15:00",
    "deleted": false,
    "deletedAt": null,
    "settlementTypeId": "CREDIT",
    "settlementTypeName": "Credit Card",
    "arrivalModeId": "FLIGHT",
    "arrivalModeName": "By Flight",
    "nationalityId": "USA",
    "nationalityName": "American",
    "resvSourceId": "ONLINE",
    "resvSourceName": "Online Booking"
  },
  "timestamp": "2025-09-20T15:15:00"
}
```

## Database Schema Changes

### Reservation Table
The following columns have been added to the `reservations` table:

| Column Name | Data Type | Nullable | Default | Description |
|-------------|-----------|----------|---------|-------------|
| deleted | BOOLEAN | NO | false | Flag indicating if the reservation is deleted |
| deleted_at | TIMESTAMP | YES | NULL | Timestamp when the reservation was deleted |

## Implementation Details

### Entity Changes
- Added `deleted` boolean field to the [Reservation](file:///d:/ashward/hotelmanager/hotelworks/src/main/java/com/hotelworks/entity/Reservation.java#L12-L276) entity with default value `false`
- Added `deletedAt` timestamp field to the [Reservation](file:///d:/ashward/hotelmanager/hotelworks/src/main/java/com/hotelworks/entity/Reservation.java#L12-L276) entity
- Added corresponding getter and setter methods
- Updated `@PrePersist` method to ensure `deleted` defaults to `false`

### Repository Changes
- Updated [ReservationRepository](file:///d:/ashward/hotelmanager/hotelworks/src/main/java/com/hotelworks/repository/ReservationRepository.java#L15-L57) to exclude deleted reservations in default queries
- Added `findDeletedReservations()` method to retrieve only deleted reservations
- Added `findByReservationNoIncludingDeleted()` method to find reservations by ID including deleted ones

### Service Changes
- Updated [ReservationService](file:///d:/ashward/hotelmanager/hotelworks/src/main/java/com/hotelworks/service/ReservationService.java#L25-L541) delete method to perform soft delete instead of hard delete
- Added `getDeletedReservations()` method to retrieve deleted reservations
- Added `restoreReservation()` method to restore deleted reservations
- Updated `mapToReservationResponse()` method to include deleted fields in responses

### Controller Changes
- Updated [ReservationController](file:///d:/ashward/hotelmanager/hotelworks/src/main/java/com/hotelworks/controller/ReservationController.java#L22-L204) delete endpoint to perform soft delete
- Added `/deleted` endpoint to retrieve deleted reservations
- Added `/{reservationNo}/restore` endpoint to restore deleted reservations

### DTO Changes
- Added `deleted` boolean field to [ReservationResponse](file:///d:/ashward/hotelmanager/hotelworks/src/main/java/com/hotelworks/dto/response/ReservationResponse.java#L8-L176) DTO
- Added `deletedAt` timestamp field to [ReservationResponse](file:///d:/ashward/hotelmanager/hotelworks/src/main/java/com/hotelworks/dto/response/ReservationResponse.java#L8-L176) DTO
- Added corresponding getter and setter methods

## Behavior

1. **Soft Delete**: When a reservation is deleted, it is not removed from the database but marked with `deleted = true` and a timestamp is recorded in `deletedAt`.

2. **Hidden from Regular Queries**: All default queries (getAll, search, etc.) automatically exclude deleted reservations.

3. **Recovery**: Deleted reservations can be restored using the restore endpoint, which sets `deleted = false` and clears the `deletedAt` timestamp.

4. **Audit Trail**: All deletion and restoration actions are tracked with timestamps, providing a complete audit trail.

## Field Descriptions

- **deleted**: Boolean flag indicating if the reservation has been deleted (`true`) or is active (`false`)
- **deletedAt**: Timestamp when the reservation was deleted. `null` for active reservations.

This implementation ensures that reservation data is preserved for audit purposes while maintaining clean user interfaces that only show active reservations.