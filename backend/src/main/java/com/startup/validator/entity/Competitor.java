package com.startup.validator.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Competitor JPA Entity representing the 'competitors' table.
 */
@Entity
@Table(name = "competitors")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Competitor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idea_id", nullable = false)
    private StartupIdea idea;

    @Column(name = "competitor_name", nullable = false, length = 100)
    private String competitorName;

    @Column(columnDefinition = "TEXT")
    private String strength;

    @Column(columnDefinition = "TEXT")
    private String weakness;

    @Column(name = "market_share", length = 50)
    private String marketShare;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
