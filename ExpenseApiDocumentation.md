# Expense Management API Documentation

## Overview
This API provides endpoints for managing hotel expenses including creation, retrieval, updating, and deletion of expense records.

## Base URL
```
/api/expenses
```

## Authentication
All endpoints require authentication with either ADMIN or USER role.

## Expense Data Model

### ExpenseRequest
```json
{
  "voucherNo": "string*",
  "date": "string (YYYY-MM-DD)*",
  "accountHeadId": "string*",
  "amount": "number*",
  "narration": "string",
  "shiftNo": "string*",
  "shiftDate": "string (YYYY-MM-DD)*"
}
```

### ExpenseResponse
```json
{
  "transactionId": "string",
  "voucherNo": "string",
  "date": "string (YYYY-MM-DD)",
  "accountHeadId": "string",
  "accountHeadName": "string",
  "amount": "number",
  "narration": "string",
  "shiftNo": "string",
  "shiftDate": "string (YYYY-MM-DD)"
}
```

## API Endpoints

### 1. Create Expense
**POST** `/api/expenses`

Create a new hotel expense.

#### Request Body
```json
{
  "voucherNo": "EXP-2023-001",
  "date": "2023-06-15",
  "accountHeadId": "ACC001",
  "amount": 1500.00,
  "narration": "Office supplies purchase",
  "shiftNo": "1",
  "shiftDate": "2023-06-15"
}
```

#### Response
```json
{
  "success": true,
  "message": "Expense created successfully",
  "data": {
    "transactionId": "EXP123456",
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
  "message": "Failed to create expense: Account head not found: ACC999"
}
```

---

### 2. Get Expense by ID
**GET** `/api/expenses/{expenseId}`

Retrieve an expense by its ID.

#### Path Parameters
- `expenseId` (string, required): The expense ID

#### Response
```json
{
  "success": true,
  "message": "Operation successful",
  "data": {
    "transactionId": "EXP123456",
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
  "message": "Failed to retrieve expense: Expense not found with ID: EXP999999"
}
```

---

### 3. Update Expense
**PUT** `/api/expenses/{expenseId}`

Update an existing expense.

#### Path Parameters
- `expenseId` (string, required): The expense ID

#### Request Body
```json
{
  "voucherNo": "EXP-2023-002",
  "date": "2023-06-16",
  "accountHeadId": "ACC002",
  "amount": 2500.00,
  "narration": "Maintenance services",
  "shiftNo": "2",
  "shiftDate": "2023-06-16"
}
```

#### Response
```json
{
  "success": true,
  "message": "Expense updated successfully",
  "data": {
    "transactionId": "EXP123456",
    "voucherNo": "EXP-2023-002",
    "date": "2023-06-16",
    "accountHeadId": "ACC002",
    "accountHeadName": "Maintenance",
    "amount": 2500.00,
    "narration": "Maintenance services",
    "shiftNo": "2",
    "shiftDate": "2023-06-16"
  }
}
```

#### Example Response (Error)
```json
{
  "success": false,
  "message": "Failed to update expense: Expense not found with ID: EXP999999"
}
```

---

### 4. Delete Expense
**DELETE** `/api/expenses/{expenseId}`

Delete an expense by its ID.

#### Path Parameters
- `expenseId` (string, required): The expense ID

#### Response
```json
{
  "success": true,
  "message": "Expense deleted successfully",
  "data": null
}
```

#### Example Response (Error)
```json
{
  "success": false,
  "message": "Failed to delete expense: Expense not found with ID: EXP999999"
}
```

---

### 5. Get All Expenses
**GET** `/api/expenses`

Retrieve all expenses.

#### Response
```json
{
  "success": true,
  "message": "Operation successful",
  "data": [
    {
      "transactionId": "EXP123456",
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
      "transactionId": "EXP123457",
      "voucherNo": "EXP-2023-002",
      "date": "2023-06-16",
      "accountHeadId": "ACC002",
      "accountHeadName": "Maintenance",
      "amount": 2500.00,
      "narration": "Maintenance services",
      "shiftNo": "2",
      "shiftDate": "2023-06-16"
    }
  ]
}
```

#### Example Response (Error)
```json
{
  "success": false,
  "message": "Failed to retrieve expenses: Database connection error"
}
```

---

### 6. Get Expenses by Voucher Number
**GET** `/api/expenses/voucher/{voucherNo}`

Retrieve expenses by voucher number.

#### Path Parameters
- `voucherNo` (string, required): The voucher number

#### Response
```json
{
  "success": true,
  "message": "Operation successful",
  "data": [
    {
      "transactionId": "EXP123456",
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

#### Example Response (Error)
```json
{
  "success": false,
  "message": "Failed to retrieve expenses: No expenses found for voucher number: EXP-9999"
}
```

---

### 7. Get Expenses by Account Head
**GET** `/api/expenses/account-head/{accountHeadId}`

Retrieve expenses by account head ID.

#### Path Parameters
- `accountHeadId` (string, required): The account head ID

#### Response
```json
{
  "success": true,
  "message": "Operation successful",
  "data": [
    {
      "transactionId": "EXP123456",
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

#### Example Response (Error)
```json
{
  "success": false,
  "message": "Failed to retrieve expenses: No expenses found for account head: ACC999"
}
```