# Complete API Documentation for Hotel Management System

## Overview
This is a comprehensive REST API for hotel management operations with JWT authentication and role-based access control.

**Base URL:** `http://localhost:8080/api`  
**Version:** 2.0.0  
**Authentication:** JWT Bearer Token

---

## Authentication

### JWT Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "userName": "admin",
  "password": "admin123"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "userId": "USR001",
    "userName": "admin",
    "userTypeId": "UTYPE001",
    "userTypeRole": "Administrator",
    "userTypeName": "Administrator",
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  },
  "timestamp": "2025-09-15T12:00:00"
}
```

### JWT Logout
```http
POST /api/auth/logout
Authorization: Bearer <token>
```

**Response:**
```json
{
  "success": true,
  "message": "Logout successful",
  "data": null,
  "timestamp": "2025-09-15T12:30:00"
}
```

### Using Authentication
Include the token in the Authorization header for all protected endpoints:
```http
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

---

## Role-Based Access Control

### User Roles
| Role | Code | Access Level | Key Functions |
|------|------|-------------|---------------|
| **Admin** | ADMIN | Full System | All operations, user management |
| **Manager** | MANAGER | Supervisory | View all, approve operations, staff management |
| **Cashier** | CASHIER | Financial | Payments, bills, reports, settlements |
| **Receptionist** | RECEPTIONIST | Front Desk | Reservations, check-ins, guest services |
| **Housekeeping** | HOUSEKEEPING | Room Ops | Room status, cleaning tasks |

### Permission Levels
- **FULL**: Complete CRUD access
- **WRITE**: Create, read, update
- **READ**: View-only access
- **NONE**: No access

---

## Complete API Endpoints

### 🏨 Core Hotel Operations

#### Reservations (`/api/reservations`)

**Create Reservation**
```http
POST /api/reservations
Authorization: Bearer <token>
Content-Type: application/json

{
  "guestName": "John Doe",
  "companyId": "COMP001",
  "roomTypeId": "DELUXE",
  "arrivalDate": "2025-09-15",
  "departureDate": "2025-09-20",
  "noOfDays": 5,
  "noOfPersons": 2,
  "noOfRooms": 1,
  "mobileNumber": "+1234567890",
  "emailId": "john.doe@email.com",
  "rate": 5000.00,
  "includingGst": "N"
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
    "companyId": "COMP001",
    "companyName": "ABC Corporation",
    "roomTypeId": "DELUXE",
    "roomTypeName": "Deluxe Room",
    "arrivalDate": "2025-09-15",
    "departureDate": "2025-09-20",
    "noOfDays": 5,
    "noOfPersons": 2,
    "noOfRooms": 1,
    "mobileNumber": "+1234567890",
    "emailId": "john.doe@email.com",
    "rate": 5000.00,
    "includingGst": "N",
    "remarks": null,
    "roomsCheckedIn": 0,
    "createdAt": "2025-09-15T10:30:00",
    "updatedAt": "2025-09-15T10:30:00"
  },
  "timestamp": "2025-09-15T10:30:00"
}
```

**Get Reservation by Number**
```http
GET /api/reservations/{reservationNo}
Authorization: Bearer <token>
```

**Update Reservation**
```http
PUT /api/reservations/{reservationNo}
Authorization: Bearer <token>
Content-Type: application/json

{
  "guestName": "John Doe Updated",
  "companyId": "COMP001",
  "roomTypeId": "DELUXE",
  "arrivalDate": "2025-09-15",
  "departureDate": "2025-09-22",
  "noOfDays": 7,
  "noOfPersons": 2,
  "noOfRooms": 1,
  "mobileNumber": "+1234567890",
  "emailId": "john.doe.updated@email.com",
  "rate": 5000.00,
  "includingGst": "N"
}
```

**Search Reservations**
```http
GET /api/reservations/search?searchTerm=John
Authorization: Bearer <token>
```

**Get Expected Arrivals**
```http
GET /api/reservations/arrivals/{date}
Authorization: Bearer <token>
```

**Get Expected Departures**
```http
GET /api/reservations/departures/{date}
Authorization: Bearer <token>
```

**Get Pending Check-ins**
```http
GET /api/reservations/pending-checkins
Authorization: Bearer <token>
```

**Check Room Availability**
```http
GET /api/reservations/check-availability?roomTypeId=DELUXE&arrivalDate=2025-09-20&departureDate=2025-09-25&requiredRooms=2
Authorization: Bearer <token>
```

**Get Available Rooms**
```http
GET /api/reservations/available-rooms?roomTypeId=DELUXE&arrivalDate=2025-09-20&departureDate=2025-09-25
Authorization: Bearer <token>
```

**Update Rooms Checked-in Count**
```http
PUT /api/reservations/{reservationNo}/rooms-checked-in
Authorization: Bearer <token>
Content-Type: application/json

{
  "roomsCheckedIn": 2
}
```

**Response:**
```json
{
  "success": true,
  "message": "Rooms checked-in count updated successfully",
  "data": {
    "reservationNo": "1/25-26",
    "guestName": "John Doe",
    "companyId": "COMP001",
    "companyName": "ABC Corporation",
    "roomTypeId": "DELUXE",
    "roomTypeName": "Deluxe Room",
    "arrivalDate": "2025-09-15",
    "departureDate": "2025-09-20",
    "noOfDays": 5,
    "noOfPersons": 2,
    "noOfRooms": 3,
    "mobileNumber": "+1234567890",
    "emailId": "john.doe@email.com",
    "rate": 5000.00,
    "includingGst": "N",
    "remarks": null,
    "roomsCheckedIn": 2,
    "createdAt": "2025-09-15T10:30:00",
    "updatedAt": "2025-09-15T12:00:00"
  },
  "timestamp": "2025-09-15T12:00:00"
}
```

**Delete Reservation**
```http
DELETE /api/reservations/{reservationNo}
Authorization: Bearer <token>
```

#### Check-ins (`/api/checkins`)

**Process Check-in**
```http
POST /api/checkins
Authorization: Bearer <token>
Content-Type: application/json

{
  "reservationNo": "1/25-26",
  "guestName": "John Doe",
  "roomId": "R101",
  "arrivalDate": "2025-09-15",
  "departureDate": "2025-09-20",
  "rate": 5000.00,
  "walkIn": "N"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Check-in processed successfully",
  "data": {
    "folioNo": "F001/25-26",
    "reservationNo": "1/25-26",
    "guestName": "John Doe",
    "roomId": "R101",
    "roomNo": "101",
    "arrivalDate": "2025-09-15",
    "departureDate": "2025-09-20",
    "mobileNumber": "+1234567890",
    "emailId": "john.doe@email.com",
    "rate": 5000.00,
    "walkIn": "N",
    "remarks": null,
    "checkedInAt": "2025-09-15T11:00:00"
  },
  "timestamp": "2025-09-15T11:00:00"
}
```

