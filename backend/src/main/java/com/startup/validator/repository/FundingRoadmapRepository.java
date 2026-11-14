package com.startup.validator.repository;

import com.startup.validator.entity.FundingRoadmap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FundingRoadmapRepository extends JpaRepository<FundingRoadmap, Long> {
    Optional<FundingRoadmap> findByIdeaId(Long ideaId);
}
