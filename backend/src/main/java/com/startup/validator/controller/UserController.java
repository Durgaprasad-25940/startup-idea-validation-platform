package com.startup.validator.controller;

import com.startup.validator.dto.UserProfileResponse;
import com.startup.validator.dto.UserProfileUpdateRequest;
import com.startup.validator.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

/**
 * UserController exposes profile endpoints requiring authorization.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Endpoint to view currently logged-in user profile: GET /api/users/profile
     * 
     * Explanation:
     * `Principal`: An interface injected by Spring containing the authenticated identity (username/email) extracted from the JWT token.
     */
    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> getUserProfile(Principal principal) {
        // principal.getName() extracts the authenticated subject (username)
        UserProfileResponse response = userService.getUserProfile(principal.getName());
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint to update user profile: PUT /api/users/profile
     * Expects JSON content matching the UserProfileUpdateRequest DTO.
     */
    @PutMapping("/profile")
    public ResponseEntity<UserProfileResponse> updateUserProfile(
            Principal principal,
            @Valid @RequestBody UserProfileUpdateRequest updateRequest) {
        
        UserProfileResponse response = userService.updateUserProfile(principal.getName(), updateRequest);
        return ResponseEntity.ok(response);
    }
}
