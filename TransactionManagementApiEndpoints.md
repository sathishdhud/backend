# Transaction Management API Endpoints

## Overview
This document provides detailed information about all transaction management API endpoints, including HTTP methods, request/response formats, and example JSON payloads.

## Base URL
```
/api/transactions
```

## Authentication
All endpoints require JWT authentication with either ADMIN or USER role.

---

## Expense Transactions

### 1. Create Expense
**Method:** `POST`  
**Endpoint:** `/api/transactions/expenses`  
**Description:** Create a new expense transaction that can be associated with a room, bill, or folio.

#### Request Body
```json
{
  "voucherNo": "string*",
  "date": "string (YYYY-MM-DD)*",
  "accountHeadId": "string*",
  "amount": "number*",
  "narration": "string",
  "shiftNo": "string*",
  "shiftDate": "string (YYYY-MM-DD)*",
  "roomNo": "string",
  "billNo": "string",
  "folioNo": "string",
  "guestName": "string"
}
```

#### Example Request
```json
{
  "voucherNo": "EXP-2023-001",
  "date": "2023-06-15",
  "accountHeadId": "ACC001",
  "amount": 1500.00,
  "narration": "Office supplies purchase",
  "shiftNo": "1",
  "shiftDate": "2023-06-15",
  "roomNo": "101",
  "billNo": "B1-23-24",
  "folioNo": "F1-23-24",
  "guestName": "John Smith"
}
```

#### Example Response (Success)
```json
{
  "success": true,
  "message": "Operation successful",
  "data": {
    "voucherNo": "EXP-2023-001",
    "date": "2023-06-15",
    "accountHeadId": "ACC001",
    "accountHeadName": "Office Supplies",
    "amount": 1500.00,
    "narration": "Office supplies purchase",
    "shiftNo": "1",
    "shiftDate": "2023-06-15"
  }
}
```

#### Example Response (Error)
```json
{
  "success": false,
  "message": "Failed to create expense: Room not found: 999"
}
```

---

### 2. Create Expense for Bill
**Method:** `POST`  
**Endpoint:** `/api/transactions/expenses/bill/{billNo}`  
**Description:** Create a new expense transaction specifically for a bill, with the bill number in the path.

#### Path Parameters
- `billNo` (string, required): The bill number

#### Request Body
```json
{
  "voucherNo": "string*",
  "date": "string (YYYY-MM-DD)*",
  "accountHeadId": "string*",
  "amount": "number*",
  "narration": "string",
  "shiftNo": "string*",
  "shiftDate": "string (YYYY-MM-DD)*",
  "roomNo": "string",
  "folioNo": "string",
  "guestName": "string"
}
```

#### Example Request
```
POST /api/transactions/expenses/bill/B1-23-24
```

```json
{
  "voucherNo": "EXP-2023-001",
  "date": "2023-06-15",
  "accountHeadId": "ACC001",
  "amount": 1500.00,
  "narration": "Laundry service for bill",
  "shiftNo": "1",
  "shiftDate": "2023-06-15",
  "roomNo": "101",
  "folioNo": "F1-23-24",
  "guestName": "John Smith"
}
```

#### Example Response (Success)
```json
{
  "success": true,
  "message": "Operation successful",
  "data": {
    "voucherNo": "EXP-2023-001",
    "date": "2023-06-15",
    "accountHeadId": "ACC001",
    "accountHeadName": "Laundry Service",
    "amount": 1500.00,
    "narration": "Laundry service for bill",
    "shiftNo": "1",
    "shiftDate": "2023-06-15"
  }
}
```

---

### 3. Get All Expenses
**Method:** `GET`  
**Endpoint:** `/api/transactions/expenses`  
**Description:** Retrieve all expense transactions.

#### Example Response (Success)
```json
{
  "success": true,
  "message": "Operation successful",
  "data": [
    {
      "voucherNo": "EXP-2023-001",
      "date": "2023-06-15",
      "accountHeadId": "ACC001",
      "accountHeadName": "Office Supplies",
      "amount": 1500.00,
      "narration": "Office supplies purchase",
      "shiftNo": "1",
      "shiftDate": "2023-06-15"
    },
    {
      "voucherNo": "EXP-2023-002",
      "date": "2023-06-16",
      "accountHeadId": "ACC002",
      "accountHeadName": "Maintenance",
      "amount": 2500.00,
      "narration": "Room maintenance",
      "shiftNo": "1",
      "shiftDate": "2023-06-16"
    }
  ]
}
```

---

### 4. Get Expenses by Room Number
**Method:** `GET`  
**Endpoint:** `/api/transactions/expenses/room/{roomNo}`  
**Description:** Retrieve all expense transactions for a specific room.

#### Path Parameters
- `roomNo` (string, required): The room number

#### Example Request
```
GET /api/transactions/expenses/room/101
```

#### Example Response (Success)
```json
{
  "success": true,
  "message": "Operation successful",
  "data": [
    {
      "voucherNo": "EXP-2023-001",
      "date": "2023-06-15",
      "accountHeadId": "ACC001",
      "accountHeadName": "Maintenance",
      "amount": 2500.00,
      "narration": "Room 101 maintenance",
      "shiftNo": "1",
      "shiftDate": "2023-06-15"
    }
  ]
}
```

---

### 5. Get Expenses by Bill Number
**Method:** `GET`  
**Endpoint:** `/api/transactions/expenses/bill/{billNo}`  
**Description:** Retrieve all expense transactions for a specific bill.

#### Path Parameters
- `billNo` (string, required): The bill number

#### Example Request
```
GET /api/transactions/expenses/bill/B1-23-24
```

