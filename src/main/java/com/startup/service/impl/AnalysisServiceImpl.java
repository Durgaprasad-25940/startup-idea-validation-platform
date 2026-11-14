package com.startup.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.startup.dto.AnalysisReportResponse;
import com.startup.dto.IdeaAnalysisResponse;
import com.startup.dto.IdeaAnalysisResponse.BusinessModelCanvasDTO;
import com.startup.dto.IdeaAnalysisResponse.CompetitorDTO;
import com.startup.dto.IdeaAnalysisResponse.FundingRoadmapDTO;
import com.startup.entity.AnalysisReport;
import com.startup.entity.Role;
import com.startup.entity.StartupIdea;
import com.startup.entity.User;
import com.startup.exception.ResourceNotFoundException;
import com.startup.repository.AnalysisReportRepository;
import com.startup.repository.StartupIdeaRepository;
import com.startup.repository.UserRepository;
import com.startup.service.AnalysisService;
import com.startup.service.OpenAiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of AnalysisService managing validation scans and saved analysis reports.
 */
@Service
public class AnalysisServiceImpl implements AnalysisService {

    private static final Logger logger = LoggerFactory.getLogger(AnalysisServiceImpl.class);

    private final StartupIdeaRepository ideaRepository;
    private final UserRepository userRepository;
    private final AnalysisReportRepository reportRepository;
    private final OpenAiService openAiService;
    private final ObjectMapper objectMapper;

    public AnalysisServiceImpl(StartupIdeaRepository ideaRepository,
                               UserRepository userRepository,
                               AnalysisReportRepository reportRepository,
                               OpenAiService openAiService,
                               ObjectMapper objectMapper) {
        this.ideaRepository = ideaRepository;
        this.userRepository = userRepository;
        this.reportRepository = reportRepository;
        this.openAiService = openAiService;
        this.objectMapper = objectMapper;
    }

    private User getUserByUsernameOrEmail(String usernameOrEmail) {
        return userRepository.findByUsername(usernameOrEmail)
                .or(() -> userRepository.findByEmail(usernameOrEmail))
                .orElseThrow(() -> new ResourceNotFoundException("User", "usernameOrEmail", usernameOrEmail));
    }

    @Override
    @Transactional
    public IdeaAnalysisResponse analyzeIdea(String usernameOrEmail, Long ideaId) {
        User user = getUserByUsernameOrEmail(usernameOrEmail);
        StartupIdea idea = ideaRepository.findById(ideaId)
                .orElseThrow(() -> new ResourceNotFoundException("StartupIdea", "id", ideaId));

        if (!idea.getUser().getId().equals(user.getId()) && !user.getRole().equals(Role.ROLE_ADMIN)) {
            throw new AccessDeniedException("You do not have permission to analyze this startup idea");
        }

        // Generate analysis via OpenAI (or intelligent domain engine)
        IdeaAnalysisResponse analysis = openAiService.generateAnalysis(idea);
        analysis.setIdeaId(ideaId);

        // Save or update AnalysisReport in MySQL analysis_reports table
        saveOrUpdateReport(idea, analysis);

        return analysis;
    }

    @Override
    @Transactional(readOnly = true)
    public IdeaAnalysisResponse getAnalysisByIdeaId(String usernameOrEmail, Long ideaId) {
        User user = getUserByUsernameOrEmail(usernameOrEmail);
        StartupIdea idea = ideaRepository.findById(ideaId)
                .orElseThrow(() -> new ResourceNotFoundException("StartupIdea", "id", ideaId));

        if (!idea.getUser().getId().equals(user.getId()) && !user.getRole().equals(Role.ROLE_ADMIN)) {
            throw new AccessDeniedException("You do not have permission to view this analysis");
        }

        AnalysisReport report = reportRepository.findByIdeaId(ideaId)
                .orElseThrow(() -> new ResourceNotFoundException("AnalysisReport", "ideaId", ideaId));

        return mapReportToAnalysisResponse(report);
    }