**Get Check-in Details**
```http
GET /api/checkins/{folioNo}
Authorization: Bearer <token>
```

**Update Check-in**
```http
PUT /api/checkins/{folioNo}
Authorization: Bearer <token>
Content-Type: application/json

{
  "guestName": "John Doe Updated",
  "roomId": "R101",
  "arrivalDate": "2025-09-15",
  "departureDate": "2025-09-22",
  "mobileNumber": "+1234567890",
  "emailId": "john.doe.updated@email.com",
  "rate": 5500.00,
  "remarks": "Extended stay"
}
```

**Get Check-in by Room**
```http
GET /api/checkins/room/{roomId}
Authorization: Bearer <token>
```

**Get In-house Guests**
```http
GET /api/checkins/inhouse
Authorization: Bearer <token>
```

**Get Expected Checkouts**
```http
GET /api/checkins/checkouts/{date}
Authorization: Bearer <token>
```

**Search Check-ins**
```http
GET /api/checkins/search?searchTerm=John
Authorization: Bearer <token>
```

#### Rooms (`/api/rooms`)

**Create Room**
```http
POST /api/rooms
Authorization: Bearer <token>
Content-Type: application/json

{
  "roomNo": "301",
  "floor": "3",
  "status": "VR",
  "roomTypeId": "TYPE0123"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Room created successfully",
  "data": {
    "roomId": "ROOM0456",
    "roomNo": "301",
    "floor": "3",
    "status": "VR",
    "roomTypeId": "DELUXE",
    "roomTypeName": "Deluxe Room",
    "guestName": null,
    "folioNo": null
  },
  "timestamp": "2025-09-15T11:30:00"
}
```

**Get All Rooms with Status**
```http
GET /api/rooms
Authorization: Bearer <token>
```

**Get Room by ID**
```http
GET /api/rooms/{roomId}
Authorization: Bearer <token>
```

**Response:**
```json
{
  "success": true,
  "message": "Operation successful",
  "data": {
    "roomId": "ROOM0456",
    "roomNo": "301",
    "floor": "3",
    "status": "VR",
    "roomTypeId": "DELUXE",
    "roomTypeName": "Deluxe Room",
    "guestName": null,
    "folioNo": null
  },
  "timestamp": "2025-09-15T11:30:00"
}
```

**Get Rooms by Status**
```http
GET /api/rooms/status/{status}
Authorization: Bearer <token>
```

**Get Available Rooms**
```http
GET /api/rooms/available
Authorization: Bearer <token>
```

**Get Occupied Rooms**
```http
GET /api/rooms/occupied
Authorization: Bearer <token>
```

**Get Rooms by Floor**
```http
GET /api/rooms/floor/{floor}
Authorization: Bearer <token>
```

**Get Occupancy Statistics**
```http
GET /api/rooms/occupancy-stats
Authorization: Bearer <token>
```

**Update Room Status**
```http
PUT /api/rooms/{roomId}/status/{status}
Authorization: Bearer <token>
```

**Update Room Details**
```http
PUT /api/rooms/{roomId}
Authorization: Bearer <token>
Content-Type: application/json

{
  "roomNo": "301A",
  "floor": "3",
  "roomTypeId": "DELUXE"
}
```

**Process Room Checkout**
```http
POST /api/rooms/{roomId}/checkout?folioNo=F0001-25-26
Authorization: Bearer <token>
```

**Check Room Availability**
```http
GET /api/rooms/{roomId}/availability?arrivalDate=2025-09-20&departureDate=2025-09-25
Authorization: Bearer <token>
```

**Trigger Automatic Update**
```http
POST /api/rooms/status/automatic-update
Authorization: Bearer <token>
```

**Check Overdue Checkouts**
```http
POST /api/rooms/status/overdue-check
Authorization: Bearer <token>
```

#### Room Types (`/api/room-types`)

**Create Room Type**
```http
POST /api/room-types
Authorization: Bearer <token>
Content-Type: application/json

{
  "typeName": "Deluxe Room",
  "noOfRooms": 15
}
```

**Response:**
```json
{
  "success": true,
  "message": "Room type created successfully",
  "data": {
    "typeId": "TYPE0123",
    "typeName": "Deluxe Room",
    "noOfRooms": 15
  },
  "timestamp": "2025-09-15T11:30:00"
}
```

**Get All Room Types**
```http
GET /api/room-types
Authorization: Bearer <token>
```

**Get Room Type by ID**
```http
GET /api/room-types/{typeId}
Authorization: Bearer <token>
```

**Update Room Type**
```http
PUT /api/room-types/{typeId}
Authorization: Bearer <token>
Content-Type: application/json

{
  "typeName": "Premium Deluxe Room",
  "noOfRooms": 20
}
```

**Delete Room Type**
```http
DELETE /api/room-types/{typeId}
Authorization: Bearer <token>
```

### 💰 Financial Management

#### Advance Payments (`/api/advances`)

**Create Advance for Reservation**
```http
POST /api/advances/reservation
Authorization: Bearer <token>
Content-Type: application/json

{
  "reservationNo": "1/24-25",
  "guestName": "John Doe",
  "modeOfPaymentId": "CASH",
  "amount": 2000.00,
  "remarks": "Advance payment for booking"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Advance created successfully",
  "data": {
    "receiptNo": "R001/24-25",
    "reservationNo": "1/24-25",
    "folioNo": null,
    "billNo": null,
    "guestName": "John Doe",
    "date": "2025-09-15",
    "arrivalDate": "2025-09-15",
    "auditDate": "2025-09-15",
    "modeOfPaymentId": "CASH",
    "modeOfPaymentName": "Cash Payment",
    "amount": 2000.00,
    "narration": "Advance payment for booking",
    "remarks": "Advance payment for booking"
  },
  "timestamp": "2025-09-15T12:00:00"
}
```

**Create Advance for In-house**
```http
POST /api/advances/inhouse
Authorization: Bearer <token>
Content-Type: application/json

{
  "folioNo": "F0001/24-25",
  "guestName": "John Doe",
  "modeOfPaymentId": "CARD",
  "amount": 1000.00,
  "remarks": "Additional advance"
}
```

