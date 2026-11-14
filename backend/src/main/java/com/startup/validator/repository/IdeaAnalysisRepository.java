package com.startup.validator.repository;

import com.startup.validator.entity.IdeaAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IdeaAnalysisRepository extends JpaRepository<IdeaAnalysis, Long> {
    Optional<IdeaAnalysis> findByIdeaId(Long ideaId);
    boolean existsByIdeaId(Long ideaId);
}
