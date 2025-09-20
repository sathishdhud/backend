# Transaction API Documentation

This document provides detailed information about the Transaction API endpoints, including request/response examples with GST handling.

## Base URL
```
http://localhost:8080/api/transactions
```

## Endpoints

### 1. Create Transaction for In-House Guest

**Endpoint:** `POST /inhouse`

**Description:** Create a post transaction for an in-house guest with automatic GST handling.

**Request Body:**
```json
{
  "folioNo": "FOLIO001",
  "roomId": "ROOM001",
  "guestName": "John Doe",
  "accHeadId": "ROOM_CHARGE",
  "amount": 1000.00,
  "includingGst": "Y",
  "narration": "Room charge for 2024-01-15"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Transaction created successfully for in-house guest",
  "data": {
    "transactionId": "TXN001",
    "folioNo": "FOLIO001",
    "billNo": "",
    "roomId": "ROOM001",
    "roomNo": "101",
    "guestName": "John Doe",
    "date": "2024-01-15",
    "auditDate": "2024-01-15",
    "accHeadId": "ROOM_CHARGE",
    "accHeadName": "Room Charges",
    "voucherNo": "",
    "amount": 1180.00,
    "narration": "Room charge for 2024-01-15"
  },
  "timestamp": "2024-01-15T10:30:00"
}
```

**Notes:**
- When `includingGst` is set to "Y", the system automatically calculates the amount including CGST and SGST
- Base amount: ₹1000
- CGST (9%): ₹90
- SGST (9%): ₹90
- Final amount: ₹1180

### 2. Create Transaction for Checkout Guest

**Endpoint:** `POST /checkout`

**Description:** Create a post transaction for a checkout guest with manual date entry.

**Request Body:**
```json
{
  "billNo": "BILL001",
  "roomId": "ROOM001",
  "guestName": "John Doe",
  "date": "2024-01-15",
  "accHeadId": "FOOD",
  "amount": 500.00,
  "includingGst": "Y",
  "narration": "Dinner charge"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Transaction created successfully for checkout guest",
  "data": {
    "transactionId": "TXN002",
    "folioNo": "",
    "billNo": "BILL001",
    "roomId": "ROOM001",
    "roomNo": "101",
    "guestName": "John Doe",
    "date": "2024-01-15",
    "auditDate": "2024-01-15",
    "accHeadId": "FOOD",
    "accHeadName": "Food & Beverage",
    "voucherNo": "",
    "amount": 590.00,
    "narration": "Dinner charge"
  },
  "timestamp": "2024-01-15T10:30:00"
}
```

**Notes:**
- When `includingGst` is set to "Y", the system automatically calculates the amount including CGST and SGST
- Base amount: ₹500
- CGST (9%): ₹45
- SGST (9%): ₹45
- Final amount: ₹590

### 3. Update Transaction

**Endpoint:** `PUT /{transactionId}`

**Description:** Update an existing transaction with GST handling.

**Request Body:**
```json
{
  "guestName": "John Smith",
  "accHeadId": "LAUNDRY",
  "amount": 200.00,
  "includingGst": "Y",
  "narration": "Laundry service"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Transaction updated successfully",
  "data": {
    "transactionId": "TXN001",
    "folioNo": "FOLIO001",
    "billNo": "",
    "roomId": "ROOM001",
    "roomNo": "101",
    "guestName": "John Smith",
    "date": "2024-01-15",
    "auditDate": "2024-01-15",
    "accHeadId": "LAUNDRY",
    "accHeadName": "Laundry Service",
    "voucherNo": "",
    "amount": 236.00,
    "narration": "Laundry service"
  },
  "timestamp": "2024-01-15T11:00:00"
}
```

**Notes:**
- When `includingGst` is set to "Y", the system automatically calculates the amount including CGST and SGST
- Base amount: ₹200
- CGST (9%): ₹18
- SGST (9%): ₹18
- Final amount: ₹236

### 4. Get Transaction by ID

**Endpoint:** `GET /{transactionId}`

