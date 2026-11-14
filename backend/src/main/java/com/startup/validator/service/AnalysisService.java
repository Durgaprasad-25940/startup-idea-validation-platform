package com.startup.validator.service;

import com.startup.validator.dto.IdeaAnalysisResponse;

/**
 * AnalysisService orchestrates AI validation analyses (SWOT, Competitors, Business Model Canvas, etc.).
 */
public interface AnalysisService {

    /**
     * Executes validation engine rules, persisting results to MySQL.
     */
    IdeaAnalysisResponse analyzeIdea(String usernameOrEmail, Long ideaId);

    /**
     * Retrieves existing validation details for a specific startup idea.
     */
    IdeaAnalysisResponse getAnalysisByIdeaId(String usernameOrEmail, Long ideaId);
}
