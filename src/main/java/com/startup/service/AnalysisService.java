package com.startup.service;

import com.startup.dto.AnalysisReportResponse;
import com.startup.dto.IdeaAnalysisResponse;

import java.util.List;

/**
 * Service interface for generating and retrieving Startup Idea validation analyses and saved reports.
 */
public interface AnalysisService {

    IdeaAnalysisResponse analyzeIdea(String usernameOrEmail, Long ideaId);

    IdeaAnalysisResponse getAnalysisByIdeaId(String usernameOrEmail, Long ideaId);

    AnalysisReportResponse getSavedReport(String usernameOrEmail, Long ideaId);

    List<AnalysisReportResponse> getAllSavedReports(String usernameOrEmail);
}
