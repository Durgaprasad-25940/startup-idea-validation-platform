package com.startup.validator.controller;

import com.startup.validator.dto.IdeaAnalysisResponse;
import com.startup.validator.service.AnalysisService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

/**
 * AnalysisController handles requests to trigger AI-based business validation scans.
 */
@RestController
@RequestMapping("/api/ideas/{ideaId}")
public class AnalysisController {

    private final AnalysisService analysisService;

    public AnalysisController(AnalysisService analysisService) {
        this.analysisService = analysisService;
    }

    /**
     * Trigger an analysis for a specific startup idea: POST /api/ideas/{ideaId}/analyze
     */
    @PostMapping("/analyze")
    public ResponseEntity<IdeaAnalysisResponse> analyzeIdea(
            Principal principal,
            @PathVariable("ideaId") Long ideaId) {
        
        IdeaAnalysisResponse response = analysisService.analyzeIdea(principal.getName(), ideaId);
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieve analysis results for a specific startup idea: GET /api/ideas/{ideaId}/analysis
     */
    @GetMapping("/analysis")
    public ResponseEntity<IdeaAnalysisResponse> getAnalysis(
            Principal principal,
            @PathVariable("ideaId") Long ideaId) {
        
        IdeaAnalysisResponse response = analysisService.getAnalysisByIdeaId(principal.getName(), ideaId);
        return ResponseEntity.ok(response);
    }
}
