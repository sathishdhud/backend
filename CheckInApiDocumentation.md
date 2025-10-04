# Check-in API Documentation

This document provides detailed information about the Check-in Management APIs, including endpoints for processing check-ins with enhanced fields similar to reservations.

## Base URL
```
http://localhost:8080/api/checkins
```

## Endpoints

### 1. Process Check-in

**Endpoint:** `POST /`

**Description:** Process guest check-in (reservation or walk-in) with enhanced fields including ID proofs and additional details

**Request Body:**
```json
{
  "reservationNo": "RES20250921001",
  "guestName": "John Doe",
  "roomId": "RM101",
  "arrivalDate": "2025-09-21",
  "departureDate": "2025-09-23",
  "mobileNumber": "9876543210",
  "emailId": "john.doe@example.com",
  "rate": 2500.00,
  "remarks": "Early check-in requested",
  "walkIn": "N",
  "idProof1": "Passport: P12345678",
  "idProof2": "Driving License: DL987654321",
  "idProof3": "Aadhar Card: 1234-5678-9012",
  "companyId": "CMP001",
  "planId": "PLAN001",
  "roomTypeId": "RT001",
  "settlementTypeId": "ST001",
  "arrivalModeId": "AM001",
  "arrivalDetails": "Flight AA123 at 14:30",
  "nationalityId": "NT001",
  "refModeId": "REF001",
  "resvSourceId": "SRC001"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Check-in processed successfully",
  "data": {
    "folioNo": "FOL20250921001",
    "reservationNo": "RES20250921001",
    "guestName": "John Doe",
    "roomId": "RM101",
    "roomNo": "101",
    "arrivalDate": "2025-09-21",
    "departureDate": "2025-09-23",
    "mobileNumber": "9876543210",
    "emailId": "john.doe@example.com",
    "rate": 2500.00,
    "remarks": "Early check-in requested",
    "auditDate": "2025-09-21",
    "walkIn": "N",
    "totalAdvances": 0.00,
    "idProof1": "Passport: P12345678",
    "idProof2": "Driving License: DL987654321",
    "idProof3": "Aadhar Card: 1234-5678-9012",
    "companyId": "CMP001",
    "planId": "PLAN001",
    "roomTypeId": "RT001",
    "settlementTypeId": "ST001",
    "arrivalModeId": "AM001",
    "arrivalDetails": "Flight AA123 at 14:30",
    "nationalityId": "NT001",
    "refModeId": "REF001",
    "resvSourceId": "SRC001"
  },
  "timestamp": "2025-09-21T10:30:00"
}
```

### 2. Get Check-in by Folio Number

**Endpoint:** `GET /{folioNo}`

**Description:** Retrieves check-in details by folio number

**Response:**
```json
{
  "success": true,
  "message": "Check-in retrieved successfully",
  "data": {
    "folioNo": "FOL20250921001",
    "reservationNo": "RES20250921001",
    "guestName": "John Doe",
    "roomId": "RM101",
    "roomNo": "101",
    "arrivalDate": "2025-09-21",
    "departureDate": "2025-09-23",
    "mobileNumber": "9876543210",
    "emailId": "john.doe@example.com",
    "rate": 2500.00,
    "remarks": "Early check-in requested",
    "auditDate": "2025-09-21",
    "walkIn": "N",
    "totalAdvances": 500.00,
    "idProof1": "Passport: P12345678",
    "idProof2": "Driving License: DL987654321",
    "idProof3": "Aadhar Card: 1234-5678-9012",
    "companyId": "CMP001",
    "planId": "PLAN001",
    "roomTypeId": "RT001",
    "settlementTypeId": "ST001",
    "arrivalModeId": "AM001",
    "arrivalDetails": "Flight AA123 at 14:30",
    "nationalityId": "NT001",
    "refModeId": "REF001",
    "resvSourceId": "SRC001"
  },
  "timestamp": "2025-09-21T10:30:00"
}
```

### 3. Get Check-in by Room

**Endpoint:** `GET /room/{roomId}`

**Description:** Get check-in details for a specific room

**Response:**
```json
{
  "success": true,
  "message": "Check-in retrieved successfully",
  "data": {
    "folioNo": "FOL20250921001",
    "reservationNo": "RES20250921001",
    "guestName": "John Doe",
    "roomId": "RM101",
    "roomNo": "101",
    "arrivalDate": "2025-09-21",
    "departureDate": "2025-09-23",
    "mobileNumber": "9876543210",
    "emailId": "john.doe@example.com",
    "rate": 2500.00,
    "remarks": "Early check-in requested",
    "auditDate": "2025-09-21",
    "walkIn": "N",
    "totalAdvances": 500.00,
    "idProof1": "Passport: P12345678",
    "idProof2": "Driving License: DL987654321",
    "idProof3": "Aadhar Card: 1234-5678-9012",
    "companyId": "CMP001",
    "planId": "PLAN001",
    "roomTypeId": "RT001",
    "settlementTypeId": "ST001",
    "arrivalModeId": "AM001",
    "arrivalDetails": "Flight AA123 at 14:30",
    "nationalityId": "NT001",
    "refModeId": "REF001",
    "resvSourceId": "SRC001"
  },
  "timestamp": "2025-09-21T10:30:00"
}
```

