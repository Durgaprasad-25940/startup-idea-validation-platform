package com.startup.dto;

import java.time.LocalDateTime;

/**
 * AnalysisReportResponse represents the structured saved report from the analysis_reports table.
 */
public class AnalysisReportResponse {

    private Long id;
    private Long ideaId;
    private String ideaTitle;
    private String marketAnalysis;
    private String competitorAnalysis;
    private String swotStrengths;
    private String swotWeaknesses;
    private String swotOpportunities;
    private String swotThreats;
    private String improvementSuggestions;
    private Integer viabilityScore;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public AnalysisReportResponse() {}

    public AnalysisReportResponse(Long id, Long ideaId, String ideaTitle, String marketAnalysis,
                                  String competitorAnalysis, String swotStrengths, String swotWeaknesses,
                                  String swotOpportunities, String swotThreats, String improvementSuggestions,
                                  Integer viabilityScore, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.ideaId = ideaId;
        this.ideaTitle = ideaTitle;
        this.marketAnalysis = marketAnalysis;
        this.competitorAnalysis = competitorAnalysis;
        this.swotStrengths = swotStrengths;
        this.swotWeaknesses = swotWeaknesses;
        this.swotOpportunities = swotOpportunities;
        this.swotThreats = swotThreats;
        this.improvementSuggestions = improvementSuggestions;
        this.viabilityScore = viabilityScore;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getIdeaId() { return ideaId; }
    public void setIdeaId(Long ideaId) { this.ideaId = ideaId; }

    public String getIdeaTitle() { return ideaTitle; }
    public void setIdeaTitle(String ideaTitle) { this.ideaTitle = ideaTitle; }

    public String getMarketAnalysis() { return marketAnalysis; }
    public void setMarketAnalysis(String marketAnalysis) { this.marketAnalysis = marketAnalysis; }

    public String getCompetitorAnalysis() { return competitorAnalysis; }
    public void setCompetitorAnalysis(String competitorAnalysis) { this.competitorAnalysis = competitorAnalysis; }

    public String getSwotStrengths() { return swotStrengths; }
    public void setSwotStrengths(String swotStrengths) { this.swotStrengths = swotStrengths; }

    public String getSwotWeaknesses() { return swotWeaknesses; }
    public void setSwotWeaknesses(String swotWeaknesses) { this.swotWeaknesses = swotWeaknesses; }

    public String getSwotOpportunities() { return swotOpportunities; }
    public void setSwotOpportunities(String swotOpportunities) { this.swotOpportunities = swotOpportunities; }

    public String getSwotThreats() { return swotThreats; }
    public void setSwotThreats(String swotThreats) { this.swotThreats = swotThreats; }

    public String getImprovementSuggestions() { return improvementSuggestions; }
    public void setImprovementSuggestions(String improvementSuggestions) { this.improvementSuggestions = improvementSuggestions; }

    public Integer getViabilityScore() { return viabilityScore; }
    public void setViabilityScore(Integer viabilityScore) { this.viabilityScore = viabilityScore; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