**Create Advance for Checkout**
```http
POST /api/advances/checkout
Authorization: Bearer <token>
Content-Type: application/json

{
  "billNo": "B0001/24-25",
  "guestName": "John Doe",
  "modeOfPaymentId": "UPI",
  "amount": 1500.00,
  "remarks": "Checkout advance"
}
```

**Create Advance Payment by Bill or Folio**
```http
POST /api/advances/pay
Authorization: Bearer <token>
Content-Type: application/json

// For bill payment:
{
  "billNo": "B0001/24-25",
  "guestName": "John Doe",
  "modeOfPaymentId": "CASH",
  "amount": 2000.00,
  "narration": "Payment against bill"
}

// For folio payment:
{
  "folioNo": "F0001/24-25",
  "guestName": "John Doe",
  "modeOfPaymentId": "CARD",
  "amount": 1000.00,
  "narration": "Payment against folio"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Advance payment created successfully",
  "data": {
    "receiptNo": "R001/24-25",
    "reservationNo": null,
    "folioNo": "F0001/24-25",
    "billNo": null,
    "guestName": "John Doe",
    "date": "2025-09-15",
    "arrivalDate": "2025-09-15",
    "auditDate": "2025-09-15",
    "modeOfPaymentId": "CARD",
    "modeOfPaymentName": "Credit Card Payment",
    "amount": 1000.00,
    "narration": "Payment against folio",
    "remarks": "Payment against folio"
  },
  "timestamp": "2025-09-15T12:00:00"
}
```

**Get Advances by Reservation**
```http
GET /api/advances/reservation/{reservationNo}
Authorization: Bearer <token>
```

**Get Advances by Folio**
```http
GET /api/advances/folio/{folioNo}
Authorization: Bearer <token>
```

**Get Advances by Bill**
```http
GET /api/advances/bill/{billNo}
Authorization: Bearer <token>
```

**Get Total Advances by Reservation**
```http
GET /api/advances/reservation/{reservationNo}/total
Authorization: Bearer <token>
```

**Response:**
```json
{
  "success": true,
  "message": "Operation successful",
  "data": 2500.00,
  "timestamp": "2025-09-15T12:00:00"
}
```

**Get Total Advances by Folio**
```http
GET /api/advances/folio/{folioNo}/total
Authorization: Bearer <token>
```

**Response:**
```json
{
  "success": true,
  "message": "Operation successful",
  "data": 1500.00,
  "timestamp": "2025-09-15T12:00:00"
}
```

**Get Total Advances by Bill**
```http
GET /api/advances/bill/{billNo}/total
Authorization: Bearer <token>
```

**Response:**
```json
{
  "success": true,
  "message": "Operation successful",
  "data": 3000.00,
  "timestamp": "2025-09-15T12:00:00"
}
```

**Get Advance Summary by Reservation**
```http
GET /api/advances/reservation/{reservationNo}/summary
Authorization: Bearer <token>
```

**Response:**
```json
{
  "success": true,
  "message": "Operation successful",
  "data": {
    "referenceNumber": "1/24-25",
    "guestName": "John Doe",
    "arrivalDate": "2025-09-15",
    "departureDate": "2025-09-20",
    "roomNo": "101",
    "totalAmount": 2500.00,
    "totalCount": 2,
    "advanceDetails": [
      {
        "receiptNo": "R001/24-25",
        "date": "2025-09-10",
        "modeOfPaymentName": "Cash Payment",
        "amount": 2000.00,
        "narration": "Advance payment for booking"
      },
      {
        "receiptNo": "R002/24-25",
        "date": "2025-09-12",
        "modeOfPaymentName": "Credit Card",
        "amount": 500.00,
        "narration": "Additional advance"
      }
    ]
  },
  "timestamp": "2025-09-15T12:00:00"
}
```

**Get Advance Summary by Bill**
```http
GET /api/advances/bill/{billNo}/summary
Authorization: Bearer <token>
```

**Response:**
```json
{
  "success": true,
  "message": "Operation successful",
  "data": {
    "referenceNumber": "B001/24-25",
    "guestName": "John Doe",
    "arrivalDate": "2025-09-15",
    "departureDate": "2025-09-20",
    "roomNo": "101",
    "totalAmount": 3000.00,
    "totalCount": 3,
    "advanceDetails": [
      {
        "receiptNo": "R001/24-25",
        "date": "2025-09-10",
        "modeOfPaymentName": "Cash Payment",
        "amount": 1000.00,
        "narration": "Advance payment for booking"
      },
      {
        "receiptNo": "R002/24-25",
        "date": "2025-09-12",
        "modeOfPaymentName": "Credit Card",
        "amount": 1000.00,
        "narration": "Additional advance"
      },
      {
        "receiptNo": "R003/24-25",
        "date": "2025-09-14",
        "modeOfPaymentName": "Online Payment",
        "amount": 1000.00,
        "narration": "Final advance"
      }
    ]
  },
  "timestamp": "2025-09-15T12:00:00"
}
```

**Get Guest Name by Bill**
```http
GET /api/advances/bill/{billNo}/guest-name
Authorization: Bearer <token>
```

**Response:**
```json
{
  "success": true,
  "message": "Operation successful",
  "data": "John Doe",
  "timestamp": "2025-09-15T12:00:00"
}
```

**Get Guest Name by Reservation**
```http
GET /api/advances/reservation/{reservationNo}/guest-name
Authorization: Bearer <token>
```

**Response:**
```json
{
  "success": true,
  "message": "Operation successful",
  "data": "John Doe",
  "timestamp": "2025-09-15T12:00:00"
}
```

**Get Advance by ID**
```http
GET /api/advances/{advanceId}
Authorization: Bearer <token>
```

**Update Advance**
```http
PUT /api/advances/{advanceId}
Authorization: Bearer <token>
Content-Type: application/json

{
  "guestName": "John Doe Updated",
  "amount": 2500.00,
  "modeOfPaymentId": "CARD",
  "remarks": "Updated advance payment"
}
```

**Delete Advance**
```http
DELETE /api/advances/{advanceId}
Authorization: Bearer <token>
```

### New Reservation-Specific Advance Endpoints

