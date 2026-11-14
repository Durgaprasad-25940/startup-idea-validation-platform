package com.startup.validator.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * UserProfileUpdateRequest stores the fields a user can edit to update their profile information.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileUpdateRequest {

    @NotBlank(message = "Full name cannot be blank")
    @Size(max = 100, message = "Full name must be less than 100 characters")
    private String fullName;

    // Optional field. If provided, we'll update the password.
    @Size(min = 6, max = 100, message = "Password must be at least 6 characters long")
    private String password;
}
