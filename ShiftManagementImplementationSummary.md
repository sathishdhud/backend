# Shift Management Implementation Summary

This document summarizes the implementation of the shift management functionality with HMS system integration.

## Overview

The implementation adds a new shift closing mechanism that automatically handles shift rotation logic based on the HMS (Hotel Management System) configuration. When a shift is closed:

1. The shift balance is stored in the shift table
2. If the current shift is not the last shift, the running shift is incremented
3. If the current shift is the last shift, the shift date is advanced and the running shift is reset to 1

## Components Added

### 1. Hmsystem Entity
- **File**: `src/main/java/com/hotelworks/entity/Hmsystem.java`
- Represents the HMS system configuration with fields:
  - `shiftDate`: Current operational date
  - `runningShift`: Current active shift number
  - `totalShift`: Total number of shifts per day

### 2. Hmsystem Repository
- **File**: `src/main/java/com/hotelworks/repository/HmsystemRepository.java`
- Provides data access methods for HMS system records
- Includes method to find the latest HMS system record

### 3. ShiftCloseRequest DTO
- **File**: `src/main/java/com/hotelworks/dto/request/ShiftCloseRequest.java`
- Data transfer object for shift closing requests
- Contains balance field

### 4. Hmsystem Initialization Service
- **File**: `src/main/java/com/hotelworks/service/HmsystemInitializationService.java`
- Automatically creates an initial HMS system record if none exists
- Sets default values: current date, running shift = 1, total shifts = 3

### 5. Updated Operations Service
- **File**: `src/main/java/com/hotelworks/service/OperationsService.java`
- Added `processShiftClose` method that implements the shift rotation logic

### 6. Updated Operations Controller
- **File**: `src/main/java/com/hotelworks/controller/OperationsController.java`
- Added `/shift-close` endpoint for closing shifts

### 7. API Documentation
- **File**: `ShiftManagementApiDocumentation.md`
- Comprehensive documentation for the new shift management functionality
- **File**: `API_Documentation.md` (updated)
- Added documentation for the new endpoint in the main API documentation

## API Endpoints

### New Endpoint
```
POST /api/operations/shift-close
```

**Request Body:**
```json
{
  "balance": 15000.00
}
```

**Response (Normal Shift):**
```json
{
  "success": true,
  "message": "Shift 1 closed successfully. Running shift incremented to 2. Balance stored in shift table.",
  "data": null,
  "timestamp": "2025-09-20T10:30:00"
}
```

**Response (Last Shift):**
```json
{
  "success": true,
  "message": "Shift 3 closed successfully. Date changed to 2025-09-21 and running shift reset to 1. Balance stored in shift table.",
  "data": null,
  "timestamp": "2025-09-20T10:30:00"
}
```

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

## Shift Rotation Process

1. **Normal Shift Closure** (when running_shift < total_shift):
   - Store balance in shift table with current date and running shift number
   - Increment running_shift by 1
   - Keep shift_date unchanged

2. **Last Shift Closure** (when running_shift = total_shift):
   - Store balance in shift table with current date and running shift number
   - Advance shift_date by 1 day
   - Reset running_shift to 1
   - Keep total_shift unchanged
   - Automatically trigger audit date change for all in-house guests

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
- Audit date automatically changed and room charges posted for all in-house guests

## Security

All shift management endpoints require ADMIN role authorization:
```http
Authorization: Bearer <jwt-token>
```

## Testing

The implementation has been designed to work with the existing system architecture and follows the same patterns used throughout the application.