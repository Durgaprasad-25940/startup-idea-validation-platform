package com.startup.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * AnalysisReport JPA Entity mapped to the 'analysis_reports' table in MySQL.
 * Stores structured AI analysis: market feasibility, competitor analysis, SWOT, and improvement recommendations.
 */
@Entity
@Table(name = "analysis_reports")
public class AnalysisReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idea_id", nullable = false, unique = true)
    private StartupIdea idea;

    @Column(name = "market_analysis", nullable = false, columnDefinition = "MEDIUMTEXT")
    private String marketAnalysis;

    @Column(name = "competitor_analysis", nullable = false, columnDefinition = "MEDIUMTEXT")
    private String competitorAnalysis;

    @Column(name = "swot_strengths", nullable = false, columnDefinition = "MEDIUMTEXT")
    private String swotStrengths;

    @Column(name = "swot_weaknesses", nullable = false, columnDefinition = "MEDIUMTEXT")
    private String swotWeaknesses;

    @Column(name = "swot_opportunities", nullable = false, columnDefinition = "MEDIUMTEXT")
    private String swotOpportunities;

    @Column(name = "swot_threats", nullable = false, columnDefinition = "MEDIUMTEXT")
    private String swotThreats;

    @Column(name = "improvement_suggestions", nullable = false, columnDefinition = "MEDIUMTEXT")
    private String improvementSuggestions;

    @Column(name = "viability_score")
    private Integer viabilityScore = 75;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public AnalysisReport() {}

    public AnalysisReport(StartupIdea idea, String marketAnalysis, String competitorAnalysis,
                          String swotStrengths, String swotWeaknesses,
                          String swotOpportunities, String swotThreats,
                          String improvementSuggestions, Integer viabilityScore) {
        this.idea = idea;
        this.marketAnalysis = marketAnalysis;
        this.competitorAnalysis = competitorAnalysis;
        this.swotStrengths = swotStrengths;
        this.swotWeaknesses = swotWeaknesses;
        this.swotOpportunities = swotOpportunities;
        this.swotThreats = swotThreats;
        this.improvementSuggestions = improvementSuggestions;
        this.viabilityScore = viabilityScore != null ? viabilityScore : 75;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public StartupIdea getIdea() { return idea; }
    public void setIdea(StartupIdea idea) { this.idea = idea; }

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
