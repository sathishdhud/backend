# API Documentation
# Hotel Management System REST API Documentation

## Overview
Comprehensive REST API for hotel management operations with JWT authentication and role-based access control.

**Base URL:** `http://localhost:8080/api`  
**Version:** 1.0.0  
**Authentication:** JWT Bearer Token

---

## Quick Start

### 1. User Login (JWT Authentication - Recommended)
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
  }
}
```

### 2. User Login (Legacy)
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
    "userId": "USR001",
    "userName": "admin",
    "userTypeId": "ADMIN",
    "userTypeRole": "Administrator",
    "loginSuccess": true,
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "permissions": [
      {"moduleName": "reservations", "permissionType": "full"}
    ]
  }
}
```

### 3. Using Authentication
```http
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### 4. User Logout (JWT)
```http
POST /api/auth/logout
Authorization: Bearer <token>
```

### 5. User Logout (Legacy)
```http
POST /api/users/logout
Authorization: Bearer <token>
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

### Role Management APIs
```http
GET /api/users/roles                     # Get available roles
POST /api/users/create-with-role         # Create user with role
PUT /api/users/{userId}/role             # Update user role
GET /api/users/by-role/{role}            # Get users by role
GET /api/admin/dashboard                 # Admin dashboard
POST /api/admin/users/bulk-create        # Bulk create users
GET /api/user-types                      # Get all user types (dynamic from database)
POST /api/user-types                     # Create new user type
```

### Role-Based Access Using userTypeRole Field

The system now supports role-based access control using the `userTypeRole` field in the user entity. This field provides more flexibility in defining user roles compared to the traditional `userTypeId` approach.

**How it works:**
1. When a user logs in, the system includes both `userTypeId` and `userTypeRole` in the JWT token
2. The role-based access interceptor first checks the `userTypeRole` field from the token
3. If `userTypeRole` is not available, it falls back to `userTypeId` for backward compatibility
4. Access permissions are determined based on the user's role
5. User types are now dynamically fetched from the database, supporting both predefined and custom roles

**Example JWT Token Payload:**
```json
{
  "userId": "USR001",
  "userName": "john.manager",
  "userTypeId": "MANAGER",
  "userTypeRole": "Hotel Manager",
  "sub": "john.manager",
  "iat": 1726303200,
  "exp": 1726389600
}
```

**Creating Users with userTypeRole:**
```http
POST /api/users
Content-Type: application/json
Authorization: Bearer <token>

{
  "userName": "frontdesk",
  "password": "frontdesk123",
  "userTypeId": "STAFF",
  "userTypeRole": "Front Desk Staff",
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

**Role-Based Endpoint Access:**

| Module | Admin | Manager | Cashier | Receptionist | Housekeeping |
|--------|-------|---------|---------|--------------|--------------|
| **Reservations** (`/api/reservations`) | FULL | READ | READ | FULL | READ |
| **Check-ins** (`/api/checkins`) | FULL | READ | READ | FULL | READ |
| **Check-outs** (`/api/rooms/{roomId}/checkout`) | FULL | FULL | READ | FULL | NONE |

| **Rooms** (`/api/rooms`) | FULL | READ | READ | READ | WRITE |
| **Room Types** (`/api/room-types`) | FULL | READ | READ | READ | NONE |
| **Payments** (`/api/advances`, `/api/bills`) | FULL | READ | FULL | FULL | NONE |
| **Transactions** (`/api/transactions`) | FULL | READ | READ | FULL | NONE |
| **Charges** (`/api/charges`) | FULL | READ | READ | FULL | NONE |
| **Reports** (`/api/reports`) | FULL | FULL | FULL | READ | NONE |
| **Users** (`/api/users`) | FULL | READ | NONE | NONE | NONE |
| **Staff Management** (`/api/users/staff`) | FULL | FULL | NONE | NONE | NONE |
| **Housekeeping** (`/api/housekeeping`) | FULL | READ | READ | READ | FULL |
| **Related Bills** (`/api/bills/{billNo}/related`) | FULL | NONE | FULL | NONE | NONE |
| **User Types** (`/api/user-types`) | FULL | READ | READ | READ | NONE |

**Permission Types:**
- **FULL**: Create, Read, Update, Delete operations
- **WRITE**: Create, Read, Update operations
- **READ**: Read-only access
- **NONE**: No access to the module

**Special Access Restrictions:**
- **Related Bills Access**: Only Cashiers and Admins can access related bills (`/api/bills/{billNo}/related`)
- **User Types Management**: Only Admins can create/delete user types, all roles can view
- All other roles will receive a 403 Forbidden response when attempting to access restricted endpoints

**Access Denied Response:**
When a user attempts to access an endpoint without proper permissions:
```json
{
  "error": "Insufficient permissions for this operation"
}

---

## Complete API Endpoints

### 🏨 **Core Hotel Operations**

#### Reservations (`/api/reservations`)
```http
POST   /api/reservations                      # Create reservation
GET    /api/reservations                      # Get all reservations
GET    /api/reservations/{reservationNo}      # Get reservation
PUT    /api/reservations/{reservationNo}      # Update reservation
GET    /api/reservations/search               # Search reservations
GET    /api/reservations/arrivals/{date}      # Expected arrivals
GET    /api/reservations/departures/{date}    # Expected departures
GET    /api/reservations/pending-checkins     # Pending check-ins
GET    /api/reservations/check-availability   # Check room availability
GET    /api/reservations/available-rooms      # Get available rooms list
```

#### Check-ins (`/api/checkins`)
```http
POST   /api/checkins                     # Process check-in
GET    /api/checkins/{folioNo}           # Get check-in details
PUT    /api/checkins/{folioNo}           # Update check-in
GET    /api/checkins/room/{roomId}       # Check-in by room
GET    /api/checkins/inhouse             # In-house guests
GET    /api/checkins/checkouts/{date}    # Expected checkouts
GET    /api/checkins/search              # Search check-ins
```

#### Rooms (`/api/rooms`)
```http
POST   /api/rooms                               # Create room
GET    /api/rooms                               # Get all rooms
GET    /api/rooms/{roomId}                      # Get specific room
PUT    /api/rooms/{roomId}                      # Update room
PUT    /api/rooms/{roomId}/status/{status}      # Update room status
POST   /api/rooms/{roomId}/checkout             # Process room checkout
GET    /api/rooms/{roomId}/availability         # Check room availability
GET    /api/rooms/available                     # Available rooms
GET    /api/rooms/occupied                      # Occupied rooms
GET    /api/rooms/status/{status}               # Rooms by status
GET    /api/rooms/floor/{floor}                 # Rooms by floor
GET    /api/rooms/occupancy-stats               # Occupancy statistics
POST   /api/rooms/status/automatic-update       # Trigger automatic update
POST   /api/rooms/status/overdue-check          # Check overdue checkouts
```

#### Room Types (`/api/room-types`)
```http
POST   /api/room-types                   # Create room type
GET    /api/room-types                   # Get all room types
GET    /api/room-types/{typeId}          # Get room type
PUT    /api/room-types/{typeId}          # Update room type
DELETE /api/room-types/{typeId}          # Delete room type
```

### 💰 **Financial Management**

#### Advance Payments (`/api/advances`)
```http
POST   /api/advances/reservation         # Advance for reservation
POST   /api/advances/inhouse             # Advance for in-house
POST   /api/advances/checkout            # Advance for checkout
GET    /api/advances/reservation/{reservationNo} # Get by reservation
GET    /api/advances/folio/{folioNo}     # Get by folio
GET    /api/advances/bill/{billNo}       # Get by bill
GET    /api/advances/reservation/{reservationNo}/total # Get total by reservation
GET    /api/advances/folio/{folioNo}/total # Get total by folio
GET    /api/advances/bill/{billNo}/total # Get total by bill
GET    /api/advances/reservation/{reservationNo}/summary # Get summary by reservation
GET    /api/advances/bill/{billNo}/summary # Get summary by bill
GET    /api/advances/{advanceId}         # Get specific advance
PUT    /api/advances/{advanceId}         # Update advance
DELETE /api/advances/{advanceId}         # Delete advance
```

#### Post Transactions (`/api/transactions`)
```http
POST   /api/transactions/inhouse         # Create in-house transaction
POST   /api/transactions/checkout        # Create checkout transaction
GET    /api/transactions/folio/{folioNo} # Get by folio
GET    /api/transactions/bill/{billNo}   # Get by bill
GET    /api/transactions/room/{roomId}   # Get by room
GET    /api/transactions/date-range      # Get by date range
GET    /api/transactions/{transactionId} # Get specific
PUT    /api/transactions/{transactionId} # Update transaction
DELETE /api/transactions/{transactionId} # Delete transaction
```

#### Bills (`/api/bills`)
```http
POST   /api/bills/generate/{folioNo}     # Generate bill
GET    /api/bills/{billNo}               # Get bill
PUT    /api/bills/{billNo}               # Update bill
GET    /api/bills/folio/{folioNo}        # Bills by folio
GET    /api/bills/search                 # Search bills
POST   /api/bills/{billNo}/split         # Split bill
GET    /api/bills/{billNo}/split/preview # Preview split
GET    /api/bills/{billNo}/related       # Get related bills (Cashier/Admin only)
POST   /api/bills/{billNo}/payment       # Process payment
GET    /api/bills/{billNo}/settlement-status # Settlement status
GET    /api/bills/pending-settlements    # Pending settlements
```

### 👥 **User & Access Management**

#### User Management (`/api/users`)
```http
POST   /api/users/login                  # User login
POST   /api/users/logout                 # User logout
POST   /api/users                        # Create user
GET    /api/users                        # Get all users
GET    /api/users/{userId}               # Get user
PUT    /api/users/{userId}               # Update user
DELETE /api/users/{userId}               # Delete user
PUT    /api/users/{userId}/rights        # Update permissions
GET    /api/users/roles                  # Available roles
POST   /api/users/create-with-role       # Create with role
PUT    /api/users/{userId}/role          # Update role
GET    /api/users/by-role/{role}         # Users by role
GET    /api/users/{userId}/permissions/{module} # Check permission
```

#### Admin Operations (`/api/admin`)
```http
GET    /api/admin/dashboard              # Admin dashboard
POST   /api/admin/users/bulk-create      # Bulk create users
GET    /api/admin/system-status          # System status
```

### 🟢 **Operations Management**

#### Hotel Operations (`/api/operations`)
```http
POST   /api/operations/audit-date-change  # Process audit date change
POST   /api/operations/shift-change       # Process shift change
```

#### Housekeeping (`/api/housekeeping`)
```http
GET    /api/housekeeping/tasks           # Get housekeeping tasks
POST   /api/housekeeping/tasks           # Create housekeeping task
PUT    /api/housekeeping/tasks/{taskId}  # Update task status
POST   /api/housekeeping/room-status     # Update room cleaning status
```

### 🏢 **Master Data Management**
```http
POST   /api/user-types                   # Create
GET    /api/user-types                   # Get all
GET    /api/user-types/{userTypeId}      # Get specific
PUT    /api/user-types/{userTypeId}      # Update
DELETE /api/user-types/{userTypeId}      # Delete
```

#### Companies (`/api/companies`)
```http
POST   /api/companies                    # Create
GET    /api/companies                    # Get all
GET    /api/companies/{companyId}        # Get specific
PUT    /api/companies/{companyId}        # Update
DELETE /api/companies/{companyId}        # Delete
```

#### Payment Modes (`/api/payment-modes`)
```http
POST   /api/payment-modes                # Create
GET    /api/payment-modes                # Get all
GET    /api/payment-modes/{id}           # Get specific
PUT    /api/payment-modes/{id}           # Update
DELETE /api/payment-modes/{id}           # Delete
```

#### Plan Types (`/api/plan-types`)
```http
POST   /api/plan-types                   # Create
GET    /api/plan-types                   # Get all
GET    /api/plan-types/{planId}          # Get specific
PUT    /api/plan-types/{planId}          # Update
DELETE /api/plan-types/{planId}          # Delete
```

#### Tax Management (`/api/taxes`)
```http
POST   /api/taxes                        # Create
GET    /api/taxes                        # Get all
GET    /api/taxes/{taxId}                # Get specific
PUT    /api/taxes/{taxId}                # Update
DELETE /api/taxes/{taxId}                # Delete
```

