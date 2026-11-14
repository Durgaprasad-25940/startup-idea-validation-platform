package com.startup.validator.service;

import com.startup.validator.dto.AdminStatsResponse;
import com.startup.validator.dto.StartupIdeaResponse;
import com.startup.validator.dto.UserProfileResponse;
import com.startup.validator.entity.StartupIdea;
import com.startup.validator.entity.User;
import com.startup.validator.exception.ResourceNotFoundException;
import com.startup.validator.repository.IdeaAnalysisRepository;
import com.startup.validator.repository.StartupIdeaRepository;
import com.startup.validator.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * AdminServiceImpl executes administration queries.
 */
@Service
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final StartupIdeaRepository startupIdeaRepository;
    private final IdeaAnalysisRepository ideaAnalysisRepository;

    public AdminServiceImpl(UserRepository userRepository,
                            StartupIdeaRepository startupIdeaRepository,
                            IdeaAnalysisRepository ideaAnalysisRepository) {
        this.userRepository = userRepository;
        this.startupIdeaRepository = startupIdeaRepository;
        this.ideaAnalysisRepository = ideaAnalysisRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public AdminStatsResponse getPlatformStats() {
        long totalUsers = userRepository.count();
        long totalIdeas = startupIdeaRepository.count();
        long totalAnalyzed = ideaAnalysisRepository.count();

        return AdminStatsResponse.builder()
                .totalUsers(totalUsers)
                .totalIdeas(totalIdeas)
                .totalAnalyzedIdeas(totalAnalyzed)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserProfileResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToProfileResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<StartupIdeaResponse> getAllIdeas() {
        return startupIdeaRepository.findAll().stream()
                .map(this::mapToIdeaResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteIdea(Long id) {
        if (!startupIdeaRepository.existsById(id)) {
            throw new ResourceNotFoundException("StartupIdea", "id", id);
        }
        startupIdeaRepository.deleteById(id);
    }

    private UserProfileResponse mapToProfileResponse(User user) {
        return UserProfileResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole().name())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    private StartupIdeaResponse mapToIdeaResponse(StartupIdea idea) {
        boolean analyzed = ideaAnalysisRepository.existsByIdeaId(idea.getId());
        return StartupIdeaResponse.builder()
                .id(idea.getId())
                .userId(idea.getUser().getId())
                .title(idea.getTitle())
                .description(idea.getDescription())
                .industry(idea.getIndustry())
                .targetAudience(idea.getTargetAudience())
                .stage(idea.getStage())
                .analyzed(analyzed)
                .createdAt(idea.getCreatedAt())
                .updatedAt(idea.getUpdatedAt())
                .build();
    }
}
