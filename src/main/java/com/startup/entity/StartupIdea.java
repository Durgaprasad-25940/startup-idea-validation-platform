package com.startup.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * StartupIdea JPA Entity mapped to the 'startup_ideas' table in MySQL.
 */
@Entity
@Table(name = "startup_ideas")
public class StartupIdea {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, length = 100)
    private String industry = "General";

    @Column(name = "target_audience", nullable = false, length = 255)
    private String targetAudience = "General Consumers";

    @Column(nullable = false, length = 50)
    private String stage = "Ideation";

    @Column(name = "problem_solved", columnDefinition = "TEXT")
    private String problemSolved;

    @Column(name = "ai_feedback", columnDefinition = "TEXT")
    private String aiFeedback;

    @Column(name = "viability_score")
    private Integer viabilityScore;

    @OneToOne(mappedBy = "idea", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private AnalysisReport analysisReport;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public StartupIdea() {}

    public StartupIdea(User user, String title, String description, String industry, String targetAudience, String stage) {
        this.user = user;
        this.title = title;
        this.description = description;
        this.industry = industry != null ? industry : "General";
        this.targetAudience = targetAudience != null ? targetAudience : "General Consumers";
        this.stage = stage != null ? stage : "Ideation";
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

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

    public String getProblemSolved() { return problemSolved; }
    public void setProblemSolved(String problemSolved) { this.problemSolved = problemSolved; }

    public String getAiFeedback() { return aiFeedback; }
    public void setAiFeedback(String aiFeedback) { this.aiFeedback = aiFeedback; }

    public Integer getViabilityScore() { return viabilityScore; }
    public void setViabilityScore(Integer viabilityScore) { this.viabilityScore = viabilityScore; }

    public AnalysisReport getAnalysisReport() { return analysisReport; }
    public void setAnalysisReport(AnalysisReport analysisReport) { this.analysisReport = analysisReport; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
