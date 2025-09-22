package com.hotelworks.controller;

import com.hotelworks.dto.request.UserLoginRequest;
import com.hotelworks.dto.response.ApiResponse;
import com.hotelworks.dto.response.UserLoginResponse;
import com.hotelworks.service.UserManagementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "JWT Authentication", description = "JWT-based Authentication APIs with Session Management")
public class JwtAuthController {

    @Autowired
    private UserManagementService userManagementService;

    @PostMapping("/login")
    @Operation(summary = "User login with session management", 
               description = "Authenticate user and return JWT token with 30-minute expiration. " +
                           "Token will be invalidated when browser tab is closed or after 30 minutes.")
    public ResponseEntity<ApiResponse<UserLoginResponse>> login(
            @Valid @RequestBody UserLoginRequest request,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) {
        try {
            UserLoginResponse response = userManagementService.authenticateUser(request);
            
            // Create HTTP session for tab closing detection
            HttpSession session = httpRequest.getSession(true);
            session.setAttribute("userId", response.getUserId());
            session.setAttribute("token", response.getToken());
            
            // Set session timeout to 30 minutes (1800 seconds)
            session.setMaxInactiveInterval(1800);
            
            // Set cookie with SameSite attribute for better security
            httpResponse.setHeader("Set-Cookie", "JSESSIONID=" + session.getId() + 
                                 "; Path=/; HttpOnly; SameSite=Lax; Max-Age=1800");
            
            return ResponseEntity.ok(ApiResponse.success("Login successful", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Login failed: " + e.getMessage()));
        }
    }

    @PostMapping("/logout")
    @Operation(summary = "User logout with session invalidation", 
               description = "Logout user, invalidate JWT token, and destroy session")
    public ResponseEntity<ApiResponse<String>> logout(
            @RequestHeader("Authorization") String authorizationHeader,
            HttpServletRequest httpRequest) {
        try {
            // Blacklist the token
            userManagementService.logoutUser(authorizationHeader);
            
            // Invalidate HTTP session
            HttpSession session = httpRequest.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            
            return ResponseEntity.ok(ApiResponse.success("Logout successful", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Logout failed: " + e.getMessage()));
        }
    }
}