#### Account Heads (`/api/account-heads`)
```http
POST   /api/account-heads                # Create
GET    /api/account-heads                # Get all
GET    /api/account-heads/{accountHeadId} # Get specific
PUT    /api/account-heads/{accountHeadId} # Update
DELETE /api/account-heads/{accountHeadId} # Delete
```

#### Nationalities (`/api/nationalities`)
```http
POST   /api/nationalities                # Create
GET    /api/nationalities                # Get all
GET    /api/nationalities/{id}           # Get specific
PUT    /api/nationalities/{id}           # Update
DELETE /api/nationalities/{id}           # Delete
```

#### Arrival Modes (`/api/arrival-modes`)
```http
POST   /api/arrival-modes                # Create
GET    /api/arrival-modes                # Get all
GET    /api/arrival-modes/{id}           # Get specific
PUT    /api/arrival-modes/{id}           # Update
DELETE /api/arrival-modes/{id}           # Delete
```

#### Reference Modes (`/api/ref-modes`)
```http
POST   /api/ref-modes                    # Create
GET    /api/ref-modes                    # Get all
GET    /api/ref-modes/{id}               # Get specific
PUT    /api/ref-modes/{id}               # Update
DELETE /api/ref-modes/{id}               # Delete
```

#### Reservation Sources (`/api/reservation-sources`)
```http
POST   /api/reservation-sources          # Create
GET    /api/reservation-sources          # Get all
GET    /api/reservation-sources/{id}     # Get specific
PUT    /api/reservation-sources/{id}     # Update
DELETE /api/reservation-sources/{id}     # Delete
```

---

## Key Request Examples

### Create Reservation
```http
POST /api/reservations
Content-Type: application/json
Authorization: Bearer <token>

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

### Process Check-in
```http
POST /api/checkins
Content-Type: application/json
Authorization: Bearer <token>

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

### Create Transaction
```http
POST /api/transactions/inhouse
Content-Type: application/json
Authorization: Bearer <token>

{
  "folioNo": "F001/25-26",
  "transactionType": "CHARGE",
  "accountHeadId": "RESTAURANT",
  "amount": 1500.00,
  "quantity": 2,
  "rate": 750.00,
  "remarks": "Dinner at restaurant"
}
```

### Generate Bill
```http
POST /api/bills/generate/F001/25-26
Authorization: Bearer <token>
```

### Update Bill
```http
PUT /api/bills/B001-25-26
Content-Type: application/json
Authorization: Bearer <token>

{
  "guestName": "John Doe Updated",
  "totalAmount": 6500.00,
  "advanceAmount": 2000.00,
  "paymentNotes": "Updated bill amount"
}
```

### Get Related Bills (Cashier/Admin only)
```http
GET /api/bills/B001-25-26/related
Authorization: Bearer <token>
```

### Create User with Role
```http
POST /api/users/create-with-role
Content-Type: application/x-www-form-urlencoded
Authorization: Bearer <token>

userName=jane.cashier&password=password123&role=CASHIER
```

### Create User with userTypeRole
```http
POST /api/users
Content-Type: application/json
Authorization: Bearer <token>

{
  "userName": "cashier1",
  "password": "cashier123",
  "userTypeId": "CASHIER",
  "userTypeRole": "Billing Cashier",
  "permissions": [
    {
      "moduleName": "PAYMENTS",
      "permissionType": "full"
    },
    {
      "moduleName": "BILLS",
      "permissionType": "full"
    },
    {
      "moduleName": "REPORTS",
      "permissionType": "full"
    }
  ]
}
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
  "timestamp": "2025-09-14T12:00:00"
}
```

### Error Response
```json
{
  "success": false,
  "message": "Error description",
  "data": null,
  "timestamp": "2025-09-14T12:00:00"
}
```

---

## HTTP Status Codes
- `200 OK` - Successful operation
- `201 Created` - Resource created
- `400 Bad Request` - Invalid request
- `401 Unauthorized` - Authentication required
- `403 Forbidden` - Insufficient permissions
- `404 Not Found` - Resource not found
- `500 Internal Server Error` - Server error

---

## Number Generation Format
- **Reservations:** `1-25-26` (sequence/fiscal-year) - Sequential: 1, 2, 3, etc.
- **Folios:** `F1-25-26` - Sequential: F1, F2, F3, etc.
- **Bills:** `B1-25-26` - Sequential: B1, B2, B3, etc.
- **Advances:** `R1/25-26` - Sequential: R1, R2, R3, etc.
- **Transactions:** `TXN12345` - Timestamp-based unique ID
- **Users:** `USER001` - Padded sequential format

---

## Testing & Documentation

**Get Users by Role:**
```http
GET /api/users/by-role/Cashier
Authorization: Bearer <token>
```

**Response:**
```json
{
  "success": true,
  "message": "Operation successful",
  "data": [
    {
      "userId": "USR001",
      "userName": "cashier1",
      "userTypeId": "CASHIER",
      "userTypeRole": "Cashier",
      "userTypeName": "Cashier",
      "permissions": [
        {
          "moduleName": "payments",
          "permissionType": "full"
        },
        {
          "moduleName": "bills",
          "permissionType": "full"
        }
      ]
    }
  ],
  "timestamp": "2025-09-15T11:38:33.832037900"
}
```

**Supported Role Names:**
- Administrator - System Administrator
- Manager - Hotel Manager
- Cashier - Cashier
- Receptionist - Receptionist
- Housekeeping Staff - Housekeeping Staff
- Any custom role names defined in the database

**Note:** The endpoint now searches by user type name rather than user type ID. For example, use "Cashier" instead of "CASHIER".

### Swagger UI
```
http://localhost:8080/swagger-ui/index.html
```

### Sample Test Workflow
1. **Login** to get JWT token
2. **Create master data** (room types, payment modes)
3. **Create reservation**
4. **Process check-in**
5. **Add charges/transactions**
6. **Generate and settle bill**
7. **Check-out guest**

### Key Features
- ✅ **JWT Authentication** with role-based access
- ✅ **Complete CRUD operations** for all entities
- ✅ **Role-based permissions** (Admin, Manager, Cashier, Receptionist, Housekeeping)
- ✅ **Bill management** with split and settlement
- ✅ **Room status management** with automated updates
- ✅ **User and permission management**
- ✅ **Admin dashboard** and bulk operations
- ✅ **Comprehensive audit trail**

---

*API Version 1.0.0 - Complete Hotel Management System*

#### Login to Get JWT Token:
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
    "userTypeName": "Administrator",
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  },
  "timestamp": "2025-09-14T12:00:00"
}
```

#### Using JWT Token in Requests:
Include the token in the Authorization header for all protected endpoints:
```http
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

#### Logout to Invalidate Token:
```http
POST /api/auth/logout
Authorization: Bearer <your-jwt-token>
```

**Response:**
```json
{
  "success": true,
  "message": "Logout successful",
  "data": null,
  "timestamp": "2025-09-14T12:30:00"
}
```

#### Session Management:
- Tokens are valid for 24 hours from generation
- Once logged out, tokens are blacklisted and cannot be reused
- Session is maintained until explicit logout or token expiration

## Main Endpoints

### 1. Reservations (`/api/reservations`)

**Create Reservation:**
```http
POST /api/reservations
Content-Type: application/json

{
  "guestName": "John Doe",
  "companyId": "COMP001",
  "planId": "PLAN001", 
  "roomTypeId": "STANDARD",
  "arrivalDate": "2025-09-15",
  "departureDate": "2025-09-20",
  "noOfDays": 5,
  "noOfPersons": 2,
  "noOfRooms": 1,
  "mobileNumber": "+1234567890",
  "emailId": "john.doe@email.com",
  "rate": 5000.00,
  "includingGst": "N",
  "remarks": "Corporate booking"
}
```

**Required Fields:**
- `guestName` (string) - Guest full name
- `arrivalDate` (date) - Check-in date (YYYY-MM-DD format)
- `departureDate` (date) - Check-out date (YYYY-MM-DD format) 
- `noOfDays` (integer) - Number of stay days
- `noOfPersons` (integer) - Number of guests
- `noOfRooms` (integer) - Number of rooms required
- `mobileNumber` (string) - Contact mobile number

**Optional Fields:**
- `companyId` (string) - Company ID for corporate bookings
- `planId` (string) - Rate plan ID
- `roomTypeId` (string) - Preferred room type
- `emailId` (string) - Guest email address
- `rate` (decimal) - Room rate per night
- `includingGst` (string) - "Y" or "N" (default: "N")
- `remarks` (string) - Additional notes

**Other Endpoints:**
- `GET /{reservationNo}` - Get reservation
- `PUT /{reservationNo}` - Update reservation
- `GET /search?searchTerm=John` - Search reservations
- `GET /arrivals/{date}` - Expected arrivals
- `GET /departures/{date}` - Expected departures

### 2. Check-ins (`/api/checkins`)

**Process Check-in from Reservation (Guest info auto-populated):**
```http
POST /api/checkins
Content-Type: application/json

{
  "reservationNo": "1/25-26",
  "roomId": "R101",
  "arrivalDate": "2025-09-15",
  "departureDate": "2025-09-20",
  "walkIn": "N",
  "remarks": "Early check-in requested"
}
```
*Note: Guest name, mobile number, email, and rate are automatically fetched from the reservation table when reservationNo is provided.*

**Process Check-in from Reservation (Override guest info):**
```http
POST /api/checkins
Content-Type: application/json

{
  "reservationNo": "1/25-26",
  "guestName": "John Doe Jr.",
  "roomId": "R101",
  "arrivalDate": "2025-09-15",
  "departureDate": "2025-09-20",
  "mobileNumber": "+1234567890",
  "emailId": "john.doe@email.com",
  "rate": 5000.00,
  "walkIn": "N",
  "remarks": "Different guest checking in"
}
```
*Note: You can still provide guest details manually to override the reservation information.*

**Walk-in Check-in (No Reservation - Guest name required):**
```http
POST /api/checkins
Content-Type: application/json

{
  "guestName": "Jane Smith",
  "roomId": "R102", 
  "arrivalDate": "2025-09-15",
  "departureDate": "2025-09-17",
  "mobileNumber": "+9876543210",
  "emailId": "jane.smith@email.com",
  "rate": 4500.00,
  "walkIn": "Y"
}
```
*Note: For walk-in guests (walkIn: "Y"), guestName is mandatory since there's no reservation to fetch it from.*

**Other Endpoints:**
- `GET /{folioNo}` - Get check-in details
- `GET /room/{roomId}` - Get check-in by room
- `GET /inhouse` - In-house guests
- `GET /checkouts/{date}` - Expected checkouts

### 3. Room Types (`/api/room-types`)

**Create Room Type (Auto-generated ID):**
```http
POST /api/room-types
Content-Type: application/json

{
  "typeName": "Deluxe Room",
  "noOfRooms": 15
}
```

**Create Room Type (Custom ID):**
```http
POST /api/room-types
Content-Type: application/json

{
  "typeId": "DELUXE",
  "typeName": "Deluxe Room",
  "noOfRooms": 15
}
```

**Get All Room Types:**
```http
GET /api/room-types
```

**Get Room Type by ID:**
```http
GET /api/room-types/{typeId}
```

**Update Room Type:**
```http
PUT /api/room-types/{typeId}
Content-Type: application/json

{
  "typeName": "Premium Deluxe Room",
  "noOfRooms": 20
}
```

**Delete Room Type:**
```http
DELETE /api/room-types/{typeId}
```

**Response:**
```json
{
  "success": true,
  "message": "Room type updated successfully",
  "data": {
    "typeId": "TYPE0123",
    "typeName": "Premium Deluxe Room",
    "noOfRooms": 20
  },
  "timestamp": "2025-09-14T11:30:00"
}
```

### 4. Rooms (`/api/rooms`)

**Create Room (Auto-generated ID):**
```http
POST /api/rooms
Content-Type: application/json

{
  "roomNo": "301",
  "floor": "3",
  "status": "VR",
  "roomTypeId": "TYPE0123"
}
```

**Create Room (Custom ID):**
```http
POST /api/rooms
Content-Type: application/json

{
  "roomId": "R301",
  "roomNo": "301",
  "floor": "3",
  "status": "VR",
  "roomTypeId": "TYPE0123"
}
```

**Get All Rooms:**
```http
GET /api/rooms
```

**Get Rooms by Status:**
```http
GET /api/rooms/status/VR
```

**Update Room Details:**
```http
PUT /api/rooms/{roomId}
Content-Type: application/json

{
  "roomNo": "301A",
  "floor": "3",
  "roomTypeId": "DELUXE",
  "status": "VR"
}
```

