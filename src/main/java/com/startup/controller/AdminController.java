package com.startup.controller;

import com.startup.dto.AdminStatsResponse;
import com.startup.dto.MessageResponse;
import com.startup.dto.StartupIdeaResponse;
import com.startup.dto.UserProfileResponse;
import com.startup.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for administrative platform monitoring and management.
 */
@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    /**
     * Get platform statistics: GET /api/admin/stats
     */
    @GetMapping("/stats")
    public ResponseEntity<AdminStatsResponse> getPlatformStats() {
        return ResponseEntity.ok(adminService.getPlatformStats());
    }

    /**
     * List all registered users: GET /api/admin/users
     */
    @GetMapping("/users")
    public ResponseEntity<List<UserProfileResponse>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    /**
     * List all ideas across the platform: GET /api/admin/ideas
     */
    @GetMapping("/ideas")
    public ResponseEntity<List<StartupIdeaResponse>> getAllIdeas() {
        return ResponseEntity.ok(adminService.getAllIdeas());
    }

    /**
     * Admin delete an idea: DELETE /api/admin/ideas/{id}
     */
    @DeleteMapping("/ideas/{id}")
    public ResponseEntity<MessageResponse> deleteIdea(@PathVariable("id") Long id) {
        adminService.deleteIdea(id);
        return ResponseEntity.ok(new MessageResponse("Startup idea deleted by Administrator."));
    }

    /**
     * Admin delete a user: DELETE /api/admin/users/{id}
     */
    @DeleteMapping("/users/{id}")
    public ResponseEntity<MessageResponse> deleteUser(@PathVariable("id") Long id) {
        adminService.deleteUser(id);
        return ResponseEntity.ok(new MessageResponse("User deleted by Administrator."));
    }
}