**Create Advance for Reservation with Bill**
```http
POST /api/advances/reservation/{reservationNo}/bill/{billNo}
Authorization: Bearer <token>
Content-Type: application/json

{
  "guestName": "John Doe",
  "modeOfPaymentId": "CASH",
  "amount": 2000.00,
  "narration": "Advance payment for reservation with bill"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Advance created successfully for reservation with bill",
  "data": {
    "receiptNo": "R001/24-25",
    "reservationNo": "1/24-25",
    "folioNo": null,
    "billNo": "B0001/24-25",
    "guestName": "John Doe",
    "date": "2025-09-15",
    "arrivalDate": "2025-09-15",
    "auditDate": "2025-09-15",
    "modeOfPaymentId": "CASH",
    "modeOfPaymentName": "Cash Payment",
    "amount": 2000.00,
    "narration": "Advance payment for reservation with bill",
    "remarks": "Advance payment for reservation with bill"
  },
  "timestamp": "2025-09-15T12:00:00"
}
```

**Create Advance for Reservation with Folio**
```http
POST /api/advances/reservation/{reservationNo}/folio/{folioNo}
Authorization: Bearer <token>
Content-Type: application/json

{
  "guestName": "John Doe",
  "modeOfPaymentId": "CARD",
  "amount": 1500.00,
  "narration": "Advance payment for reservation with folio"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Advance created successfully for reservation with folio",
  "data": {
    "receiptNo": "R002/24-25",
    "reservationNo": "1/24-25",
    "folioNo": "F0001/24-25",
    "billNo": null,
    "guestName": "John Doe",
    "date": "2025-09-15",
    "arrivalDate": "2025-09-15",
    "auditDate": "2025-09-15",
    "modeOfPaymentId": "CARD",
    "modeOfPaymentName": "Credit Card Payment",
    "amount": 1500.00,
    "narration": "Advance payment for reservation with folio",
    "remarks": "Advance payment for reservation with folio"
  },
  "timestamp": "2025-09-15T12:00:00"
}
```

#### Post Transactions (`/api/transactions`)

**Create In-house Transaction**
```http
POST /api/transactions/inhouse
Authorization: Bearer <token>
Content-Type: application/json

{
  "folioNo": "F0001/24-25",
  "guestName": "John Doe",
  "accHeadId": "RESTAURANT",
  "amount": 850.00,
  "narration": "Restaurant charges"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Transaction created successfully",
  "data": {
    "transactionId": "TXN001/24-25",
    "folioNo": "F0001/24-25",
    "billNo": null,
    "roomId": "ROOM001",
    "roomNo": "101",
    "guestName": "John Doe",
    "date": "2025-09-15",
    "auditDate": "2025-09-15",
    "accHeadId": "RESTAURANT",
    "accHeadName": "Restaurant Services",
    "voucherNo": "V001",
    "amount": 850.00,
    "narration": "Restaurant charges",
    "remarks": "Restaurant charges"
  },
  "timestamp": "2025-09-15T12:30:00"
}
```

**Create Checkout Transaction**
```http
POST /api/transactions/checkout
Authorization: Bearer <token>
Content-Type: application/json

{
  "billNo": "B0001/24-25",
  "guestName": "John Doe",
  "accHeadId": "LAUNDRY",
  "amount": 120.00,
  "narration": "Laundry services"
}
```

**Get Transactions by Folio**
```http
GET /api/transactions/folio/{folioNo}
Authorization: Bearer <token>
```

**Get Transactions by Bill**
```http
GET /api/transactions/bill/{billNo}
Authorization: Bearer <token>
```

**Get Transactions by Room**
```http
GET /api/transactions/room/{roomId}
Authorization: Bearer <token>
```

**Get Transactions by Date Range**
```http
GET /api/transactions/date-range?startDate=2025-09-01&endDate=2025-09-30
Authorization: Bearer <token>
```

**Get Transaction by ID**
```http
GET /api/transactions/{transactionId}
Authorization: Bearer <token>
```

**Update Transaction**
```http
PUT /api/transactions/{transactionId}
Authorization: Bearer <token>
Content-Type: application/json

{
  "guestName": "John Doe Updated",
  "accHeadId": "ROOM_SERVICE",
  "amount": 900.00,
  "narration": "Updated room service charges"
}
```

**Delete Transaction**
```http
DELETE /api/transactions/{transactionId}
Authorization: Bearer <token>
```

#### Bills (`/api/bills`)

**Generate Bill**
```http
POST /api/bills/generate/{folioNo}
Authorization: Bearer <token>
```

**Response:**
```json
{
  "success": true,
  "message": "Bill generated successfully",
  "data": {
    "billNo": "B8126-25-26",
    "folioNo": "F2150-25-26",
    "guestName": "John Doe",
    "roomId": "ROOM3881",
    "roomNo": "101",
    "totalAmount": 2650.00,
    "advanceAmount": 1000.00,
    "balanceAmount": 1650.00,
    "generatedAt": "2025-09-15T14:38:58.132763",
    "transactions": [
      {
        "transactionId": "TXN001-25-26",
        "folioNo": "F2150-25-26",
        "billNo": "B8126-25-26",
        "roomId": "ROOM3881",
        "roomNo": "101",
        "guestName": "John Doe",
        "date": "2025-09-14",
        "auditDate": "2025-09-14",
        "accHeadId": "ACC001",
        "accHeadName": "Room Service",
        "voucherNo": "V001",
        "amount": 1500.00,
        "narration": "Food order"
      },
      {
        "transactionId": "TXN002-25-26",
        "folioNo": "F2150-25-26",
        "billNo": "B8126-25-26",
        "roomId": "ROOM3881",
        "roomNo": "101",
        "guestName": "John Doe",
        "date": "2025-09-14",
        "auditDate": "2025-09-14",
        "accHeadId": "ACC002",
        "accHeadName": "Laundry",
        "voucherNo": "V002",
        "amount": 1150.00,
        "narration": "Dry cleaning"
      }
    ],
    "advances": [
      {
        "receiptNo": "R001-25-26",
        "folioNo": "F2150-25-26",
        "billNo": "B8126-25-26",
        "guestName": "John Doe",
        "date": "2025-09-13",
        "arrivalDate": "2025-09-13",
        "auditDate": "2025-09-13",
        "modeOfPaymentId": "CASH",
        "modeOfPaymentName": "Cash Payment",
        "amount": 1000.00,
        "narration": "Advance payment"
      }
    ]
  },
  "timestamp": "2025-09-15T14:41:50.146944400"
}
```

**Get Bill by Number**
```http
GET /api/bills/{billNo}
Authorization: Bearer <token>
```

**Update Bill**
```http
PUT /api/bills/{billNo}
Authorization: Bearer <token>
Content-Type: application/json

{
  "guestName": "John Doe Updated",
  "totalAmount": 6500.00,
  "advanceAmount": 2000.00,
  "paymentNotes": "Updated bill amount"
}
```

**Get Bill by Folio**
```http
GET /api/bills/folio/{folioNo}
Authorization: Bearer <token>
```

