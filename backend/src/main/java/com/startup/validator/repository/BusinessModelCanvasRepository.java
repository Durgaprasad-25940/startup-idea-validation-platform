package com.startup.validator.repository;

import com.startup.validator.entity.BusinessModelCanvas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BusinessModelCanvasRepository extends JpaRepository<BusinessModelCanvas, Long> {
    Optional<BusinessModelCanvas> findByIdeaId(Long ideaId);
}
