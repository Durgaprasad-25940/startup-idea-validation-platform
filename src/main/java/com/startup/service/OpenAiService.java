package com.startup.service;

import com.startup.dto.IdeaAnalysisResponse;
import com.startup.entity.StartupIdea;

/**
 * Service for AI-powered startup idea validation.
 */
public interface OpenAiService {

    IdeaAnalysisResponse generateAnalysis(StartupIdea idea);
}
