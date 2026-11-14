package com.startup.repository;

import com.startup.entity.AnalysisReport;
import com.startup.entity.StartupIdea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA Repository for AnalysisReport entity operations.
 */
@Repository
public interface AnalysisReportRepository extends JpaRepository<AnalysisReport, Long> {

    Optional<AnalysisReport> findByIdeaId(Long ideaId);

    Optional<AnalysisReport> findByIdea(StartupIdea idea);

    boolean existsByIdeaId(Long ideaId);

    void deleteByIdeaId(Long ideaId);

    @Query("SELECT r FROM AnalysisReport r WHERE r.idea.user.id = :userId ORDER BY r.createdAt DESC")
    List<AnalysisReport> findByUserId(@Param("userId") Long userId);
}
