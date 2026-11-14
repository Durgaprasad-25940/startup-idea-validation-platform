package com.startup.validator.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * SwotAnalysis JPA Entity representing the 'swot_analyses' table.
 */
@Entity
@Table(name = "swot_analyses")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SwotAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idea_id", nullable = false, unique = true)
    private StartupIdea idea;

    @Column(columnDefinition = "TEXT")
    private String strengths;

    @Column(columnDefinition = "TEXT")
    private String weaknesses;

    @Column(columnDefinition = "TEXT")
    private String opportunities;

    @Column(columnDefinition = "TEXT")
    private String threats;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