    @Override
    @Transactional(readOnly = true)
    public AnalysisReportResponse getSavedReport(String usernameOrEmail, Long ideaId) {
        User user = getUserByUsernameOrEmail(usernameOrEmail);
        StartupIdea idea = ideaRepository.findById(ideaId)
                .orElseThrow(() -> new ResourceNotFoundException("StartupIdea", "id", ideaId));

        if (!idea.getUser().getId().equals(user.getId()) && !user.getRole().equals(Role.ROLE_ADMIN)) {
            throw new AccessDeniedException("You do not have permission to view this report");
        }

        AnalysisReport report = reportRepository.findByIdeaId(ideaId)
                .orElseThrow(() -> new ResourceNotFoundException("AnalysisReport", "ideaId", ideaId));

        return mapToReportResponse(report);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnalysisReportResponse> getAllSavedReports(String usernameOrEmail) {
        User user = getUserByUsernameOrEmail(usernameOrEmail);
        List<AnalysisReport> reports = reportRepository.findByUserId(user.getId());
        return reports.stream().map(this::mapToReportResponse).collect(Collectors.toList());
    }

    private void saveOrUpdateReport(StartupIdea idea, IdeaAnalysisResponse analysis) {
        AnalysisReport report = reportRepository.findByIdeaId(idea.getId()).orElse(null);
        if (report == null) {
            report = new AnalysisReport();
            report.setIdea(idea);
        }

        report.setMarketAnalysis(serializeMarket(analysis));
        report.setCompetitorAnalysis(serializeCompetitors(analysis.getCompetitors()));
        report.setSwotStrengths(analysis.getStrengths() != null ? analysis.getStrengths() : "N/A");
        report.setSwotWeaknesses(analysis.getWeaknesses() != null ? analysis.getWeaknesses() : "N/A");
        report.setSwotOpportunities(analysis.getOpportunities() != null ? analysis.getOpportunities() : "N/A");
        report.setSwotThreats(analysis.getThreats() != null ? analysis.getThreats() : "N/A");
        report.setImprovementSuggestions(serializeSuggestions(analysis));
        report.setViabilityScore(analysis.getViabilityScore() != null ? analysis.getViabilityScore() : 75);

        reportRepository.save(report);
    }

    private String serializeMarket(IdeaAnalysisResponse analysis) {
        try {
            MarketContainer mc = new MarketContainer(analysis.getMarketFeasibility(), analysis.getRiskAssessment());
            return objectMapper.writeValueAsString(mc);
        } catch (Exception e) {
            return analysis.getMarketFeasibility() + "\nRisk: " + analysis.getRiskAssessment();
        }
    }

    private String serializeCompetitors(List<CompetitorDTO> competitors) {
        try {
            return objectMapper.writeValueAsString(competitors != null ? competitors : new ArrayList<>());
        } catch (Exception e) {
            return "[]";
        }
    }

    private String serializeSuggestions(IdeaAnalysisResponse analysis) {
        try {
            SuggestionsContainer sc = new SuggestionsContainer(
                    analysis.getRecommendations(),
                    analysis.getBusinessModelCanvas(),
                    analysis.getFundingRoadmap()
            );
            return objectMapper.writeValueAsString(sc);
        } catch (Exception e) {
            return analysis.getRecommendations() != null ? analysis.getRecommendations() : "";
        }
    }

    private IdeaAnalysisResponse mapReportToAnalysisResponse(AnalysisReport report) {
        IdeaAnalysisResponse resp = new IdeaAnalysisResponse();
        resp.setIdeaId(report.getIdea().getId());
        resp.setViabilityScore(report.getViabilityScore());
        resp.setStrengths(report.getSwotStrengths());
        resp.setWeaknesses(report.getSwotWeaknesses());
        resp.setOpportunities(report.getSwotOpportunities());
        resp.setThreats(report.getSwotThreats());

        // Parse Market
        try {
            MarketContainer mc = objectMapper.readValue(report.getMarketAnalysis(), MarketContainer.class);
            resp.setMarketFeasibility(mc.marketFeasibility);
            resp.setRiskAssessment(mc.riskAssessment);
        } catch (Exception e) {
            resp.setMarketFeasibility(report.getMarketAnalysis());
            resp.setRiskAssessment("Standard risk assessment");
        }

        // Parse Competitors
        try {
            List<CompetitorDTO> comps = objectMapper.readValue(
                    report.getCompetitorAnalysis(),
                    new TypeReference<List<CompetitorDTO>>() {}
            );
            resp.setCompetitors(comps);
        } catch (Exception e) {
            resp.setCompetitors(new ArrayList<>());
        }

        // Parse Suggestions, BMC, Funding
        try {
            SuggestionsContainer sc = objectMapper.readValue(report.getImprovementSuggestions(), SuggestionsContainer.class);
            resp.setRecommendations(sc.recommendations);
            resp.setBusinessModelCanvas(sc.businessModelCanvas);
            resp.setFundingRoadmap(sc.fundingRoadmap);
        } catch (Exception e) {
            resp.setRecommendations(report.getImprovementSuggestions());
        }

        return resp;
    }

    private AnalysisReportResponse mapToReportResponse(AnalysisReport report) {
        return new AnalysisReportResponse(
                report.getId(),
                report.getIdea().getId(),
                report.getIdea().getTitle(),
                report.getMarketAnalysis(),
                report.getCompetitorAnalysis(),
                report.getSwotStrengths(),
                report.getSwotWeaknesses(),
                report.getSwotOpportunities(),
                report.getSwotThreats(),
                report.getImprovementSuggestions(),
                report.getViabilityScore(),
                report.getCreatedAt(),
                report.getUpdatedAt()
        );
    }

    // Helper containers for clean JSON storage in MySQL mediumtext columns
    public static class MarketContainer {
        public String marketFeasibility;
        public String riskAssessment;

        public MarketContainer() {}
        public MarketContainer(String marketFeasibility, String riskAssessment) {
            this.marketFeasibility = marketFeasibility;
            this.riskAssessment = riskAssessment;
        }
    }

    public static class SuggestionsContainer {
        public String recommendations;
        public BusinessModelCanvasDTO businessModelCanvas;
        public FundingRoadmapDTO fundingRoadmap;

        public SuggestionsContainer() {}
        public SuggestionsContainer(String recommendations, BusinessModelCanvasDTO businessModelCanvas, FundingRoadmapDTO fundingRoadmap) {
            this.recommendations = recommendations;
            this.businessModelCanvas = businessModelCanvas;
            this.fundingRoadmap = fundingRoadmap;
        }
    }
}
