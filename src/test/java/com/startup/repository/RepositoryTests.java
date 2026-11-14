package com.startup.repository;

import com.startup.entity.AnalysisReport;
import com.startup.entity.Role;
import com.startup.entity.StartupIdea;
import com.startup.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class RepositoryTests {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StartupIdeaRepository startupIdeaRepository;

    @Autowired
    private AnalysisReportRepository analysisReportRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        analysisReportRepository.deleteAll();
        startupIdeaRepository.deleteAll();
        userRepository.deleteAll();

        testUser = new User("founder1", "founder1@startup.io", "hashedPass123", "Founder One", Role.ROLE_USER);
        testUser = userRepository.save(testUser);
    }

    @Test
    @DisplayName("Module 1 Test: Save and Retrieve User")
    void testSaveAndFindUser() {
        Optional<User> byUsername = userRepository.findByUsername("founder1");
        assertTrue(byUsername.isPresent());
        assertEquals("founder1@startup.io", byUsername.get().getEmail());
        assertEquals("Founder One", byUsername.get().getFullName());
        assertEquals(Role.ROLE_USER, byUsername.get().getRole());
    }

    @Test
    @DisplayName("Module 1 Test: Save StartupIdea with foreign key to User")
    void testSaveStartupIdea() {
        StartupIdea idea = new StartupIdea(
                testUser,
                "AI Health Tracker",
                "Personalized health companion leveraging sensor data.",
                "Healthcare",
                "Chronic illness patients",
                "MVP"
        );
        StartupIdea saved = startupIdeaRepository.save(idea);

        assertNotNull(saved.getId());
        assertEquals("AI Health Tracker", saved.getTitle());
        assertEquals(testUser.getId(), saved.getUser().getId());

        List<StartupIdea> userIdeas = startupIdeaRepository.findByUserOrderByCreatedAtDesc(testUser);
        assertEquals(1, userIdeas.size());
        assertEquals("Healthcare", userIdeas.get(0).getIndustry());
    }

    @Test
    @DisplayName("Module 1 Test: Save AnalysisReport with OneToOne foreign key to StartupIdea")
    void testSaveAnalysisReport() {
        StartupIdea idea = new StartupIdea(
                testUser,
                "Fintech Flow",
                "Automated B2B micro-lending platform.",
                "Fintech",
                "SMB owners",
                "Ideation"
        );
        StartupIdea savedIdea = startupIdeaRepository.save(idea);

        AnalysisReport report = new AnalysisReport(
                savedIdea,
                "High growth potential in emerging markets.",
                "Top competitors include Brex and Stripe Capital.",
                "Agile architecture, low overhead",
                "Regulatory licensing barriers",
                "Underserved micro-merchants",
                "Default risk during market downturns",
                "Obtain regulatory sandbox approval first.",
                82
        );
        AnalysisReport savedReport = analysisReportRepository.save(report);

        assertNotNull(savedReport.getId());
        assertEquals(savedIdea.getId(), savedReport.getIdea().getId());
        assertEquals(82, savedReport.getViabilityScore());

        Optional<AnalysisReport> fetched = analysisReportRepository.findByIdeaId(savedIdea.getId());
        assertTrue(fetched.isPresent());
        assertEquals("High growth potential in emerging markets.", fetched.get().getMarketAnalysis());
    }
}
