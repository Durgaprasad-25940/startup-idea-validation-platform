package com.startup.validator.repository;

import com.startup.validator.entity.SwotAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SwotAnalysisRepository extends JpaRepository<SwotAnalysis, Long> {
    Optional<SwotAnalysis> findByIdeaId(Long ideaId);
}
