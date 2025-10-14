# Expense API Usage Guide

## Creating an Expense with All Fields

To properly create an expense that includes guest name, folio number, and room information, you need to include all relevant fields in your request body.

### Example Request with All Fields

```json
{
  "voucherNo": "EXP-001",
  "date": "2025-10-12",
  "accountHeadId": "RE",
  "amount": 16,
  "narration": "Restaurant expense for room 101",
  "shiftNo": "1",
  "shiftDate": "2025-10-12",
  "roomNo": "101",
  "folioNo": "F1-25-26",
  "guestName": "John Doe"
}
```

### Example Request with Minimal Fields

```json
{
  "voucherNo": "EXP-002",
  "date": "2025-10-12",
  "accountHeadId": "HK",
  "amount": 14,
  "narration": "Housekeeping service",
  "shiftNo": "1",
  "shiftDate": "2025-10-12"
}
```

### Expected Response with All Fields

When you provide all the fields, you should get a response like:

```json
{
  "success": true,
  "message": "Expense created successfully",
  "data": {
    "transactionId": "TXN12345",
    "voucherNo": "EXP-001",
    "date": "2025-10-12",
    "accountHeadId": "RE",
    "accountHeadName": "RESTAURANT",
    "amount": 16,
    "narration": "Restaurant expense for room 101",
    "shiftNo": "1",
    "shiftDate": "2025-10-12",
    "folioNo": "F1-25-26",
    "billNo": null,
    "roomId": "ROOM001",
    "roomNo": "101",
    "guestName": "John Doe",
    "auditDate": "2025-10-12"
  },
  "timestamp": "2025-10-12T23:35:37.733881700"
}
```

### Expected Response with Minimal Fields

When you provide only minimal fields, you should get a response like:

```json
{
  "success": true,
  "message": "Expense created successfully",
  "data": {
    "transactionId": "TXN67890",
    "voucherNo": "EXP-002",
    "date": "2025-10-12",
    "accountHeadId": "HK",
    "accountHeadName": "HOUSEKEEPING",
    "amount": 14,
    "narration": "Housekeeping service",
    "shiftNo": "1",
    "shiftDate": "2025-10-12",
    "folioNo": null,
    "billNo": null,
    "roomId": null,
    "roomNo": null,
    "guestName": "Unknown Guest",
    "auditDate": "2025-10-12"
  },
  "timestamp": "2025-10-12T23:35:37.733881700"
}
```

## API Endpoint

POST `/api/expenses`

## Field Descriptions

| Field | Required | Description |
|-------|----------|-------------|
| voucherNo | Yes | Voucher number for the expense |
| date | Yes | Date of the expense |
| accountHeadId | Yes | Account head ID (e.g., "RE" for Restaurant, "HK" for Housekeeping) |
| amount | Yes | Amount of the expense |
| narration | No | Description of the expense |
| shiftNo | Yes | Shift number |
| shiftDate | Yes | Shift date |
| roomNo | No | Room number (if applicable) |
| folioNo | No | Folio number (if applicable) |
| guestName | No | Guest name (if applicable) |

## Notes

1. When `roomNo` is provided, the system will automatically look up the corresponding `roomId` and include it in the transaction.
2. When `folioNo` is provided, the system will validate that the folio exists.
3. When `guestName` is not provided but `folioNo` is, the system will try to get the guest name from the folio.
4. When `guestName` is not provided and cannot be derived from other fields, it defaults to "Unknown Guest".
5. The `auditDate` is automatically set to the current date when the transaction is created.