**Update Room Status Only:**
```http
PUT /api/rooms/{roomId}/status/OD
```

**Process Room Checkout:**
```http
POST /api/rooms/{roomId}/checkout?folioNo=F0001-25-26
```

**Check Room Availability:**
```http
GET /api/rooms/{roomId}/availability?arrivalDate=2025-09-20&departureDate=2025-09-25
```

**Response:**
```json
{
  "success": true,
  "message": "Room updated successfully",
  "data": {
    "roomId": "ROOM0456",
    "roomNo": "301A",
    "floor": "3",
    "status": "VR",
    "roomTypeId": "DELUXE",
    "roomTypeName": "Deluxe Room",
    "guestName": null,
    "folioNo": null
  },
  "timestamp": "2025-09-14T11:30:00"
}
```

### 5. Advances (`/api/advances`)

**Create Advance for Reservation:**
```http
POST /api/advances/reservation
Content-Type: application/json

{
  "reservationNo": "1/24-25",
  "guestName": "John Doe",
  "modeOfPaymentId": "CASH",
  "amount": 2000.00,
  "remarks": "Advance payment for booking"
}
```

**Create Advance for In-house Guest:**
```http
POST /api/advances/inhouse
Content-Type: application/json

{
  "folioNo": "F0001/24-25",
  "guestName": "John Doe",
  "modeOfPaymentId": "CARD",
  "amount": 1000.00,
  "remarks": "Additional advance"
}
```

**Get Advances by Reservation:**
```http
GET /api/advances/reservation/{reservationNo}
```

**Get Advance by ID:**
```http
GET /api/advances/{advanceId}
```

**Update Advance:**
```http
PUT /api/advances/{advanceId}
Content-Type: application/json

{
  "guestName": "John Doe Updated",
  "amount": 2500.00,
  "modeOfPaymentId": "CARD",
  "remarks": "Updated advance payment"
}
```

**Delete Advance:**
```http
DELETE /api/advances/{advanceId}
```

### 6. Payment Modes (`/api/payment-modes`)

**Create Payment Mode:**
```http
POST /api/payment-modes
Content-Type: application/json

{
  "id": "CASH",
  "name": "Cash Payment"
}
```

**Get All Payment Modes:**
```http
GET /api/payment-modes
``'

**Get Payment Mode by ID:**
```http
GET /api/payment-modes/{id}
```

**Update Payment Mode:**
```http
PUT /api/payment-modes/{id}
Content-Type: application/json

{
  "id": "CASH",
  "name": "Cash Payment Updated"
}
```

**Delete Payment Mode:**
```http
DELETE /api/payment-modes/{id}
```

### 7. Plan Types (`/api/plan-types`)

**Create Plan Type (Auto-generated ID):**
```http
POST /api/plan-types
Content-Type: application/json

{
  "planName": "Corporate Plan",
  "discountPercentage": 15.00
}
```

**Create Plan Type (Custom ID):**
```http
POST /api/plan-types
Content-Type: application/json

{
  "planId": "CORPORATE",
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
  "timestamp": "2025-09-14T11:30:00"
}
```

**Get All Plan Types:**
```http
GET /api/plan-types
```

**Get Plan Type by ID:**
```http
GET /api/plan-types/{planId}
```

**Update Plan Type:**
```http
PUT /api/plan-types/{planId}
Content-Type: application/json

{
  "planId": "CORPORATE",
  "planName": "Corporate Plan Updated",
  "discountPercentage": 20.00
}
```

**Delete Plan Type:**
```http
DELETE /api/plan-types/{planId}
```

### 8. Companies (`/api/companies`)

**Create Company (Auto-generated ID):**
```http
POST /api/companies
Content-Type: application/json

{
  "companyName": "Grand Hotels Ltd",
  "address1": "123 Business District",
  "address2": "Downtown Area",
  "address3": "Metro City",
  "gstNumber": "22ABCDE1234F1Z5"
}
```

**Create Company (Custom ID):**
```http
POST /api/companies
Content-Type: application/json

{
  "companyId": "GRAND001",
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
  "timestamp": "2025-09-14T11:30:00"
}
```

**Get All Companies:**
```http
GET /api/companies
```

**Get Company by ID:**
```http
GET /api/companies/{companyId}
```

**Update Company:**
```http
PUT /api/companies/{companyId}
Content-Type: application/json

{
  "companyName": "Grand Hotels Ltd Updated",
  "address1": "456 New Business District",
  "address2": "Uptown Area",
  "address3": "Metro City",
  "gstNumber": "22ABCDE1234F1Z5"
}
```

**Delete Company:**
```http
DELETE /api/companies/{companyId}
```

### 9. Post Transactions (`/api/transactions`)

**Create Transaction for In-house:**
```http
POST /api/transactions/inhouse
{
  "folioNo": "F0001/24-25",
  "guestName": "John Doe",
  "accHeadId": "RESTAURANT",
  "amount": 850.00,
  "narration": "Restaurant charges"
}
```

*Note: All transaction responses now include roomNo field populated from the rooms table for better room identification in reports and bills.*

### 10. Bills (`/api/bills`)

**Generate Bill:**
```http
POST /api/bills/generate/{folioNo}
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
    "generatedAt": "2025-09-14T14:38:58.132763",
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
  "timestamp": "2025-09-14T14:41:50.146944400"
}
```

**Get Bill by Number:**
```http
GET /api/bills/{billNo}
```

**Response:** (Same format as Generate Bill)

**Get Bill by Folio:**
```http
GET /api/bills/folio/{folioNo}
```

**Search Bills:**
```http
GET /api/bills/search?searchTerm=John
```

**Get Related Bills (Original + All Splits):**
```http
GET /api/bills/{billNo}/related
```

**Response:**
```json
{
  "success": true,
  "message": "Related bills retrieved successfully",
  "data": [
    {
      "billNo": "B0021-24-25",
      "guestName": "John Doe",
      "folioNo": "F323232/24-25",
      "roomNo": "401",
      "totalAmount": 10000.00,
      "advanceAmount": 5000.00,
      "balanceAmount": 5000.00,
      "generatedAt": "2025-09-14T10:00:00",
      "transactions": [...],
      "advances": [...]
    }
  ],
  "timestamp": "2025-09-14T11:30:00"
}
```

#### Split Bills Functionality

**Get Split Bill Preview:**
```http
GET /api/bills/{billNo}/split/preview
```

**Response:**
```json
{
  "success": true,
  "message": "Split bill preview retrieved successfully",
  "data": {
    "originalBillNo": "B0021-24-25",
    "guestName": "John Doe",
    "folioNo": "F323232/24-25",
    "roomNo": "401",
    "originalTotalAmount": 15000.00,
    "originalAdvanceAmount": 5000.00,
    "transactions": [
      {
        "transactionId": "TXN001/24-25",
        "voucherNo": "V001",
        "date": "2025-09-14",
        "transactionName": "Room Service",
        "amount": 2500.00,
        "narration": "Food order",
        "selectedForNewBill": false
      },
      {
        "transactionId": "TXN002/24-25",
        "voucherNo": "V002",
        "date": "2025-09-14",
        "transactionName": "Laundry",
        "amount": 1200.00,
        "narration": "Dry cleaning",
        "selectedForNewBill": false
      },
      {
        "transactionId": "TXN003/24-25",
        "voucherNo": "V003",
        "date": "2025-09-14",
        "transactionName": "Restaurant",
        "amount": 3800.00,
        "narration": "Dinner",
        "selectedForNewBill": false
      }
    ],
    "advances": [
      {
        "receiptNo": "R001/24-25",
        "amount": 5000.00,
        "modeOfPaymentName": "Cash",
        "date": "2025-09-13"
      }
    ]
  },
  "timestamp": "2025-09-14T11:30:00"
}
```

**Execute Split Bill:**
```http
POST /api/bills/{billNo}/split
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

**Split Bill Response:**
```json
{
  "success": true,
  "message": "Bill split successfully",
  "data": {
    "originalBillNo": "B0021-24-25",
    "newBillNo": "B0021-24-25-S1",
    "guestName": "John Doe",
    "folioNo": "F323232/24-25",
    "roomNo": "401",
    "originalBillRemainingAmount": 10000.00,
    "originalBillAdvanceAmount": 5000.00,
    "originalBillBalanceAmount": 5000.00,
    "newBillTotalAmount": 5000.00,
    "newBillAdvanceAmount": 0.00,
    "newBillBalanceAmount": 5000.00,
    "splitDateTime": "2025-09-14T11:30:00",
    "splitNarration": "Split bill for restaurant and laundry charges",
    "transactionsMovedToNewBill": 2,
    "transactionsRemainingInOriginal": 1
  },
  "timestamp": "2025-09-14T11:30:00"
}
```

### 11. Advances (`/api/advances`)

**Create Advance:**
```http
POST /api/advances
Content-Type: application/json

{
  "reservationNo": "1/24-25",
  "guestName": "John Doe",
  "date": "2025-09-10",
  "modeOfPaymentId": "CASH",
  "amount": 2000.00,
  "narration": "Advance payment for booking"
}
```

**Get Advance by ID:**
```http
GET /api/advances/{advanceId}
```

**Update Advance:**
```http
PUT /api/advances/{advanceId}
Content-Type: application/json

{
  "reservationNo": "1/24-25",
  "guestName": "John Doe",
  "date": "2025-09-10",
  "modeOfPaymentId": "CASH",
  "amount": 2000.00,
  "narration": "Advance payment for booking"
}
```

**Delete Advance:**
```http
DELETE /api/advances/{advanceId}
```

**Get Total Advances by Reservation:**
```http
GET /api/advances/reservation/{reservationNo}/total
```

**Response:**
```json
{
  "success": true,
  "message": "Operation successful",
  "data": 2500.00,
  "timestamp": "2025-09-14T11:30:00"
}
```

**Get Total Advances by Folio:**
```http
GET /api/advances/folio/{folioNo}/total
```

**Response:**
```json
{
  "success": true,
  "message": "Operation successful",
  "data": 1500.00,
  "timestamp": "2025-09-14T11:30:00"
}
```

**Get Total Advances by Bill:**
```http
GET /api/advances/bill/{billNo}/total
```

**Response:**
```json
{
  "success": true,
  "message": "Operation successful",
  "data": 3000.00,
  "timestamp": "2025-09-14T11:30:00"
}
```

**Get Advance Summary by Reservation:**
```http
GET /api/advances/reservation/{reservationNo}/summary
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
  "timestamp": "2025-09-14T11:30:00"
}
```

**Get Advance Summary by Bill:**
```http
GET /api/advances/bill/{billNo}/summary
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
  "timestamp": "2025-09-14T11:30:00"
}
```

### 6. Payment Modes (`/api/payment-modes`)

**Get All Payment Modes:**
```http
GET /api/payment-modes
```

**Get Payment Mode by ID:**
```http
GET /api/payment-modes/{paymentModeId}
```

**Create Payment Mode:**
```http
POST /api/payment-modes
Content-Type: application/json

{
  "paymentModeName": "Credit Card",
  "description": "Payment via credit card"
}
```

**Update Payment Mode:**
```http
PUT /api/payment-modes/{paymentModeId}
Content-Type: application/json

{
  "paymentModeName": "Credit Card",
  "description": "Payment via credit card"
}
```

**Delete Payment Mode:**
```http
DELETE /api/payment-modes/{paymentModeId}
```

### 7. Account Heads (`/api/account-heads`)

**Get All Account Heads:**
```http
GET /api/account-heads
```

**Get Account Head by ID:**
```http
GET /api/account-heads/{accountHeadId}
```

**Create Account Head:**
```http
POST /api/account-heads
Content-Type: application/json

{
  "accountHeadName": "Room Service",
  "description": "Charges for room service"
}
```

**Update Account Head:**
```http
PUT /api/account-heads/{accountHeadId}
Content-Type: application/json

{
  "accountHeadName": "Room Service",
  "description": "Charges for room service"
}
```

**Delete Account Head:**
```http
DELETE /api/account-heads/{accountHeadId}
```

### 8. Reservations (`/api/reservations`)

**Get All Reservations:**
```http
GET /api/reservations
```

**Get Reservation by ID:**
```http
GET /api/reservations/{reservationId}
```

