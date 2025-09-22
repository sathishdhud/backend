# JWT Authentication API Documentation

This document provides detailed information about the JWT Authentication APIs with enhanced session management including 30-minute token expiration and tab closing detection.

## Base URL
```
http://localhost:8080/api/auth
```

## Endpoints

### 1. User Login with Session Management

**Endpoint:** `POST /login`

**Description:** Authenticate user and return JWT token with 30-minute expiration. Token will be invalidated when browser tab is closed or after 30 minutes.

**Request Body:**
```json
{
  "userName": "admin",
  "password": "admin123"
}
```

**Response:**
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "userId": "USR001",
    "userName": "admin",
    "userTypeId": "UTYPE001",
    "userTypeRole": "Administrator",
    "userTypeName": "Administrator",
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "loginSuccess": true,
    "permissions": [
      {
        "moduleName": "reservations",
        "permissionType": "full"
      }
    ]
  },
  "timestamp": "2025-09-22T10:30:00"
}
```

### 2. User Logout with Session Invalidation

**Endpoint:** `POST /logout`

**Description:** Logout user, invalidate JWT token, and destroy session.

**Headers:**
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Response:**
```json
{
  "success": true,
  "message": "Logout successful",
  "data": null,
  "timestamp": "2025-09-22T10:30:00"
}
```

## Token Management Features

### 1. 30-Minute Token Expiration
- All JWT tokens now expire after 30 minutes (1800 seconds)
- Automatic token validation and expiration handling
- Warning notification 5 minutes before token expiry
- Automatic redirect to login page on token expiration

### 2. Tab Closing Detection
- Tokens are automatically cleared when browser tab is closed
- Session storage is used to track tab visibility state
- Tokens are invalidated when tab has been hidden for more than 30 minutes
- Unique tab identification prevents token reuse across tabs

### 3. Direct URL Access Prevention
- Authentication status checked on every page load
- Automatic redirect to login page when accessing protected routes without valid authentication
- Works for both token expiration and tab closing scenarios

### 4. Session Security
- HttpOnly cookies with SameSite attribute for CSRF protection
- Session timeout synchronized with token expiration
- Automatic token blacklisting on logout
- Enhanced validation every 10 seconds for better accuracy

## Implementation Details

### Backend Implementation
1. **JWT Configuration**: Token expiration reduced from 24 hours to 30 minutes
2. **Session Management**: HTTP sessions created with 30-minute timeout
3. **Token Blacklisting**: Tokens invalidated immediately on logout
4. **Enhanced Controller**: New JwtAuthController with session handling
5. **Improved Filter**: Enhanced JwtAuthenticationFilter with additional validation

### Frontend Implementation
1. **Session Manager**: JavaScript class for comprehensive token management
2. **Automatic Validation**: Periodic token validation every 10 seconds
3. **Expiry Handling**: Automatic logout on token expiration
4. **User Notifications**: Warning alerts before token expiry with session extension option
5. **Tab Isolation**: Unique tab IDs prevent token reuse across browser tabs
6. **Closure Detection**: Multiple event listeners for comprehensive tab closing detection

## Security Features

### Token Security
- 30-minute expiration time for enhanced security
- Immediate invalidation on logout
- Automatic clearing on tab close
- Blacklisting to prevent token reuse
- Tab isolation to prevent cross-tab token usage

### Session Security
- HttpOnly cookies to prevent XSS attacks
- SameSite attribute to prevent CSRF attacks
- Synchronized session and token expiration
- Visibility change detection for tab management
- Direct URL access prevention for protected routes

## Error Responses

### Invalid Credentials
```json
{
  "success": false,
  "message": "Login failed: Invalid username or password",
  "data": null,
  "timestamp": "2025-09-22T10:30:00"
}
```

### Expired Token
```json
{
  "success": false,
  "message": "Login failed: Token expired",
  "data": null,
  "timestamp": "2025-09-22T10:30:00"
}
```

### Invalid Token
```json
{
  "error": "Invalid token"
}
```

### Blacklisted Token
```json
{
  "error": "Token has been invalidated"
}
```

## Usage Examples

### Login Request
```javascript
// Using fetch API
fetch('/api/auth/login', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json'
  },
  body: JSON.stringify({
    userName: 'admin',
    password: 'admin123'
  })
})
.then(response => response.json())
.then(data => {
  if (data.success) {
    // Save token using session manager
    window.sessionManager.saveToken(data.data.token);
    // Redirect to dashboard
    window.location.href = '/dashboard';
  } else {
    console.error('Login failed:', data.message);
  }
});
```

### Authenticated Request
```javascript
// Using fetch API with token
const token = window.sessionManager.getToken();
fetch('/api/reservations', {
  method: 'GET',
  headers: {
    'Authorization': `Bearer ${token}`,
    'Content-Type': 'application/json'
  }
})
.then(response => response.json())
.then(data => {
  // Handle response
  console.log(data);
});
```

### Logout Request
```javascript
// Using fetch API
const token = window.sessionManager.getToken();
fetch('/api/auth/logout', {
  method: 'POST',
  headers: {
    'Authorization': `Bearer ${token}`,
    'Content-Type': 'application/json'
  }
})
.then(response => response.json())
.then(data => {
  if (data.success) {
    // Clear token and redirect to login
    window.sessionManager.clearToken();
    window.location.href = '/login';
  }
});
```

## Configuration

### Application Properties
```properties
# JWT Configuration
jwt.secret=hotelworks-jwt-secret-key-2024-very-long-and-secure-key-for-signing
jwt.expiration=1800000  # 30 minutes in milliseconds
```

### Security Configuration
- Stateless sessions with JWT authentication
- Public access to login/logout endpoints
- Role-based access control for protected endpoints
- CORS configuration for cross-origin requests

## Best Practices

### Token Handling
1. Always store tokens securely (HttpOnly cookies preferred)
2. Implement automatic token refresh before expiry
3. Validate tokens on every authenticated request
4. Clear tokens immediately on logout
5. Use unique identifiers for tab isolation

### Session Management
1. Set appropriate session timeouts
2. Monitor tab visibility for security
3. Provide user warnings before session expiry
4. Handle session expiration gracefully
5. Implement comprehensive tab closing detection

### Security Recommendations
1. Use HTTPS in production environments
2. Implement proper password hashing (BCrypt)
3. Validate all user inputs
4. Regularly rotate JWT secret keys
5. Monitor authentication logs for suspicious activity
6. Implement multi-factor authentication for sensitive operations