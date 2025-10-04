# Transaction Management API Documentation

## Overview
This API provides endpoints for managing expense transactions and sales receipts in the hotel management system. Transactions can be associated with rooms, bills, and folios for better tracking and reporting.

## Base URL
```
/api/transactions
```

## Authentication
All endpoints require authentication with either ADMIN or USER role.

## Expense Transactions

### Create Expense
**POST** `/api/transactions/expenses`

Create a new expense transaction that can be associated with a room, bill, or folio.

#### Request Body
```json
{
  "voucherNo": "EXP-2023-001",
  "date": "2023-06-15",
  "accountHeadId": "ACC001",
  "amount": 1500.00,
  "narration": "Office supplies purchase",
  "shiftNo": "1",
  "shiftDate": "2023-06-15",
  "roomNo": "101",           // Optional: Associate with room
  "billNo": "B1-23-24",      // Optional: Associate with bill
  "folioNo": "F1-23-24",     // Optional: Associate with folio
  "guestName": "John Smith"  // Optional: Guest name
}
```

#### Response
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

### Get All Expenses
**GET** `/api/transactions/expenses`

Retrieve all expense transactions.

#### Response
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
    }
  ]
}
```

### Get Expenses by Room Number
**GET** `/api/transactions/expenses/room/{roomNo}`

Retrieve all expense transactions for a specific room.

#### Response
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

### Get Expenses by Bill Number
**GET** `/api/transactions/expenses/bill/{billNo}`

Retrieve all expense transactions for a specific bill.

#### Response
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

### Get Expenses by Folio Number
**GET** `/api/transactions/expenses/folio/{folioNo}`

Retrieve all expense transactions for a specific folio.

#### Response
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

## Sales Receipts

### Create Sales Receipt
**POST** `/api/transactions/sales-receipts`

Create a new sales receipt.

#### Request Body
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

#### Response
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

### Get All Sales Receipts
**GET** `/api/transactions/sales-receipts`

Retrieve all sales receipts.

#### Response
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
    }
  ]
}
```

## Error Responses
All endpoints return error responses in the following format when an error occurs:

```json
{
  "success": false,
  "message": "Failed to create expense: Room not found: 999"
}
```