package com.startup.validator.service;

import com.startup.validator.dto.IdeaAnalysisResponse;
import com.startup.validator.dto.IdeaAnalysisResponse.BusinessModelCanvasDTO;
import com.startup.validator.dto.IdeaAnalysisResponse.CompetitorDTO;
import com.startup.validator.dto.IdeaAnalysisResponse.FundingRoadmapDTO;
import com.startup.validator.entity.*;
import com.startup.validator.exception.ResourceNotFoundException;
import com.startup.validator.repository.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * AnalysisServiceImpl implements validation logic using a detailed mock-AI business rules engine.
 */
@Service
public class AnalysisServiceImpl implements AnalysisService {

    private final StartupIdeaRepository startupIdeaRepository;
    private final UserRepository userRepository;
    private final IdeaAnalysisRepository ideaAnalysisRepository;
    private final SwotAnalysisRepository swotAnalysisRepository;
    private final CompetitorRepository competitorRepository;
    private final BusinessModelCanvasRepository businessModelCanvasRepository;
    private final FundingRoadmapRepository fundingRoadmapRepository;

    public AnalysisServiceImpl(StartupIdeaRepository startupIdeaRepository,
                               UserRepository userRepository,
                               IdeaAnalysisRepository ideaAnalysisRepository,
                               SwotAnalysisRepository swotAnalysisRepository,
                               CompetitorRepository competitorRepository,
                               BusinessModelCanvasRepository businessModelCanvasRepository,
                               FundingRoadmapRepository fundingRoadmapRepository) {
        this.startupIdeaRepository = startupIdeaRepository;
        this.userRepository = userRepository;
        this.ideaAnalysisRepository = ideaAnalysisRepository;
        this.swotAnalysisRepository = swotAnalysisRepository;
        this.competitorRepository = competitorRepository;
        this.businessModelCanvasRepository = businessModelCanvasRepository;
        this.fundingRoadmapRepository = fundingRoadmapRepository;
    }

    @Override
    @Transactional
    public IdeaAnalysisResponse analyzeIdea(String usernameOrEmail, Long ideaId) {
        User user = userRepository.findByUsername(usernameOrEmail)
                .or(() -> userRepository.findByEmail(usernameOrEmail))
                .orElseThrow(() -> new ResourceNotFoundException("User", "usernameOrEmail", usernameOrEmail));

        StartupIdea idea = startupIdeaRepository.findById(ideaId)
                .orElseThrow(() -> new ResourceNotFoundException("StartupIdea", "id", ideaId));

        if (!idea.getUser().getId().equals(user.getId()) && !user.getRole().equals(Role.ADMIN)) {
            throw new AccessDeniedException("You do not have permission to analyze this startup idea");
        }

        // Clean up previous analyses to support fresh validation
        cleanUpPreviousAnalysis(ideaId);

        // Run the dynamic business rule generator
        IdeaAnalysisResponse generatedData = generateMockAIAnalysis(idea);

        // 1. Save Core Idea Analysis
        IdeaAnalysis ideaAnalysis = IdeaAnalysis.builder()
                .idea(idea)
                .marketFeasibility(generatedData.getMarketFeasibility())
                .riskAssessment(generatedData.getRiskAssessment())
                .recommendations(generatedData.getRecommendations())
                .build();
        ideaAnalysisRepository.save(ideaAnalysis);

        // 2. Save SWOT Details
        SwotAnalysis swotAnalysis = SwotAnalysis.builder()
                .idea(idea)
                .strengths(generatedData.getStrengths())
                .weaknesses(generatedData.getWeaknesses())
                .opportunities(generatedData.getOpportunities())
                .threats(generatedData.getThreats())
                .build();
        swotAnalysisRepository.save(swotAnalysis);

        // 3. Save Business Model Canvas
        BusinessModelCanvasDTO bmcDto = generatedData.getBusinessModelCanvas();
        BusinessModelCanvas bmc = BusinessModelCanvas.builder()
                .idea(idea)
                .keyPartners(bmcDto.getKeyPartners())
                .keyActivities(bmcDto.getKeyActivities())
                .keyResources(bmcDto.getKeyResources())
                .valuePropositions(bmcDto.getValuePropositions())
                .customerRelationships(bmcDto.getCustomerRelationships())
                .channels(bmcDto.getChannels())
                .customerSegments(bmcDto.getCustomerSegments())
                .costStructure(bmcDto.getCostStructure())
                .revenueStreams(bmcDto.getRevenueStreams())
                .build();
        businessModelCanvasRepository.save(bmc);

        // 4. Save Funding Suggestions Roadmap
        FundingRoadmapDTO fundingDto = generatedData.getFundingRoadmap();
        FundingRoadmap roadmap = FundingRoadmap.builder()
                .idea(idea)
                .fundingStage(fundingDto.getFundingStage())
                .targetAmount(fundingDto.getTargetAmount())
                .usageDetails(fundingDto.getUsageDetails())
                .milestoneGoal(fundingDto.getMilestoneGoal())
                .build();
        fundingRoadmapRepository.save(roadmap);

        // 5. Save Competitors
        List<Competitor> competitors = generatedData.getCompetitors().stream()
                .map(compDto -> Competitor.builder()
                        .idea(idea)
                        .competitorName(compDto.getCompetitorName())
                        .strength(compDto.getStrength())
                        .weakness(compDto.getWeakness())
                        .marketShare(compDto.getMarketShare())
                        .build())
                .collect(Collectors.toList());
        competitorRepository.saveAll(competitors);

        return generatedData;
    }

