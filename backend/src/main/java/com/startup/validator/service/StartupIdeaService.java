package com.startup.validator.service;

import com.startup.validator.dto.StartupIdeaRequest;
import com.startup.validator.dto.StartupIdeaResponse;

import java.util.List;

/**
 * StartupIdeaService handles business operations on startup ideas.
 */
public interface StartupIdeaService {

    /**
     * Submit a new startup idea.
     */
    StartupIdeaResponse createIdea(String usernameOrEmail, StartupIdeaRequest request);

    /**
     * Retrieve all ideas of the logged-in user, filtered by title/desc search query and industry.
     */
    List<StartupIdeaResponse> getAllIdeas(String usernameOrEmail, String search, String industry);

    /**
     * Retrieve a specific idea's details.
     */
    StartupIdeaResponse getIdeaById(String usernameOrEmail, Long id);

    /**
     * Modify details of an existing idea.
     */
    StartupIdeaResponse updateIdea(String usernameOrEmail, Long id, StartupIdeaRequest request);

    /**
     * Remove an idea from the database.
     */
    void deleteIdea(String usernameOrEmail, Long id);

    /**
     * Admin-only: retrieve all ideas registered on the platform.
     */
    List<StartupIdeaResponse> getGlobalIdeas();

    /**
     * Admin-only: delete any idea on the platform.
     */
    void adminDeleteIdea(Long id);
}
