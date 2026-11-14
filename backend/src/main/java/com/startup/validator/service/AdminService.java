package com.startup.validator.service;

import com.startup.validator.dto.AdminStatsResponse;
import com.startup.validator.dto.StartupIdeaResponse;
import com.startup.validator.dto.UserProfileResponse;

import java.util.List;

/**
 * AdminService exposes administrative management functions.
 */
public interface AdminService {

    AdminStatsResponse getPlatformStats();

    List<UserProfileResponse> getAllUsers();

    List<StartupIdeaResponse> getAllIdeas();

    void deleteIdea(Long id);
}