**Search Bills**
```http
GET /api/bills/search?searchTerm=John
Authorization: Bearer <token>
```

**Get Split Bill Preview**
```http
GET /api/bills/{billNo}/split/preview
Authorization: Bearer <token>
```

**Execute Split Bill**
```http
POST /api/bills/{billNo}/split
Authorization: Bearer <token>
Content-Type: application/json

{
  "selectedTransactions": [
    {
      "transactionId": "TXN002/24-25",
      "selectedForNewBill": true
    },
    {
      "transactionId": "TXN003/24-25", 
      "selectedForNewBill": true
    }
  ],
  "newBillNarration": "Split bill for restaurant and laundry charges"
}
```

**Get Related Bills**
```http
GET /api/bills/{billNo}/related
Authorization: Bearer <token>
```

**Process Payment**
```http
POST /api/bills/{billNo}/payment
Authorization: Bearer <token>
Content-Type: application/json

{
  "paymentAmount": 2000.00,
  "modeOfPaymentId": "CASH",
  "paymentNotes": "Partial payment - guest will return for balance"
}
```

**Get Bill Settlement Status**
```http
GET /api/bills/{billNo}/settlement-status
Authorization: Bearer <token>
```

**Get Pending Bill Settlements**
```http
GET /api/bills/pending-settlements
Authorization: Bearer <token>
```

### 👥 User & Access Management

#### User Management (`/api/users`)

**User Login (Legacy)**
```http
POST /api/users/login
Content-Type: application/json

{
  "userName": "admin",
  "password": "admin123"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "userId": "USER001",
    "userName": "admin",
    "userTypeId": "ADMIN",
    "userTypeName": "Administrator",
    "loginSuccess": true,
    "permissions": [
      {
        "moduleName": "RESERVATIONS",
        "permissionType": "write"
      },
      {
        "moduleName": "CHECKINS",
        "permissionType": "write"
      }
    ]
  },
  "timestamp": "2025-09-15T11:30:00"
}
```

**User Logout (Legacy)**
```http
POST /api/users/logout
Authorization: Bearer <token>
```

**Create User**
```http
POST /api/users
Authorization: Bearer <token>
Content-Type: application/json

{
  "userName": "frontdesk",
  "password": "frontdesk123",
  "userTypeId": "STAFF",
  "userTypeRole": "Front Desk Staff",
  "permissions": [
    {
      "moduleName": "RESERVATIONS",
      "permissionType": "write"
    },
    {
      "moduleName": "ADVANCES",
      "permissionType": "read"
    }
  ]
}
```

**Response:**
```json
{
  "success": true,
  "message": "User created successfully",
  "data": {
    "userId": "USR002",
    "userName": "frontdesk",
    "userTypeId": "STAFF",
    "userTypeRole": "Front Desk Staff",
    "userTypeName": "Staff"
  },
  "timestamp": "2025-09-15T12:00:00"
}
```

**Get All Users**
```http
GET /api/users
Authorization: Bearer <token>
```

**Get User by ID**
```http
GET /api/users/{userId}
Authorization: Bearer <token>
```

**Update User Profile**
```http
PUT /api/users/{userId}
Authorization: Bearer <token>
Content-Type: application/json

{
  "userName": "frontdesk_updated",
  "userTypeId": "STAFF",
  "userTypeRole": "Front Desk Staff"
}
```

**Delete User**
```http
DELETE /api/users/{userId}
Authorization: Bearer <token>
```

**Update User Rights**
```http
PUT /api/users/{userId}/rights
Authorization: Bearer <token>
Content-Type: application/json

{
  "permissions": [
    {
      "moduleName": "RESERVATIONS",
      "permissionType": "full"
    },
    {
      "moduleName": "CHECKINS",
      "permissionType": "full"
    },
    {
      "moduleName": "ADVANCES",
      "permissionType": "full"
    }
  ]
}
```

**Get Available Roles**
```http
GET /api/users/roles
Authorization: Bearer <token>
```

**Create User with Role**
```http
POST /api/users/create-with-role
Authorization: Bearer <token>
Content-Type: application/x-www-form-urlencoded

userName=jane.cashier&password=password123&role=CASHIER
```

**Update User Role**
```http
PUT /api/users/{userId}/role
Authorization: Bearer <token>
Content-Type: application/x-www-form-urlencoded

role=CASHIER
```

**Get Users by Role**
```http
GET /api/users/by-role/{role}
Authorization: Bearer <token>
```

**Check User Permission**
```http
GET /api/users/{userId}/permissions/{module}?permissionType=full
Authorization: Bearer <token>
```

#### Admin Operations (`/api/admin`)

**Get Admin Dashboard**
```http
GET /api/admin/dashboard
Authorization: Bearer <token>
```

**Response:**
```json
{
  "success": true,
  "message": "Dashboard data retrieved",
  "data": {
    "totalUsers": 15,
    "usersByRole": {
      "Admin": 1,
      "Manager": 2,
      "Cashier": 3,
      "Receptionist": 5,
      "Housekeeping Staff": 4
    },
    "availableRoles": [
      {
        "roleCode": "ADMIN",
        "displayName": "Admin",
        "description": "System administrator with full access to all modules and configurations"
      },
      {
        "roleCode": "MANAGER",
        "displayName": "Manager",
        "description": "Manager with full oversight access, approval capabilities, and staff management"
      }
    ]
  },
  "timestamp": "2025-09-15T13:00:00"
}
```

**Bulk Create Users**
```http
POST /api/admin/users/bulk-create
Authorization: Bearer <token>
Content-Type: application/json

[
  {
    "userName": "reception1",
    "password": "reception123",
    "role": "RECEPTIONIST"
  },
  {
    "userName": "reception2",
    "password": "reception123",
    "role": "RECEPTIONIST"
  }
]
```

**Get System Status**
```http
GET /api/admin/system-status
Authorization: Bearer <token>
```

#### User Types (`/api/user-types`)

**Create User Type**
```http
POST /api/user-types
Authorization: Bearer <token>
Content-Type: application/json

{
  "typeName": "Custom Role"
}
```

**Response:**
```json
{
  "success": true,
  "message": "User type created successfully",
  "data": {
    "userTypeId": "UTYPE0001",
    "typeName": "Custom Role"
  },
  "timestamp": "2025-09-15T11:30:00"
}
```

**Get All User Types**
```http
GET /api/user-types
Authorization: Bearer <token>
```

**Get User Type by ID**
```http
GET /api/user-types/{userTypeId}
Authorization: Bearer <token>
```

