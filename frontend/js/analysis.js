// CLIENT-SIDE VALIDATION ANALYSIS CONTROLLER
document.addEventListener('DOMContentLoaded', () => {
    // 1. Guard route
    checkAuthAndRedirect();

    const currentUser = getCurrentUser();
    if (currentUser && currentUser.role === 'ADMIN') {
        const menuAdmin = document.getElementById('menu-admin');
        if (menuAdmin) menuAdmin.style.display = 'block';
    }

    const urlParams = new URLSearchParams(window.location.search);
    const ideaId = urlParams.get('id');

    if (!ideaId) {
        window.location.href = 'dashboard.html';
        return;
    }

    const reportTitle = document.getElementById('report-title');
    const reportMeta = document.getElementById('report-meta');
    const alertError = document.getElementById('alert-error');

    // SWOT References
    const swotStrengths = document.getElementById('data-swot-strengths');
    const swotWeaknesses = document.getElementById('data-swot-weaknesses');
    const swotOpportunities = document.getElementById('data-swot-opportunities');
    const swotThreats = document.getElementById('data-swot-threats');

    // Core Analysis References
    const marketFeasibility = document.getElementById('data-market-feasibility');
    const riskAssessment = document.getElementById('data-risk-assessment');
    const recommendations = document.getElementById('data-recommendations');

    // Competitors Table References
    const competitorsTableBody = document.getElementById('competitors-table-body');

    // BMC References
    const bmcKeyPartners = document.getElementById('bmc-key-partners');
    const bmcKeyActivities = document.getElementById('bmc-key-activities');
    const bmcKeyResources = document.getElementById('bmc-key-resources');
    const bmcValuePropositions = document.getElementById('bmc-value-propositions');
    const bmcCustomerRelationships = document.getElementById('bmc-customer-relationships');
    const bmcChannels = document.getElementById('bmc-channels');
    const bmcCustomerSegments = document.getElementById('bmc-customer-segments');
    const bmcCostStructure = document.getElementById('bmc-cost-structure');
    const bmcRevenueStreams = document.getElementById('bmc-revenue-streams');

    // Funding Roadmap References
    const roadmapTarget = document.getElementById('roadmap-funding-target');
    const roadmapStage = document.getElementById('roadmap-funding-stage');
    const roadmapUsage = document.getElementById('roadmap-funding-usage');
    const roadmapMilestone = document.getElementById('roadmap-funding-milestone');

    // Execute fetches
    loadIdeaAndAnalysis();

    async function loadIdeaAndAnalysis() {
        try {
            // 1. Fetch Idea Headers metadata
            const ideaResponse = await fetch(`${API_BASE_URL}/ideas/${ideaId}`, {
                method: 'GET',
                headers: getAuthHeader()
            });

            if (!ideaResponse.ok) {
                showError('Failed to retrieve startup idea details.');
                return;
            }
            const ideaData = await ideaResponse.json();
            
            // Populate page header details
            reportTitle.textContent = `Validation Report: ${ideaData.title}`;
            reportMeta.innerHTML = `
                <span class="badge badge-industry"><i class="fa-solid fa-tag"></i> ${escapeHtml(ideaData.industry)}</span>
                <span class="badge badge-stage"><i class="fa-solid fa-bars-progress"></i> ${escapeHtml(ideaData.stage)}</span>
            `;

            // 2. Fetch Aggregated Analysis details
            const analysisResponse = await fetch(`${API_BASE_URL}/ideas/${ideaId}/analysis`, {
                method: 'GET',
                headers: getAuthHeader()
            });

            if (!analysisResponse.ok) {
                if (analysisResponse.status === 404) {
                    showError('No validation report has been run yet. Please run verification analysis from the main Dashboard.');
                } else {
                    showError('Failed to fetch validation report analytics.');
                }
                return;
            }
            const analysisData = await analysisResponse.json();
            populateReport(analysisData);

        } catch (error) {
            console.error('Validation report fetching error:', error);
            showError('Unable to connect to service. Verify backend is running.');
        }
    }

    function populateReport(data) {
        // 1. Core Analytics
        marketFeasibility.textContent = data.marketFeasibility;
        riskAssessment.textContent = data.riskAssessment;
        recommendations.textContent = data.recommendations;

        // 2. SWOT
        swotStrengths.textContent = data.strengths;
        swotWeaknesses.textContent = data.weaknesses;
        swotOpportunities.textContent = data.opportunities;
        swotThreats.textContent = data.threats;

        // 3. Competitors
        competitorsTableBody.innerHTML = '';
        if (data.competitors && data.competitors.length > 0) {
            data.competitors.forEach(comp => {
                const tr = document.createElement('tr');
                tr.innerHTML = `
                    <td style="font-weight: 600; color: var(--text-primary);"><i class="fa-solid fa-user-ninja" style="color: var(--color-secondary); margin-right: 0.5rem;"></i> ${escapeHtml(comp.competitorName)}</td>
                    <td>${escapeHtml(comp.strength)}</td>
                    <td>${escapeHtml(comp.weakness)}</td>
                    <td style="font-weight: 600; color: var(--color-secondary);">${escapeHtml(comp.marketShare)}</td>
                `;
                competitorsTableBody.appendChild(tr);
            });
        } else {
            competitorsTableBody.innerHTML = `<tr><td colspan="4" style="text-align: center; color: var(--text-muted);">No competitors mapped.</td></tr>`;
        }

        // 4. Business Model Canvas
        if (data.businessModelCanvas) {
            const bmc = data.businessModelCanvas;
            bmcKeyPartners.textContent = bmc.keyPartners;
            bmcKeyActivities.textContent = bmc.keyActivities;
            bmcKeyResources.textContent = bmc.keyResources;
            bmcValuePropositions.textContent = bmc.valuePropositions;
            bmcCustomerRelationships.textContent = bmc.customerRelationships;
            bmcChannels.textContent = bmc.channels;
            bmcCustomerSegments.textContent = bmc.customerSegments;
            bmcCostStructure.textContent = bmc.costStructure;
            bmcRevenueStreams.textContent = bmc.revenueStreams;
        }

        // 5. Funding Roadmap
        if (data.fundingRoadmap) {
            const funding = data.fundingRoadmap;
            
            // Format currency target
            const formattedAmount = new Intl.NumberFormat('en-US', {
                style: 'currency',
                currency: 'USD'
            }).format(funding.targetAmount);

            roadmapTarget.textContent = formattedAmount;
            roadmapStage.textContent = funding.fundingStage;
            roadmapUsage.textContent = funding.usageDetails;
            roadmapMilestone.textContent = funding.milestoneGoal;
        }
    }

    // Tab Switcher handler
    window.switchTab = (tabId, button) => {
        // Remove active class from all tab buttons
        document.querySelectorAll('.tab-btn').forEach(btn => btn.classList.remove('active'));
        
        // Hide all tab contents
        document.querySelectorAll('.tab-content').forEach(content => content.classList.remove('active'));

        // Activate selected elements
        button.classList.add('active');
        document.getElementById(tabId).classList.add('active');
    };

    function showError(message) {
        alertError.innerHTML = `<i class="fa-solid fa-triangle-exclamation"></i> ${message}`;
        alertError.style.display = 'block';
        
        // Disable tab buttons on fatal error
        document.querySelectorAll('.tab-btn').forEach(btn => btn.disabled = true);
    }

    function escapeHtml(text) {
        if (!text) return '';
        return text
            .replace(/&/g, "&amp;")
            .replace(/</g, "&lt;")
            .replace(/>/g, "&gt;")
            .replace(/"/g, "&quot;")
            .replace(/'/g, "&#039;");
    }
});
