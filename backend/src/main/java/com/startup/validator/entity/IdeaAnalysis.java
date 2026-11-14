package com.startup.validator.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * IdeaAnalysis JPA Entity representing the 'idea_analyses' table.
 * 
 * Stores general market feasibility analysis, risk scores, and technical roadmap recommendations.
 */
@Entity
@Table(name = "idea_analyses")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IdeaAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idea_id", nullable = false, unique = true)
    private StartupIdea idea;

    @Column(name = "market_feasibility", columnDefinition = "TEXT")
    private String marketFeasibility;

    @Column(name = "risk_assessment", columnDefinition = "TEXT")
    private String riskAssessment;

    @Column(columnDefinition = "TEXT")
    private String recommendations;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
