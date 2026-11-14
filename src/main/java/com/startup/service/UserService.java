package com.startup.service;

import com.startup.dto.AuthResponse;
import com.startup.dto.LoginRequest;
import com.startup.dto.RegisterRequest;
import com.startup.dto.UserProfileResponse;
import com.startup.dto.UserProfileUpdateRequest;

/**
 * Service interface defining user registration, authentication, and profile management operations.
 */
public interface UserService {

    AuthResponse registerUser(RegisterRequest registerRequest);

    AuthResponse loginUser(LoginRequest loginRequest);

    UserProfileResponse getUserProfile(String usernameOrEmail);

    UserProfileResponse updateUserProfile(String usernameOrEmail, UserProfileUpdateRequest updateRequest);
}
