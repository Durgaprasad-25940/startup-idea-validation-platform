package com.startup.validator.service;

import com.startup.validator.dto.StartupIdeaRequest;
import com.startup.validator.dto.StartupIdeaResponse;
import com.startup.validator.entity.Role;
import com.startup.validator.entity.StartupIdea;
import com.startup.validator.entity.User;
import com.startup.validator.exception.ResourceNotFoundException;
import com.startup.validator.repository.IdeaAnalysisRepository;
import com.startup.validator.repository.StartupIdeaRepository;
import com.startup.validator.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * StartupIdeaServiceImpl executes data retrieval, validation, and authorization check boundaries on startup ideas.
 */
@Service
public class StartupIdeaServiceImpl implements StartupIdeaService {

    private final StartupIdeaRepository startupIdeaRepository;
    private final UserRepository userRepository;
    private final IdeaAnalysisRepository ideaAnalysisRepository;

    public StartupIdeaServiceImpl(StartupIdeaRepository startupIdeaRepository,
                                  UserRepository userRepository,
                                  IdeaAnalysisRepository ideaAnalysisRepository) {
        this.startupIdeaRepository = startupIdeaRepository;
        this.userRepository = userRepository;
        this.ideaAnalysisRepository = ideaAnalysisRepository;
    }

    @Override
    @Transactional
    public StartupIdeaResponse createIdea(String usernameOrEmail, StartupIdeaRequest request) {
        User user = userRepository.findByUsername(usernameOrEmail)
                .or(() -> userRepository.findByEmail(usernameOrEmail))
                .orElseThrow(() -> new ResourceNotFoundException("User", "usernameOrEmail", usernameOrEmail));

        StartupIdea idea = StartupIdea.builder()
                .user(user)
                .title(request.getTitle())
                .description(request.getDescription())
                .industry(request.getIndustry())
                .targetAudience(request.getTargetAudience())
                .stage(request.getStage())
                .build();

        StartupIdea savedIdea = startupIdeaRepository.save(idea);
        return mapToResponse(savedIdea);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StartupIdeaResponse> getAllIdeas(String usernameOrEmail, String search, String industry) {
        User user = userRepository.findByUsername(usernameOrEmail)
                .or(() -> userRepository.findByEmail(usernameOrEmail))
                .orElseThrow(() -> new ResourceNotFoundException("User", "usernameOrEmail", usernameOrEmail));

        String searchParam = (search == null || search.trim().isEmpty()) ? null : search.trim();
        String industryParam = (industry == null || industry.trim().isEmpty() || "All".equalsIgnoreCase(industry)) ? null : industry.trim();

        return startupIdeaRepository.searchIdeas(user, searchParam, industryParam)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public StartupIdeaResponse getIdeaById(String usernameOrEmail, Long id) {
        User user = userRepository.findByUsername(usernameOrEmail)
                .or(() -> userRepository.findByEmail(usernameOrEmail))
                .orElseThrow(() -> new ResourceNotFoundException("User", "usernameOrEmail", usernameOrEmail));

        StartupIdea idea = startupIdeaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("StartupIdea", "id", id));

        // Grant access only to the resource owner or an admin
        if (!idea.getUser().getId().equals(user.getId()) && !user.getRole().equals(Role.ADMIN)) {
            throw new AccessDeniedException("You do not have permission to view this startup idea");
        }

        return mapToResponse(idea);
    }

    @Override
    @Transactional
    public StartupIdeaResponse updateIdea(String usernameOrEmail, Long id, StartupIdeaRequest request) {
        User user = userRepository.findByUsername(usernameOrEmail)
                .or(() -> userRepository.findByEmail(usernameOrEmail))
                .orElseThrow(() -> new ResourceNotFoundException("User", "usernameOrEmail", usernameOrEmail));

        StartupIdea idea = startupIdeaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("StartupIdea", "id", id));

        if (!idea.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("You do not have permission to update this startup idea");
        }

        idea.setTitle(request.getTitle());
        idea.setDescription(request.getDescription());
        idea.setIndustry(request.getIndustry());
        idea.setTargetAudience(request.getTargetAudience());
        idea.setStage(request.getStage());

        StartupIdea updatedIdea = startupIdeaRepository.save(idea);
        return mapToResponse(updatedIdea);
    }

    @Override
    @Transactional
    public void deleteIdea(String usernameOrEmail, Long id) {
        User user = userRepository.findByUsername(usernameOrEmail)
                .or(() -> userRepository.findByEmail(usernameOrEmail))
                .orElseThrow(() -> new ResourceNotFoundException("User", "usernameOrEmail", usernameOrEmail));

        StartupIdea idea = startupIdeaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("StartupIdea", "id", id));

        if (!idea.getUser().getId().equals(user.getId()) && !user.getRole().equals(Role.ADMIN)) {
            throw new AccessDeniedException("You do not have permission to delete this startup idea");
        }

        startupIdeaRepository.delete(idea);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StartupIdeaResponse> getGlobalIdeas() {
        return startupIdeaRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void adminDeleteIdea(Long id) {
        if (!startupIdeaRepository.existsById(id)) {
            throw new ResourceNotFoundException("StartupIdea", "id", id);
        }
        startupIdeaRepository.deleteById(id);
    }

    /**
     * Map entity user structure to dynamic responses checking whether analyses have been run.
     */
    private StartupIdeaResponse mapToResponse(StartupIdea idea) {
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
