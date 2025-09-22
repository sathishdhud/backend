# Transaction Management API Documentation

This document provides detailed information about the Transaction Management APIs, including endpoints for managing expenses and sales receipts.

## Base URL
```
http://localhost:8080/api/transactions
```

## Endpoints

### 1. Create Expense

**Endpoint:** `POST /expenses`

**Description:** Create a new expense transaction with shift information

**Request Body:**
```json
{
  "voucherNo": "EXP20250921001",
  "date": "2025-09-21",
  "accountHeadId": "OFFICE_SUPPLIES",
  "amount": 1500.00,
  "narration": "Office supplies purchase",
  "shiftNo": "1",
  "shiftDate": "2025-09-21"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Expense created successfully",
  "data": {
    "voucherNo": "EXP20250921001",
    "date": "2025-09-21",
    "accountHeadId": "OFFICE_SUPPLIES",
    "accountHeadName": "Office Supplies",
    "amount": 1500.00,
    "narration": "Office supplies purchase",
    "shiftNo": "1",
    "shiftDate": "2025-09-21"
  },
  "timestamp": "2025-09-21T10:30:00"
}
```

### 2. Create Sales Receipt

**Endpoint:** `POST /sales-receipts`

**Description:** Create a new sales receipt with shift information

**Request Body:**
```json
{
  "receiptNo": "REC20250921001",
  "date": "2025-09-21",
  "modeOfPaymentId": "CASH",
  "amount": 2500.00,
  "voucherNo": "V20250921001",
  "narration": "Room payment",
  "shiftNo": "1",
  "shiftDate": "2025-09-21"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Sales receipt created successfully",
  "data": {
    "receiptNo": "REC20250921001",
    "date": "2025-09-21",
    "modeOfPaymentId": "CASH",
    "modeOfPaymentName": "Cash",
    "amount": 2500.00,
    "voucherNo": "V20250921001",
    "narration": "Room payment",
    "shiftNo": "1",
    "shiftDate": "2025-09-21"
  },
  "timestamp": "2025-09-21T10:30:00"
}
```

### 3. Get All Expenses

**Endpoint:** `GET /expenses`

**Description:** Retrieve all expense transactions

**Response:**
```json
{
  "success": true,
  "message": "Expenses retrieved successfully",
  "data": [
    {
      "voucherNo": "EXP20250921001",
      "date": "2025-09-21",
      "accountHeadId": "OFFICE_SUPPLIES",
      "accountHeadName": "Office Supplies",
      "amount": 1500.00,
      "narration": "Office supplies purchase",
      "shiftNo": "1",
      "shiftDate": "2025-09-21"
    }
  ],
  "timestamp": "2025-09-21T10:30:00"
}
```

### 4. Get All Sales Receipts

**Endpoint:** `GET /sales-receipts`

**Description:** Retrieve all sales receipts

**Response:**
```json
{
  "success": true,
  "message": "Sales receipts retrieved successfully",
  "data": [
    {
      "receiptNo": "REC20250921001",
      "date": "2025-09-21",
      "modeOfPaymentId": "CASH",
      "modeOfPaymentName": "Cash",
      "amount": 2500.00,
      "voucherNo": "V20250921001",
      "narration": "Room payment",
      "shiftNo": "1",
      "shiftDate": "2025-09-21"
    }
  ],
  "timestamp": "2025-09-21T10:30:00"
}
```

## Data Models

### Expense Request
| Field | Type | Required | Description |
|-------|------|----------|-------------|
| voucherNo | String | Yes | Unique voucher number |
| date | Date | Yes | Transaction date |
| accountHeadId | String | Yes | Account head identifier |
| amount | BigDecimal | Yes | Transaction amount |
| narration | String | No | Description of the transaction |
| shiftNo | String | Yes | Shift number |
| shiftDate | Date | Yes | Shift date |

### Sales Receipt Request
| Field | Type | Required | Description |
|-------|------|----------|-------------|
| receiptNo | String | Yes | Unique receipt number |
| date | Date | Yes | Receipt date |
| modeOfPaymentId | String | Yes | Mode of payment identifier |
| amount | BigDecimal | Yes | Receipt amount |
| voucherNo | String | No | Associated voucher number |
| narration | String | No | Description of the receipt |
| shiftNo | String | Yes | Shift number |
| shiftDate | Date | Yes | Shift date |

### Expense Response
| Field | Type | Description |
|-------|------|-------------|
| voucherNo | String | Unique voucher number |
| date | Date | Transaction date |
| accountHeadId | String | Account head identifier |
| accountHeadName | String | Account head name |
| amount | BigDecimal | Transaction amount |
| narration | String | Description of the transaction |
| shiftNo | String | Shift number |
| shiftDate | Date | Shift date |

### Sales Receipt Response
| Field | Type | Description |
|-------|------|-------------|
| receiptNo | String | Unique receipt number |
| date | Date | Receipt date |
| modeOfPaymentId | String | Mode of payment identifier |
| modeOfPaymentName | String | Mode of payment name |
| amount | BigDecimal | Receipt amount |
| voucherNo | String | Associated voucher number |
| narration | String | Description of the receipt |
| shiftNo | String | Shift number |
| shiftDate | Date | Shift date |