**Update User Type**
```http
PUT /api/user-types/{userTypeId}
Authorization: Bearer <token>
Content-Type: application/json

{
  "typeName": "Updated Custom Role"
}
```

**Delete User Type**
```http
DELETE /api/user-types/{userTypeId}
Authorization: Bearer <token>
```

### 🟢 Operations Management

#### Hotel Operations (`/api/operations`)

**Process Audit Date Change**
```http
POST /api/operations/audit-date-change
Authorization: Bearer <token>
Content-Type: application/json

{
  "confirmation": "YES"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Audit date change processed successfully. Room charges and taxes posted for 5 in-house guests.",
  "data": null,
  "timestamp": "2025-09-15T11:30:00"
}
```

**Process Shift Change**
```http
POST /api/operations/shift-change
Authorization: Bearer <token>
Content-Type: application/json

{
  "shiftDate": "2025-09-15",
  "shiftNo": "1",
  "balance": 15000.00
}
```

#### Housekeeping (`/api/housekeeping`)

Housekeeping Management endpoints for creating, updating, and deleting housekeeping tasks with full support for all required fields.

**Housekeeping Task Fields:**
- **taskId**: Unique identifier for the housekeeping task
- **roomId**: Room identifier that the task is associated with
- **status**: Current status of the housekeeping task (VR, OD, OI, Blocked)
- **assignedTo**: Staff member assigned to the task
- **notes**: Additional notes or instructions for the task
- **createdAt**: Timestamp when the task was created
- **updatedAt**: Timestamp when the task was last updated

**Create Housekeeping Task (Add):**
```http
POST /api/housekeeping/tasks
Authorization: Bearer <token>
Content-Type: application/json

{
  "roomId": "ROOM001",
  "status": "VR",
  "notes": "Deep cleaning required",
  "assignedTo": "HK001"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Housekeeping task created successfully",
  "data": {
    "taskId": 1,
    "roomId": "ROOM001",
    "status": "VR",
    "assignedTo": "HK001",
    "notes": "Deep cleaning required",
    "createdAt": "2025-09-15T11:38:33.832037900",
    "updatedAt": "2025-09-15T11:38:33.832037900"
  },
  "timestamp": "2025-09-15T11:38:33.832037900"
}
```

**Update Housekeeping Task (Edit):**
```http
PUT /api/housekeeping/tasks/1
Authorization: Bearer <token>
Content-Type: application/json

{
  "roomId": "ROOM001",
  "status": "OI",
  "notes": "Cleaning completed",
  "assignedTo": "HK001"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Housekeeping task updated successfully",
  "data": {
    "taskId": 1,
    "roomId": "ROOM001",
    "status": "OI",
    "assignedTo": "HK001",
    "notes": "Cleaning completed",
    "createdAt": "2025-09-15T11:38:33.832037900",
    "updatedAt": "2025-09-15T12:00:00.000000000"
  },
  "timestamp": "2025-09-15T12:00:00.000000000"
}
```

**Delete Housekeeping Task:**
```http
DELETE /api/housekeeping/tasks/1
Authorization: Bearer <token>
```

**Response:**
```json
{
  "success": true,
  "message": "Housekeeping task deleted successfully",
  "data": null,
  "timestamp": "2025-09-15T12:00:00.000000000"
}
```

**Get Housekeeping Task by ID (Show):**
```http
GET /api/housekeeping/tasks/1
Authorization: Bearer <token>
```

**Response:**
```json
{
  "success": true,
  "message": "Operation successful",
  "data": {
    "taskId": 1,
    "roomId": "ROOM001",
    "roomNo": "101",
    "floor": "1",
    "status": "OI",
    "assignedTo": "HK001",
    "notes": "Cleaning completed",
    "createdAt": "2025-09-15T11:38:33.832037900",
    "updatedAt": "2025-09-15T12:00:00.000000000"
  },
  "timestamp": "2025-09-15T12:00:00.000000000"
}
```

**Get All Housekeeping Tasks:**
```http
GET /api/housekeeping/tasks
Authorization: Bearer <token>
```

**Response:**
```json
{
  "success": true,
  "message": "Operation successful",
  "data": [
    {
      "roomId": "ROOM001",
      "roomNo": "101",
      "floor": "1",
      "status": "OI",
      "roomTypeId": "RTYPE001",
      "roomTypeName": "Deluxe Room",
      "guestName": "John Doe",
      "folioNo": "FOLIO001"
    },
    {
      "roomId": "ROOM002",
      "roomNo": "102",
      "floor": "1",
      "status": "VR",
      "roomTypeId": "RTYPE002",
      "roomTypeName": "Standard Room"
    }
  ],
  "timestamp": "2025-09-15T11:38:33.832037900"
}
```

**Update Room Cleaning Status:**
```http
POST /api/housekeeping/room-status
Authorization: Bearer <token>
Content-Type: application/x-www-form-urlencoded

roomId=ROOM001&status=VR
```

**Response:**
```json
{
  "success": true,
  "message": "Room status updated successfully",
  "data": {
    "roomId": "ROOM001",
    "roomNo": "101",
    "floor": "1",
    "status": "VR",
    "roomTypeId": "RTYPE001",
    "roomTypeName": "Deluxe Room"
  },
  "timestamp": "2025-09-15T11:38:33.832037900"
}
```

**Get Rooms by Cleaning Status:**
```http
GET /api/housekeeping/rooms-by-status?status=VR
Authorization: Bearer <token>
```

**Response:**
```json
{
  "success": true,
  "message": "Operation successful",
  "data": [
    {
      "roomId": "ROOM002",
      "roomNo": "102",
      "floor": "1",
      "status": "VR",
      "roomTypeId": "RTYPE002",
      "roomTypeName": "Standard Room"
    },
    {
      "roomId": "ROOM003",
      "roomNo": "103",
      "floor": "1",
      "status": "VR",
      "roomTypeId": "RTYPE001",
      "roomTypeName": "Deluxe Room"
    }
  ],
  "timestamp": "2025-09-15T11:38:33.832037900"
}
```

**Get Housekeeping Tasks by Room:**
```http
GET /api/housekeeping/tasks/room/ROOM001
Authorization: Bearer <token>
```

**Response:**
```json
{
  "success": true,
  "message": "Operation successful",
  "data": [
    {
      "taskId": 1,
      "roomId": "ROOM001",
      "roomNo": "101",
      "floor": "1",
      "status": "OI",
      "assignedTo": "HK001",
      "notes": "Cleaning completed",
      "createdAt": "2025-09-15T11:38:33.832037900",
      "updatedAt": "2025-09-15T12:00:00.000000000"
    }
  ],
  "timestamp": "2025-09-15T12:00:00.000000000"
}
```