    @Override
    @Transactional(readOnly = true)
    public IdeaAnalysisResponse getAnalysisByIdeaId(String usernameOrEmail, Long ideaId) {
        User user = userRepository.findByUsername(usernameOrEmail)
                .or(() -> userRepository.findByEmail(usernameOrEmail))
                .orElseThrow(() -> new ResourceNotFoundException("User", "usernameOrEmail", usernameOrEmail));

        StartupIdea idea = startupIdeaRepository.findById(ideaId)
                .orElseThrow(() -> new ResourceNotFoundException("StartupIdea", "id", ideaId));

        if (!idea.getUser().getId().equals(user.getId()) && !user.getRole().equals(Role.ADMIN)) {
            throw new AccessDeniedException("You do not have permission to view this analysis");
        }

        IdeaAnalysis analysis = ideaAnalysisRepository.findByIdeaId(ideaId)
                .orElseThrow(() -> new ResourceNotFoundException("IdeaAnalysis", "ideaId", ideaId));

        SwotAnalysis swot = swotAnalysisRepository.findByIdeaId(ideaId).orElse(null);
        BusinessModelCanvas bmc = businessModelCanvasRepository.findByIdeaId(ideaId).orElse(null);
        FundingRoadmap funding = fundingRoadmapRepository.findByIdeaId(ideaId).orElse(null);
        List<Competitor> competitors = competitorRepository.findByIdeaId(ideaId);

        return assembleResponse(ideaId, analysis, swot, bmc, funding, competitors);
    }

    /**
     * Purge existing related entities to prevent duplicate unique keys.
     */
    private void cleanUpPreviousAnalysis(Long ideaId) {
        ideaAnalysisRepository.findByIdeaId(ideaId).ifPresent(ideaAnalysisRepository::delete);
        swotAnalysisRepository.findByIdeaId(ideaId).ifPresent(swotAnalysisRepository::delete);
        businessModelCanvasRepository.findByIdeaId(ideaId).ifPresent(businessModelCanvasRepository::delete);
        fundingRoadmapRepository.findByIdeaId(ideaId).ifPresent(fundingRoadmapRepository::delete);
        
        List<Competitor> existingCompetitors = competitorRepository.findByIdeaId(ideaId);
        if (!existingCompetitors.isEmpty()) {
            competitorRepository.deleteAll(existingCompetitors);
        }
    }