**Create Reservation:**
```http
POST /api/reservations
Content-Type: application/json

{
  "guestName": "John Doe",
  "arrivalDate": "2025-09-15",
  "departureDate": "2025-09-20",
  "roomId": "ROOM3881",
  "roomNo": "101",
  "guestCount": 2,
  "status": "CONFIRMED",
  "narration": "Reservation for John Doe"
}
```

**Update Reservation:**
```http
PUT /api/reservations/{reservationId}
Content-Type: application/json

{
  "guestName": "John Doe",
  "arrivalDate": "2025-09-15",
  "departureDate": "2025-09-20",
  "roomId": "ROOM3881",
  "roomNo": "101",
  "guestCount": 2,
  "status": "CONFIRMED",
  "narration": "Reservation for John Doe"
}
```

**Delete Reservation:**
```http
DELETE /api/reservations/{reservationId}
```

**Get Total Reservations by Room:**
```http
GET /api/reservations/room/{roomId}/total
```

**Response:**
```json
{
  "success": true,
  "message": "Operation successful",
  "data": 5,
  "timestamp": "2025-09-14T11:30:00"
}
```

**Get Reservation Summary by Room:**
```http
GET /api/reservations/room/{roomId}/summary
```

**Response:**
```json
{
  "success": true,
  "message": "Operation successful",
  "data": {
    "roomId": "ROOM3881",
    "roomNo": "101",
    "totalReservations": 5,
    "reservations": [
      {
        "reservationNo": "1/24-25",
        "guestName": "John Doe",
        "arrivalDate": "2025-09-15",
        "departureDate": "2025-09-20",
        "guestCount": 2,
        "status": "CONFIRMED",
        "narration": "Reservation for John Doe"
      },
      {
        "reservationNo": "2/24-25",
        "guestName": "Jane Smith",
        "arrivalDate": "2025-09-16",
        "departureDate": "2025-09-18",
        "guestCount": 1,
        "status": "CHECKED_IN",
        "narration": "Reservation for Jane Smith"
      }
    ]
  },
  "timestamp": "2025-09-14T11:30:00"
}
```

### 12. Folios (`/api/folios`)

**Create Folio:**
```http
POST /api/folios
Content-Type: application/json

{
  "reservationNo": "1/24-25",
  "guestName": "John Doe",
  "arrivalDate": "2025-09-15",
  "departureDate": "2025-09-20",
  "roomId": "ROOM3881",
  "roomNo": "101",
  "guestCount": 2,
  "status": "OPEN",
  "narration": "Folio for John Doe"
}
```

**Get Folio by ID:**
```http
GET /api/folios/{folioId}
```

**Update Folio:**
```http
PUT /api/folios/{folioId}
Content-Type: application/json

{
  "reservationNo": "1/24-25",
  "guestName": "John Doe",
  "arrivalDate": "2025-09-15",
  "departureDate": "2025-09-20",
  "roomId": "ROOM3881",
  "roomNo": "101",
  "guestCount": 2,
  "status": "OPEN",
  "narration": "Folio for John Doe"
}
```

**Delete Folio:**
```http
DELETE /api/folios/{folioId}
```

**Get Total Folios by Room:**
```http
GET /api/folios/room/{roomId}/total
```

**Response:**
```json
{
  "success": true,
  "message": "Operation successful",
  "data": 3,
  "timestamp": "2025-09-14T11:30:00"
}
```

**Get Folio Summary by Room:**
```http
GET /api/folios/room/{roomId}/summary
```

**Response:**
```json
{
  "success": true,
  "message": "Operation successful",
  "data": {
    "roomId": "ROOM3881",
    "roomNo": "101",
    "totalFolios": 3,
    "folios": [
      {
        "folioNo": "F0001/24-25",
        "guestName": "John Doe",
        "arrivalDate": "2025-09-15",
        "departureDate": "2025-09-20",
        "guestCount": 2,
        "status": "OPEN",
        "narration": "Folio for John Doe"
      },
      {
        "folioNo": "F0002/24-25",
        "guestName": "Jane Smith",
        "arrivalDate": "2025-09-16",
        "departureDate": "2025-09-18",
        "guestCount": 1,
        "status": "CLOSED",
        "narration": "Folio for Jane Smith"
      }
    ]
  },
  "timestamp": "2025-09-14T11:30:00"
}
```

### 13. Rooms (`/api/rooms`)

**Get All Rooms:**
```http
GET /api/rooms
```

**Get Room by ID:**
```http
GET /api/rooms/{roomId}
```

**Create Room:**
```http
POST /api/rooms
Content-Type: application/json

{
  "roomNo": "101",
  "roomType": "Deluxe",
  "floor": 1,
  "status": "AVAILABLE",
  "description": "Deluxe room with a king-size bed"
}
```

**Update Room:**
```http
PUT /api/rooms/{roomId}
Content-Type: application/json

{
  "roomNo": "101",
  "roomType": "Deluxe",
  "floor": 1,
  "status": "AVAILABLE",
  "description": "Deluxe room with a king-size bed"
}
```

**Delete Room:**
```http
DELETE /api/rooms/{roomId}
```

**Get Total Rooms by Type:**
```http
GET /api/rooms/type/{roomType}/total
```

**Response:**
```json
{
  "success": true,
  "message": "Operation successful",
  "data": 10,
  "timestamp": "2025-09-14T11:30:00"
}
```

**Get Room Summary by Type:**
```http
GET /api/rooms/type/{roomType}/summary
```

**Response:**
```json
{
  "success": true,
  "message": "Operation successful",
  "data": {
    "roomType": "Deluxe",
    "totalRooms": 10,
    "rooms": [
      {
        "roomId": "ROOM3881",
        "roomNo": "101",
        "floor": 1,
        "status": "AVAILABLE",
        "description": "Deluxe room with a king-size bed"
      },
      {
        "roomId": "ROOM3882",
        "roomNo": "102",
        "floor": 1,
        "status": "OCCUPIED",
        "description": "Deluxe room with a king-size bed"
      }
    ]
  },
  "timestamp": "2025-09-14T11:30:00"
}
```


**Get Related Bills (Original + All Splits):**
```http
GET /api/bills/{billNo}/related
```

**Response:**
```json
{
  "success": true,
  "message": "Related bills retrieved successfully",
  "data": [
    {
      "billNo": "B0021-24-25",
      "guestName": "John Doe",
      "folioNo": "F323232/24-25",
      "roomNo": "401",
      "totalAmount": 10000.00,
      "advanceAmount": 5000.00,
      "balanceAmount": 5000.00,
      "generatedAt": "2025-09-14T10:00:00",
      "isSplitBill": false,
      "splitSequence": null,
      "originalBillNo": null
    },
    {
      "billNo": "B0021-24-25-S1",
      "guestName": "John Doe",
      "folioNo": "F323232/24-25",
      "roomNo": "401",
      "totalAmount": 5000.00,
      "advanceAmount": 0.00,
      "balanceAmount": 5000.00,
      "generatedAt": "2025-09-14T11:30:00",
      "isSplitBill": true,
      "splitSequence": 1,
      "originalBillNo": "B0021-24-25"
    }
  ],
  "timestamp": "2025-09-14T11:30:00"
}
```

#### Split Bill Workflow:

1. **Preview Phase**: Get all transactions and advances
   - Call `GET /api/bills/{billNo}/split/preview`
   - Review all transactions with selection options
   - UI shows table format as requested:
   
   | Bill Number | Guest Name | Folio Number | Room Number |
   |-------------|------------|--------------|-------------|
   | 21 | XYZ | 323232 | 401 |
   
   | Voucher Number | Date | Transaction Name | Amount | New Bill Y/N |
   |----------------|------|------------------|--------|---------------|
   | V001 | 2025-09-14 | Room Service | 2500.00 | N |
   | V002 | 2025-09-14 | Laundry | 1200.00 | Y |
   | V003 | 2025-09-14 | Restaurant | 3800.00 | Y |

2. **Selection Phase**: User selects transactions for new bill
   - Mark desired transactions with `selectedForNewBill: true`
   - Add optional narration for the new bill

3. **Execution Phase**: Process the split
   - Call `POST /api/bills/{billNo}/split`
   - System creates new bill with selected transactions
   - Original bill amount is reduced accordingly
   - Both bills maintain same guest details (name, folio, room)

4. **Result**: Two separate bills with consistent numbering
   - **Original Bill (B0021-24-25)**: Reduced amount with remaining transactions
   - **Split Bill (B0021-24-25-S1)**: Contains only selected transactions
   - **Relationship Tracking**: Split bills maintain reference to original
   - **Sequential Numbering**: Multiple splits get S1, S2, S3, etc.
   - Each bill can be settled independently

#### **Bill Number Format:**
- **Original Bill**: `B3906-25-26`
- **First Split**: `B3906-25-26-S1`
- **Second Split**: `B3906-25-26-S2`
- **Third Split**: `B3906-25-26-S3`

This consistent numbering ensures:
- ✅ **URL-Safe**: No forward slashes in endpoints
- ✅ **Relationship Tracking**: Easy to identify related bills
- ✅ **Sequential Order**: Clear split sequence
- ✅ **Audit Trail**: Complete bill splitting history

#### **Bill Relationship Population:**

Bills automatically populate related data following a consistent pattern:

1. **Room Information**: Fetched by roomId to get room number
2. **Transactions**: 
   - First attempts to find by billNo
   - Falls back to folioNo for newly generated bills
   - All transactions are linked to the bill during generation
3. **Advances**:
   - First attempts to find by billNo  
   - Falls back to folioNo for newly generated bills
   - All advances are linked to the bill during generation

**Note**: When a bill is generated from a folio, the system automatically:
- Updates all folio transactions to reference the new billNo
- Updates all folio advances to reference the new billNo
- Ensures data consistency across the system

This ensures that bill responses always include complete transaction and advance history.

#### **Common Issues & Solutions:**

**Issue: Empty transactions/advances arrays**
```json
{
  "transactions": [],
  "advances": []
}
```

**Solution**: This was resolved in the latest update. The system now:
1. Automatically links transactions and advances to bills during generation
2. Uses fallback queries to find data by folioNo if billNo queries return empty
3. Ensures data consistency across the system

**Issue: Missing room number**
```json
{
  "roomNo": null
}
```

**Solution**: Room number is populated using the response population pattern by fetching the room entity using roomId.

#### **Deferred Payment Support:**

The system now supports **deferred payments** where guests can receive bills and pay later. This addresses the common hotel scenario where guests don't have money immediately and need to settle bills at a later time.

**Bill Settlement Workflow:**

1. **Bill Generation**: Bills are generated with settlement status tracking
   ```json
   {
     "billNo": "B0001-25-26",
     "guestName": "John Doe",
     "totalAmount": 5000.00,
     "settlementStatus": "PENDING", // PENDING, PARTIAL, SETTLED
     "paidAmount": 0.00,
     "balanceAmount": 5000.00
   }
   ```

2. **Guest Receives Bill Number**: Staff provides bill number for future payment

3. **Payment Processing**: Guest returns later to settle bill using bill number

**Process Payment Against Bill Number:**
```http
POST /api/bills/{billNo}/payment
Content-Type: application/json

{
  "paymentAmount": 2000.00,
  "modeOfPaymentId": "CASH",
  "paymentNotes": "Partial payment - guest will return for balance"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Payment processed successfully",
  "data": {
    "billNo": "B0001-25-26",
    "guestName": "John Doe",
    "originalAmount": 5000.00,
    "totalPaidAmount": 2000.00,
    "balanceAmount": 3000.00,
    "settlementStatus": "PARTIAL",
    "lastPaymentDate": "2025-09-14T15:30:00",
    "currentPaymentAmount": 2000.00,
    "modeOfPaymentName": "Cash Payment",
    "paymentNotes": "Payment: 2000.00 via CASH on 2025-09-14T15:30:00 (Partial payment - guest will return for balance)"
  },
  "timestamp": "2025-09-14T15:30:00"
}
```

**Check Bill Settlement Status:**
```http
GET /api/bills/{billNo}/settlement-status
```

**Get All Pending Bills:**
```http
GET /api/bills/pending-settlements
```

