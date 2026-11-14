package com.startup.validator.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * StartupIdeaRequest captures data inputs from clients submitting or updating ideas.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StartupIdeaRequest {

    @NotBlank(message = "Title is required")
    @Size(max = 150, message = "Title cannot exceed 150 characters")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    @NotBlank(message = "Industry is required")
    @Size(max = 100, message = "Industry cannot exceed 100 characters")
    private String industry;

    @NotBlank(message = "Target audience is required")
    @Size(max = 255, message = "Target audience cannot exceed 255 characters")
    private String targetAudience;

    @NotBlank(message = "Startup stage is required")
    @Size(max = 50, message = "Stage cannot exceed 50 characters")
    private String stage;
}