    /**
     * Assembles child records into a single consolidated Response payload.
     */
    private IdeaAnalysisResponse assembleResponse(Long ideaId, IdeaAnalysis analysis, SwotAnalysis swot,
                                                   BusinessModelCanvas bmc, FundingRoadmap funding, List<Competitor> competitors) {
        
        List<CompetitorDTO> competitorDTOs = competitors.stream()
                .map(c -> CompetitorDTO.builder()
                        .competitorName(c.getCompetitorName())
                        .strength(c.getStrength())
                        .weakness(c.getWeakness())
                        .marketShare(c.getMarketShare())
                        .build())
                .collect(Collectors.toList());

        BusinessModelCanvasDTO bmcDTO = null;
        if (bmc != null) {
            bmcDTO = BusinessModelCanvasDTO.builder()
                    .keyPartners(bmc.getKeyPartners())
                    .keyActivities(bmc.getKeyActivities())
                    .keyResources(bmc.getKeyResources())
                    .valuePropositions(bmc.getValuePropositions())
                    .customerRelationships(bmc.getCustomerRelationships())
                    .channels(bmc.getChannels())
                    .customerSegments(bmc.getCustomerSegments())
                    .costStructure(bmc.getCostStructure())
                    .revenueStreams(bmc.getRevenueStreams())
                    .build();
        }

        FundingRoadmapDTO fundingDTO = null;
        if (funding != null) {
            fundingDTO = FundingRoadmapDTO.builder()
                    .fundingStage(funding.getFundingStage())
                    .targetAmount(funding.getTargetAmount())
                    .usageDetails(funding.getUsageDetails())
                    .milestoneGoal(funding.getMilestoneGoal())
                    .build();
        }

        return IdeaAnalysisResponse.builder()
                .ideaId(ideaId)
                .marketFeasibility(analysis.getMarketFeasibility())
                .riskAssessment(analysis.getRiskAssessment())
                .recommendations(analysis.getRecommendations())
                .strengths(swot != null ? swot.getStrengths() : null)
                .weaknesses(swot != null ? swot.getWeaknesses() : null)
                .opportunities(swot != null ? swot.getOpportunities() : null)
                .threats(swot != null ? swot.getThreats() : null)
                .competitors(competitorDTOs)
                .businessModelCanvas(bmcDTO)
                .fundingRoadmap(fundingDTO)
                .build();
    }