### 4. Search Check-ins

**Endpoint:** `GET /search`

**Description:** Search check-ins by guest name, folio number, or room ID

**Parameters:**
- `searchTerm` (required): Search term

**Response:**
```json
{
  "success": true,
  "message": "Check-ins retrieved successfully",
  "data": [
    {
      "folioNo": "FOL20250921001",
      "reservationNo": "RES20250921001",
      "guestName": "John Doe",
      "roomId": "RM101",
      "roomNo": "101",
      "arrivalDate": "2025-09-21",
      "departureDate": "2025-09-23",
      "mobileNumber": "9876543210",
      "emailId": "john.doe@example.com",
      "rate": 2500.00,
      "remarks": "Early check-in requested",
      "auditDate": "2025-09-21",
      "walkIn": "N",
      "totalAdvances": 500.00,
      "idProof1": "Passport: P12345678",
      "idProof2": "Driving License: DL987654321",
      "idProof3": "Aadhar Card: 1234-5678-9012",
      "companyId": "CMP001",
      "planId": "PLAN001",
      "roomTypeId": "RT001",
      "settlementTypeId": "ST001",
      "arrivalModeId": "AM001",
      "arrivalDetails": "Flight AA123 at 14:30",
      "nationalityId": "NT001",
      "refModeId": "REF001",
      "resvSourceId": "SRC001"
    }
  ],
  "timestamp": "2025-09-21T10:30:00"
}
```

### 5. Get In-house Guests

**Endpoint:** `GET /inhouse`

**Description:** Get all currently in-house guests

**Response:**
```json
{
  "success": true,
  "message": "In-house guests retrieved successfully",
  "data": [
    {
      "folioNo": "FOL20250921001",
      "reservationNo": "RES20250921001",
      "guestName": "John Doe",
      "roomId": "RM101",
      "roomNo": "101",
      "arrivalDate": "2025-09-21",
      "departureDate": "2025-09-23",
      "mobileNumber": "9876543210",
      "emailId": "john.doe@example.com",
      "rate": 2500.00,
      "remarks": "Early check-in requested",
      "auditDate": "2025-09-21",
      "walkIn": "N",
      "totalAdvances": 500.00,
      "idProof1": "Passport: P12345678",
      "idProof2": "Driving License: DL987654321",
      "idProof3": "Aadhar Card: 1234-5678-9012",
      "companyId": "CMP001",
      "planId": "PLAN001",
      "roomTypeId": "RT001",
      "settlementTypeId": "ST001",
      "arrivalModeId": "AM001",
      "arrivalDetails": "Flight AA123 at 14:30",
      "nationalityId": "NT001",
      "refModeId": "REF001",
      "resvSourceId": "SRC001"
    }
  ],
  "timestamp": "2025-09-21T10:30:00"
}
```

### 6. Get Expected Checkouts

**Endpoint:** `GET /checkouts/{date}`

**Description:** Get expected checkouts for a specific date

**Parameters:**
- `date` (required): Checkout date (YYYY-MM-DD)

**Response:**
```json
{
  "success": true,
  "message": "Expected checkouts retrieved successfully",
  "data": [
    {
      "folioNo": "FOL20250921001",
      "reservationNo": "RES20250921001",
      "guestName": "John Doe",
      "roomId": "RM101",
      "roomNo": "101",
      "arrivalDate": "2025-09-21",
      "departureDate": "2025-09-23",
      "mobileNumber": "9876543210",
      "emailId": "john.doe@example.com",
      "rate": 2500.00,
      "remarks": "Early check-in requested",
      "auditDate": "2025-09-21",
      "walkIn": "N",
      "totalAdvances": 500.00,
      "idProof1": "Passport: P12345678",
      "idProof2": "Driving License: DL987654321",
      "idProof3": "Aadhar Card: 1234-5678-9012",
      "companyId": "CMP001",
      "planId": "PLAN001",
      "roomTypeId": "RT001",
      "settlementTypeId": "ST001",
      "arrivalModeId": "AM001",
      "arrivalDetails": "Flight AA123 at 14:30",
      "nationalityId": "NT001",
      "refModeId": "REF001",
      "resvSourceId": "SRC001"
    }
  ],
  "timestamp": "2025-09-21T10:30:00"
}
```

