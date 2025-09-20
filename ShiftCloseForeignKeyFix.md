# Shift Close Foreign Key Constraint Fix

## Issue Description
When closing a shift, the system was encountering a foreign key constraint error:
```
Error: Failed to close shift: could not execute statement [Cannot add or update a child row: a foreign key constraint fails (railway.post_transactions, CONSTRAINT FKfksg5eas9mf53h8lu3awy64cy FOREIGN KEY (acc_head_id) REFERENCES hotel_account_head (acc_head_id))] [insert into post_transactions (acc_head_id,amount,audit_date,bill_no,date,folio_no,guest_name,narration,room_id,voucher_no,transaction_id) values (?,?,?,?,?,?,?,?,?,?,?)]; SQL [insert into post_transactions (acc_head_id,amount,audit_date,bill_no,date,folio_no,guest_name,narration,room_id,voucher_no,transaction_id) values (?,?,?,?,?,?,?,?,?,?,?)]; constraint [null]
```

## Root Cause
The system was trying to insert transactions with account head IDs ("ROOM_CHARGES", "CGST", "SGST") that did not exist in the `hotel_account_head` table, causing a foreign key constraint violation.

## Solution Implemented

### 1. Enhanced Account Head Repository
Added a method to check if an account head exists by its ID:
```java
boolean existsByAccHeadId(String accHeadId);
```

### 2. Account Head Validation and Creation
Added a method `ensureRequiredAccountHeadsExist()` that:
- Checks if required account heads exist in the database
- Creates them if they don't exist
- Specifically creates account heads for:
  - ROOM_CHARGES (Room Charges)
  - CGST (Central Goods and Services Tax)
  - SGST (State Goods and Services Tax)

### 3. Integration Points
The `ensureRequiredAccountHeadsExist()` method is called:
- During manual audit date change processing
- During automatic audit date change processing (when closing the last shift)
- Before processing any transactions that require these account heads

## Code Changes

### OperationsService.java
1. Added `HotelAccountHeadRepository` dependency
2. Added `ensureRequiredAccountHeadsExist()` method
3. Integrated account head validation in:
   - `processAuditDateChange()`
   - `processShiftClose()` (for last shift scenario)
   - `processAutomaticAuditDateChange()`

### HotelAccountHeadRepository.java
1. Added `existsByAccHeadId()` method

## Account Heads Created
When the system runs, it will automatically create these account heads if they don't exist:

| Account Head ID | Name                          |
|----------------|-------------------------------|
| ROOM_CHARGES   | Room Charges                  |
| CGST           | Central Goods and Services Tax|
| SGST           | State Goods and Services Tax  |

## Benefits
1. **Automatic Setup**: No manual database setup required for account heads
2. **Error Prevention**: Prevents foreign key constraint violations
3. **Consistency**: Ensures consistent account head usage across the system
4. **Maintainability**: Centralized account head management

## Testing
The fix has been implemented to ensure:
- Account heads are created only when they don't exist
- Transactions can be posted without foreign key violations
- Shift closing process works correctly with audit date changes
- Both manual and automatic audit date changes work properly