**Response:**
```json
{
  "success": true,
  "message": "Pending settlements retrieved successfully",
  "data": [
    {
      "billNo": "B0001-25-26",
      "guestName": "John Doe",
      "originalAmount": 5000.00,
      "totalPaidAmount": 2000.00,
      "balanceAmount": 3000.00,
      "settlementStatus": "PARTIAL",
      "lastPaymentDate": "2025-09-14T15:30:00"
    },
    {
      "billNo": "B0002-25-26",
      "guestName": "Jane Smith",
      "originalAmount": 3500.00,
      "totalPaidAmount": 0.00,
      "balanceAmount": 3500.00,
      "settlementStatus": "PENDING",
      "lastPaymentDate": null
    }
  ],
  "timestamp": "2025-09-14T15:30:00"
}
```

**Settlement Status Types:**
- **PENDING**: No payments received yet
- **PARTIAL**: Some payment received, balance remaining
- **SETTLED**: Full payment received

**Benefits:**
- ✅ **Guest Convenience**: Guests can pay when they have money
- ✅ **Revenue Tracking**: All outstanding amounts are tracked
- ✅ **Payment History**: Complete audit trail of all payments
- ✅ **Flexible Payments**: Multiple partial payments supported
- ✅ **Staff Efficiency**: Easy lookup by bill number
- ✅ **Audit Support**: Automatic payment notes and timestamps

## 🏠 Room Status Management (`/api/rooms`)

### **Automatic Room Status Updates**

The system automatically manages room status when departure dates arrive, making rooms available for new reservations without manual intervention.

**Scheduled Operations:**
- **Daily at 6:00 AM**: Process automatic room status updates for departure dates
- **Every 2 hours (8 AM - 8 PM)**: Check for overdue checkouts

### **Room Status Codes**
- **VR**: Vacant Ready (Available for new reservations)
- **OD**: Occupied Dirty (Guest checked in, room needs cleaning)
- **OI**: Occupied Inspected (Guest checked in, room cleaned) 
- **Blocked**: Room blocked for maintenance

### **Manual Room Status Endpoints**

**Update Room Status:**
```http
PUT /api/rooms/{roomId}/status/{status}
```

**Process Room Checkout:**
```http
POST /api/rooms/{roomId}/checkout?folioNo=F0001-25-26
```

**Check Room Availability:**
```http
GET /api/rooms/{roomId}/availability?arrivalDate=2025-09-20&departureDate=2025-09-25
```

**Response:**
```json
{
  "success": true,
  "message": "Room is available for the requested dates",
  "data": true,
  "timestamp": "2025-09-14T15:30:00"
}
```

**Trigger Automatic Update:**
```http
POST /api/rooms/status/automatic-update
```

**Response:**
```json
{
  "success": true,
  "message": "Automatic room status update completed: 3 successful, 0 failed",
  "data": {
    "processingDate": "2025-09-14",
    "totalCheckouts": 3,
    "successfulUpdates": 3,
    "failedUpdates": 0,
    "updatedRooms": [
      {
        "roomId": "R101",
        "roomNo": "101",
        "guestName": "John Doe",
        "notes": "Available for new booking"
      }
    ],
    "failedUpdatesList": []
  },
  "timestamp": "2025-09-14T15:30:00"
}
```

**Check Overdue Checkouts:**
```http
POST /api/rooms/status/overdue-check
```

### **Enhanced Reservation Availability**

**Check Room Availability for Reservations:**
```http
GET /api/reservations/check-availability?roomTypeId=DELUXE&arrivalDate=2025-09-20&departureDate=2025-09-25&requiredRooms=2
```

**Response:**
```json
{
  "success": true,
  "message": "Sufficient rooms available (2 requested for DELUXE)",
  "data": true,
  "timestamp": "2025-09-14T15:30:00"
}
```

**Get Available Rooms List:**
```http
GET /api/reservations/available-rooms?roomTypeId=DELUXE&arrivalDate=2025-09-20&departureDate=2025-09-25
```

**Response:**
```json
{
  "success": true,
  "message": "Found 3 available rooms for DELUXE between 2025-09-20 and 2025-09-25",
  "data": [
    {
      "roomId": "R201",
      "roomNo": "201",
      "floor": "2",
      "status": "VR",
      "roomTypeId": "DELUXE"
    },
    {
      "roomId": "R202",
      "roomNo": "202",
      "floor": "2",
      "status": "VR",
      "roomTypeId": "DELUXE"
    }
  ],
  "timestamp": "2025-09-14T15:30:00"
}
```

### **Business Benefits**
- ✅ **Automatic Operations**: Rooms become available without staff intervention
- ✅ **Overdue Tracking**: Identify guests who have overstayed
- ✅ **Real-time Availability**: Accurate room availability for reservations
- ✅ **Staff Efficiency**: Manual override options for special cases
- ✅ **Revenue Optimization**: Rooms available immediately after departure dates
- ✅ **Guest Experience**: Faster check-in process with pre-available rooms

### 11. Rooms (`/api/rooms`)

- `GET /` - All rooms with status
- `GET /available` - Available rooms  
- `GET /occupied` - Occupied rooms
- `GET /occupancy-stats` - Occupancy statistics
- `PUT /{roomId}/status/{status}` - Update room status

### 12. Operations (`/api/operations`)

**Audit Date Change:**
```http
POST /api/operations/audit-date-change
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
  "timestamp": "2025-09-14T11:30:00"
}
```

**Shift Change:**
```http
POST /api/operations/shift-change
Content-Type: application/json

{
  "shiftDate": "2025-09-14",
  "shiftNo": "1",
  "balance": 15000.00
}
```

### 13. User Types (`/api/user-types`)

The user types system has been enhanced to support dynamic roles from the database while maintaining backward compatibility with predefined roles.

**Key Features:**
- User types are now dynamically fetched from the database
- Supports both predefined roles (ADMIN, MANAGER, CASHIER, etc.) and custom roles
- Duplicate type names are prevented during creation
- All user types include userTypeId in API responses

**Get All User Types:**
```http
GET /api/user-types
```

**Response:**
```json
{
  "success": true,
  "message": "Operation successful",
  "data": [
    {
      "userTypeId": "ADMIN",
      "typeName": "Administrator",
      "roleCode": "ADMIN",
      "displayName": "Admin",
      "description": "System administrator with full access to all modules and configurations"
    },
    {
      "userTypeId": "MANAGER",
      "typeName": "Manager",
      "roleCode": "MANAGER",
      "displayName": "Manager",
      "description": "Manager with full oversight access, approval capabilities, and staff management"
    },
    {
      "userTypeId": "CASHIER",
      "typeName": "Cashier",
      "roleCode": "CASHIER",
      "displayName": "Cashier",
      "description": "Cashier with payment processing, financial reports, and settlement verification"
    }
  ],
  "timestamp": "2025-09-15T11:38:33.832037900"
}
```

**Create User Type:**
```http
POST /api/user-types
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
  "timestamp": "2025-09-14T11:30:00"
}
```

**Create User Type with Specific ID:**
```http
POST /api/user-types
Content-Type: application/json

{
  "userTypeId": "CUSTOM01",
  "typeName": "Custom Role Type"
}
```

**Duplicate Prevention:**
The system now prevents creating user types with duplicate names:
```json
{
  "success": false,
  "message": "Failed to create user type: User type name already exists: Administrator",
  "data": null,
  "timestamp": "2025-09-15T11:40:00"
}
```

**Create Common User Types:**
```http
POST /api/user-types
Content-Type: application/json

{
  "userTypeId": "ADMIN",
  "typeName": "Administrator"
}
```

```http
POST /api/user-types
Content-Type: application/json

{
  "userTypeId": "STAFF",
  "typeName": "Staff"
}
```

```http
POST /api/user-types
Content-Type: application/json

{
  "userTypeId": "MANAGER",
  "typeName": "Manager"
}
```

### 14. User Management (`/api/users`)

**User Login (Legacy - for backward compatibility):**
```http
POST /api/users/login
Content-Type: application/json

{
  "userName": "admin",
  "password": "admin123"
}
```

**JWT Login (Recommended):**
```http
POST /api/auth/login
Content-Type: application/json

{
  "userName": "admin",
  "password": "admin123"
}
```

**JWT Login Response:**
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
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiJVU1IwMDEiLCJ1c2VyTmFtZSI6ImFkbWluIiwidXNlclR5cGVJZCI6IlVUWVBFMDAxIiwic3ViIjoiYWRtaW4iLCJpYXQiOjE3MjYzMDMyMDAsImV4cCI6MTcyNjM4OTYwMH0.signature"
  },
  "timestamp": "2025-09-14T11:30:00"
}
```

### Role Management APIs
```http
GET /api/users/roles                     # Get available roles (now dynamic from database)
POST /api/users/create-with-role         # Create user with role
PUT /api/users/{userId}/role             # Update user role
GET /api/users/by-role/{role}            # Get users by role
GET /api/admin/dashboard                 # Admin dashboard
POST /api/admin/users/bulk-create        # Bulk create users
GET /api/user-types                      # Get all user types (dynamic from database)
POST /api/user-types                     # Create new user type
```

**Get Available Roles (Dynamic from Database):**
```http
GET /api/users/roles
Authorization: Bearer <token>
```

**Response:**
```json
{
  "success": true,
  "message": "Operation successful",
  "data": [
    {
      "roleCode": "MANAGER",
      "displayName": "Manager",
      "description": "Manager with full oversight access, approval capabilities, and staff management",
      "userTypeId": "MANAGER"
    },
    {
      "roleCode": "CASHIER",
      "displayName": "Cashier",
      "description": "Cashier with payment processing, financial reports, and settlement verification",
      "userTypeId": "CASHIER"
    },
    {
      "roleCode": "RECEPTIONIST",
      "displayName": "Receptionist",
      "description": "Receptionist with guest data management, check-in/out, and basic operations",
      "userTypeId": "RECEPTIONIST"
    },
    {
      "roleCode": "ADMIN",
      "displayName": "Admin",
      "description": "System administrator with full access to all modules and configurations",
      "userTypeId": "ADMIN"
    },
    {
      "roleCode": "HOUSEKEEPING",
      "displayName": "Housekeeping Staff",
      "description": "Housekeeping staff with room status management and cleaning task coordination",
      "userTypeId": "HOUSEKEEPING"
    }
  ],
  "timestamp": "2025-09-15T11:38:33.832037900"
}
```

**Create User with Role:**
```http
POST /api/users/create-with-role
Content-Type: application/json
Authorization: Bearer <token>

{
  "userName": "newmanager",
  "password": "newmanager123",
  "role": "MANAGER"
}
```

**Update User Role:**
```http
PUT /api/users/{userId}/role
Content-Type: application/json
Authorization: Bearer <token>

{
  "role": "CASHIER"
}
```

**Get Users by Role:**
```http
GET /api/users/by-role/MANAGER
Authorization: Bearer <token>
```


**JWT Logout:**
```http
POST /api/auth/logout
Authorization: Bearer <your-jwt-token>
```

**Legacy Login Response:**
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
  "timestamp": "2025-09-14T11:30:00"
}
```

**Create User:**
```http
POST /api/users
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

### 15. Tax Master (`/api/taxes`)

**Create Tax (Auto-generated ID):**
```http
POST /api/taxes
Content-Type: application/json

{
  "taxName": "CGST",
  "percentage": 9.0
}
```

**Create Tax (Custom ID):**
```http
POST /api/taxes
Content-Type: application/json

```

#### Housekeeping (`/api/housekeeping`)

**Get Housekeeping Tasks:**
```http
GET /api/housekeeping/tasks
Authorization: Bearer <token>

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

{
  "taxId": "CGST",
  "taxName": "Central Goods and Services Tax",
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
  "timestamp": "2025-09-14T11:30:00"
}
```

### 5. Reservation Management (`/api/reservations`)

**Create Reservation:**
```http
POST /api/reservations
Content-Type: application/json
Authorization: Bearer <token>