**Get Housekeeping Tasks by Status:**
```http
GET /api/housekeeping/tasks/status/VR
Authorization: Bearer <token>
```

**Response:**
```json
{
  "success": true,
  "message": "Operation successful",
  "data": [
    {
      "taskId": 2,
      "roomId": "ROOM002",
      "roomNo": "102",
      "floor": "1",
      "status": "VR",
      "assignedTo": "HK002",
      "notes": "Ready for guests",
      "createdAt": "2025-09-15T11:38:33.832037900",
      "updatedAt": "2025-09-15T11:38:33.832037900"
    }
  ],
  "timestamp": "2025-09-15T11:38:33.832037900"
}
```

**Get Housekeeping Statistics:**
```http
GET /api/housekeeping/statistics
Authorization: Bearer <token>
```

**Response:**
```json
{
  "success": true,
  "message": "Operation successful",
  "data": {
    "totalRooms": 50,
    "occupiedRooms": 25,
    "availableRooms": 20,
    "blockedRooms": 3,
    "outOfOrderRooms": 2,
    "occupancyPercentage": 50.0
  },
  "timestamp": "2025-09-15T11:38:33.832037900"
}
```

**Supported Room Statuses:**
- **VR** - Vacant Ready (Clean and ready for guests)
- **OD** - Occupied Dirty (Currently occupied by guests)
- **OI** - Occupied In-house (Occupied, but may need cleaning during stay)
- **Blocked** - Blocked/Out of Order (Not available for booking)

### 🏢 Master Data Management

#### Companies (`/api/companies`)

**Create Company**
```http
POST /api/companies
Authorization: Bearer <token>
Content-Type: application/json

{
  "companyName": "Grand Hotels Ltd",
  "address1": "123 Business District",
  "address2": "Downtown Area",
  "address3": "Metro City",
  "gstNumber": "22ABCDE1234F1Z5"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Company created successfully",
  "data": {
    "companyId": "COMP0123",
    "companyName": "Grand Hotels Ltd",
    "address1": "123 Business District",
    "address2": "Downtown Area",
    "address3": "Metro City",
    "gstNumber": "22ABCDE1234F1Z5"
  },
  "timestamp": "2025-09-15T11:30:00"
}
```

**Get All Companies**
```http
GET /api/companies
Authorization: Bearer <token>
```

**Get Company by ID**
```http
GET /api/companies/{companyId}
Authorization: Bearer <token>
```

**Update Company**
```http
PUT /api/companies/{companyId}
Authorization: Bearer <token>
Content-Type: application/json

{
  "companyName": "Grand Hotels Ltd Updated",
  "address1": "456 New Business District",
  "address2": "Uptown Area",
  "address3": "Metro City",
  "gstNumber": "22ABCDE1234F1Z5"
}
```

**Delete Company**
```http
DELETE /api/companies/{companyId}
Authorization: Bearer <token>
```

#### Payment Modes (`/api/payment-modes`)

**Create Payment Mode**
```http
POST /api/payment-modes
Authorization: Bearer <token>
Content-Type: application/json

{
  "id": "CASH",
  "name": "Cash Payment"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Payment mode created successfully",
  "data": {
    "id": "CASH",
    "name": "Cash Payment"
  },
  "timestamp": "2025-09-15T11:30:00"
}
```

**Get All Payment Modes**
```http
GET /api/payment-modes
Authorization: Bearer <token>
```

**Get Payment Mode by ID**
```http
GET /api/payment-modes/{id}
Authorization: Bearer <token>
```

**Update Payment Mode**
```http
PUT /api/payment-modes/{id}
Authorization: Bearer <token>
Content-Type: application/json

{
  "id": "CASH",
  "name": "Cash Payment Updated"
}
```

**Delete Payment Mode**
```http
DELETE /api/payment-modes/{id}
Authorization: Bearer <token>
```

#### Settlement Types (`/api/settlement-types`)

**Create Settlement Type**
```http
POST /api/settlement-types
Authorization: Bearer <token>
Content-Type: application/json

{
  "id": "CASH",
  "name": "Cash Settlement"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Settlement type created successfully",
  "data": {
    "id": "CASH",
    "name": "Cash Settlement"
  },
  "timestamp": "2025-09-15T11:30:00"
}
```

**Get All Settlement Types**
```http
GET /api/settlement-types
Authorization: Bearer <token>
```

**Get Settlement Type by ID**
```http
GET /api/settlement-types/{id}
Authorization: Bearer <token>
```

**Update Settlement Type**
```http
PUT /api/settlement-types/{id}
Authorization: Bearer <token>
Content-Type: application/json

{
  "id": "CASH",
  "name": "Cash Settlement Updated"
}
```

**Delete Settlement Type**
```http
DELETE /api/settlement-types/{id}
Authorization: Bearer <token>
```

#### Plan Types (`/api/plan-types`)

**Create Plan Type**
```http
POST /api/plan-types
Authorization: Bearer <token>
Content-Type: application/json

{
  "planName": "Corporate Plan",
  "discountPercentage": 15.00
}
```

**Response:**
```json
{
  "success": true,
  "message": "Plan type created successfully",
  "data": {
    "planId": "PLAN0123",
    "planName": "Corporate Plan",
    "discountPercentage": 15.00
  },
  "timestamp": "2025-09-15T11:30:00"
}
```

**Get All Plan Types**
```http
GET /api/plan-types
Authorization: Bearer <token>
```

**Get Plan Type by ID**
```http
GET /api/plan-types/{planId}
Authorization: Bearer <token>
```

**Update Plan Type**
```http
PUT /api/plan-types/{planId}
Authorization: Bearer <token>
Content-Type: application/json

{
  "planName": "Corporate Plan Updated",
  "discountPercentage": 20.00
}
```

**Delete Plan Type**
```http
DELETE /api/plan-types/{planId}
Authorization: Bearer <token>
```

#### Tax Management (`/api/taxes`)

**Create Tax**
```http
POST /api/taxes
Authorization: Bearer <token>
Content-Type: application/json

{
  "taxName": "CGST",
  "percentage": 9.0
}
```

**Response:**
```json
{
  "success": true,
  "message": "Tax created successfully",
  "data": {
    "taxId": "TAX0001",
    "taxName": "CGST",
    "percentage": 9.0
  },
  "timestamp": "2025-09-15T11:30:00"
}
```

**Get All Taxes**
```http
GET /api/taxes
Authorization: Bearer <token>
```

