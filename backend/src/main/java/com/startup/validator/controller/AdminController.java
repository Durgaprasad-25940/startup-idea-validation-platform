package com.startup.validator.controller;

import com.startup.validator.dto.AdminStatsResponse;
import com.startup.validator.dto.StartupIdeaResponse;
import com.startup.validator.dto.UserProfileResponse;
import com.startup.validator.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * AdminController manages admin-only endpoints. Enforced at filter chain configuration levels.
 */
@RestController
@RequestMapping("/api/admin")
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
        AdminStatsResponse response = adminService.getPlatformStats();
        return ResponseEntity.ok(response);
    }

    /**
     * Get list of all registered platform users: GET /api/admin/users
     */
    @GetMapping("/users")
    public ResponseEntity<List<UserProfileResponse>> getAllUsers() {
        List<UserProfileResponse> response = adminService.getAllUsers();
        return ResponseEntity.ok(response);
    }

    /**
     * Get list of all submitted platform ideas: GET /api/admin/ideas
     */
    @GetMapping("/ideas")
    public ResponseEntity<List<StartupIdeaResponse>> getAllIdeas() {
        List<StartupIdeaResponse> response = adminService.getAllIdeas();
        return ResponseEntity.ok(response);
    }

    /**
     * Administrative delete action of any startup idea: DELETE /api/admin/ideas/{id}
     */
    @DeleteMapping("/ideas/{id}")
    public ResponseEntity<String> deleteIdea(@PathVariable("id") Long id) {
        adminService.deleteIdea(id);
        return ResponseEntity.ok("Startup idea deleted by Administrator.");
    }
}
