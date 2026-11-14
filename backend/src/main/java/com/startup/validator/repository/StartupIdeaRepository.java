package com.startup.validator.repository;

import com.startup.validator.entity.StartupIdea;
import com.startup.validator.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * StartupIdeaRepository provides data operations on the 'startup_ideas' table.
 */
@Repository
public interface StartupIdeaRepository extends JpaRepository<StartupIdea, Long> {

    /**
     * Retrieve all ideas submitted by a specific user.
     */
    List<StartupIdea> findByUser(User user);

    /**
     * Dynamic custom query for searching and filtering a user's ideas by title and industry.
     */
    @Query("SELECT s FROM StartupIdea s WHERE s.user = :user " +
           "AND (:title IS NULL OR LOWER(s.title) LIKE LOWER(CONCAT('%', :title, '%')) " +
           "OR LOWER(s.description) LIKE LOWER(CONCAT('%', :title, '%'))) " +
           "AND (:industry IS NULL OR s.industry = :industry)")
    List<StartupIdea> searchIdeas(@Param("user") User user, 
                                  @Param("title") String title, 
                                  @Param("industry") String industry);

    /**
     * Count the total number of ideas submitted by a user.
     */
    long countByUser(User user);
}