{
  "guestName": "John Smith",
  "companyId": "COMP001",
  "companyName": "ABC Corporation",
  "planId": "PLAN001",
  "planName": "Business Plan",
  "roomTypeId": "RTYPE001",
  "roomTypeName": "Deluxe Room",
  "arrivalDate": "2025-09-20",
  "departureDate": "2025-09-25",
  "noOfDays": 5,
  "noOfPersons": 2,
  "noOfRooms": 1,
  "mobileNumber": "+1234567890",
  "emailId": "john.smith@example.com",
  "rate": 150.00,
  "includingGst": "Y",
  "remarks": "Early check-in requested",
  "settlementTypeId": "CASH",
  "settlementTypeName": "Cash Payment",
  "arrivalModeId": "FLIGHT",
  "arrivalModeName": "Flight",
  "arrivalDetails": "Flight AA123, Terminal 2",
  "nationalityId": "IND",
  "nationalityName": "Indian",
  "refModeId": "ONLINE",
  "refModeName": "Online Booking",
  "resvSourceId": "DIRECT",
  "resvSourceName": "Direct Booking"
}
```

**Required Fields:**
- `guestName` (string) - Guest full name
- `arrivalDate` (date) - Check-in date (YYYY-MM-DD format)
- `departureDate` (date) - Check-out date (YYYY-MM-DD format) 
- `noOfDays` (integer) - Number of stay days
- `noOfPersons` (integer) - Number of guests
- `noOfRooms` (integer) - Number of rooms required
- `mobileNumber` (string) - Contact mobile number

**Optional Fields:**
- `companyId` (string) - Company ID for corporate bookings
- `companyName` (string) - Company name for corporate bookings
- `planId` (string) - Rate plan ID
- `planName` (string) - Rate plan name
- `roomTypeId` (string) - Preferred room type ID
- `roomTypeName` (string) - Preferred room type name
- `emailId` (string) - Guest email address
- `rate` (decimal) - Room rate per night
- `includingGst` (string) - "Y" or "N" (default: "N")
- `remarks` (string) - Additional notes
- `settlementTypeId` (string) - Settlement type ID
- `settlementTypeName` (string) - Settlement type name
- `arrivalModeId` (string) - Arrival mode ID
- `arrivalModeName` (string) - Arrival mode name
- `arrivalDetails` (string) - Arrival details (flight number, train details, etc.)
- `nationalityId` (string) - Nationality ID
- `nationalityName` (string) - Nationality name
- `refModeId` (string) - Reference mode ID
- `refModeName` (string) - Reference mode name
- `resvSourceId` (string) - Reservation source ID
- `resvSourceName` (string) - Reservation source name

**Create Reservation Response:**
```json
{
  "success": true,
  "message": "Reservation created successfully",
  "data": {
    "reservationNo": "3-25-26",
    "guestName": "John Smith",
    "companyId": "COMP001",
    "companyName": "ABC Corporation",
    "planId": "PLAN001",
    "planName": "Business Plan",
    "roomTypeId": "RTYPE001",
    "roomTypeName": "Deluxe Room",
    "arrivalDate": "2025-09-20",
    "departureDate": "2025-09-25",
    "noOfDays": 5,
    "noOfPersons": 2,
    "noOfRooms": 1,
    "mobileNumber": "+1234567890",
    "emailId": "john.smith@example.com",
    "rate": 150.00,
    "includingGst": "Y",
    "remarks": "Early check-in requested",
    "roomsCheckedIn": 0,
    "createdAt": "2025-09-15T10:30:00",
    "updatedAt": "2025-09-15T10:30:00",
    "settlementTypeId": "CASH",
    "settlementTypeName": "Cash Payment",
    "arrivalModeId": "FLIGHT",
    "arrivalModeName": "Flight",
    "arrivalDetails": "Flight AA123, Terminal 2",
    "nationalityId": "IND",
    "nationalityName": "Indian",
    "refModeId": "ONLINE",
    "refModeName": "Online Booking",
    "resvSourceId": "DIRECT",
    "resvSourceName": "Direct Booking"
  },
  "timestamp": "2025-09-15T11:38:33.832037900"
}
```

**Other Endpoints:**
- `GET /{reservationNo}` - Get reservation
- `PUT /{reservationNo}` - Update reservation
- `GET /search?searchTerm=John` - Search reservations
- `GET /arrivals/{date}` - Expected arrivals
- `GET /departures/{date}` - Expected departures

**Get All Reservations:**
```http
GET /api/reservations
```

**Response:**
```json
{
  "success": true,
  "message": "Operation successful",
  "data": [
    {
      "reservationNo": "1-25-26",
      "guestName": "John Smith",
      "companyId": "COMP001",
      "companyName": "ABC Corporation",
      "planId": "PLAN001",
      "planName": "Business Plan",
      "roomTypeId": "RTYPE001",
      "roomTypeName": "Deluxe Room",
      "arrivalDate": "2025-09-20",
      "departureDate": "2025-09-25",
      "noOfDays": 5,
      "noOfPersons": 2,
      "noOfRooms": 1,
      "mobileNumber": "+1234567890",
      "emailId": "john.smith@example.com",
      "rate": 150.00,
      "includingGst": "Y",
      "remarks": "Early check-in requested",
      "roomsCheckedIn": 0,
      "createdAt": "2025-09-15T10:30:00",
      "updatedAt": "2025-09-15T10:30:00",
      "settlementTypeId": "CASH",
      "settlementTypeName": "Cash Payment",
      "arrivalModeId": "FLIGHT",
      "arrivalModeName": "Flight",
      "arrivalDetails": "Flight AA123, Terminal 2",
      "nationalityId": "IND",
      "nationalityName": "Indian",
      "refModeId": "ONLINE",
      "refModeName": "Online Booking",
      "resvSourceId": "DIRECT",
      "resvSourceName": "Direct Booking"
    },
    {
      "reservationNo": "2-25-26",
      "guestName": "Jane Doe",
      "companyId": null,
      "companyName": null,
      "planId": null,
      "planName": null,
      "roomTypeId": "RTYPE002",
      "roomTypeName": "Standard Room",
      "arrivalDate": "2025-09-22",
      "departureDate": "2025-09-24",
      "noOfDays": 2,
      "noOfPersons": 1,
      "noOfRooms": 1,
      "mobileNumber": "+9876543210",
      "emailId": "jane.doe@example.com",
      "rate": 100.00,
      "includingGst": "N",
      "remarks": "Late checkout requested",
      "roomsCheckedIn": 1,
      "createdAt": "2025-09-10T14:15:00",
      "updatedAt": "2025-09-15T09:45:00",
      "settlementTypeId": null,
      "settlementTypeName": null,
      "arrivalModeId": null,
      "arrivalModeName": null,
      "arrivalDetails": null,
      "nationalityId": null,
      "nationalityName": null,
      "refModeId": null,
      "refModeName": null,
      "resvSourceId": null,
      "resvSourceName": null
    }
  ],
  "timestamp": "2025-09-15T11:38:33.832037900"
}
```

**Search Reservations:**
```http
GET /api/reservations/search?searchTerm=John
Authorization: Bearer <token>
```

**Response:**
```json
{
  "success": true,
  "message": "Operation successful",
  "data": [
    {
      "reservationNo": "1-25-26",
      "guestName": "John Smith",
      "companyId": "COMP001",
      "companyName": "ABC Corporation",
      "planId": "PLAN001",
      "planName": "Business Plan",
      "roomTypeId": "RTYPE001",
      "roomTypeName": "Deluxe Room",
      "arrivalDate": "2025-09-20",
      "departureDate": "2025-09-25",
      "noOfDays": 5,
      "noOfPersons": 2,
      "noOfRooms": 1,
      "mobileNumber": "+1234567890",
      "emailId": "john.smith@example.com",
      "rate": 150.00,
      "includingGst": "Y",
      "remarks": "Early check-in requested",
      "roomsCheckedIn": 0,
      "createdAt": "2025-09-15T10:30:00",
      "updatedAt": "2025-09-15T10:30:00",
      "settlementTypeId": "CASH",
      "settlementTypeName": "Cash Payment",
      "arrivalModeId": "FLIGHT",
      "arrivalModeName": "Flight",
      "arrivalDetails": "Flight AA123, Terminal 2",
      "nationalityId": "IND",
      "nationalityName": "Indian",
      "refModeId": "ONLINE",
      "refModeName": "Online Booking",
      "resvSourceId": "DIRECT",
      "resvSourceName": "Direct Booking"
    }
  ],
  "timestamp": "2025-09-15T11:38:33.832037900"
}
```

**Search Functionality Details:**
The search endpoint allows you to search reservations by multiple criteria:
- **Guest Name**: Searches for reservations where the guest name contains the search term
- **Mobile Number**: Searches for reservations with the specified mobile number
- **Reservation Number**: Searches for reservations with the exact reservation number

**Example Search Queries:**
1. Search by guest name: `/api/reservations/search?searchTerm=Smith`
2. Search by mobile number: `/api/reservations/search?searchTerm=+1234567890`
3. Search by reservation number: `/api/reservations/search?searchTerm=1-25-26`

**Expected Arrivals:**
```http
GET /api/reservations/arrivals/2025-09-20
Authorization: Bearer <token>
```

**Expected Departures:**
```http
GET /api/reservations/departures/2025-09-25
Authorization: Bearer <token>
```

**Pending Check-ins:**
```http
GET /api/reservations/pending-checkins
Authorization: Bearer <token>
```



```

### 2. Check-ins (`/api/checkins`)
**Get All Taxes:**
```http
GET /api/taxes
```

**Get Tax by ID:**
```http
GET /api/taxes/{taxId}
```

**Update Tax:**
```http
PUT /api/taxes/{taxId}
Content-Type: application/json

{
  "taxName": "CGST Updated",
  "percentage": 9.5
}
```

**Delete Tax:**
```http
DELETE /api/taxes/{taxId}
```

### 16. Account Heads (`/api/account-heads`)

**Create Account Head (Auto-generated ID):**
```http
POST /api/account-heads
Content-Type: application/json

{
  "accountName": "Restaurant"
}
```

**Create Account Head (Custom ID):**
```http
POST /api/account-heads
Content-Type: application/json

{
  "accountHeadId": "RESTAURANT",
  "accountName": "Restaurant Services"
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
  "timestamp": "2025-09-14T11:30:00"
}
```

**Get All Account Heads:**
```http
GET /api/account-heads
```

**Get Account Head by ID:**
```http
GET /api/account-heads/{accountHeadId}
```

**Update Account Head:**
```http
PUT /api/account-heads/{accountHeadId}
Content-Type: application/json

{
  "accountName": "Restaurant Services Updated"
}
```

**Delete Account Head:**
```http
DELETE /api/account-heads/{accountHeadId}
```

### 17. Nationalities (`/api/nationalities`)

**Create Nationality (Auto-generated ID):**
```http
POST /api/nationalities
Content-Type: application/json

{
  "nationality": "Indian"
}
```

**Create Nationality (Custom ID):**
```http
POST /api/nationalities
Content-Type: application/json

{
  "id": "IND",
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
  "timestamp": "2025-09-14T11:30:00"
}
```

**Get All Nationalities:**
```http
GET /api/nationalities
```

**Get Nationality by ID:**
```http
GET /api/nationalities/{id}
```

**Update Nationality:**
```

```http
PUT /api/nationalities/{id}
Content-Type: application/json

{
  "nationality": "Indian Updated"
}
```

**Delete Nationality:**
```http
DELETE /api/nationalities/{id}
```

### 18. Reference Modes (`/api/ref-modes`)

**Create Reference Mode (Auto-generated ID):**
```http
POST /api/ref-modes
Content-Type: application/json

{
  "refMode": "Online Booking"
}
```

**Create Reference Mode (Custom ID):**
```http
POST /api/ref-modes
Content-Type: application/json

{
  "id": "ONLINE",
  "refMode": "Online Booking Platform"
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
  "timestamp": "2025-09-14T11:30:00"
}
```

**Get All Reference Modes:**
```http
GET /api/ref-modes
```

**Get Reference Mode by ID:**
```http
GET /api/ref-modes/{id}
```

**Update Reference Mode:**
```http
PUT /api/ref-modes/{id}
Content-Type: application/json

{
  "refMode": "Online Booking Updated"
}
```

**Delete Reference Mode:**
```http
DELETE /api/ref-modes/{id}
```

### 19. Arrival Modes (`/api/arrival-modes`)

**Create Arrival Mode (Auto-generated ID):**
```http
POST /api/arrival-modes
Content-Type: application/json

{
  "arrivalMode": "Flight"
}
```

**Create Arrival Mode (Custom ID):**
```http
POST /api/arrival-modes
Content-Type: application/json

{
  "id": "FLIGHT",
  "arrivalMode": "Commercial Flight"
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
  "timestamp": "2025-09-14T11:30:00"
}
```

**Get All Arrival Modes:**
```http
GET /api/arrival-modes
```

**Get Arrival Mode by ID:**
```http
GET /api/arrival-modes/{id}
```

**Update Arrival Mode:**
```http
PUT /api/arrival-modes/{id}
Content-Type: application/json

