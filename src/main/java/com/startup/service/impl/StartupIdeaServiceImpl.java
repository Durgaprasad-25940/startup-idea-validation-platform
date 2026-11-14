package com.startup.service.impl;

import com.startup.dto.StartupIdeaRequest;
import com.startup.dto.StartupIdeaResponse;
import com.startup.entity.Role;
import com.startup.entity.StartupIdea;
import com.startup.entity.User;
import com.startup.exception.ResourceNotFoundException;
import com.startup.repository.AnalysisReportRepository;
import com.startup.repository.StartupIdeaRepository;
import com.startup.repository.UserRepository;
import com.startup.service.StartupIdeaService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service implementation for Startup Idea operations.
 */
@Service
public class StartupIdeaServiceImpl implements StartupIdeaService {

    private final StartupIdeaRepository ideaRepository;
    private final UserRepository userRepository;
    private final AnalysisReportRepository analysisReportRepository;

    public StartupIdeaServiceImpl(StartupIdeaRepository ideaRepository,
                                  UserRepository userRepository,
                                  AnalysisReportRepository analysisReportRepository) {
        this.ideaRepository = ideaRepository;
        this.userRepository = userRepository;
        this.analysisReportRepository = analysisReportRepository;
    }

    private User getUserByUsernameOrEmail(String usernameOrEmail) {
        return userRepository.findByUsername(usernameOrEmail)
                .or(() -> userRepository.findByEmail(usernameOrEmail))
                .orElseThrow(() -> new ResourceNotFoundException("User", "usernameOrEmail", usernameOrEmail));
    }

    @Override
    @Transactional
    public StartupIdeaResponse createIdea(String usernameOrEmail, StartupIdeaRequest request) {
        User user = getUserByUsernameOrEmail(usernameOrEmail);

        StartupIdea idea = new StartupIdea();
        idea.setUser(user);
        idea.setTitle(request.getTitle());
        idea.setDescription(request.getDescription());
        idea.setIndustry(request.getIndustry());
        idea.setTargetAudience(request.getTargetAudience());
        idea.setStage(request.getStage());

        StartupIdea savedIdea = ideaRepository.save(idea);
        return mapToResponse(savedIdea);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StartupIdeaResponse> getAllIdeas(String usernameOrEmail, String search, String industry) {
        User user = getUserByUsernameOrEmail(usernameOrEmail);

        List<StartupIdea> ideas;
        if ((search != null && !search.trim().isEmpty()) || (industry != null && !industry.trim().isEmpty() && !"All".equalsIgnoreCase(industry))) {
            ideas = ideaRepository.searchIdeas(user.getId(), search, "All".equalsIgnoreCase(industry) ? null : industry);
        } else {
            ideas = ideaRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
        }

        return ideas.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public StartupIdeaResponse getIdeaById(String usernameOrEmail, Long id) {
        User user = getUserByUsernameOrEmail(usernameOrEmail);
        StartupIdea idea = ideaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("StartupIdea", "id", id));

        if (!idea.getUser().getId().equals(user.getId()) && !user.getRole().equals(Role.ROLE_ADMIN)) {
            throw new AccessDeniedException("You do not have permission to view this startup idea");
        }

        return mapToResponse(idea);
    }

    @Override
    @Transactional
    public StartupIdeaResponse updateIdea(String usernameOrEmail, Long id, StartupIdeaRequest request) {
        User user = getUserByUsernameOrEmail(usernameOrEmail);
        StartupIdea idea = ideaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("StartupIdea", "id", id));

        if (!idea.getUser().getId().equals(user.getId()) && !user.getRole().equals(Role.ROLE_ADMIN)) {
            throw new AccessDeniedException("You do not have permission to edit this startup idea");
        }

        idea.setTitle(request.getTitle());
        idea.setDescription(request.getDescription());
        idea.setIndustry(request.getIndustry());
        idea.setTargetAudience(request.getTargetAudience());
        idea.setStage(request.getStage());

        StartupIdea updatedIdea = ideaRepository.save(idea);
        return mapToResponse(updatedIdea);
    }

    @Override
    @Transactional
    public void deleteIdea(String usernameOrEmail, Long id) {
        User user = getUserByUsernameOrEmail(usernameOrEmail);
        StartupIdea idea = ideaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("StartupIdea", "id", id));

        if (!idea.getUser().getId().equals(user.getId()) && !user.getRole().equals(Role.ROLE_ADMIN)) {
            throw new AccessDeniedException("You do not have permission to delete this startup idea");
        }

        ideaRepository.delete(idea);
    }

    private StartupIdeaResponse mapToResponse(StartupIdea idea) {
        boolean analyzed = idea.getAnalysisReport() != null 
                || analysisReportRepository.findByIdeaId(idea.getId()).isPresent();

        return new StartupIdeaResponse(
                idea.getId(),
                idea.getUser() != null ? idea.getUser().getId() : null,
                idea.getTitle(),
                idea.getDescription(),
                idea.getIndustry(),
                idea.getTargetAudience(),
                idea.getStage(),
                analyzed,
                idea.getCreatedAt(),
                idea.getUpdatedAt()
        );
    }
}
