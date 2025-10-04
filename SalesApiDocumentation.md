# Sales Management API Documentation

## Overview
This API provides endpoints for managing hotel sales records including creation, retrieval, updating, and deletion of sales records.

## Base URL
```
/api/sales
```

## Authentication
All endpoints require authentication with either ADMIN or USER role.

## Sales Data Model

### SalesRequest
```json
{
  "receiptNumber": "string*",
  "date": "string (YYYY-MM-DD)*",
  "modeOfPayment": "string*",
  "amount": "number*",
  "voucherNumber": "string*",
  "narration": "string"
}
```

### SalesResponse
```json
{
  "salesId": "string",
  "receiptNumber": "string",
  "date": "string (YYYY-MM-DD)",
  "modeOfPayment": "string",
  "amount": "number",
  "voucherNumber": "string",
  "narration": "string"
}
```

## API Endpoints

### 1. Create Sales Record
**POST** `/api/sales`

Create a new hotel sales record.

#### Request Body
```json
{
  "receiptNumber": "REC-2023-001",
  "date": "2023-06-15",
  "modeOfPayment": "Cash",
  "amount": 1500.00,
  "voucherNumber": "V-001",
  "narration": "Room service"
}
```

#### Response
```json
{
  "success": true,
  "message": "Sales record created successfully",
  "data": {
    "salesId": "S123456",
    "receiptNumber": "REC-2023-001",
    "date": "2023-06-15",
    "modeOfPayment": "Cash",
    "amount": 1500.00,
    "voucherNumber": "V-001",
    "narration": "Room service"
  }
}
```

#### Example Response (Error)
```json
{
  "success": false,
  "message": "Failed to create sales record: Invalid date format"
}
```

---

### 2. Get Sales by ID
**GET** `/api/sales/{salesId}`

Retrieve a sales record by its ID.

#### Path Parameters
- `salesId` (string, required): The sales record ID

#### Response
```json
{
  "success": true,
  "message": "Operation successful",
  "data": {
    "salesId": "S123456",
    "receiptNumber": "REC-2023-001",
    "date": "2023-06-15",
    "modeOfPayment": "Cash",
    "amount": 1500.00,
    "voucherNumber": "V-001",
    "narration": "Room service"
  }
}
```

#### Example Response (Error)
```json
{
  "success": false,
  "message": "Sales record not found: Sales not found with ID: S999999"
}
```

---

### 3. Update Sales Record
**PUT** `/api/sales/{salesId}`

Update an existing sales record.

#### Path Parameters
- `salesId` (string, required): The sales record ID

#### Request Body
```json
{
  "receiptNumber": "REC-2023-002",
  "date": "2023-06-16",
  "modeOfPayment": "Credit Card",
  "amount": 2500.00,
  "voucherNumber": "V-002",
  "narration": "Restaurant service"
}
```

#### Response
```json
{
  "success": true,
  "message": "Sales record updated successfully",
  "data": {
    "salesId": "S123456",
    "receiptNumber": "REC-2023-002",
    "date": "2023-06-16",
    "modeOfPayment": "Credit Card",
    "amount": 2500.00,
    "voucherNumber": "V-002",
    "narration": "Restaurant service"
  }
}
```

#### Example Response (Error)
```json
{
  "success": false,
  "message": "Failed to update sales record: Sales not found with ID: S999999"
}
```

---

### 4. Delete Sales Record
**DELETE** `/api/sales/{salesId}`

Delete a sales record by its ID.

#### Path Parameters
- `salesId` (string, required): The sales record ID

#### Response
```json
{
  "success": true,
  "message": "Sales record deleted successfully",
  "data": null
}
```

#### Example Response (Error)
```json
{
  "success": false,
  "message": "Failed to delete sales record: Sales not found with ID: S999999"
}
```

---

### 5. Get All Sales Records
**GET** `/api/sales`

Retrieve all sales records.

#### Response
```json
{
  "success": true,
  "message": "Operation successful",
  "data": [
    {
      "salesId": "S123456",
      "receiptNumber": "REC-2023-001",
      "date": "2023-06-15",
      "modeOfPayment": "Cash",
      "amount": 1500.00,
      "voucherNumber": "V-001",
      "narration": "Room service"
    },
    {
      "salesId": "S123457",
      "receiptNumber": "REC-2023-002",
      "date": "2023-06-16",
      "modeOfPayment": "Credit Card",
      "amount": 2500.00,
      "voucherNumber": "V-002",
      "narration": "Restaurant service"
    }
  ]
}
```

#### Example Response (Error)
```json
{
  "success": false,
  "message": "Failed to retrieve sales records: Database connection error"
}
```