package com.startup.validator.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * StartupIdeaResponse is returned to clients listing or accessing startup ideas.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
}
