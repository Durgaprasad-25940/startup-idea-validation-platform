package com.startup.validator.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * Aggregated validation payload response representing the combined analysis of a startup idea.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IdeaAnalysisResponse {

    private Long ideaId;
    
    // Core analysis fields
    private String marketFeasibility;
    private String riskAssessment;
    private String recommendations;

    // SWOT analysis details
    private String strengths;
    private String weaknesses;
    private String opportunities;
    private String threats;

    // Competitors list
    private List<CompetitorDTO> competitors;

    // Business Model Canvas configuration
    private BusinessModelCanvasDTO businessModelCanvas;

    // Funding suggestions roadmap
    private FundingRoadmapDTO fundingRoadmap;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CompetitorDTO {
        private String competitorName;
        private String strength;
        private String weakness;
        private String marketShare;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BusinessModelCanvasDTO {
        private String keyPartners;
        private String keyActivities;
        private String keyResources;
        private String valuePropositions;
        private String customerRelationships;
        private String channels;
        private String customerSegments;
        private String costStructure;
        private String revenueStreams;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FundingRoadmapDTO {
        private String fundingStage;
        private BigDecimal targetAmount;
        private String usageDetails;
        private String milestoneGoal;
    }
}