{
  "arrivalMode": "Flight Updated"
}
```

**Delete Arrival Mode:**
```http
DELETE /api/arrival-modes/{id}
```

### 20. Reservation Sources (`/api/reservation-sources`)

**Create Reservation Source (Auto-generated ID):**
```http
POST /api/reservation-sources
Content-Type: application/json

{
  "resvSource": "Direct Booking"
}
```

**Create Reservation Source (Custom ID):**
```http
POST /api/reservation-sources
Content-Type: application/json

{
  "id": "DIRECT",
  "resvSource": "Direct Hotel Booking"
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
  "timestamp": "2025-09-14T11:30:00"
}
```

**Get All Reservation Sources:**
```http
GET /api/reservation-sources
```

**Get Reservation Source by ID:**
```http
GET /api/reservation-sources/{id}
```

**Update Reservation Source:**
```http
PUT /api/reservation-sources/{id}
Content-Type: application/json

{
  "resvSource": "Direct Booking Updated"
}
```

**Delete Reservation Source:**
```http
DELETE /api/reservation-sources/{id}
```

## Room Status Codes
- **VR**: Vacant Ready (Available)
- **OD**: Occupied Dirty (Guest checked in)
- **OI**: Occupied Inspected (Cleaned)
- **Blocked**: Maintenance

## Response Format
```json
{
  "success": true,
  "message": "Operation successful",
  "data": {...},
  "timestamp": "2025-09-14T14:30:00"
}
```

**Error Response Format:**
```json
{
  "success": false,
  "message": "Error description",
  "data": null,
  "timestamp": "2025-09-14T14:30:00"
}
```
  "timestamp": "2024-01-15T14:30:00"
}
```

## Error Codes
- 200: Success
- 201: Created
- 400: Bad Request
- 401: Unauthorized
- 404: Not Found
- 500: Server Error

## Detailed JSON Examples

### Complete Reservation Registration Flow

#### 1. Create Room Type First
```json
POST /api/room-types
{
  "typeName": "Deluxe Room", 
  "noOfRooms": 15
}
```

#### 2. Create Payment Modes
```json
POST /api/payment-modes
{
  "id": "CASH",
  "name": "Cash Payment"
}
```

```json
POST /api/payment-modes
{
  "id": "CARD",
  "name": "Credit/Debit Card"
}
```

```json
POST /api/payment-modes
{
  "id": "UPI",
  "name": "UPI Payment"
}
```

#### 3. Create Companies
```json
POST /api/companies
{
  "companyName": "TechCorp Solutions",
  "address1": "456 Technology Park",
  "address2": "Innovation District",
  "address3": "Tech City",
  "gstNumber": "24TECHC5678G2A1"
}
```

```json
POST /api/companies
{
  "companyName": "Business Partners Ltd",
  "address1": "789 Corporate Avenue",
  "address2": "Business District",
  "address3": "Commerce City",
  "gstNumber": "27BIZPA9012H3B2"
}
```

#### 4. Create Plan Types
```json
POST /api/plan-types
{
  "planName": "Corporate Plan",
  "discountPercentage": 15.00
}
```

```json
POST /api/plan-types
{
  "planName": "Weekend Special",
  "discountPercentage": 10.00
}
```

```json
POST /api/plan-types
{
  "planName": "Regular Plan",
  "discountPercentage": 0.00
}
```

#### 5. Create Rooms
```json
POST /api/rooms
{
  "roomNo": "201",
  "floor": "2", 
  "status": "VR",
  "roomTypeId": "DELUXE"
}
```

#### 6. Create Reservation
```json
POST /api/reservations
{
  "guestName": "Alice Johnson",
  "companyId": "COMP001",
  "planId": "PLAN001",
  "roomTypeId": "DELUXE",
  "arrivalDate": "2025-09-20",
  "departureDate": "2025-09-25",
  "noOfDays": 5,
  "noOfPersons": 2,
  "noOfRooms": 1,
  "mobileNumber": "+91-9876543210",
  "emailId": "alice.johnson@company.com",
  "rate": 8000.00,
  "includingGst": "Y",
  "remarks": "VIP guest - late checkout requested"
}
```

#### 7. Create Advance Payment
```json
POST /api/advances/reservation
{
  "reservationNo": "0002/24-25",
  "guestName": "Alice Johnson",
  "modeOfPaymentId": "CARD",
  "amount": 15000.00,
  "remarks": "50% advance payment"
}
```

#### 8. Process Check-in
```json
POST /api/checkins
{
  "reservationNo": "0002/24-25",
  "guestName": "Alice Johnson",
  "roomId": "R201",
  "arrivalDate": "2025-09-20",
  "departureDate": "2025-09-25",
  "mobileNumber": "+91-9876543210",
  "emailId": "alice.johnson@company.com",
  "rate": 8000.00,
  "walkIn": "N",
  "remarks": "Checked in at 2:00 PM"
}
```

### Validation Rules

#### Reservation Validation
- **Date Validation**: `arrivalDate` cannot be in the past
- **Date Logic**: `departureDate` must be after `arrivalDate`
- **Required Fields**: `guestName`, `arrivalDate`, `departureDate`, `noOfDays`, `noOfPersons`, `noOfRooms`, `mobileNumber`
- **GST Flag**: `includingGst` accepts only "Y" or "N" (default: "N")
- **Phone Format**: `mobileNumber` should include country code

