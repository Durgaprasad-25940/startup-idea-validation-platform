package com.startup.validator.service;

import com.startup.validator.dto.*;

/**
 * UserService defines the core operations relating to user accounts.
 */
public interface UserService {

    /**
     * Registers a new user account inside the database.
     * @param registerRequest details of the new user.
     * @return AuthResponse containing token and user metadata.
     */
    AuthResponse registerUser(RegisterRequest registerRequest);

    /**
     * Authenticates a user using credentials.
     * @param loginRequest login credentials.
     * @return AuthResponse containing token and user metadata.
     */
    AuthResponse loginUser(LoginRequest loginRequest);

    /**
     * Retrieves the profile information of a user.
     * @param usernameOrEmail the identifier of the user.
     * @return UserProfileResponse details.
     */
    UserProfileResponse getUserProfile(String usernameOrEmail);

    /**
     * Updates profile details of the user.
     * @param usernameOrEmail the identifier of the user to update.
     * @param updateRequest properties to be updated.
     * @return UserProfileResponse the updated details.
     */
    UserProfileResponse updateUserProfile(String usernameOrEmail, UserProfileUpdateRequest updateRequest);
}
