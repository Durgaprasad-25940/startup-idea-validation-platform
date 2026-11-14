package com.startup.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * UserProfileUpdateRequest captures user profile modifications.
 */
public class UserProfileUpdateRequest {

    @NotBlank(message = "Full name cannot be blank")
    @Size(max = 100, message = "Full name must be less than 100 characters")
    private String fullName;

    // Optional: Only updated if a new non-empty password is submitted
    @Size(min = 6, max = 100, message = "Password must be at least 6 characters long")
    private String password;

    public UserProfileUpdateRequest() {}

    public UserProfileUpdateRequest(String fullName, String password) {
        this.fullName = fullName;
        this.password = password;
    }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
