package com.startup.service;

import com.startup.dto.StartupIdeaRequest;
import com.startup.dto.StartupIdeaResponse;

import java.util.List;

/**
 * Service interface for managing Startup Ideas.
 */
public interface StartupIdeaService {

    StartupIdeaResponse createIdea(String usernameOrEmail, StartupIdeaRequest request);

    List<StartupIdeaResponse> getAllIdeas(String usernameOrEmail, String search, String industry);

    StartupIdeaResponse getIdeaById(String usernameOrEmail, Long id);

    StartupIdeaResponse updateIdea(String usernameOrEmail, Long id, StartupIdeaRequest request);

    void deleteIdea(String usernameOrEmail, Long id);
}
