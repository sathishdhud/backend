# Deleted Reservations in Separate Table Implementation

## Overview
This document describes the implementation of deleted reservation functionality using a separate table approach. Instead of marking reservations as deleted in the same table, deleted reservations are moved to a dedicated [reserv_deleted](file://d:\ashward\hotelmanager\hotelworks\src\main\java\com\hotelworks\repository\DeletedReservationRepository.java#L27-L27) table while being removed from the active reservations table.

## API Endpoints

### Delete Reservation (Move to Deleted Table)
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

### New reserv_deleted Table
A new table `reserv_deleted` has been created with the same structure as the `reservations` table, plus a `deleted_at` timestamp column:

| Column Name | Data Type | Nullable | Description |
|-------------|-----------|----------|-------------|
| reservation_no | VARCHAR(255) | NO | Primary key, same as in reservations table |
| guest_name | VARCHAR(255) | NO | Guest name |
| company_id | VARCHAR(255) | YES | Company ID |
| plan_id | VARCHAR(255) | YES | Plan ID |
| room_type_id | VARCHAR(255) | YES | Room type ID |
| arrival_date | DATE | NO | Arrival date |
| departure_date | DATE | NO | Departure date |
| no_of_days | INTEGER | NO | Number of days |
| no_of_persons | INTEGER | NO | Number of persons |
| no_of_rooms | INTEGER | NO | Number of rooms |
| mobile_number | VARCHAR(255) | NO | Mobile number |
| email_id | VARCHAR(255) | YES | Email ID |
| rate | DECIMAL | YES | Rate |
| including_gst | VARCHAR(1) | YES | Including GST flag |
| remarks | VARCHAR(255) | YES | Remarks |
| rooms_checked_in | INTEGER | YES | Rooms checked in |
| id_proof1 | VARCHAR(255) | YES | ID Proof 1 |
| id_proof2 | VARCHAR(255) | YES | ID Proof 2 |
| id_proof3 | VARCHAR(255) | YES | ID Proof 3 |
| settlement_type_id | VARCHAR(255) | YES | Settlement type ID |
| arrival_mode_id | VARCHAR(255) | YES | Arrival mode ID |
| arrival_details | VARCHAR(255) | YES | Arrival details |
| nationality_id | VARCHAR(255) | YES | Nationality ID |
| ref_mode_id | VARCHAR(255) | YES | Reference mode ID |
| resv_source_id | VARCHAR(255) | YES | Reservation source ID |
| created_at | TIMESTAMP | YES | Created timestamp |
| updated_at | TIMESTAMP | YES | Updated timestamp |
| deleted_at | TIMESTAMP | YES | Deletion timestamp |

## Implementation Details

### Entity Changes
- Created new [DeletedReservation](file:///d:/ashward/hotelmanager/hotelworks/src/main/java/com/hotelworks/entity/DeletedReservation.java#L12-L230) entity that mirrors the [Reservation](file:///d:/ashward/hotelmanager/hotelworks/src/main/java/com/hotelworks/entity/Reservation.java#L12-L276) entity structure
- Added constructor to [DeletedReservation](file:///d:/ashward/hotelmanager/hotelworks/src/main/java/com/hotelworks/entity/DeletedReservation.java#L12-L230) that takes a [Reservation](file:///d:/ashward/hotelmanager/hotelworks/src/main/java/com/hotelworks/entity/Reservation.java#L12-L276) object and copies all fields
- Added [deletedAt](file://d:\ashward\hotelmanager\hotelworks\src\main\java\com\hotelworks\entity\DeletedReservation.java#L109-L109) timestamp field to track when the reservation was deleted

### Repository Changes
- Created [DeletedReservationRepository](file:///d:/ashward/hotelmanager/hotelworks/src/main/java/com/hotelworks/repository/DeletedReservationRepository.java#L15-L27) for accessing the [reserv_deleted](file://d:\ashward\hotelmanager\hotelworks\src\main\java\com\hotelworks\repository\DeletedReservationRepository.java#L27-L27) table
- Added basic query methods for finding deleted reservations

### Service Changes
- Updated [ReservationService](file:///d:/ashward/hotelmanager/hotelworks/src/main/java/com/hotelworks/service/ReservationService.java#L25-L578) [deleteReservation()](file://d:\ashward\hotelmanager\hotelworks\src\main\java\com\hotelworks\service\ReservationService.java#L440-L453) method to move reservations to the deleted table
- Added [getDeletedReservations()](file://d:\ashward\hotelmanager\hotelworks\src\main\java\com\hotelworks\service\ReservationService.java#L479-L485) method to retrieve deleted reservations
- Added [restoreReservation()](file://d:\ashward\hotelmanager\hotelworks\src\main\java\com\hotelworks\service\ReservationService.java#L490-L524) method to restore deleted reservations to the active table
- Created [DeletedReservationResponse](file:///d:/ashward/hotelmanager/hotelworks/src/main/java/com/hotelworks/dto/response/DeletedReservationResponse.java#L11-L204) DTO for deleted reservation responses

### Controller Changes
- Updated [ReservationController](file:///d:/ashward/hotelmanager/hotelworks/src/main/java/com/hotelworks/controller/ReservationController.java#L22-L230) delete endpoint to move reservations to the deleted table
- Added `/deleted` endpoint to retrieve deleted reservations using [DeletedReservationResponse](file:///d:/ashward/hotelmanager/hotelworks/src/main/java/com/hotelworks/dto/response/DeletedReservationResponse.java#L11-L204)
- Added `/{reservationNo}/restore` endpoint to restore deleted reservations

## Behavior

1. **Delete Operation**: When a reservation is deleted, it is removed from the active `reservations` table and a copy is created in the `reserv_deleted` table with a deletion timestamp.

2. **Hidden from Regular Queries**: Deleted reservations are completely removed from the active reservations table and do not appear in any regular reservation queries.

3. **Recovery**: Deleted reservations can be restored using the restore endpoint, which moves them back to the active reservations table and removes them from the deleted table.

4. **Audit Trail**: All deletion actions are tracked with timestamps in the separate deleted table, providing a complete audit trail.

## Field Descriptions

- **deletedAt**: Timestamp when the reservation was deleted and moved to the separate table.

This implementation ensures that reservation data is preserved for audit purposes in a separate table while maintaining clean user interfaces that only show active reservations.