    /**
     * Rules engine representing AI Validation logic generating outputs based on industry, target audience, and description keywords.
     */
    private IdeaAnalysisResponse generateMockAIAnalysis(StartupIdea idea) {
        String industry = idea.getIndustry();
        String audience = idea.getTargetAudience();
        String stage = idea.getStage();
        String title = idea.getTitle();

        // 1. Set values based on industry
        String marketFeasibility;
        String riskAssessment;
        String recommendations;
        String strengths, weaknesses, opportunities, threats;
        
        List<CompetitorDTO> competitors = new ArrayList<>();
        BusinessModelCanvasDTO bmc;
        FundingRoadmapDTO funding;

        if ("Healthcare".equalsIgnoreCase(industry) || "Biotech".equalsIgnoreCase(industry)) {
            marketFeasibility = "Highly lucrative but requires navigating strict compliance frameworks (e.g. HIPAA, FDA). High initial barrier to entry ensures strong defensibility for successful players.";
            riskAssessment = "HIGH (Score: 8/10). Main risks are regulatory rejection, clinical validation timeline slippage, and high R&D infrastructure burn rates.";
            recommendations = "Focus on obtaining early clinical validation. Partner with medical research institutions. Hire a dedicated regulatory advisor in year 1.";
            
            strengths = "Addressing critical human needs; potential for IP protection (patents); high customer lifetime value.";
            weaknesses = "Long development cycle; high initial startup capital requirements; complex distribution channels.";
            opportunities = "Telehealth integrations; AI-powered diagnostics; global aging population trends.";
            threats = "Regulatory changes; medical malpractice liabilities; competition from established pharma giants.";

            competitors.add(new CompetitorDTO("Medtronic Corp", "Established hospital relationships", "Slow software integration speeds", "15%"));
            competitors.add(new CompetitorDTO("Helix Diagnostics", "Proprietary testing kits", "Limited telehealth options", "8%"));
            
            bmc = BusinessModelCanvasDTO.builder()
                    .keyPartners("Hospitals, Clinics, Research Laboratories, Medical Insurers")
                    .keyActivities("R&D, Clinical Trials, Software Development, Compliance Audits")
                    .keyResources("Patents, Medical Researchers, Medical IoT Hardware, Secure Servers")
                    .valuePropositions("Secure digital tracking to minimize diagnostic slippage by 45% for " + audience)
                    .customerRelationships("Trust-based consultancies, enterprise service level agreements (SLAs)")
                    .channels("B2B Sales Teams, Medical Conferences, Direct-to-Hospital networks")
                    .customerSegments(audience + ", Medical Directors, Lab Technicians")
                    .costStructure("Research and Development salaries, Clinical certification fees, Cloud hosting (HIPAA compliant)")
                    .revenueStreams("Hardware sales margin, Software subscription (SaaS) per hospital node")
                    .build();

        } else if ("Finance".equalsIgnoreCase(industry) || "Fintech".equalsIgnoreCase(industry)) {
            marketFeasibility = "Exceptional market size with strong digital transformation tailwinds. High consumer demand for cost-reduction, transparent transactions, and decentralized ledger controls.";
            riskAssessment = "MEDIUM-HIGH (Score: 7/10). Risks revolve around transaction fraud liabilities, security breaches, and compliance with anti-money laundering (AML/KYC) standards.";
            recommendations = "Secure third-party security audits immediately. Build relationships with regional sponsor banks for rapid regulatory sandboxing.";
            
            strengths = "Scalable transactional business model; zero physical inventory; high recurring payment metrics.";
            weaknesses = "High customer acquisition costs (CAC); reliance on legacy banking infrastructure integrations.";
            opportunities = "Cross-border micropayments; blockchain asset management; AI fraud-prevention tools.";
            threats = "Central bank rate adjustments; cyber-attacks; strict regional finance compliance rules.";

            competitors.add(new CompetitorDTO("Stripe", "Developer-friendly APIs, massive scale", "High transaction fees for micro-payments", "35%"));
            competitors.add(new CompetitorDTO("Revolut", "Low fees, multi-currency features", "Complex customer support networks", "12%"));

            bmc = BusinessModelCanvasDTO.builder()
                    .keyPartners("Sponsor Banks, Visa/Mastercard networks, KYC Verification Providers")
                    .keyActivities("Payment processing logic, API security optimization, Fraud monitoring")
                    .keyResources("Payment licensing, High-performance ledger database, Security Engineers")
                    .valuePropositions("Instant transaction settlements reducing transaction overhead by 30% for " + audience)
                    .customerRelationships("Automated API portals, 24/7 technical customer support")
                    .channels("Online developer documentations, Product Hunt launch, Developer advocacy programs")
                    .customerSegments(audience + ", Small Businesses, International Freelancers")
                    .costStructure("Payment network interchange fees, Customer acquisition marketing, Security audits")
                    .revenueStreams("0.5% - 2.0% transaction commission fees, Premium account monthly SaaS subscription")
                    .build();

        } else { // Tech / E-commerce / General
            marketFeasibility = "Large and rapidly expanding target market with low initial setup barriers. Success depends heavily on brand positioning, user acquisition strategies, and viral growth loops.";
            riskAssessment = "MEDIUM (Score: 5/10). Main risks are low switching barriers for customers, intense competitor clusters, and rising ad acquisition costs.";
            recommendations = "Prioritize building an MVP with a stellar user experience. Focus on organic customer acquisition channels (SEO, word-of-mouth). Optimize user retention.";
            
            strengths = "Fast setup speeds; low overhead capital; flexibility to pivot code features based on feedback.";
            weaknesses = "Low initial entry barrier creates massive competitors; dependency on advertising platforms.";
            opportunities = "Integrations with popular platforms; AI automation; niche marketing targeting " + audience;
            threats = "Rapid change in consumer preferences; platform fee increases; copycat platforms.";

            competitors.add(new CompetitorDTO("Shopify Solutions", "Huge app marketplace", "High transaction fees, generic templates", "40%"));
            competitors.add(new CompetitorDTO("Etsy Global", "Built-in audience for niche creators", "Strict platform rules and high listing fees", "20%"));

            bmc = BusinessModelCanvasDTO.builder()
                    .keyPartners("E-commerce platforms, Logistical suppliers, Marketing Influencers")
                    .keyActivities("Product catalog curations, SEO marketing, User interface optimizations")
                    .keyResources("Domain assets, Brand reputation, Cloud database infrastructure")
                    .valuePropositions("A seamless, frictionless portal designed specifically to help " + audience + " connect with verified solutions")
                    .customerRelationships("Self-service portals, Automated email support, Community forums")
                    .channels("Social media advertising (TikTok/Instagram), Search engine optimizations (SEO)")
                    .customerSegments(audience + ", Digital Native consumers")
                    .costStructure("Social media advertising budgets, Cloud hosting costs, Developer maintenance salaries")
                    .revenueStreams("Subscription membership model, Lead generation commissions, Platform ad slots")
                    .build();
        }

        // 2. Adjust Funding stage suggestion based on the current stage of the idea
        String fundingStage;
        BigDecimal targetAmount;
        String usageDetails;
        String milestoneGoal;

        if ("MVP".equalsIgnoreCase(stage)) {
            fundingStage = "Seed Round";
            targetAmount = new BigDecimal("250000.00");
            usageDetails = "60% towards hiring core product developers, 20% for early customer acquisition ads, 20% for legal compliance and hosting infrastructure.";
            milestoneGoal = "Build out a polished public-facing Beta version of " + title + ", acquire first 1,000 active users, and demonstrate a 15% month-over-month growth rate.";
        } else if ("Scaling".equalsIgnoreCase(stage)) {
            fundingStage = "Series A";
            targetAmount = new BigDecimal("1500000.00");
            usageDetails = "45% for scaling customer acquisition channels (sales/marketing), 35% for enterprise engineering hires, 20% for international market expansions.";
            milestoneGoal = "Achieve $1.5M Annual Recurring Revenue (ARR), expand operations into 2 new geographical territories, and secure enterprise partnerships.";
        } else { // Ideation
            fundingStage = "Pre-Seed / Angel Round";
            targetAmount = new BigDecimal("75000.00");
            usageDetails = "50% for prototype wireframing and initial MVP coding, 30% for qualitative target customer interviews, 20% for initial server setups and branding.";
            milestoneGoal = "Develop functional click-through wireframes, run landing page sign-up test to get 500 waitlist users, and build a MVP version of " + title + ".";
        }

        funding = FundingRoadmapDTO.builder()
                .fundingStage(fundingStage)
                .targetAmount(targetAmount)
                .usageDetails(usageDetails)
                .milestoneGoal(milestoneGoal)
                .build();

        return IdeaAnalysisResponse.builder()
                .ideaId(idea.getId())
                .marketFeasibility(marketFeasibility)
                .riskAssessment(riskAssessment)
                .recommendations(recommendations)
                .strengths(strengths)
                .weaknesses(weaknesses)
                .opportunities(opportunities)
                .threats(threats)
                .competitors(competitors)
                .businessModelCanvas(bmc)
                .fundingRoadmap(funding)
                .build();
    }
}