#### Room Validation
- **Room ID**: Optional - auto-generated if not provided (format: ROOM####)
- **Room Number**: Required - physical room number
- **Floor**: Required - floor location
- **Status**: Required - room status (VR, OD, OI, Blocked)
- **Room Type ID**: Optional - must exist in room_type table if provided
- **Auto-generation**: If roomId is not provided or empty, system generates unique ID

#### Company Validation
- **Type ID**: Optional - auto-generated if not provided (format: TYPE####)
- **Type Name**: Required - unique room type description
- **Number of Rooms**: Required - must be at least 1
- **Auto-generation**: If typeId is not provided or empty, system generates unique ID

#### Plan Type Validation
- **Plan ID**: Optional - auto-generated if not provided (format: PLAN####)
- **Plan Name**: Required - unique plan description
- **Discount Percentage**: Optional - must be between 0.0 and 100.0
- **Auto-generation**: If planId is not provided or empty, system generates unique ID

#### Company Validation
- **Company ID**: Optional - auto-generated if not provided (format: COMP####)
- **Company Name**: Required - unique company name
- **Address Fields**: Address1 is required, Address2 and Address3 are optional
- **GST Number**: Optional - should follow GST format if provided
- **Auto-generation**: If companyId is not provided or empty, system generates unique ID

#### Check-in Validation
- **Room Availability**: Room must have status "VR" (Vacant Ready)
- **Room Occupancy**: Room cannot be already occupied
- **Reservation Reference**: For non-walk-ins, `reservationNo` must exist

### Common Error Responses

```json
{
  "success": false,
  "message": "Validation failed: Arrival date cannot be in the past",
  "data": null,
  "timestamp": "2025-09-14T10:30:00"
}
```

```json
{
  "success": false,
  "message": "Room type not found: PREMIUM",
  "data": null,
  "timestamp": "2025-09-14T10:30:00"
}
```

```json
{
  "success": false,
  "message": "Room is not available for check-in: R101",
  "data": null,
  "timestamp": "2025-09-14T10:30:00"
}
```

## 📋 Complete CRUD Operations Summary

### ✅ **Entities with Full CRUD (Create, Read, Update, Delete):**
- **Room Types** - POST, GET, PUT, DELETE
- **Rooms** - POST, GET, PUT (details + status), DELETE (status changes)
- **Companies** - POST, GET, PUT, DELETE
- **Payment Modes** - POST, GET, PUT, DELETE
- **Plan Types** - POST, GET, PUT, DELETE
- **Account Heads** - POST, GET, PUT, DELETE
- **Tax Master** - POST, GET, PUT, DELETE
- **Nationalities** - POST, GET, PUT, DELETE
- **Reference Modes** - POST, GET, PUT, DELETE
- **Arrival Modes** - POST, GET, PUT, DELETE
- **Reservation Sources** - POST, GET, PUT, DELETE
- **User Types** - POST, GET, PUT, DELETE
- **Users** - POST, GET, PUT (profile + rights), DELETE
- **Advances** - POST, GET, PUT, DELETE
- **Post Transactions** - POST, GET, PUT, DELETE
- **Reservations** - POST, GET, PUT
- **Check-ins** - POST, GET, PUT

### ✅ **Entities with Specialized Operations:**
- **Bills** - POST (generate), GET, settlement operations
- **Room Status** - Automatic updates, manual changes, availability checks
- **User Authentication** - Login, logout, JWT token management
- **Operations** - Audit date change, shift management

### 🎯 **Update Capabilities Added:**

#### **Room Management Updates:**
- `PUT /api/rooms/{roomId}` - Update room details (number, floor, type)
- `PUT /api/rooms/{roomId}/status/{status}` - Update room status only
- Enhanced validation prevents status changes for occupied rooms

#### **Advance Management Updates:**
- `PUT /api/advances/{advanceId}` - Update advance details
- `DELETE /api/advances/{advanceId}` - Remove advance payments
- Individual advance retrieval by ID

#### **Transaction Management Updates:**
- `PUT /api/transactions/{transactionId}` - Update transaction details
- `DELETE /api/transactions/{transactionId}` - Remove transactions
- Individual transaction retrieval by ID

#### **Check-in Management Updates:**
- `PUT /api/checkins/{folioNo}` - Update check-in details
- Supports departure date changes, rate updates, contact information

#### **User Profile Updates:**
- `PUT /api/users/{userId}` - Update user profile (username, type, password)
- `PUT /api/users/{userId}/rights` - Update user permissions separately
- Comprehensive user management with rights isolation

### ✅ **Core Hotel Operations**
- **Reservations**: Complete booking management with multi-room support
- **Check-ins**: Walk-in and reservation-based check-ins
- **Advances**: Payment handling for reservations, in-house, and checkout guests
- **Post Transactions**: Guest expense management
- **Billing**: Comprehensive bill generation and management
- **Room Management**: Real-time room status and occupancy tracking

### ✅ **Administrative Functions**
- **Audit Date Change**: Automated room charge and tax posting
- **Shift Management**: Shift balance tracking and updates
- **User Rights Management**: Role-based access control with module permissions
- **Master Data Management**: Complete CRUD for all lookup tables

### ✅ **Master Data Entities**
- **Tax Master**: Tax configuration and management
- **Account Heads**: Transaction categorization
- **Room Types**: Room category management
- **Payment Modes**: Payment method configuration
- **Plan Types**: Rate plan management
- **Companies**: Corporate client management
- **Nationalities**: Guest nationality options
- **Reference Modes**: Guest acquisition tracking
- **Arrival Modes**: Guest arrival method tracking
- **Reservation Sources**: Booking source management

## Complete API Endpoint Summary

### Core Reservation & Guest Management
| Controller | Base Path | Purpose | Endpoints Count |
|------------|-----------|---------|----------------|
| **ReservationController** | `/api/reservations` | Manage guest reservations | 6 |
| **CheckInController** | `/api/checkins` | Process check-ins and manage in-house guests | 5 |
| **AdvanceController** | `/api/advances` | Handle advance payments | 7 |
| **PostTransactionController** | `/api/transactions` | Manage guest transactions and charges | 5 |
| **BillController** | `/api/bills` | Generate and manage bills with split functionality | 9 |

### Room & Property Management
| Controller | Base Path | Purpose | Endpoints Count |
|------------|-----------|---------|----------------|
| **RoomController** | `/api/rooms` | Manage rooms and availability | 6 |
| **RoomTypeController** | `/api/room-types` | Define room categories | 5 |

### Master Data Management
| Controller | Base Path | Purpose | Endpoints Count |
|------------|-----------|---------|----------------|
| **CompanyController** | `/api/companies` | Manage corporate clients | 5 |
| **PlanTypeController** | `/api/plan-types` | Manage rate plans and discounts | 5 |
| **PaymentModeController** | `/api/payment-modes` | Define payment methods | 5 |
| **TaxationController** | `/api/taxes` | Configure tax rates | 5 |
| **AccountHeadController** | `/api/account-heads` | Manage expense categories | 5 |
| **NationalityController** | `/api/nationalities` | Manage guest nationalities | 5 |
| **RefModeController** | `/api/ref-modes` | Define reference modes | 5 |
| **ArrivalModeController** | `/api/arrival-modes` | Manage transportation modes | 5 |
| **ResvSourceController** | `/api/reservation-sources` | Define booking sources | 5 |

### System Management
| Controller | Base Path | Purpose | Endpoints Count |
|------------|-----------|---------|----------------|
| **UserManagementController** | `/api/users` & `/api/auth` | User authentication with JWT | 4 |
| **UserTypeController** | `/api/user-types` | Manage user roles | 6 |
| **OperationsController** | `/api/operations` | System operations (audit, shift) | 2 |

### Total API Endpoints: **108+**

## API Features Summary

### 🔐 **Authentication & Security**
- **JWT Token-based Authentication**: Secure login with 24-hour token validity
- **Session Management**: Token blacklisting on logout for security
- **User Rights Management**: Role-based access control with module permissions
- **Password Security**: Encrypted password storage

### 🏨 **Core Hotel Operations**
- **Reservation Management**: Create, update, search reservations with arrival/departure tracking
- **Check-in/Check-out**: Support for both walk-in and reservation-based check-ins
- **Guest Folio Management**: Complete transaction tracking per guest stay
- **Advance Payments**: Multi-stage advance collection (reservation, in-house, checkout)
- **Bill Generation**: Automated bill creation with transaction consolidation
- **Split Bills**: Advanced bill splitting with transaction selection

### 💰 **Financial Management**
- **Multiple Payment Modes**: Cash, Card, UPI, and custom payment methods
- **Tax Management**: Configurable tax rates and calculations
- **Account Head Tracking**: Categorized expense and revenue tracking
- **Audit Trail**: Complete transaction history with date tracking

### 🏢 **Property Management**
- **Room Inventory**: Real-time room status and availability tracking
- **Room Types**: Flexible room categorization with capacity management
- **Occupancy Reports**: In-house guest tracking and statistics
- **Status Management**: VR (Vacant Ready), OD (Occupied Dirty), etc.

### 📊 **Business Intelligence**
- **Search Capabilities**: Global search across reservations, guests, and bills
- **Date-based Reports**: Arrivals, departures, and expected checkouts
- **Financial Summaries**: Advance totals, transaction summaries
- **Relationship Tracking**: Split bill relationships and audit trails

### 🎯 **Master Data Management**
- **Corporate Clients**: Company profile management with GST details
- **Rate Plans**: Flexible pricing with discount percentages
- **Guest Profiles**: Nationality, reference modes, arrival methods
- **System Configuration**: User types, reservation sources, operational settings

### 🔄 **System Operations**
- **Audit Date Change**: Automated posting of room charges and taxes
- **Shift Management**: Shift balance tracking and handover
- **Number Generation**: Consistent ID formatting across all entities
- **Data Validation**: Comprehensive input validation and error handling

### ✅ **System Features**
- **Auto-ID Generation**: All entities support automatic unique ID generation
- **Accounting Year Format**: Transaction numbers follow `####/YY-YY` format
- **Data Validation**: Comprehensive input validation and referential integrity
- **Response Population**: Related entity details automatically populated in responses
- **Error Handling**: Consistent error response format across all endpoints
- **JWT Security**: Token-based authentication with session management
- **Split Bill Management**: Advanced bill splitting with sequential numbering (S1, S2, S3)
- **URL-Safe Identifiers**: Hyphen-based numbering to avoid routing conflicts

### 📋 **Standard Response Format**
All endpoints follow consistent ApiResponse wrapper:
```json
{
  "success": true|false,
  "message": "Descriptive message",
  "data": { /* Actual response data */ },
  "timestamp": "ISO datetime string"
}
```

### 🚀 **Performance Features**
- **Efficient Queries**: Optimized database access patterns
- **Relationship Loading**: Smart loading of related entities
- **Pagination Support**: Built-in for large datasets
- **Search Optimization**: Fast text-based search across multiple fields

---

## 🆕 Latest Features & Updates

### **Enhanced Role-Based Access Control (RBAC)**
Implemented comprehensive user role management with dynamic role support from database:

| Role | Code | Primary Functions | Key Permissions |
|------|------|-------------------|----------------|
| **Admin** | ADMIN | System administration | Full access to all operations, user management |
| **Manager** | MANAGER | Hotel operations oversight | View all reports, approve operations, staff management |
| **Accountant** | ACCOUNTANT | Financial management | Payment processing, bill settlements, financial reports |
| **Receptionist** | RECEPTIONIST | Front desk operations | Reservations, check-ins, guest services |
| **Housekeeping** | HOUSEKEEPING | Room operations | Room status updates, cleaning task management |

**Enhanced RBAC Features:**
- **Dynamic Role Management**: User roles are now dynamically fetched from the database
- **Duplicate Prevention**: System prevents creating user types with duplicate names
- **Enhanced Role Information**: API responses now include userTypeId for all roles
- **Backward Compatibility**: Maintains compatibility with existing enum-based roles

**RBAC Endpoints:**
```http
GET    /api/users/roles                     # Get available roles (dynamic from database)
POST   /api/users/create-with-role          # Create user with specific role
PUT    /api/users/{userId}/role             # Update user role
GET    /api/users/by-role/{role}            # Get users by role
GET    /api/users/{userId}/permissions/{module} # Check user permissions
GET    /api/user-types                      # Get all user types (dynamic from database)
```

### **Advanced Bill Management**

#### **Deferred Payment Support**
Guests can now receive bills and pay later, supporting real-world hotel scenarios:

```http
POST   /api/bills/{billNo}/payment          # Process payment against bill number
GET    /api/bills/{billNo}/settlement-status # Check payment status
GET    /api/bills/pending-settlements       # Get all pending payments
PUT    /api/bills/{billNo}                  # Update bill information
```

**Settlement Status Types:**
- `PENDING`: No payments received
- `PARTIAL`: Some payment received, balance remaining  
- `SETTLED`: Full payment completed

#### **Enhanced Split Bill Functionality**
Advanced bill splitting with transaction selection and sequential numbering:

```http
GET    /api/bills/{billNo}/split/preview    # Preview split options
POST   /api/bills/{billNo}/split            # Execute split with selection
GET    /api/bills/{billNo}/related          # Get all related bills
```

**Split Bill Numbering:**
- Original: `B3906-25-26`
- Split 1: `B3906-25-26-S1`
- Split 2: `B3906-25-26-S2`
- URL-safe hyphen format prevents routing conflicts

### **Automatic Room Status Management**

#### **Scheduled Operations**
Automatic room status updates without manual intervention:

- **Daily 6:00 AM**: Process departure date room updates
- **Every 2 hours (8 AM - 8 PM)**: Check for overdue checkouts

#### **Smart Room Availability**
Real-time room availability checking for reservations:

```http
GET    /api/reservations/check-availability # Check room availability
GET    /api/reservations/available-rooms    # Get available rooms list
GET    /api/rooms/{roomId}/availability     # Check specific room availability
```

#### **Manual Override Operations**
```http
POST   /api/rooms/status/automatic-update   # Trigger manual update
POST   /api/rooms/status/overdue-check      # Check overdue guests
POST   /api/rooms/{roomId}/checkout         # Manual room checkout
```

### **Enhanced Authentication System**

#### **JWT Token Authentication (Recommended)**
```http
POST   /api/auth/login                      # JWT-based login
POST   /api/auth/logout                     # Secure JWT logout
```

**Features:**
- 24-hour token validity
- Token blacklisting on logout
- Stateless authentication
- Role-based access control integration

#### **Legacy Authentication (Backward Compatible)**
```http
POST   /api/users/login                     # Legacy login endpoint
POST   /api/users/logout                    # Legacy logout endpoint
```

### **Operations Management**

#### **Audit Date Change Process**
Automated posting of charges for all in-house guests:

```http
POST   /api/operations/audit-date-change
```

**Automated Processing:**
- Posts room charges for all in-house guests
- Calculates and posts CGST (9%) and SGST (9%)
- Updates system audit date
- Provides detailed processing report

#### **Shift Management**
Comprehensive shift balance tracking:

```http
POST   /api/operations/shift-change
```

**Features:**
- Track shift balances by date and shift number
- Update existing shifts or create new records
- Maintain audit trail of shift handovers

### **Admin Dashboard & Management**

#### **Administrative Endpoints**
```http
GET    /api/admin/dashboard                 # Admin dashboard with statistics
POST   /api/admin/users/bulk-create         # Bulk user creation
GET    /api/admin/system-status             # System health information
```

**Dashboard Features:**
- Total user count and role distribution
- System health status
- User statistics by role
- Available roles listing

### **Housekeeping Integration**

#### **Future Housekeeping Features**
```http
GET    /api/housekeeping/tasks              # Get housekeeping tasks
POST   /api/housekeeping/tasks              # Create tasks
PUT    /api/housekeeping/tasks/{taskId}     # Update task status
POST   /api/housekeeping/room-status        # Update room cleaning status
```

*Note: Housekeeping endpoints are prepared for future implementation*

### **Enhanced Search & Navigation**

#### **Global Search Capabilities**
Expanded search across all major entities:

```http
GET    /api/reservations/search?searchTerm=John
GET    /api/checkins/search?searchTerm=Doe
GET    /api/bills/search?searchTerm=B001
```

#### **Date-based Operations**
```http
GET    /api/reservations/arrivals/{date}    # Expected arrivals
GET    /api/reservations/departures/{date}  # Expected departures
GET    /api/checkins/checkouts/{date}       # Expected checkouts
```

### **Complete CRUD Operations**

#### **Universal Update Support**
All entities now support full CRUD operations:

- **Create**: POST endpoints for all entities
- **Read**: GET endpoints with search and filtering
- **Update**: PUT endpoints for all modifiable entities
- **Delete**: DELETE endpoints where appropriate

#### **Master Data Management**
Complete CRUD for all lookup entities:

```http
# Account Heads
POST/GET/PUT/DELETE /api/account-heads

# Nationalities  
POST/GET/PUT/DELETE /api/nationalities

# Tax Management
POST/GET/PUT/DELETE /api/taxes

# Reference Modes
POST/GET/PUT/DELETE /api/ref-modes

# Arrival Modes
POST/GET/PUT/DELETE /api/arrival-modes

# Reservation Sources
POST/GET/PUT/DELETE /api/reservation-sources
```

### **System Improvements**

#### **Enhanced Data Validation**
- Comprehensive input validation across all endpoints
- Referential integrity checking
- Business rule validation
- Detailed error messages

#### **Response Population**
- Automatic population of related entity details
- Consistent response formats
- Related data inclusion (room numbers, company names, etc.)

#### **Number Generation**
- Accounting year format: `1/25-26` (sequential numbering)
- Consistent across all entities
- Sequential numbering within fiscal years (1, 2, 3...)
- No zero-padding for main entities (reservations, bills, folios)

#### **URL-Safe Design**
- Hyphen-based bill numbering (`B001-25-26-S1`)
- No forward slashes in URL parameters
- Consistent endpoint structure

---

## 📋 Quick Reference

### **Essential Endpoints**
```http
# Authentication
POST   /api/auth/login                      # JWT Login (Recommended)
POST   /api/auth/logout                     # JWT Logout

# Core Operations
POST   /api/reservations                    # Create reservation
POST   /api/checkins                        # Process check-in
POST   /api/transactions/inhouse            # Add guest charges
POST   /api/bills/generate/{folioNo}        # Generate bill

# Room Management
GET    /api/rooms                           # Get all rooms with status
PUT    /api/rooms/{roomId}/status/{status}  # Update room status



# Financial Operations
POST   /api/advances/inhouse                # Process advance payment
POST   /api/bills/{billNo}/payment          # Process bill payment
GET    /api/bills/pending-settlements       # Get pending payments

# Administrative
GET    /api/admin/dashboard                 # Admin dashboard
POST   /api/operations/audit-date-change    # Audit date change
```

### **Role Access Quick Reference**
| Endpoint Category | Admin | Manager | Accountant | Receptionist | Housekeeping |
|-------------------|-------|---------|------------|--------------|-------------|
| **Reservations** | FULL | READ | READ | FULL | NONE |
| **Check-ins** | FULL | READ | READ | FULL | READ |
| **Bills & Payments** | FULL | READ | FULL | WRITE | NONE |
| **Room Status** | FULL | WRITE | READ | WRITE | FULL |
| **User Management** | FULL | WRITE | NONE | NONE | NONE |
| **Reports** | FULL | FULL | FULL | READ | READ |

---

*API Documentation Version 2.0.0 - Complete Hotel Management System with Advanced Features*