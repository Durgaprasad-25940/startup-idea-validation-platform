package com.startup.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * StartupIdeaRequest captures data inputs from clients submitting or updating ideas.
 */
public class StartupIdeaRequest {

    @NotBlank(message = "Title is required")
    @Size(min = 2, max = 150, message = "Title must be between 2 and 150 characters")
    private String title;

    @NotBlank(message = "Description is required")
    @Size(min = 5, message = "Description must be at least 5 characters long")
    private String description;

    private String industry = "General";

    private String targetAudience = "General Consumers";

    private String stage = "Ideation";

    public StartupIdeaRequest() {}

    public StartupIdeaRequest(String title, String description, String industry, String targetAudience, String stage) {
        this.title = title;
        this.description = description;
        this.industry = (industry != null && !industry.trim().isEmpty()) ? industry : "General";
        this.targetAudience = (targetAudience != null && !targetAudience.trim().isEmpty()) ? targetAudience : "General Consumers";
        this.stage = (stage != null && !stage.trim().isEmpty()) ? stage : "Ideation";
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getIndustry() {
        return (industry != null && !industry.trim().isEmpty()) ? industry : "General";
    }
    public void setIndustry(String industry) {
        this.industry = (industry != null && !industry.trim().isEmpty()) ? industry : "General";
    }

    public String getTargetAudience() {
        return (targetAudience != null && !targetAudience.trim().isEmpty()) ? targetAudience : "General Consumers";
    }
    public void setTargetAudience(String targetAudience) {
        this.targetAudience = (targetAudience != null && !targetAudience.trim().isEmpty()) ? targetAudience : "General Consumers";
    }

    public String getStage() {
        return (stage != null && !stage.trim().isEmpty()) ? stage : "Ideation";
    }
    public void setStage(String stage) {
        this.stage = (stage != null && !stage.trim().isEmpty()) ? stage : "Ideation";
    }
}
