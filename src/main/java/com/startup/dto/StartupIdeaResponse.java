package com.startup.dto;

import java.time.LocalDateTime;

/**
 * StartupIdeaResponse is returned to clients listing or accessing startup ideas.
 */
public class StartupIdeaResponse {

    private Long id;
    private Long userId;
    private String title;
    private String description;
    private String industry;
    private String targetAudience;
    private String stage;
    private boolean analyzed;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public StartupIdeaResponse() {}

    public StartupIdeaResponse(Long id, Long userId, String title, String description,
                               String industry, String targetAudience, String stage,
                               boolean analyzed, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.description = description;
        this.industry = industry;
        this.targetAudience = targetAudience;
        this.stage = stage;
        this.analyzed = analyzed;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getIndustry() { return industry; }
    public void setIndustry(String industry) { this.industry = industry; }

    public String getTargetAudience() { return targetAudience; }
    public void setTargetAudience(String targetAudience) { this.targetAudience = targetAudience; }

    public String getStage() { return stage; }
    public void setStage(String stage) { this.stage = stage; }

    public boolean isAnalyzed() { return analyzed; }
    public void setAnalyzed(boolean analyzed) { this.analyzed = analyzed; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