### 7. Update Check-in Details

**Endpoint:** `PUT /{folioNo}`

**Description:** Update check-in information (guest name, departure date, rate, remarks, ID proofs, and additional details)

**Request Body:**
```json
{
  "guestName": "John Smith",
  "includingGst": "N",
  "departureDate": "2025-09-24",
  "rate": 3000.00,
  "remarks": "Extended stay requested",
  "mobileNumber": "9876543210",
  "emailId": "john.doe@example.com",
  "idProof1": "Passport: P12345678",
  "idProof2": "Driving License: DL987654321",
  "idProof3": "Aadhar Card: 1234-5678-9012",
  "companyId": "CMP001",
  "planId": "PLAN001",
  "roomTypeId": "RT001",
  "settlementTypeId": "ST001",
  "arrivalModeId": "AM001",
  "arrivalDetails": "Flight AA123 at 14:30",
  "nationalityId": "NT001",
  "refModeId": "REF001",
  "resvSourceId": "SRC001",
  "walkIn": "N"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Check-in updated successfully",
  "data": {
    "folioNo": "FOL20250921001",
    "reservationNo": "RES20250921001",
    "guestName": "John Smith",
    "roomId": "RM101",
    "roomNo": "101",
    "arrivalDate": "2025-09-21",
    "departureDate": "2025-09-24",
    "mobileNumber": "9876543210",
    "emailId": "john.doe@example.com",
    "rate": 3000.00,
    "remarks": "Extended stay requested",
    "auditDate": "2025-09-21",
    "walkIn": "N",
    "includingGst": "N",
    "totalAdvances": 500.00,
    "idProof1": "Passport: P12345678",
    "idProof2": "Driving License: DL987654321",
    "idProof3": "Aadhar Card: 1234-5678-9012",
    "companyId": "CMP001",
    "planId": "PLAN001",
    "roomTypeId": "RT001",
    "settlementTypeId": "ST001",
    "arrivalModeId": "AM001",
    "arrivalDetails": "Flight AA123 at 14:30",
    "nationalityId": "NT001",
    "refModeId": "REF001",
    "resvSourceId": "SRC001"
  },
  "timestamp": "2025-09-21T10:30:00"
}
```

## Data Models

### Check-in Request
| Field | Type | Required | Description |
|-------|------|----------|-------------|
| reservationNo | String | No | Reservation number (for reservation check-ins) |
| guestName | String | Conditional | Guest name (required for walk-ins) |
| roomId | String | Yes | Room ID |
| arrivalDate | Date | Yes | Arrival date |
| departureDate | Date | Yes | Departure date |
| mobileNumber | String | Yes | Mobile number |
| emailId | String | No | Email ID |
| rate | BigDecimal | No | Room rate |
| remarks | String | No | Remarks |
| walkIn | String | Yes | Walk-in flag (Y/N) |
| idProof1 | String | No | ID Proof 1 |
| idProof2 | String | No | ID Proof 2 |
| idProof3 | String | No | ID Proof 3 |
| companyId | String | No | Company ID |
| planId | String | No | Plan ID |
| roomTypeId | String | No | Room Type ID |
| settlementTypeId | String | No | Settlement Type ID |
| arrivalModeId | String | No | Arrival Mode ID |
| arrivalDetails | String | No | Arrival Details |
| nationalityId | String | No | Nationality ID |
| refModeId | String | No | Reference Mode ID |
| resvSourceId | String | No | Reservation Source ID |

### Check-in Response
| Field | Type | Description |
|-------|------|-------------|
| folioNo | String | Folio number |
| reservationNo | String | Reservation number |
| guestName | String | Guest name |
| roomId | String | Room ID |
| roomNo | String | Room number |
| arrivalDate | Date | Arrival date |
| departureDate | Date | Departure date |
| mobileNumber | String | Mobile number |
| emailId | String | Email ID |
| rate | BigDecimal | Room rate |
| remarks | String | Remarks |
| auditDate | Date | Audit date |
| walkIn | String | Walk-in flag (Y/N) |
| totalAdvances | BigDecimal | Total advances |
| idProof1 | String | ID Proof 1 |
| idProof2 | String | ID Proof 2 |
| idProof3 | String | ID Proof 3 |
| companyId | String | Company ID |
| planId | String | Plan ID |
| roomTypeId | String | Room Type ID |
| settlementTypeId | String | Settlement Type ID |
| arrivalModeId | String | Arrival Mode ID |
| arrivalDetails | String | Arrival Details |
| nationalityId | String | Nationality ID |
| refModeId | String | Reference Mode ID |
| resvSourceId | String | Reservation Source ID |