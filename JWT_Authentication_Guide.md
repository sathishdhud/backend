# JWT Authentication Usage Guide

## Overview
The Hotel Management System now uses JWT (JSON Web Token) based authentication to maintain user sessions until logout.

## How It Works

### 1. Login Process
```http
POST /api/auth/login
Content-Type: application/json

{
  "userName": "admin",
  "password": "admin123"
}
```

**Success Response:**
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "userId": "USR001",
    "userName": "admin",
    "userTypeId": "UTYPE001",
    "userTypeName": "Administrator",
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiJVU1IwMDEiLCJ1c2VyTmFtZSI6ImFkbWluIiwidXNlclR5cGVJZCI6IlVUWVBFMDAxIiwic3ViIjoiYWRtaW4iLCJpYXQiOjE3MjYzMDMyMDAsImV4cCI6MTcyNjM4OTYwMH0.signature"
  },
  "timestamp": "2025-09-14T12:00:00"
}
```

### 2. Using the Token
After successful login, use the token in the Authorization header for all API requests:

```http
GET /api/reservations
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiJVU1IwMDEi...
```

### 3. Session Maintenance
- **Token Validity**: 24 hours from generation
- **Auto-renewal**: Tokens do not auto-renew; user must login again after expiration
- **Session Persistence**: Session is maintained until logout or token expiration

### 4. Logout Process
```http
POST /api/auth/logout
Authorization: Bearer <your-jwt-token>
```

**Success Response:**
```json
{
  "success": true,
  "message": "Logout successful",
  "data": null,
  "timestamp": "2025-09-14T12:30:00"
}
```

## Security Features

### Token Blacklisting
- When a user logs out, the token is added to a blacklist
- Blacklisted tokens cannot be used even if they haven't expired
- This ensures secure logout functionality

### Token Structure
The JWT token contains:
- `userId`: User's unique identifier
- `userName`: Username used for login
- `userTypeId`: User's role/type identifier
- `iat`: Token issued time
- `exp`: Token expiration time

### Error Handling

**Invalid Credentials:**
```json
{
  "success": false,
  "message": "Invalid username or password",
  "data": null,
  "timestamp": "2025-09-14T12:00:00"
}
```

**Expired Token:**
```json
{
  "success": false,
  "message": "Token expired",
  "data": null,
  "timestamp": "2025-09-14T12:00:00"
}
```

**Invalid Token:**
```json
{
  "success": false,
  "message": "Invalid token",
  "data": null,
  "timestamp": "2025-09-14T12:00:00"
}
```

## Implementation Details

### Backend Components
1. **JwtService**: Handles token generation and validation
2. **TokenBlacklistService**: Manages invalidated tokens
3. **JwtAuthenticationFilter**: Processes incoming requests with JWT tokens
4. **SecurityConfig**: Configures Spring Security with JWT authentication

### Configuration
JWT settings in `application.properties`:
```properties
# JWT Configuration
jwt.secret=hotelworks-jwt-secret-key-2024-very-long-and-secure-key-for-signing
jwt.expiration=86400000  # 24 hours in milliseconds
```

## Testing the System

### Step 1: Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"userName":"admin","password":"admin123"}'
```

### Step 2: Use Protected Endpoint
```bash
curl -X GET http://localhost:8080/api/reservations \
  -H "Authorization: Bearer <token-from-login-response>"
```

### Step 3: Logout
```bash
curl -X POST http://localhost:8080/api/auth/logout \
  -H "Authorization: Bearer <your-token>"
```

## Migration from Basic Auth
- Old basic authentication endpoints still work for backward compatibility
- New applications should use JWT authentication for better security
- JWT tokens provide stateless authentication suitable for distributed systems