# Shift Close API Documentation

This document provides detailed information about the Shift Close API endpoint with enhanced financial tracking capabilities.

## Base URL
```
http://localhost:8080/api/operations
```

## Endpoint

### Close Current Shift

**Endpoint:** `POST /shift-close`

**Description:** Close the current shift and handle shift rotation logic. When closing a shift:
- Stores opening_balance, closing_balance, total_income, and total_expense in the shift table
- Sets the audit_date for audit tracking
- Increments the running shift number
- If the current shift is the last shift (equal to total shifts):
  - Advances the shift date by one day
  - Resets the running shift to 1
  - Automatically triggers audit date change for all in-house guests

**Request Body:**
```json
{
  "openingBalance": 10000.00,
  "closingBalance": 15000.00,
  "totalIncome": 7500.00,
  "totalExpense": 2500.00
}
```

**Field Descriptions:**
| Field | Type | Required | Description |
|-------|------|----------|-------------|
| openingBalance | BigDecimal | Yes | The opening balance at the start of the shift |
| closingBalance | BigDecimal | Yes | The closing balance at the end of the shift |
| totalIncome | BigDecimal | Yes | The total income during the shift |
| totalExpense | BigDecimal | Yes | The total expenses during the shift |

**Response (Normal Shift Closure):**
```json
{
  "success": true,
  "message": "Shift 1 closed successfully. Running shift incremented to 2. All shift details stored in shift table.",
  "data": null,
  "timestamp": "2025-09-22T10:30:00"
}
```

**Response (Last Shift Closure):**
```json
{
  "success": true,
  "message": "Shift 3 closed successfully. Date changed to 2025-09-22 and running shift reset to 1. Audit date also updated. All shift details stored in shift table.",
  "data": null,
  "timestamp": "2025-09-22T10:30:00"
}
```

**Error Response:**
```json
{
  "success": false,
  "message": "Failed to close shift: No HMS system record found",
  "data": null,
  "timestamp": "2025-09-22T10:30:00"
}
```

## Database Schema

### Shift Table (shift)
| Column | Type | Description |
|--------|------|-------------|
| id | BIGINT (Primary Key) | Unique identifier |
| shift_no | VARCHAR | Shift number (1, 2, 3, etc.) |
| shift_date | DATE | Date of the shift |
| audit_date | DATE | Audit date for the shift |
| opening_balance | DECIMAL | Opening balance at start of shift |
| closing_balance | DECIMAL | Closing balance at end of shift |
| total_income | DECIMAL | Total income during shift |
| total_expense | DECIMAL | Total expenses during shift |
| created_at | DATETIME | Record creation timestamp |
| updated_at | DATETIME | Record update timestamp |

### HMS System Table (hmsystem)
| Column | Type | Description |
|--------|------|-------------|
| id | BIGINT (Primary Key) | Unique identifier |
| shift_date | DATE | Current operational date |
| running_shift | INT | Current active shift number |
| total_shift | INT | Total number of shifts per day |
| created_at | DATETIME | Record creation timestamp |
| updated_at | DATETIME | Record update timestamp |

## HMS System Logic

The HMS (Hotel Management System) table maintains the following information:
- **shift_date**: The current operational date
- **running_shift**: The current active shift number (1, 2, 3, etc.)
- **total_shift**: The total number of shifts per day (typically 3)

### Shift Rotation Process

1. **Normal Shift Closure** (when running_shift < total_shift):
   - Store all financial details in shift table with current date and running shift number
   - Increment running_shift by 1
   - Keep shift_date unchanged

2. **Last Shift Closure** (when running_shift = total_shift):
   - Store all financial details in shift table with current date and running shift number
   - Advance shift_date by 1 day
   - Reset running_shift to 1
   - Keep total_shift unchanged
   - Automatically trigger audit date change for all in-house guests

### Example Scenario

Assuming a hotel with 3 shifts per day:

**Initial State:**
- shift_date: 2025-09-21
- running_shift: 1
- total_shift: 3

**After Closing Shift 1:**
- Shift record created: 
  - shift_date=2025-09-21
  - shift_no=1
  - opening_balance=10000.00
  - closing_balance=15000.00
  - total_income=7500.00
  - total_expense=2500.00
- New HMS state: shift_date=2025-09-21, running_shift=2, total_shift=3

**After Closing Shift 2:**
- Shift record created: 
  - shift_date=2025-09-21
  - shift_no=2
  - opening_balance=15000.00
  - closing_balance=12000.00
  - total_income=5000.00
  - total_expense=8000.00
- New HMS state: shift_date=2025-09-21, running_shift=3, total_shift=3

**After Closing Shift 3 (Last Shift):**
- Shift record created: 
  - shift_date=2025-09-21
  - shift_no=3
  - opening_balance=12000.00
  - closing_balance=18000.00
  - total_income=9000.00
  - total_expense=3000.00
- New HMS state: shift_date=2025-09-22, running_shift=1, total_shift=3

## Implementation Details

The shift close functionality is implemented in the following components:

1. **Shift Entity**: Represents individual shift records with enhanced financial fields
2. **ShiftCloseRequest DTO**: Request object for shift closing with financial details
3. **ShiftRepository**: Data access for shift records
4. **OperationsService**: Business logic for shift management including processShiftClose method
5. **OperationsController**: REST API endpoint for shift operations

## Security

All shift management endpoints require ADMIN role authorization:
```http
Authorization: Bearer <jwt-token>
```