**Description:** Retrieve a specific transaction by its ID.

**Response:**
```json
{
  "success": true,
  "message": "Operation successful",
  "data": {
    "transactionId": "TXN001",
    "folioNo": "FOLIO001",
    "billNo": "",
    "roomId": "ROOM001",
    "roomNo": "101",
    "guestName": "John Smith",
    "date": "2024-01-15",
    "auditDate": "2024-01-15",
    "accHeadId": "LAUNDRY",
    "accHeadName": "Laundry Service",
    "voucherNo": "",
    "amount": 236.00,
    "narration": "Laundry service"
  },
  "timestamp": "2024-01-15T11:15:00"
}
```

### 5. Get Transactions by Folio

**Endpoint:** `GET /folio/{folioNo}`

**Description:** Retrieve all transactions for a specific folio.

**Response:**
```json
{
  "success": true,
  "message": "Operation successful",
  "data": [
    {
      "transactionId": "TXN001",
      "folioNo": "FOLIO001",
      "billNo": "",
      "roomId": "ROOM001",
      "roomNo": "101",
      "guestName": "John Smith",
      "date": "2024-01-15",
      "auditDate": "2024-01-15",
      "accHeadId": "LAUNDRY",
      "accHeadName": "Laundry Service",
      "voucherNo": "",
      "amount": 236.00,
      "narration": "Laundry service"
    },
    {
      "transactionId": "TXN002",
      "folioNo": "FOLIO001",
      "billNo": "",
      "roomId": "ROOM001",
      "roomNo": "101",
      "guestName": "John Smith",
      "date": "2024-01-15",
      "auditDate": "2024-01-15",
      "accHeadId": "ROOM_CHARGE",
      "accHeadName": "Room Charges",
      "voucherNo": "",
      "amount": 1180.00,
      "narration": "Room charge for 2024-01-15"
    }
  ],
  "timestamp": "2024-01-15T11:20:00"
}
```

### 6. Get Transactions by Bill

**Endpoint:** `GET /bill/{billNo}`

**Description:** Retrieve all transactions for a specific bill.

**Response:**
```json
{
  "success": true,
  "message": "Operation successful",
  "data": [
    {
      "transactionId": "TXN003",
      "folioNo": "",
      "billNo": "BILL001",
      "roomId": "ROOM001",
      "roomNo": "101",
      "guestName": "John Doe",
      "date": "2024-01-15",
      "auditDate": "2024-01-15",
      "accHeadId": "FOOD",
      "accHeadName": "Food & Beverage",
      "voucherNo": "",
      "amount": 590.00,
      "narration": "Dinner charge"
    }
  ],
  "timestamp": "2024-01-15T11:25:00"
}
```

### 7. Get Transactions by Room

**Endpoint:** `GET /room/{roomId}`

**Description:** Retrieve all transactions for a specific room.

**Response:**
```json
{
  "success": true,
  "message": "Operation successful",
  "data": [
    {
      "transactionId": "TXN001",
      "folioNo": "FOLIO001",
      "billNo": "",
      "roomId": "ROOM001",
      "roomNo": "101",
      "guestName": "John Smith",
      "date": "2024-01-15",
      "auditDate": "2024-01-15",
      "accHeadId": "LAUNDRY",
      "accHeadName": "Laundry Service",
      "voucherNo": "",
      "amount": 236.00,
      "narration": "Laundry service"
    },
    {
      "transactionId": "TXN002",
      "folioNo": "FOLIO001",
      "billNo": "",
      "roomId": "ROOM001",
      "roomNo": "101",
      "guestName": "John Smith",
      "date": "2024-01-15",
      "auditDate": "2024-01-15",
      "accHeadId": "ROOM_CHARGE",
      "accHeadName": "Room Charges",
      "voucherNo": "",
      "amount": 1180.00,
      "narration": "Room charge for 2024-01-15"
    },
    {
      "transactionId": "TXN003",
      "folioNo": "",
      "billNo": "BILL001",
      "roomId": "ROOM001",
      "roomNo": "101",
      "guestName": "John Doe",
      "date": "2024-01-15",
      "auditDate": "2024-01-15",
      "accHeadId": "FOOD",
      "accHeadName": "Food & Beverage",
      "voucherNo": "",
      "amount": 590.00,
      "narration": "Dinner charge"
    }
  ],
  "timestamp": "2024-01-15T11:30:00"
}
```

