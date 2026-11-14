package com.startup.controller;

import com.startup.dto.UserProfileResponse;
import com.startup.dto.UserProfileUpdateRequest;
import com.startup.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

/**
 * REST controller for authenticated user profile operations.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Get current user's profile: GET /api/users/profile
     */
    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> getProfile(Principal principal) {
        UserProfileResponse response = userService.getUserProfile(principal.getName());
        return ResponseEntity.ok(response);
    }

    /**
     * Update current user's profile: PUT /api/users/profile
     */
    @PutMapping("/profile")
    public ResponseEntity<UserProfileResponse> updateProfile(
            Principal principal,
            @Valid @RequestBody UserProfileUpdateRequest updateRequest) {
        UserProfileResponse response = userService.updateUserProfile(principal.getName(), updateRequest);
        return ResponseEntity.ok(response);
    }

    /**
     * Health Check API: GET /api/users/health
     */
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Startup Idea Validation Platform API is up and running!");
    }
}
