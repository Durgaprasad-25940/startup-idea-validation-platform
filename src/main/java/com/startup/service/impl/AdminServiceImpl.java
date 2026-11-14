package com.startup.service.impl;

import com.startup.dto.AdminStatsResponse;
import com.startup.dto.StartupIdeaResponse;
import com.startup.dto.UserProfileResponse;
import com.startup.entity.StartupIdea;
import com.startup.entity.User;
import com.startup.exception.ResourceNotFoundException;
import com.startup.repository.AnalysisReportRepository;
import com.startup.repository.StartupIdeaRepository;
import com.startup.repository.UserRepository;
import com.startup.service.AdminService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final StartupIdeaRepository ideaRepository;
    private final AnalysisReportRepository reportRepository;

    public AdminServiceImpl(UserRepository userRepository,
                            StartupIdeaRepository ideaRepository,
                            AnalysisReportRepository reportRepository) {
        this.userRepository = userRepository;
        this.ideaRepository = ideaRepository;
        this.reportRepository = reportRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public AdminStatsResponse getPlatformStats() {
        long totalUsers = userRepository.count();
        long totalIdeas = ideaRepository.count();
        long totalAnalyzed = reportRepository.count();
        return new AdminStatsResponse(totalUsers, totalIdeas, totalAnalyzed);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserProfileResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> new UserProfileResponse(
                        user.getId(),
                        user.getUsername(),
                        user.getEmail(),
                        user.getFullName(),
                        user.getRoleName(),
                        user.getCreatedAt(),
                        user.getUpdatedAt()
                ))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<StartupIdeaResponse> getAllIdeas() {
        return ideaRepository.findAll().stream()
                .map(idea -> new StartupIdeaResponse(
                        idea.getId(),
                        idea.getUser() != null ? idea.getUser().getId() : null,
                        idea.getTitle(),
                        idea.getDescription(),
                        idea.getIndustry(),
                        idea.getTargetAudience(),
                        idea.getStage(),
                        reportRepository.findByIdeaId(idea.getId()).isPresent(),
                        idea.getCreatedAt(),
                        idea.getUpdatedAt()
                ))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteIdea(Long id) {
        StartupIdea idea = ideaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("StartupIdea", "id", id));
        ideaRepository.delete(idea);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        userRepository.delete(user);
    }
}
