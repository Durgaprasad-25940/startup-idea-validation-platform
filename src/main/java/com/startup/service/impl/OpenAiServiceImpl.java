package com.startup.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.startup.dto.IdeaAnalysisResponse;
import com.startup.dto.IdeaAnalysisResponse.BusinessModelCanvasDTO;
import com.startup.dto.IdeaAnalysisResponse.CompetitorDTO;
import com.startup.dto.IdeaAnalysisResponse.FundingRoadmapDTO;
import com.startup.entity.StartupIdea;
import com.startup.service.OpenAiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.*;

/**
 * Service implementation connecting to OpenAI Chat Completion API with fallback to a domain-rule engine.
 */
@Service
public class OpenAiServiceImpl implements OpenAiService {

    private static final Logger logger = LoggerFactory.getLogger(OpenAiServiceImpl.class);

    @Value("${openai.api.key:}")
    private String apiKey;

    @Value("${openai.model:gpt-4o-mini}")
    private String model;

    @Value("${openai.api.url:https://api.openai.com/v1/chat/completions}")
    private String apiUrl;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public OpenAiServiceImpl(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public IdeaAnalysisResponse generateAnalysis(StartupIdea idea) {
        if (apiKey != null && !apiKey.trim().isEmpty() && !apiKey.contains("your-") && !apiKey.contains("sk-placeholder")) {
            try {
                logger.info("Attempting OpenAI analysis for idea: {}", idea.getTitle());
                IdeaAnalysisResponse openAiResult = callOpenAiApi(idea);
                if (openAiResult != null) {
                    return openAiResult;
                }
            } catch (Exception ex) {
                logger.warn("OpenAI API call failed (falling back to built-in validation engine): {}", ex.getMessage());
            }
        } else {
            logger.info("No OpenAI API key supplied or placeholder used. Utilizing built-in validation engine.");
        }

        return generateRuleBasedAnalysis(idea);
    }

    private IdeaAnalysisResponse callOpenAiApi(StartupIdea idea) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey.trim());

