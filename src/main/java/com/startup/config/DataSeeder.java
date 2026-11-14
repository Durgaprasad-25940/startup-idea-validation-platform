package com.startup.config;

import com.startup.dto.StartupIdeaRequest;
import com.startup.dto.StartupIdeaResponse;
import com.startup.entity.Role;
import com.startup.entity.User;
import com.startup.repository.StartupIdeaRepository;
import com.startup.repository.UserRepository;
import com.startup.service.AnalysisService;
import com.startup.service.StartupIdeaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Initializes default demo accounts and pre-populated startup ideas on first run.
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataSeeder.class);

    private final UserRepository userRepository;
    private final StartupIdeaRepository ideaRepository;
    private final StartupIdeaService ideaService;
    private final AnalysisService analysisService;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UserRepository userRepository,
                      StartupIdeaRepository ideaRepository,
                      StartupIdeaService ideaService,
                      AnalysisService analysisService,
                      PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.ideaRepository = ideaRepository;
        this.ideaService = ideaService;
        this.analysisService = analysisService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        try {
            seedUsersAndIdeas();
        } catch (Exception ex) {
            logger.warn("DataSeeder note: {}", ex.getMessage());
        }
    }

    private void seedUsersAndIdeas() {
        // 1. Seed Demo Founder User
        User founder = userRepository.findByUsername("founder").orElseGet(() -> {
            logger.info("Seeding demo user: founder (password: password123)");
            User user = new User(
                    "founder",
                    "founder@startup.io",
                    passwordEncoder.encode("password123"),
                    "Alex Founder",
                    Role.ROLE_USER
            );
            return userRepository.save(user);
        });

        // 2. Seed Demo Admin User
        userRepository.findByUsername("admin").orElseGet(() -> {
            logger.info("Seeding demo admin: admin (password: admin123)");
            User admin = new User(
                    "admin",
                    "admin@startup.io",
                    passwordEncoder.encode("admin123"),
                    "Platform Administrator",
                    Role.ROLE_ADMIN
            );
            return userRepository.save(admin);
        });

        // 3. Seed Demo Ideas if founder has no ideas yet
        if (ideaRepository.countByUserId(founder.getId()) == 0) {
            logger.info("Seeding sample startup ideas and analyses for demo founder...");

            // Idea 1: Validated Healthcare Idea
            StartupIdeaResponse idea1 = ideaService.createIdea("founder", new StartupIdeaRequest(
                    "MedAssist AI",
                    "An automated clinical decision copilot that summarizes medical records and warns physicians of rare drug-drug interactions.",
                    "Healthcare",
                    "General Practitioners & Outpatient Clinics",
                    "Ideation"
            ));
            analysisService.analyzeIdea("founder", idea1.getId());

            // Idea 2: Validated Fintech Idea
            StartupIdeaResponse idea2 = ideaService.createIdea("founder", new StartupIdeaRequest(
                    "FlowPay",
                    "A multi-currency micro-transaction ledger that reduces cross-border exchange fees by 60% and provides instant settlements.",
                    "Finance",
                    "International Freelancers & Cross-Border SMBs",
                    "MVP"
            ));
            analysisService.analyzeIdea("founder", idea2.getId());

            // Idea 3: Needs Analysis (EdTech)
            ideaService.createIdea("founder", new StartupIdeaRequest(
                    "SkillForge",
                    "An interactive cohort-based learning platform featuring automated AI code review and personalized technical career roadmaps.",
                    "Education",
                    "Career Switchers & Aspiring Software Developers",
                    "Prototype"
            ));

            // Idea 4: Needs Analysis (E-Commerce)
            ideaService.createIdea("founder", new StartupIdeaRequest(
                    "EcoCart",
                    "A sustainable shopping extension that calculates the carbon footprint of products and suggests verified green alternatives.",
                    "E-Commerce",
                    "Environmentally Conscious Online Shoppers",
                    "Ideation"
            ));

            logger.info("Demo data seeding completed successfully!");
        }
    }
}
