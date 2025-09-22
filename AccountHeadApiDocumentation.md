# Account Head API Documentation

This document provides detailed information about the Account Head Management APIs, including endpoints for creating, retrieving, updating, and deleting account heads with enhanced fields.

## Base URL
```
http://localhost:8080/api/account-heads
```

## Endpoints

### 1. Create Account Head

**Endpoint:** `POST /`

**Description:** Create a new account head with company name, cheque number, and date

**Request Body:**
```json
{
  "accHeadId": "ACC001",
  "name": "Office Supplies",
  "companyName": "ABC Office Solutions",
  "chequeNumber": "CHQ20250921001",
  "date": "2025-09-21"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Account head created successfully",
  "data": {
    "accHeadId": "ACC001",
    "name": "Office Supplies",
    "companyName": "ABC Office Solutions",
    "chequeNumber": "CHQ20250921001",
    "date": "2025-09-21"
  },
  "timestamp": "2025-09-21T10:30:00"
}
```

### 2. Get All Account Heads

**Endpoint:** `GET /`

**Description:** Retrieve all account heads

**Response:**
```json
{
  "success": true,
  "message": "Account heads retrieved successfully",
  "data": [
    {
      "accHeadId": "ACC001",
      "name": "Office Supplies",
      "companyName": "ABC Office Solutions",
      "chequeNumber": "CHQ20250921001",
      "date": "2025-09-21"
    },
    {
      "accHeadId": "ACC002",
      "name": "Utilities",
      "companyName": "City Utilities Corp",
      "chequeNumber": "CHQ20250921002",
      "date": "2025-09-21"
    }
  ],
  "timestamp": "2025-09-21T10:30:00"
}
```

### 3. Get Account Head by ID

**Endpoint:** `GET /{accountHeadId}`

**Description:** Retrieve a specific account head by ID

**Response:**
```json
{
  "success": true,
  "message": "Account head retrieved successfully",
  "data": {
    "accHeadId": "ACC001",
    "name": "Office Supplies",
    "companyName": "ABC Office Solutions",
    "chequeNumber": "CHQ20250921001",
    "date": "2025-09-21"
  },
  "timestamp": "2025-09-21T10:30:00"
}
```

### 4. Update Account Head

**Endpoint:** `PUT /{accountHeadId}`

**Description:** Update an existing account head

**Request Body:**
```json
{
  "name": "Office Supplies & Equipment",
  "companyName": "ABC Office Solutions & Equipment",
  "chequeNumber": "CHQ20250921003",
  "date": "2025-09-21"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Account head updated successfully",
  "data": {
    "accHeadId": "ACC001",
    "name": "Office Supplies & Equipment",
    "companyName": "ABC Office Solutions & Equipment",
    "chequeNumber": "CHQ20250921003",
    "date": "2025-09-21"
  },
  "timestamp": "2025-09-21T10:30:00"
}
```

### 5. Delete Account Head

**Endpoint:** `DELETE /{accountHeadId}`

**Description:** Delete an account head by ID

**Response:**
```json
{
  "success": true,
  "message": "Account head deleted successfully",
  "data": null,
  "timestamp": "2025-09-21T10:30:00"
}
```

## Data Models

### Account Head Request
| Field | Type | Required | Description |
|-------|------|----------|-------------|
| accHeadId | String | Yes (auto-generated if not provided) | Unique account head identifier |
| name | String | Yes | Account head name |
| companyName | String | No | Company name associated with the account |
| chequeNumber | String | No | Cheque number for the transaction |
| date | Date | No | Date of the transaction |

### Account Head Response
| Field | Type | Description |
|-------|------|-------------|
| accHeadId | String | Unique account head identifier |
| name | String | Account head name |
| companyName | String | Company name associated with the account |
| chequeNumber | String | Cheque number for the transaction |
| date | Date | Date of the transaction |