        String prompt = buildPrompt(idea);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", model);
        requestBody.put("temperature", 0.7);

        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "system", "content",
                "You are an expert venture capitalist and startup incubator analyst. " +
                "Evaluate startup ideas rigorously and output STRICTLY valid JSON conforming to the requested schema with no markdown ticks."));
        messages.add(Map.of("role", "user", "content", prompt));

        requestBody.put("messages", messages);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, entity, String.class);

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode choices = root.path("choices");
            if (choices.isArray() && !choices.isEmpty()) {
                String content = choices.get(0).path("message").path("content").asText();
                // Strip markdown backticks if present
                content = content.replaceAll("```json", "").replaceAll("```", "").trim();
                return parseAnalysisJson(content, idea.getId());
            }
        }
        return null;
    }

    private String buildPrompt(StartupIdea idea) {
        return String.format(
                "Analyze this startup idea and return JSON with keys:\n" +
                "- viabilityScore (integer 1-100)\n" +
                "- marketFeasibility (string)\n" +
                "- riskAssessment (string)\n" +
                "- recommendations (string)\n" +
                "- strengths (string)\n" +
                "- weaknesses (string)\n" +
                "- opportunities (string)\n" +
                "- threats (string)\n" +
                "- competitors (array of objects with competitorName, strength, weakness, marketShare)\n" +
                "- businessModelCanvas (object with keyPartners, keyActivities, keyResources, valuePropositions, customerRelationships, channels, customerSegments, costStructure, revenueStreams)\n" +
                "- fundingRoadmap (object with fundingStage, targetAmount, usageDetails, milestoneGoal)\n\n" +
                "Startup Details:\n" +
                "Title: %s\n" +
                "Description: %s\n" +
                "Industry: %s\n" +
                "Target Audience: %s\n" +
                "Stage: %s",
                idea.getTitle(), idea.getDescription(), idea.getIndustry(), idea.getTargetAudience(), idea.getStage()
        );
    }

    private IdeaAnalysisResponse parseAnalysisJson(String json, Long ideaId) {
        try {
            IdeaAnalysisResponse resp = objectMapper.readValue(json, IdeaAnalysisResponse.class);
            resp.setIdeaId(ideaId);
            return resp;
        } catch (Exception ex) {
            logger.error("Failed to parse OpenAI JSON output: {}", ex.getMessage());
            return null;
        }
    }

    /**
     * Built-in intelligent validation rules engine used when OpenAI API is unavailable.
     */
    public IdeaAnalysisResponse generateRuleBasedAnalysis(StartupIdea idea) {
        String industry = idea.getIndustry() != null ? idea.getIndustry() : "General";
        String audience = idea.getTargetAudience() != null ? idea.getTargetAudience() : "General Consumers";
        String stage = idea.getStage() != null ? idea.getStage() : "Ideation";
        String title = idea.getTitle() != null ? idea.getTitle() : "Startup Idea";

        String marketFeasibility;
        String riskAssessment;
        String recommendations;
        String strengths, weaknesses, opportunities, threats;
        int viabilityScore = 78;

        List<CompetitorDTO> competitors = new ArrayList<>();
        BusinessModelCanvasDTO bmc = new BusinessModelCanvasDTO();
        FundingRoadmapDTO funding = new FundingRoadmapDTO();

        if ("Healthcare".equalsIgnoreCase(industry) || "Biotech".equalsIgnoreCase(industry) || "Healthtech".equalsIgnoreCase(industry)) {
            viabilityScore = 82;
            marketFeasibility = "High market demand driven by global healthcare digital transformation. High regulatory barriers (HIPAA, FDA) create significant defensibility against copycats once compliance is attained.";
            riskAssessment = "HIGH (Score: 8/10). Regulatory approval delays, clinical trial costs, and slow hospital procurement cycles represent key operational risks.";
            recommendations = "Obtain initial clinical validation studies early. Partner with research hospitals and onboard a healthcare regulatory compliance advisor in Year 1.";

            strengths = "Addresses critical healthcare friction points; high customer lifetime value (LTV); strong intellectual property potential.";
            weaknesses = "Prolonged enterprise sales cycles (6-18 months); stringent data privacy requirements; capital-intensive R&D.";
            opportunities = "Telehealth integrations; AI-assisted patient diagnostics; expanding aging demographic market.";
            threats = "Shifting government healthcare reimbursement policies; established healthcare conglomerate acquisitions or patent challenges.";

            competitors.add(new CompetitorDTO("Medtronic Digital", "Global clinical trial network", "Legacy software architecture", "18%"));
            competitors.add(new CompetitorDTO("Oscar Health Tech", "Consumer-facing digital experience", "High burn rate on customer acquisition", "12%"));

            bmc.setKeyPartners("Hospitals, Clinics, Research Laboratories, Medical Insurance Networks");
            bmc.setKeyActivities("R&D, Clinical Validations, HIPAA Security Compliance Audits");
            bmc.setKeyResources("Clinical Patents, Health Informatics Engineers, Secure Cloud Infrastructure");
            bmc.setValuePropositions("Streamlined diagnostic and monitoring platform reducing clinical turnaround by 40% for " + audience);
            bmc.setCustomerRelationships("Dedicated enterprise account managers, 24/7 technical SLAs");
            bmc.setChannels("B2B Healthcare Conferences, Direct Hospital Procurement, Medical Associations");
            bmc.setCustomerSegments(audience + ", Healthcare Providers, Clinic Operators");
            bmc.setCostStructure("Clinical certification fees, HIPAA-compliant server hosting, Clinical research salaries");
            bmc.setRevenueStreams("Annual software licensing (SaaS) per hospital node, Professional implementation fees");

        } else if ("Finance".equalsIgnoreCase(industry) || "Fintech".equalsIgnoreCase(industry)) {
            viabilityScore = 85;
            marketFeasibility = "Tremendous total addressable market (TAM). Financial institutions and end-users increasingly migrate to automated, low-cost digital transaction layers.";
            riskAssessment = "MEDIUM-HIGH (Score: 7/10). Cyber security exposure, fraud prevention liabilities, and financial licensing (AML/KYC compliance) are primary considerations.";
            recommendations = "Partner with established banking infrastructure providers (BaaS) to shorten licensing timelines. Execute independent SOC2 and penetration testing before launching.";

            strengths = "Highly scalable transactional recurring revenue; negative working capital cycles; minimal physical infrastructure requirements.";
            weaknesses = "High user acquisition cost (CAC); dependency on partner banks; consumer trust barrier in early stages.";
            opportunities = "Cross-border payment automation; embedded finance APIs; micro-investment tooling for emerging markets.";
            threats = "Regulatory tightening on capital requirements; crypto/fiat volatility; aggressive pricing pressure from legacy fintechs.";

            competitors.add(new CompetitorDTO("Stripe", "Unrivaled developer experience and documentation", "High processing fees for micro-transactions", "38%"));
            competitors.add(new CompetitorDTO("Revolut", "Low foreign exchange spreads and retail virality", "Customer support scaling bottlenecks", "14%"));

            bmc.setKeyPartners("Sponsor Banks, Card Networks (Visa/Mastercard), Identity Verification Vendors (Plaid/Onfido)");
            bmc.setKeyActivities("Transaction routing algorithms, Real-time fraud anomaly detection, API uptime optimization");
            bmc.setKeyResources("Banking-as-a-Service partnerships, PCI-DSS certified cloud ledger, Compliance officers");
            bmc.setValuePropositions("Transparent and low-friction financial workflow saving 35% in transaction fees for " + audience);
            bmc.setCustomerRelationships("Automated self-serve developer portal, Priority chat support");
            bmc.setChannels("API documentation, Fintech communities, Organic product comparisons, Referral incentives");
            bmc.setCustomerSegments(audience + ", Small and Medium Enterprises, Digital Nomads");
            bmc.setCostStructure("Interchange processing fees, Bank sponsorship fees, Security infrastructure audits");
            bmc.setRevenueStreams("0.8% - 1.8% transaction commission, Monthly tier subscription for advanced reporting");

        } else if ("Education".equalsIgnoreCase(industry) || "Edtech".equalsIgnoreCase(industry)) {
            viabilityScore = 76;
            marketFeasibility = "Steady upward growth due to demand for upskilling, remote certifications, and interactive learning environments.";
            riskAssessment = "MEDIUM (Score: 6/10). High initial student drop-off rates and seasonal enrollment fluctuations require strong community retention loops.";
            recommendations = "Incorporate gamification and community peer-accountability groups to increase course completion rates past 60%.";

            strengths = "Reusable content assets with negligible marginal delivery cost; global distribution capability.";
            weaknesses = "Content fatigue; low course completion benchmarks across free tiers; credential recognition barrier.";
            opportunities = "Corporate enterprise upskilling contracts; AI personalized tutoring engines; cohort-based cohorts.";
            threats = "Free open-source tutorials (YouTube, Coursera free audits); institutional resistance to non-accredited credentials.";

            competitors.add(new CompetitorDTO("Coursera", "University accreditation partnerships", "Low completion rates on asynchronous video tracks", "30%"));
            competitors.add(new CompetitorDTO("Duolingo", "Industry-leading gamification retention", "Limited depth for advanced professional training", "22%"));

            bmc.setKeyPartners("Subject Matter Experts, Corporate Talent Directors, University Career Centers");
            bmc.setKeyActivities("Curriculum design, Video production, Interactive coding/exercise grading engine");
            bmc.setKeyResources("Proprietary syllabus, Video library, Instructor community");
            bmc.setValuePropositions("Interactive mastery-based learning taking " + audience + " to industry readiness in 12 weeks");
            bmc.setCustomerRelationships("Cohort Slack/Discord communities, Mentor 1-on-1 office hours");
            bmc.setChannels("Campus ambassador programs, YouTube technical tutorials, LinkedIn social proof");
            bmc.setCustomerSegments(audience + ", Career Switchers, Corporate Learning & Development teams");
            bmc.setCostStructure("Instructor royalties, Video streaming bandwidth, Student acquisition ads");
            bmc.setRevenueStreams("Monthly/Annual learner subscription, B2B corporate seat licensing, Job placement recruiting fees");

        } else {
            // General / Technology / SaaS / AI / E-Commerce
            viabilityScore = 80;
            marketFeasibility = "Rapidly expanding ecosystem with accessible low-barrier testing environments. Product differentiation and defensible acquisition flywheels are critical for market share.";
            riskAssessment = "MEDIUM (Score: 5/10). Main risks are competitive saturation, customer churn in crowded software categories, and rising paid ad acquisition costs.";
            recommendations = "Deploy a lightweight MVP within 30 days to validate customer willingness to pay. Build strong customer feedback loops and optimize for organic referral virality.";

            strengths = "Short time-to-market; agile iteration velocity; low upfront infrastructure capital.";
            weaknesses = "Low switching barriers for early adopters; vulnerable to copycat clones without data network effects.";
            opportunities = "AI workflow automation; niche vertical specialization; community-driven product expansion.";
            threats = "Platform risk (changes to third-party APIs); rapid shift in open-source alternative availability.";

            competitors.add(new CompetitorDTO("Market Leader Inc", "Extensive feature set and enterprise footprint", "Bloated UI and expensive tiered plans", "32%"));
            competitors.add(new CompetitorDTO("Agile Startup Co", "Modern UI and competitive pricing", "Lack of enterprise security certifications", "15%"));

            bmc.setKeyPartners("Cloud Infrastructure (AWS/GCP), Technology integrations, Open-source tool maintainers");
            bmc.setKeyActivities("Continuous software delivery, Conversion rate optimization, Community building");
            bmc.setKeyResources("Proprietary algorithms, Customer usage data, Agile software engineering team");
            bmc.setValuePropositions("Modern, high-velocity solution engineered to automate and empower " + audience);
            bmc.setCustomerRelationships("Product-led growth (PLG) onboarding, Community forums, Direct founder communication");
            bmc.setChannels("Product Hunt launch, Technical blog / SEO content, Twitter/LinkedIn thought leadership");
            bmc.setCustomerSegments(audience + ", Modern Founders, Digital Creators");
            bmc.setCostStructure("Cloud hosting infrastructure, Engineering and design talent, Growth experiments");
            bmc.setRevenueStreams("Freemium to Paid SaaS tiers ($29 - $199/month), Custom enterprise plans");
        }

        // Stage-based Funding suggestions
        String fundingStage;
        BigDecimal targetAmount;
        String usageDetails;
        String milestoneGoal;

        if ("MVP".equalsIgnoreCase(stage) || "Prototype".equalsIgnoreCase(stage)) {
            fundingStage = "Seed Round";
            targetAmount = new BigDecimal("250000.00");
            usageDetails = "60% towards hiring core developers, 20% for pilot customer acquisition, 20% for legal compliance and hosting.";
            milestoneGoal = "Build out a polished public-facing Beta of " + title + ", acquire first 1,000 active users, and achieve 15% MoM growth.";
        } else if ("Scaling".equalsIgnoreCase(stage) || "Growth".equalsIgnoreCase(stage)) {
            fundingStage = "Series A";
            targetAmount = new BigDecimal("1500000.00");
            usageDetails = "45% for scaling customer acquisition channels, 35% for enterprise engineering hires, 20% for market expansion.";
            milestoneGoal = "Achieve $1.5M ARR, expand operations into 2 new target regions, and secure key enterprise partnerships.";
        } else {
            // Ideation
            fundingStage = "Pre-Seed / Angel Round";
            targetAmount = new BigDecimal("75000.00");
            usageDetails = "50% for prototype wireframing and MVP development, 30% for customer discovery interviews, 20% for initial branding.";
            milestoneGoal = "Develop functional click-through wireframes, run landing page validation with 500 waitlist signups, and launch the MVP.";
        }

        funding.setFundingStage(fundingStage);
        funding.setTargetAmount(targetAmount);
        funding.setUsageDetails(usageDetails);
        funding.setMilestoneGoal(milestoneGoal);

        IdeaAnalysisResponse response = new IdeaAnalysisResponse();
        response.setIdeaId(idea.getId());
        response.setViabilityScore(viabilityScore);
        response.setMarketFeasibility(marketFeasibility);
        response.setRiskAssessment(riskAssessment);
        response.setRecommendations(recommendations);
        response.setStrengths(strengths);
        response.setWeaknesses(weaknesses);
        response.setOpportunities(opportunities);
        response.setThreats(threats);
        response.setCompetitors(competitors);
        response.setBusinessModelCanvas(bmc);
        response.setFundingRoadmap(funding);

        return response;
    }
}
