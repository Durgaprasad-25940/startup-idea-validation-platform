package com.startup.validator.controller;

import com.startup.validator.dto.StartupIdeaRequest;
import com.startup.validator.dto.StartupIdeaResponse;
import com.startup.validator.service.StartupIdeaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

/**
 * StartupIdeaController manages CRUD operations for startup ideas.
 */
@RestController
@RequestMapping("/api/ideas")
public class StartupIdeaController {

    private final StartupIdeaService startupIdeaService;

    public StartupIdeaController(StartupIdeaService startupIdeaService) {
        this.startupIdeaService = startupIdeaService;
    }

    /**
     * Submit a new startup idea: POST /api/ideas
     */
    @PostMapping
    public ResponseEntity<StartupIdeaResponse> createIdea(
            Principal principal,
            @Valid @RequestBody StartupIdeaRequest request) {
        
        StartupIdeaResponse response = startupIdeaService.createIdea(principal.getName(), request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Retrieve startup ideas submitted by the user: GET /api/ideas
     * Supports optional search filters.
     */
    @GetMapping
    public ResponseEntity<List<StartupIdeaResponse>> getAllIdeas(
            Principal principal,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "industry", required = false) String industry) {
        
        List<StartupIdeaResponse> response = startupIdeaService.getAllIdeas(principal.getName(), search, industry);
        return ResponseEntity.ok(response);
    }

    /**
     * Get details of a specific idea: GET /api/ideas/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<StartupIdeaResponse> getIdeaById(
            Principal principal,
            @PathVariable("id") Long id) {
        
        StartupIdeaResponse response = startupIdeaService.getIdeaById(principal.getName(), id);
        return ResponseEntity.ok(response);
    }

    /**
     * Update an existing startup idea: PUT /api/ideas/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<StartupIdeaResponse> updateIdea(
            Principal principal,
            @PathVariable("id") Long id,
            @Valid @RequestBody StartupIdeaRequest request) {
        
        StartupIdeaResponse response = startupIdeaService.updateIdea(principal.getName(), id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Delete a startup idea: DELETE /api/ideas/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteIdea(
            Principal principal,
            @PathVariable("id") Long id) {
        
        startupIdeaService.deleteIdea(principal.getName(), id);
        return ResponseEntity.ok("Startup idea deleted successfully.");
    }
}
