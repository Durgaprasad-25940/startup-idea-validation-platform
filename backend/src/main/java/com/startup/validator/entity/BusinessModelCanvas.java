package com.startup.validator.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * BusinessModelCanvas JPA Entity representing the 'business_model_canvases' table.
 */
@Entity
@Table(name = "business_model_canvases")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BusinessModelCanvas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idea_id", nullable = false, unique = true)
    private StartupIdea idea;

    @Column(name = "key_partners", columnDefinition = "TEXT")
    private String keyPartners;

    @Column(name = "key_activities", columnDefinition = "TEXT")
    private String keyActivities;

    @Column(name = "key_resources", columnDefinition = "TEXT")
    private String keyResources;

    @Column(name = "value_propositions", columnDefinition = "TEXT")
    private String valuePropositions;

    @Column(name = "customer_relationships", columnDefinition = "TEXT")
    private String customerRelationships;

    @Column(name = "channels", columnDefinition = "TEXT")
    private String channels;

    @Column(name = "customer_segments", columnDefinition = "TEXT")
    private String customerSegments;

    @Column(name = "cost_structure", columnDefinition = "TEXT")
    private String costStructure;

    @Column(name = "revenue_streams", columnDefinition = "TEXT")
    private String revenueStreams;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
