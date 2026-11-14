package com.startup.controller;

import com.startup.dto.AnalysisReportResponse;
import com.startup.dto.IdeaAnalysisResponse;
import com.startup.service.AnalysisService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

/**
 * Controller for generating and retrieving Startup Idea validation analyses and saved reports.
 */
@RestController
@RequestMapping("/api")
public class AnalysisController {

    private final AnalysisService analysisService;

    public AnalysisController(AnalysisService analysisService) {
        this.analysisService = analysisService;
    }

    /**
     * Trigger an AI analysis for a specific startup idea: POST /api/ideas/{ideaId}/analyze
     */
    @PostMapping("/ideas/{ideaId}/analyze")
    public ResponseEntity<IdeaAnalysisResponse> analyzeIdea(
            Principal principal,
            @PathVariable("ideaId") Long ideaId) {
        IdeaAnalysisResponse response = analysisService.analyzeIdea(principal.getName(), ideaId);
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieve analysis results for a specific startup idea: GET /api/ideas/{ideaId}/analysis
     */
    @GetMapping("/ideas/{ideaId}/analysis")
    public ResponseEntity<IdeaAnalysisResponse> getAnalysis(
            Principal principal,
            @PathVariable("ideaId") Long ideaId) {
        IdeaAnalysisResponse response = analysisService.getAnalysisByIdeaId(principal.getName(), ideaId);
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieve the saved report from analysis_reports table: GET /api/ideas/{ideaId}/report
     */
    @GetMapping("/ideas/{ideaId}/report")
    public ResponseEntity<AnalysisReportResponse> getSavedReport(
            Principal principal,
            @PathVariable("ideaId") Long ideaId) {
        AnalysisReportResponse response = analysisService.getSavedReport(principal.getName(), ideaId);
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieve all saved reports for the authenticated user: GET /api/reports
     */
    @GetMapping("/reports")
    public ResponseEntity<List<AnalysisReportResponse>> getAllSavedReports(Principal principal) {
        List<AnalysisReportResponse> reports = analysisService.getAllSavedReports(principal.getName());
        return ResponseEntity.ok(reports);
    }
}
