package com.startup.controller;

import com.startup.dto.AuthResponse;
import com.startup.dto.LoginRequest;
import com.startup.dto.RegisterRequest;
import com.startup.dto.UserProfileResponse;
import com.startup.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

/**
 * REST controller for public authentication and registration endpoints.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    /**
     * User registration endpoint: POST /api/auth/register (or /api/auth/signup)
     */
    @PostMapping(value = {"/register", "/signup"})
    public ResponseEntity<AuthResponse> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        AuthResponse response = userService.registerUser(registerRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * User login endpoint: POST /api/auth/login (or /api/auth/signin)
     */
    @PostMapping(value = {"/login", "/signin"})
    public ResponseEntity<AuthResponse> loginUser(@Valid @RequestBody LoginRequest loginRequest) {
        AuthResponse response = userService.loginUser(loginRequest);
        return ResponseEntity.ok(response);
    }

    /**
     * Current authenticated user profile check: GET /api/auth/me
     */
    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getCurrentUser(Principal principal) {
        UserProfileResponse response = userService.getUserProfile(principal.getName());
        return ResponseEntity.ok(response);
    }
}