### 8. Get Total Transactions by Folio

**Endpoint:** `GET /folio/{folioNo}/total`

**Description:** Get the total transaction amount for a specific folio.

**Response:**
```json
{
  "success": true,
  "message": "Operation successful",
  "data": 1416.00,
  "timestamp": "2024-01-15T11:35:00"
}
```

### 9. Get Total Transactions by Bill

**Endpoint:** `GET /bill/{billNo}/total`

**Description:** Get the total transaction amount for a specific bill.

**Response:**
```json
{
  "success": true,
  "message": "Operation successful",
  "data": 590.00,
  "timestamp": "2024-01-15T11:40:00"
}
```

### 10. Get Transactions by Date Range

**Endpoint:** `GET /date-range?startDate=2024-01-15&endDate=2024-01-15`

**Description:** Retrieve transactions between two dates.

**Response:**
```json
{
  "success": true,
  "message": "Operation successful",
  "data": [
    {
      "transactionId": "TXN001",
      "folioNo": "FOLIO001",
      "billNo": "",
      "roomId": "ROOM001",
      "roomNo": "101",
      "guestName": "John Smith",
      "date": "2024-01-15",
      "auditDate": "2024-01-15",
      "accHeadId": "LAUNDRY",
      "accHeadName": "Laundry Service",
      "voucherNo": "",
      "amount": 236.00,
      "narration": "Laundry service"
    },
    {
      "transactionId": "TXN002",
      "folioNo": "FOLIO001",
      "billNo": "",
      "roomId": "ROOM001",
      "roomNo": "101",
      "guestName": "John Smith",
      "date": "2024-01-15",
      "auditDate": "2024-01-15",
      "accHeadId": "ROOM_CHARGE",
      "accHeadName": "Room Charges",
      "voucherNo": "",
      "amount": 1180.00,
      "narration": "Room charge for 2024-01-15"
    },
    {
      "transactionId": "TXN003",
      "folioNo": "",
      "billNo": "BILL001",
      "roomId": "ROOM001",
      "roomNo": "101",
      "guestName": "John Doe",
      "date": "2024-01-15",
      "auditDate": "2024-01-15",
      "accHeadId": "FOOD",
      "accHeadName": "Food & Beverage",
      "voucherNo": "",
      "amount": 590.00,
      "narration": "Dinner charge"
    }
  ],
  "timestamp": "2024-01-15T11:45:00"
}
```

### 11. Delete Transaction

**Endpoint:** `DELETE /{transactionId}`

**Description:** Delete a specific transaction by its ID.

**Response:**
```json
{
  "success": true,
  "message": "Transaction deleted successfully",
  "data": null,
  "timestamp": "2024-01-15T11:50:00"
}
```

## GST Handling Logic

When creating or updating transactions with the `includingGst` field set to "Y":

1. **GST Calculation:**
   - The system retrieves CGST and SGST rates from the taxation table
   - CGST and SGST are typically 9% each (18% total)
   - Final amount = Base Amount × (1 + (CGST + SGST) / 100)

2. **Example Calculations:**
   - Base amount: ₹1000
   - CGST (9%): ₹90
   - SGST (9%): ₹90
   - Final amount: ₹1180

3. **Storage:**
   - The calculated amount (including taxes) is stored in the database
   - This ensures accurate billing and reporting throughout the system

## Error Responses

All error responses follow the same format:

```json
{
  "success": false,
  "message": "Error description",
  "data": null,
  "timestamp": "2024-01-15T10:30:00"
}
```

Common error scenarios:
- Invalid folio or bill number
- Missing required fields
- Invalid account head ID
- Amount must be greater than zero
- Transaction not found