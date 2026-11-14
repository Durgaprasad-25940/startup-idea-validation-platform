package com.startup.validator.controller;

import com.startup.validator.dto.AuthResponse;
import com.startup.validator.dto.LoginRequest;
import com.startup.validator.dto.RegisterRequest;
import com.startup.validator.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * AuthController manages public registration and login endpoint requests.
 * 
 * Explanation of Annotations:
 * `@RestController`: Marks this class as an API controller where response bodies are written directly to HTTP responses in JSON.
 * `@RequestMapping`: Sets base URL path mappings.
 * `@PostMapping`: Maps HTTP POST requests to specified subpaths.
 * `@Valid`: Triggers evaluation of input validation annotations (like `@NotBlank`) on the incoming DTO.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Endpoint for user registration: POST /api/auth/register
     * Expects JSON content matching the RegisterRequest DTO.
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        AuthResponse response = userService.registerUser(registerRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Endpoint for user login: POST /api/auth/login
     * Expects JSON credentials matching the LoginRequest DTO.
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> loginUser(@Valid @RequestBody LoginRequest loginRequest) {
        AuthResponse response = userService.loginUser(loginRequest);
        return ResponseEntity.ok(response);
    }
}