#### Example Response (Success)
```json
{
  "success": true,
  "message": "Operation successful",
  "data": [
    {
      "voucherNo": "EXP-2023-002",
      "date": "2023-06-15",
      "accountHeadId": "ACC002",
      "accountHeadName": "Laundry Service",
      "amount": 1200.00,
      "narration": "Laundry for bill B1-23-24",
      "shiftNo": "1",
      "shiftDate": "2023-06-15"
    }
  ]
}
```

---

### 6. Get Expenses by Folio Number
**Method:** `GET`  
**Endpoint:** `/api/transactions/expenses/folio/{folioNo}`  
**Description:** Retrieve all expense transactions for a specific folio.

#### Path Parameters
- `folioNo` (string, required): The folio number

#### Example Request
```
GET /api/transactions/expenses/folio/F1-23-24
```

#### Example Response (Success)
```json
{
  "success": true,
  "message": "Operation successful",
  "data": [
    {
      "voucherNo": "EXP-2023-003",
      "date": "2023-06-15",
      "accountHeadId": "ACC003",
      "accountHeadName": "Restaurant",
      "amount": 3500.00,
      "narration": "Dinner charge for folio F1-23-24",
      "shiftNo": "1",
      "shiftDate": "2023-06-15"
    }
  ]
}
```

---

## Sales Receipts

### 7. Create Sales Receipt
**Method:** `POST`  
**Endpoint:** `/api/transactions/sales-receipts`  
**Description:** Create a new sales receipt.

#### Request Body
```json
{
  "receiptNo": "string*",
  "date": "string (YYYY-MM-DD)*",
  "modeOfPaymentId": "string*",
  "amount": "number*",
  "voucherNo": "string",
  "narration": "string",
  "shiftNo": "string*",
  "shiftDate": "string (YYYY-MM-DD)*"
}
```

#### Example Request
```json
{
  "receiptNo": "REC-2023-001",
  "date": "2023-06-15",
  "modeOfPaymentId": "CASH",
  "amount": 5000.00,
  "voucherNo": "V-2023-001",
  "narration": "Payment for services",
  "shiftNo": "1",
  "shiftDate": "2023-06-15"
}
```

#### Example Response (Success)
```json
{
  "success": true,
  "message": "Operation successful",
  "data": {
    "receiptNo": "REC-2023-001",
    "date": "2023-06-15",
    "modeOfPaymentId": "CASH",
    "modeOfPaymentName": "Cash",
    "amount": 5000.00,
    "voucherNo": "V-2023-001",
    "narration": "Payment for services",
    "shiftNo": "1",
    "shiftDate": "2023-06-15"
  }
}
```

---

### 8. Create Sales Receipt for Bill
**Method:** `POST`  
**Endpoint:** `/api/transactions/sales-receipts/bill/{billNo}`  
**Description:** Create a new sales receipt specifically for a bill, with the bill number in the path.

#### Path Parameters
- `billNo` (string, required): The bill number

#### Request Body
```json
{
  "receiptNo": "string*",
  "date": "string (YYYY-MM-DD)*",
  "modeOfPaymentId": "string*",
  "amount": "number*",
  "voucherNo": "string",
  "narration": "string",
  "shiftNo": "string*",
  "shiftDate": "string (YYYY-MM-DD)*"
}
```

#### Example Request
```
POST /api/transactions/sales-receipts/bill/B1-23-24
```

```json
{
  "receiptNo": "REC-2023-001",
  "date": "2023-06-15",
  "modeOfPaymentId": "CASH",
  "amount": 5000.00,
  "voucherNo": "V-2023-001",
  "narration": "Payment for bill B1-23-24",
  "shiftNo": "1",
  "shiftDate": "2023-06-15"
}
```

#### Example Response (Success)
```json
{
  "success": true,
  "message": "Operation successful",
  "data": {
    "receiptNo": "REC-2023-001",
    "date": "2023-06-15",
    "modeOfPaymentId": "CASH",
    "modeOfPaymentName": "Cash",
    "amount": 5000.00,
    "voucherNo": "V-2023-001",
    "narration": "Payment for bill B1-23-24",
    "shiftNo": "1",
    "shiftDate": "2023-06-15"
  }
}
```

---

### 9. Get All Sales Receipts
**Method:** `GET`  
**Endpoint:** `/api/transactions/sales-receipts`  
**Description:** Retrieve all sales receipts.

#### Example Response (Success)
```json
{
  "success": true,
  "message": "Operation successful",
  "data": [
    {
      "receiptNo": "REC-2023-001",
      "date": "2023-06-15",
      "modeOfPaymentId": "CASH",
      "modeOfPaymentName": "Cash",
      "amount": 5000.00,
      "voucherNo": "V-2023-001",
      "narration": "Payment for services",
      "shiftNo": "1",
      "shiftDate": "2023-06-15"
    },
    {
      "receiptNo": "REC-2023-002",
      "date": "2023-06-16",
      "modeOfPaymentId": "CARD",
      "modeOfPaymentName": "Credit Card",
      "amount": 7500.00,
      "voucherNo": "V-2023-002",
      "narration": "Room payment",
      "shiftNo": "1",
      "shiftDate": "2023-06-16"
    }
  ]
}
```

---

## Error Response Format
All endpoints return error responses in the following format when an error occurs:

```json
{
  "success": false,
  "message": "Error description message"
}
```

## Field Requirements
Fields marked with `*` are required. All date fields should be in `YYYY-MM-DD` format.

## Response Codes
- `200`: Success
- `400`: Bad Request (validation errors, missing required fields)
- `401`: Unauthorized (missing or invalid authentication)
- `403`: Forbidden (insufficient permissions)
- `500`: Internal Server Error