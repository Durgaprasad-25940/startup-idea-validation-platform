package com.startup.controller;

import com.startup.dto.MessageResponse;
import com.startup.dto.StartupIdeaRequest;
import com.startup.dto.StartupIdeaResponse;
import com.startup.service.StartupIdeaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

/**
 * REST controller for managing startup ideas.
 */
@RestController
@RequestMapping("/api/ideas")
public class StartupIdeaController {

    private final StartupIdeaService ideaService;

    public StartupIdeaController(StartupIdeaService ideaService) {
        this.ideaService = ideaService;
    }

    /**
     * Create a new startup idea: POST /api/ideas
     */
    @PostMapping
    public ResponseEntity<StartupIdeaResponse> createIdea(
            Principal principal,
            @Valid @RequestBody StartupIdeaRequest request) {
        StartupIdeaResponse response = ideaService.createIdea(principal.getName(), request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Get all startup ideas for current user (with optional search and industry filter): GET /api/ideas
     */
    @GetMapping
    public ResponseEntity<List<StartupIdeaResponse>> getAllIdeas(
            Principal principal,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "industry", required = false) String industry) {
        List<StartupIdeaResponse> response = ideaService.getAllIdeas(principal.getName(), search, industry);
        return ResponseEntity.ok(response);
    }

    /**
     * Get a specific idea by ID: GET /api/ideas/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<StartupIdeaResponse> getIdeaById(
            Principal principal,
            @PathVariable("id") Long id) {
        StartupIdeaResponse response = ideaService.getIdeaById(principal.getName(), id);
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
        StartupIdeaResponse response = ideaService.updateIdea(principal.getName(), id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Delete a startup idea: DELETE /api/ideas/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteIdea(
            Principal principal,
            @PathVariable("id") Long id) {
        ideaService.deleteIdea(principal.getName(), id);
        return ResponseEntity.ok(new MessageResponse("Startup idea deleted successfully."));
    }
}
