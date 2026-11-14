package com.startup.service;

import com.startup.dto.AdminStatsResponse;
import com.startup.dto.StartupIdeaResponse;
import com.startup.dto.UserProfileResponse;

import java.util.List;

public interface AdminService {
    AdminStatsResponse getPlatformStats();
    List<UserProfileResponse> getAllUsers();
    List<StartupIdeaResponse> getAllIdeas();
    void deleteIdea(Long id);
    void deleteUser(Long id);
}
