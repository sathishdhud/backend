# Shift Management with Automatic Audit Date Change

## Overview
This enhancement ensures that when the running shift reaches the total shifts (e.g., shift 3 of 3), closing the shift will:
1. Change the shift date to the next day
2. Reset the running shift to 1
3. Automatically trigger an audit date change for all in-house guests

## Implementation Details

### Enhanced Shift Close Logic
The `processShiftClose` method in `OperationsService` now includes automatic audit date change triggering:

```java
// Check if this is the last shift
if (runningShift.equals(totalShift)) {
    // Last shift - increment date and reset running shift
    Hmsystem newHmsystem = new Hmsystem();
    newHmsystem.setShiftDate(hmsystem.getShiftDate().plusDays(1));
    newHmsystem.setRunningShift(1);
    newHmsystem.setTotalShift(totalShift);
    hmsystemRepository.save(newHmsystem);
    
    // Also trigger audit date change for the new date
    processAutomaticAuditDateChange();
    
    return String.format("Shift %d closed successfully. Date changed to %s and running shift reset to 1. Audit date also updated. Balance stored in shift table.",
        runningShift, newHmsystem.getShiftDate().toString());
}
```

### Automatic Audit Date Change
A new private method `processAutomaticAuditDateChange()` was added to handle the automatic audit date change:

```java
/**
 * Process automatic audit date change - posts room charges and taxes for all in-house guests
 * This is called automatically when the shift date changes
 */
private void processAutomaticAuditDateChange() {
    LocalDate currentDate = LocalDate.now();
    List<CheckIn> inHouseGuests = checkInRepository.findInHouseGuests(currentDate);
    
    int processedCount = 0;
    
    for (CheckIn checkIn : inHouseGuests) {
        // Post room charges
        postRoomCharges(checkIn);
        
        // Post taxes (CGST and SGST)
        postTaxes(checkIn);
        
        processedCount++;
    }
    
    System.out.println(String.format("Automatic audit date change processed. Room charges and taxes posted for %d in-house guests.", processedCount));
}
```

## Workflow Example

### Scenario: Hotel with 3 shifts per day

**Day 1 (2025-09-20)**
- Shift 1 closed: Running shift becomes 2
- Shift 2 closed: Running shift becomes 3
- Shift 3 closed: 
  - Shift date changes to 2025-09-21
  - Running shift resets to 1
  - Audit date automatically updated for all in-house guests
  - Room charges and taxes posted for all in-house guests

**Day 2 (2025-09-21)**
- Shift 1 closed: Running shift becomes 2
- Shift 2 closed: Running shift becomes 3
- Shift 3 closed:
  - Shift date changes to 2025-09-22
  - Running shift resets to 1
  - Audit date automatically updated for all in-house guests
  - Room charges and taxes posted for all in-house guests

## API Response Updates

The response message for closing the last shift now includes information about the automatic audit date change:

**Before:**
```
"Shift 3 closed successfully. Date changed to 2025-09-21 and running shift reset to 1. Balance stored in shift table."
```

**After:**
```
"Shift 3 closed successfully. Date changed to 2025-09-21 and running shift reset to 1. Audit date also updated. Balance stored in shift table."
```

## Benefits

1. **Automation**: Eliminates the need for manual audit date change when closing the last shift
2. **Consistency**: Ensures audit dates are always up-to-date when shifts change
3. **Efficiency**: Reduces manual operations for hotel staff
4. **Accuracy**: Prevents missed audit date changes that could affect billing

## Security
All operations maintain the existing security model requiring ADMIN role authorization.