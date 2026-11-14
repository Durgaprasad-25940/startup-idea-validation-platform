package com.startup.validator.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * FundingRoadmap JPA Entity representing the 'funding_roadmaps' table.
 */
@Entity
@Table(name = "funding_roadmaps")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FundingRoadmap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idea_id", nullable = false, unique = true)
    private StartupIdea idea;

    @Column(name = "funding_stage", nullable = false, length = 50)
    private String fundingStage;

    @Column(name = "target_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal targetAmount;

    @Column(name = "usage_details", columnDefinition = "TEXT")
    private String usageDetails;

    @Column(name = "milestone_goal", columnDefinition = "TEXT")
    private String milestoneGoal;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
