package com.startup.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Aggregated validation payload response representing the combined analysis of a startup idea.
 */
public class IdeaAnalysisResponse {

    private Long ideaId;
    private Integer viabilityScore = 75;

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
    private List<CompetitorDTO> competitors = new ArrayList<>();

    // Business Model Canvas configuration
    private BusinessModelCanvasDTO businessModelCanvas;

    // Funding suggestions roadmap
    private FundingRoadmapDTO fundingRoadmap;

    public IdeaAnalysisResponse() {}

    public Long getIdeaId() { return ideaId; }
    public void setIdeaId(Long ideaId) { this.ideaId = ideaId; }

    public Integer getViabilityScore() { return viabilityScore; }
    public void setViabilityScore(Integer viabilityScore) { this.viabilityScore = viabilityScore; }

    public String getMarketFeasibility() { return marketFeasibility; }
    public void setMarketFeasibility(String marketFeasibility) { this.marketFeasibility = marketFeasibility; }

    public String getRiskAssessment() { return riskAssessment; }
    public void setRiskAssessment(String riskAssessment) { this.riskAssessment = riskAssessment; }

    public String getRecommendations() { return recommendations; }
    public void setRecommendations(String recommendations) { this.recommendations = recommendations; }

    public String getStrengths() { return strengths; }
    public void setStrengths(String strengths) { this.strengths = strengths; }

    public String getWeaknesses() { return weaknesses; }
    public void setWeaknesses(String weaknesses) { this.weaknesses = weaknesses; }

    public String getOpportunities() { return opportunities; }
    public void setOpportunities(String opportunities) { this.opportunities = opportunities; }

    public String getThreats() { return threats; }
    public void setThreats(String threats) { this.threats = threats; }

    public List<CompetitorDTO> getCompetitors() { return competitors; }
    public void setCompetitors(List<CompetitorDTO> competitors) { this.competitors = competitors; }

    public BusinessModelCanvasDTO getBusinessModelCanvas() { return businessModelCanvas; }
    public void setBusinessModelCanvas(BusinessModelCanvasDTO businessModelCanvas) { this.businessModelCanvas = businessModelCanvas; }

    public FundingRoadmapDTO getFundingRoadmap() { return fundingRoadmap; }
    public void setFundingRoadmap(FundingRoadmapDTO fundingRoadmap) { this.fundingRoadmap = fundingRoadmap; }

    public static class CompetitorDTO {
        private String competitorName;
        private String strength;
        private String weakness;
        private String marketShare;

        public CompetitorDTO() {}

        public CompetitorDTO(String competitorName, String strength, String weakness, String marketShare) {
            this.competitorName = competitorName;
            this.strength = strength;
            this.weakness = weakness;
            this.marketShare = marketShare;
        }

        public String getCompetitorName() { return competitorName; }
        public void setCompetitorName(String competitorName) { this.competitorName = competitorName; }

        public String getStrength() { return strength; }
        public void setStrength(String strength) { this.strength = strength; }

        public String getWeakness() { return weakness; }
        public void setWeakness(String weakness) { this.weakness = weakness; }

        public String getMarketShare() { return marketShare; }
        public void setMarketShare(String marketShare) { this.marketShare = marketShare; }
    }

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

        public BusinessModelCanvasDTO() {}

        public String getKeyPartners() { return keyPartners; }
        public void setKeyPartners(String keyPartners) { this.keyPartners = keyPartners; }

        public String getKeyActivities() { return keyActivities; }
        public void setKeyActivities(String keyActivities) { this.keyActivities = keyActivities; }

        public String getKeyResources() { return keyResources; }
        public void setKeyResources(String keyResources) { this.keyResources = keyResources; }

        public String getValuePropositions() { return valuePropositions; }
        public void setValuePropositions(String valuePropositions) { this.valuePropositions = valuePropositions; }

        public String getCustomerRelationships() { return customerRelationships; }
        public void setCustomerRelationships(String customerRelationships) { this.customerRelationships = customerRelationships; }

        public String getChannels() { return channels; }
        public void setChannels(String channels) { this.channels = channels; }

        public String getCustomerSegments() { return customerSegments; }
        public void setCustomerSegments(String customerSegments) { this.customerSegments = customerSegments; }

        public String getCostStructure() { return costStructure; }
        public void setCostStructure(String costStructure) { this.costStructure = costStructure; }

        public String getRevenueStreams() { return revenueStreams; }
        public void setRevenueStreams(String revenueStreams) { this.revenueStreams = revenueStreams; }
    }

    public static class FundingRoadmapDTO {
        private String fundingStage;
        private BigDecimal targetAmount;
        private String usageDetails;
        private String milestoneGoal;

        public FundingRoadmapDTO() {}

        public FundingRoadmapDTO(String fundingStage, BigDecimal targetAmount, String usageDetails, String milestoneGoal) {
            this.fundingStage = fundingStage;
            this.targetAmount = targetAmount;
            this.usageDetails = usageDetails;
            this.milestoneGoal = milestoneGoal;
        }

        public String getFundingStage() { return fundingStage; }
        public void setFundingStage(String fundingStage) { this.fundingStage = fundingStage; }

        public BigDecimal getTargetAmount() { return targetAmount; }
        public void setTargetAmount(BigDecimal targetAmount) { this.targetAmount = targetAmount; }

        public String getUsageDetails() { return usageDetails; }
        public void setUsageDetails(String usageDetails) { this.usageDetails = usageDetails; }

        public String getMilestoneGoal() { return milestoneGoal; }
        public void setMilestoneGoal(String milestoneGoal) { this.milestoneGoal = milestoneGoal; }
    }
}