**Get Tax by ID**
```http
GET /api/taxes/{taxId}
Authorization: Bearer <token>
```

**Update Tax**
```http
PUT /api/taxes/{taxId}
Authorization: Bearer <token>
Content-Type: application/json

{
  "taxName": "CGST Updated",
  "percentage": 9.5
}
```

**Delete Tax**
```http
DELETE /api/taxes/{taxId}
Authorization: Bearer <token>
```

#### Account Heads (`/api/account-heads`)

**Create Account Head**
```http
POST /api/account-heads
Authorization: Bearer <token>
Content-Type: application/json

{
  "accountName": "Restaurant"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Account head created successfully",
  "data": {
    "accountHeadId": "ACC0001",
    "accountName": "Restaurant"
  },
  "timestamp": "2025-09-15T11:30:00"
}
```

**Get All Account Heads**
```http
GET /api/account-heads
Authorization: Bearer <token>
```

**Get Account Head by ID**
```http
GET /api/account-heads/{accountHeadId}
Authorization: Bearer <token>
```

**Update Account Head**
```http
PUT /api/account-heads/{accountHeadId}
Authorization: Bearer <token>
Content-Type: application/json

{
  "accountName": "Restaurant Services Updated"
}
```

**Delete Account Head**
```http
DELETE /api/account-heads/{accountHeadId}
Authorization: Bearer <token>
```

#### Nationalities (`/api/nationalities`)

**Create Nationality**
```http
POST /api/nationalities
Authorization: Bearer <token>
Content-Type: application/json

{
  "nationality": "Indian"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Nationality created successfully",
  "data": {
    "id": "NAT0001",
    "nationality": "Indian"
  },
  "timestamp": "2025-09-15T11:30:00"
}
```

**Get All Nationalities**
```http
GET /api/nationalities
Authorization: Bearer <token>
```

**Get Nationality by ID**
```http
GET /api/nationalities/{id}
Authorization: Bearer <token>
```

**Update Nationality**
```http
PUT /api/nationalities/{id}
Authorization: Bearer <token>
Content-Type: application/json

{
  "nationality": "Indian Updated"
}
```

**Delete Nationality**
```http
DELETE /api/nationalities/{id}
Authorization: Bearer <token>
```

#### Reference Modes (`/api/ref-modes`)

**Create Reference Mode**
```http
POST /api/ref-modes
Authorization: Bearer <token>
Content-Type: application/json

{
  "refMode": "Online Booking"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Reference mode created successfully",
  "data": {
    "id": "REF0001",
    "refMode": "Online Booking"
  },
  "timestamp": "2025-09-15T11:30:00"
}
```

**Get All Reference Modes**
```http
GET /api/ref-modes
Authorization: Bearer <token>
```

**Get Reference Mode by ID**
```http
GET /api/ref-modes/{id}
Authorization: Bearer <token>
```

**Update Reference Mode**
```http
PUT /api/ref-modes/{id}
Authorization: Bearer <token>
Content-Type: application/json

{
  "refMode": "Online Booking Updated"
}
```

**Delete Reference Mode**
```http
DELETE /api/ref-modes/{id}
Authorization: Bearer <token>
```

#### Arrival Modes (`/api/arrival-modes`)

**Create Arrival Mode**
```http
POST /api/arrival-modes
Authorization: Bearer <token>
Content-Type: application/json

{
  "arrivalMode": "Flight"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Arrival mode created successfully",
  "data": {
    "id": "ARR0001",
    "arrivalMode": "Flight"
  },
  "timestamp": "2025-09-15T11:30:00"
}
```

**Get All Arrival Modes**
```http
GET /api/arrival-modes
Authorization: Bearer <token>
```

**Get Arrival Mode by ID**
```http
GET /api/arrival-modes/{id}
Authorization: Bearer <token>
```

**Update Arrival Mode**
```http
PUT /api/arrival-modes/{id}
Authorization: Bearer <token>
Content-Type: application/json

{
  "arrivalMode": "Flight Updated"
}
```

**Delete Arrival Mode**
```http
DELETE /api/arrival-modes/{id}
Authorization: Bearer <token>
```

#### Reservation Sources (`/api/reservation-sources`)

**Create Reservation Source**
```http
POST /api/reservation-sources
Authorization: Bearer <token>
Content-Type: application/json

{
  "resvSource": "Direct Booking"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Reservation source created successfully",
  "data": {
    "id": "SRC0001",
    "resvSource": "Direct Booking"
  },
  "timestamp": "2025-09-15T11:30:00"
}
```

**Get All Reservation Sources**
```http
GET /api/reservation-sources
Authorization: Bearer <token>
```

**Get Reservation Source by ID**
```http
GET /api/reservation-sources/{id}
Authorization: Bearer <token>
```

**Update Reservation Source**
```http
PUT /api/reservation-sources/{id}
Authorization: Bearer <token>
Content-Type: application/json

{
  "resvSource": "Direct Booking Updated"
}
```

**Delete Reservation Source**
```http
DELETE /api/reservation-sources/{id}
Authorization: Bearer <token>
```

---

## Response Format

### Success Response
```json
{
  "success": true,
  "message": "Operation completed successfully",
  "data": {
    // Response data
  },
  "timestamp": "2025-09-15T12:00:00"
}
```

### Error Response
```json
{
  "success": false,
  "message": "Error description",
  "data": null,
  "timestamp": "2025-09-15T12:00:00"
}
```

## HTTP Status Codes
- `200 OK` - Successful operation
- `201 Created` - Resource created
- `400 Bad Request` - Invalid request
- `401 Unauthorized` - Authentication required
- `403 Forbidden` - Insufficient permissions
- `404 Not Found` - Resource not found
- `500 Internal Server Error` - Server error

## Number Generation Format
- **Reservations:** `1-25-26` (sequence/fiscal-year) - Sequential: 1, 2, 3, etc.
- **Folios:** `F1-25-26` - Sequential: F1, F2, F3, etc.
- **Bills:** `B1-25-26` - Sequential: B1, B2, B3, etc.
- **Advances:** `R1/25-26` - Sequential: R1, R2, R3, etc.
- **Transactions:** `TXN12345` - Timestamp-based unique ID
- **Users:** `USER001` - Padded sequential format

## Room Status Codes
- **VR**: Vacant Ready (Available)
- **OD**: Occupied Dirty (Guest checked in)
- **OI**: Occupied Inspected (Cleaned)
- **Blocked**: Maintenance

---

*API Documentation Version 2.0.0 - Complete Hotel Management System with Advanced Features*