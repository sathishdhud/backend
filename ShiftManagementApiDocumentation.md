# Shift Management API Documentation

This document provides detailed information about the Shift Management API endpoints, including request/response examples with shift rotation logic.

## Base URL
```
http://localhost:8080/api/operations
```

## Endpoints

### 1. Close Current Shift

**Endpoint:** `POST /shift-close`

**Description:** Close the current shift and handle shift rotation logic. When closing a shift:
- Stores the balance in the shift table
- Increments the running shift number
- If the current shift is the last shift (equal to total shifts):
  - Advances the shift date by one day
  - Resets the running shift to 1
  - Automatically triggers audit date change for all in-house guests

**Request Body:**
```json
{
  "balance": 15000.00
}
```

**Response (Normal Shift Closure):**
```json
{
  "success": true,
  "message": "Shift 1 closed successfully. Running shift incremented to 2. Balance stored in shift table.",
  "data": null,
  "timestamp": "2025-09-20T10:30:00"
}
```

**Response (Last Shift Closure):**
```json
{
  "success": true,
  "message": "Shift 3 closed successfully. Date changed to 2025-09-21 and running shift reset to 1. Audit date also updated. Balance stored in shift table.",
  "data": null,
  "timestamp": "2025-09-20T10:30:00"
}
```

### 2. Manual Shift Change

**Endpoint:** `POST /shift-change`

**Description:** Manually update or create a shift record with specific date and shift number.

**Request Body:**
```json
{
  "shiftDate": "2025-09-20",
  "shiftNo": "1",
  "balance": 15000.00
}
```

**Response:**
```json
{
  "success": true,
  "message": "Shift balance updated successfully",
  "data": null,
  "timestamp": "2025-09-20T10:30:00"
}
```

### 3. Get All Shifts

**Endpoint:** `GET /shifts` (This would need to be implemented separately)

**Description:** Retrieve all shift records (Note: This endpoint would need to be implemented separately as it's not part of the current operations controller)

## HMS System Logic

The HMS (Hotel Management System) table maintains the following information:
- **shift_date**: The current operational date
- **running_shift**: The current active shift number (1, 2, 3, etc.)
- **total_shift**: The total number of shifts per day (typically 3)

### Shift Rotation Process

1. **Normal Shift Closure** (when running_shift < total_shift):
   - Store balance in shift table with current date and running shift number
   - Increment running_shift by 1
   - Keep shift_date unchanged

2. **Last Shift Closure** (when running_shift = total_shift):
   - Store balance in shift table with current date and running shift number
   - Advance shift_date by 1 day
   - Reset running_shift to 1
   - Keep total_shift unchanged

### Example Scenario

Assuming a hotel with 3 shifts per day:

**Initial State:**
- shift_date: 2025-09-20
- running_shift: 1
- total_shift: 3

**After Closing Shift 1:**
- Shift record created: shift_date=2025-09-20, shift_no=1, balance=15000.00
- New HMS state: shift_date=2025-09-20, running_shift=2, total_shift=3

**After Closing Shift 2:**
- Shift record created: shift_date=2025-09-20, shift_no=2, balance=12000.00
- New HMS state: shift_date=2025-09-20, running_shift=3, total_shift=3

**After Closing Shift 3 (Last Shift):**
- Shift record created: shift_date=2025-09-20, shift_no=3, balance=18000.00
- New HMS state: shift_date=2025-09-21, running_shift=1, total_shift=3

## Error Responses

All error responses follow the same format:

```json
{
  "success": false,
  "message": "Error description",
  "data": null,
  "timestamp": "2025-09-20T10:30:00"
}
```

Common error scenarios:
- No HMS system record found
- Invalid HMS system configuration
- Missing required fields
- Database errors

## Database Schema

### HMS System Table (hmsystem)
| Column | Type | Description |
|--------|------|-------------|
| id | BIGINT (Primary Key) | Unique identifier |
| shift_date | DATE | Current operational date |
| running_shift | INT | Current active shift number |
| total_shift | INT | Total number of shifts per day |
| created_at | DATETIME | Record creation timestamp |
| updated_at | DATETIME | Record update timestamp |

### Shift Table (shift)
| Column | Type | Description |
|--------|------|-------------|
| id | BIGINT (Primary Key) | Unique identifier |
| shift_no | VARCHAR | Shift number (1, 2, 3, etc.) |
| shift_date | DATE | Date of the shift |
| balance | DECIMAL | Shift balance amount |
| created_at | DATETIME | Record creation timestamp |
| updated_at | DATETIME | Record update timestamp |

## Implementation Details

The shift management functionality is implemented in the following components:

1. **Hmsystem Entity**: Represents the HMS system configuration
2. **Shift Entity**: Represents individual shift records
3. **HmsystemRepository**: Data access for HMS system records
4. **ShiftRepository**: Data access for shift records
5. **OperationsService**: Business logic for shift management
6. **OperationsController**: REST API endpoints for shift operations
7. **HmsystemInitializationService**: Initializes the HMS system with default values

## Security

All shift management endpoints require ADMIN role authorization:
```http
Authorization: Bearer <jwt-token>
```