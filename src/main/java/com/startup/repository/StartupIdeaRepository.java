package com.startup.repository;

import com.startup.entity.StartupIdea;
import com.startup.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA Repository for StartupIdea entity operations.
 */
@Repository
public interface StartupIdeaRepository extends JpaRepository<StartupIdea, Long> {

    List<StartupIdea> findByUserOrderByCreatedAtDesc(User user);

    List<StartupIdea> findByUserIdOrderByCreatedAtDesc(Long userId);

    Optional<StartupIdea> findByIdAndUser(Long id, User user);

    Optional<StartupIdea> findByIdAndUserId(Long id, Long userId);

    @Query("SELECT i FROM StartupIdea i WHERE i.user.id = :userId " +
           "AND (:industry IS NULL OR :industry = '' OR i.industry = :industry) " +
           "AND (:search IS NULL OR :search = '' OR LOWER(i.title) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(i.description) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "ORDER BY i.createdAt DESC")
    List<StartupIdea> searchIdeas(@Param("userId") Long userId,
                                  @Param("search") String search,
                                  @Param("industry") String industry);

    long countByUserId(Long userId);
}
