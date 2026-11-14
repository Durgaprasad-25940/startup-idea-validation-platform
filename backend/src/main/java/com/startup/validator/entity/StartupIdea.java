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
 * StartupIdea JPA Entity representing the 'startup_ideas' table in the MySQL database.
 * 
 * Each idea is submitted by a single User (Many-to-One relationship).
 */
@Entity
@Table(name = "startup_ideas")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StartupIdea {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Many-to-One relationship referencing the User who submitted the idea
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, length = 100)
    private String industry;

    @Column(name = "target_audience", nullable = false, length = 255)
    private String targetAudience;

    @Column(nullable = false, length = 50)
    private String stage;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
