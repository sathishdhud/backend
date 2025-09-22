# Authentication Testing Plan

This document outlines the testing plan to verify that all authentication requirements have been properly implemented.

## Test Scenarios

### Scenario 1: Token Expiration within 30 Minutes

**Objective**: Verify that tokens expire after 30 minutes of inactivity

**Steps**:
1. Log in to the application
2. Note the login time
3. Stay inactive for 30 minutes
4. Attempt to access a protected resource
5. Verify automatic redirect to login page

**Expected Results**:
- User receives warning 5 minutes before expiration
- Token expires exactly 30 minutes after creation
- User is automatically redirected to login page
- Appropriate expiration message is displayed

### Scenario 2: Token Expiration on Tab Close

**Objective**: Verify that tokens are invalidated when browser tab is closed

**Steps**:
1. Log in to the application in a browser tab
2. Note the tab ID (visible in sessionStorage)
3. Close the browser tab
4. Reopen the application URL in a new tab
5. Verify redirect to login page

**Expected Results**:
- Token is cleared immediately when tab closes
- Closure flag is set in sessionStorage
- New tab cannot access protected routes with old token
- User is redirected to login page

### Scenario 3: Direct URL Access Prevention

**Objective**: Verify that direct URL access to protected routes redirects to login

**Steps**:
1. Ensure user is logged out or token is expired
2. Directly navigate to a protected route (e.g., /dashboard)
3. Verify automatic redirect to login page

**Expected Results**:
- Authentication check runs on page load
- Protected routes are inaccessible without valid token
- User is redirected to login page
- No access to protected content

### Scenario 4: Session Extension

**Objective**: Verify that users can extend their sessions

**Steps**:
1. Log in to the application
2. Wait for 25 minutes (5 minutes before expiration)
3. Click "Stay Logged In" when warning appears
4. Continue using application for another 30 minutes
5. Verify session extension works

**Expected Results**:
- Warning appears 5 minutes before expiration
- Session extends for another 30 minutes when user chooses to stay
- Token expiry time is updated
- User can continue working without interruption

### Scenario 5: Cross-Tab Isolation

**Objective**: Verify that tokens are isolated to their creating tab

**Steps**:
1. Log in to the application in Tab A
2. Copy the URL from Tab A
3. Paste URL in Tab B
4. Verify Tab B requires separate login

**Expected Results**:
- Tab A has valid authentication
- Tab B redirects to login (no token reuse)
- Each tab has unique session
- Tokens don't work across tabs

### Scenario 6: Backend Token Validation

**Objective**: Verify server-side token validation

**Steps**:
1. Log in to the application
2. Manually modify the token in localStorage
3. Make an API request with modified token
4. Verify server rejects invalid token

**Expected Results**:
- Server validates token signature
- Server checks token expiration
- Invalid tokens are rejected with 401 error
- Appropriate error message is returned

## Test Data

### Valid User Credentials
- Username: admin
- Password: admin123

### Test URLs
- Login: /login
- Dashboard: /dashboard
- Protected API: /api/reservations
- Logout: /api/auth/logout

## Test Tools

### Browser Developer Tools
- Network tab to monitor API requests
- Application tab to view localStorage/sessionStorage
- Console to view JavaScript logs

### API Testing Tools
- Postman or curl for direct API testing
- JWT decoder to inspect token contents

## Expected Outcomes

### Success Criteria
- All scenarios pass as described
- No security vulnerabilities identified
- User experience is smooth and intuitive
- Error handling is appropriate and user-friendly

### Failure Indicators
- Tokens don't expire after 30 minutes
- Tokens remain valid after tab closing
- Direct URL access bypasses authentication
- Session extension doesn't work
- Cross-tab token reuse is possible
- Server accepts invalid tokens

## Test Execution

### Manual Testing
1. Execute each scenario manually in browser
2. Document results and any issues found
3. Verify error messages are appropriate
4. Confirm user experience is smooth

### Automated Testing
1. Create automated tests for critical scenarios
2. Include token validation tests
3. Test API endpoints with valid/invalid tokens
4. Verify redirect behavior

## Edge Cases

### Browser Refresh
- Token should remain valid after refresh (if not expired)
- Session should continue normally

### Multiple Tabs
- Each tab should have independent authentication
- Closing one tab shouldn't affect others

### Network Disconnection
- System should handle gracefully
- Token validation should continue when connection restored

### Browser Back/Forward
- Authentication state should be consistent
- No bypassing of security checks

## Reporting

### Test Results Documentation
- Record results for each scenario
- Document any failures or unexpected behavior
- Include screenshots for UI-related issues
- Provide steps to reproduce any issues found

### Issue Resolution
- Prioritize security-related issues
- Address user experience problems
- Fix any bypasses of authentication requirements
- Verify fixes with retesting

This testing plan ensures comprehensive verification of the enhanced authentication system and confirms that all requirements are properly implemented.