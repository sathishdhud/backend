# Audit Date Change Implementation

## Overview
This document describes the implementation of the audit date change functionality that automatically posts daily room charges and taxes for all in-house guests, along with enhanced GST handling in reservations.

## Changes Made

### 1. Enhanced RoomStatusScheduler
Modified the existing `RoomStatusScheduler` class to include the new audit date functionality:

- Added required repository dependencies:
  - `CheckInRepository`
  - `RoomRepository`
  - `TaxationRepository`
  - `PostTransactionRepository`
  - `HotelAccountHeadRepository`
  - `NumberGenerationService`

- Added new scheduled method `postDailyRoomChargesAndTaxes()`:
  - Runs daily at 1:00 AM using cron expression `0 0 1 * * *`
  - Finds all in-house guests using `checkInRepository.findInHouseGuests()`
  - For each guest:
    - Retrieves room details and rate
    - Calculates CGST and SGST based on taxation records
    - Posts room charge transaction
    - Posts CGST transaction if applicable
    - Posts SGST transaction if applicable

### 2. Enhanced RoomStatusManagementService
Modified the existing `RoomStatusManagementService` class to initialize required data:

- Added required repository dependencies:
  - `HotelAccountHeadRepository`
  - `TaxationRepository`

- Added `initializeAuditData()` method with `@PostConstruct` annotation:
  - Creates "Room Charges" account head if it doesn't exist
  - Creates "CGST" account head if it doesn't exist
  - Creates "SGST" account head if it doesn't exist
  - Creates CGST tax record (9%) if it doesn't exist
  - Creates SGST tax record (9%) if it doesn't exist

### 3. Enhanced ReservationService
Modified the existing `ReservationService` class to handle GST inclusion properly:

- Added `TaxationRepository` dependency
- Enhanced `createReservation()` and `updateReservation()` methods to calculate rates with taxes
- Added `calculateRateWithTaxes()` method to handle GST calculations

### 4. Enhanced PostTransactionService
Modified the existing `PostTransactionService` class to handle GST inclusion for transactions:

- Added `TaxationRepository` dependency
- Enhanced `createTransactionEntity()` and `updateTransaction()` methods to calculate amounts with taxes
- Added `calculateAmountWithTaxes()` method to handle GST calculations for transactions

### 5. Enabled Scheduling
Modified `HotelManagementApplication` class to enable scheduling:

- Added `@EnableScheduling` annotation

## Functionality Details

### Scheduled Task
The audit date change task runs automatically every day at 1:00 AM and performs the following operations:

1. **Identify In-House Guests**: Queries the database for all guests with active stays
2. **Calculate Charges**: For each guest, calculates:
   - Room charge based on the rate in the check-in record
   - Properly handles GST-inclusive and exclusive rates
   - CGST (9% of room charge) if CGST tax record exists
   - SGST (9% of room charge) if SGST tax record exists
3. **Post Transactions**: Creates and saves transaction records for:
   - Room charge
   - CGST (if applicable)
   - SGST (if applicable)
4. **Logging**: Provides detailed logging of successful and failed operations

### GST Handling in Reservations
When creating or updating reservations, the system properly handles GST inclusion:

1. **GST Exclusive Rates**: The rate is stored as-is without modification
2. **GST Inclusive Rates**: The rate is automatically updated to include CGST and SGST from the taxation table
   - If base rate is ₹1000 and CGST/SGST are both 9%, the stored rate becomes ₹1180
   - This ensures accurate billing and reporting throughout the system

### GST Handling in Transactions
When creating or updating transactions, the system properly handles GST inclusion:

1. **GST Exclusive Amounts**: The amount is updated to include CGST and SGST from the taxation table
2. **GST Inclusive Amounts**: The amount is automatically updated to include CGST and SGST from the taxation table
   - If base amount is ₹1000 and CGST/SGST are both 9%, the stored amount becomes ₹1180
   - This ensures accurate billing and reporting throughout the system

### Data Initialization
When the application starts, it ensures that all required data exists:

1. **Account Heads**:
   - "Room Charges" (ID: ROOM_CHARGE)
   - "CGST" (ID: CGST)
   - "SGST" (ID: SGST)

2. **Tax Records**:
   - CGST: 9% tax rate
   - SGST: 9% tax rate

## API Documentation Updates

### Operations Endpoint
The existing manual audit date change endpoint remains available:
- `POST /api/operations/audit-date-change` - Process audit date change manually

## Testing

### Unit Tests
Created comprehensive tests to verify the functionality:

1. `AuditDateSchedulerTest` - Verifies that the application context loads correctly with scheduled tasks
2. `AuditDateFunctionalityTest` - Tests the core functionality including:
   - Data initialization
   - In-house guest identification
   - Transaction posting

## Error Handling

The implementation includes robust error handling:

1. **Graceful Failures**: If posting charges for one guest fails, the system continues with other guests
2. **Detailed Logging**: All errors are logged with specific information about what went wrong
3. **Validation**: Checks for required data before processing (room rates, tax records, etc.)

## Configuration

The scheduled task uses the following cron expression:
```
0 0 1 * * *
```

This means it runs at 1:00 AM every day. The timing can be adjusted by modifying the cron expression in the `@Scheduled` annotation.

## Manual Trigger

The functionality can also be triggered manually through the existing API endpoint:
```
POST /api/operations/audit-date-change
```

This allows administrators to run the process on-demand if needed.