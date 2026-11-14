// CLIENT-SIDE DASHBOARD CONTROLLER
document.addEventListener('DOMContentLoaded', () => {
    // 1. Guard route access
    checkAuthAndRedirect();

    const currentUser = getCurrentUser();
    
    // Show admin panel navigation option if role matches ADMIN
    if (currentUser && currentUser.role === 'ADMIN') {
        const menuAdmin = document.getElementById('menu-admin');
        if (menuAdmin) menuAdmin.style.display = 'block';
    }

    const searchInput = document.getElementById('search-input');
    const industryFilter = document.getElementById('industry-filter');
    const ideasListContainer = document.getElementById('ideas-list-container');
    const statTotalIdeas = document.getElementById('stat-total-ideas');
    const statValidatedIdeas = document.getElementById('stat-validated-ideas');
    const alertError = document.getElementById('alert-error');
    const alertSuccess = document.getElementById('alert-success');

    // Load elements initially
    fetchIdeas();

    // Event listeners for real-time search & filter
    searchInput.addEventListener('input', debounce(fetchIdeas, 400));
    industryFilter.addEventListener('change', fetchIdeas);

    async function fetchIdeas() {
        const searchQuery = searchInput.value.trim();
        const industryQuery = industryFilter.value;
        
        let url = `${API_BASE_URL}/ideas`;
        const params = [];
        if (searchQuery) params.push(`search=${encodeURIComponent(searchQuery)}`);
        if (industryQuery && industryQuery !== 'All') params.push(`industry=${encodeURIComponent(industryQuery)}`);
        
        if (params.length > 0) {
            url += `?${params.join('&')}`;
        }

        try {
            const response = await fetch(url, {
                method: 'GET',
                headers: getAuthHeader()
            });

            if (response.ok) {
                const ideas = await response.json();
                renderIdeas(ideas);
            } else {
                if (response.status === 401 || response.status === 403) {
                    logout();
                } else {
                    showError('Failed to retrieve startup idea list.');
                }
            }
        } catch (error) {
            console.error('Fetch Ideas Error:', error);
            showError('Unable to connect to backend server. Verify service status.');
        }
    }

    function renderIdeas(ideas) {
        ideasListContainer.innerHTML = '';

        // Calculate and update counters
        const total = ideas.length;
        const analyzed = ideas.filter(i => i.analyzed).length;
        statTotalIdeas.textContent = total;
        statValidatedIdeas.textContent = analyzed;

        if (ideas.length === 0) {
            ideasListContainer.innerHTML = `
                <div class="empty-state">
                    <i class="fa-solid fa-folder-open"></i>
                    <h3>No Startup Ideas Found</h3>
                    <p>Get started by clicking 'Add New Idea' above to design your validation canvas.</p>
                </div>
            `;
            return;
        }

        ideas.forEach(idea => {
            const card = document.createElement('div');
            card.className = 'idea-card';
            
            // Format timestamps nicely
            const createdDate = new Date(idea.createdAt).toLocaleDateString('en-US', {
                year: 'numeric',
                month: 'short',
                day: 'numeric'
            });

            const analysisBadge = idea.analyzed 
                ? `<span class="badge badge-analyzed"><i class="fa-solid fa-circle-check"></i> Validated</span>`
                : `<span class="badge badge-pending"><i class="fa-solid fa-circle-pause"></i> Needs Analysis</span>`;

            const actionButtons = idea.analyzed
                ? `
                   <a href="analysis.html?id=${idea.id}" class="btn-action-sm btn-view-analysis">
                       <i class="fa-solid fa-chart-line"></i> View Analysis
                   </a>
                   <button onclick="runAnalysis(${idea.id}, this)" class="btn-action-sm btn-analyze" style="background: rgba(99, 102, 241, 0.1); border: 1px solid rgba(99, 102, 241, 0.3); color: #a5b4fc;">
                       <i class="fa-solid fa-arrows-rotate"></i> Re-Analyze
                   </button>
                  `
                : `
                   <button onclick="runAnalysis(${idea.id}, this)" class="btn-action-sm btn-analyze">
                       <i class="fa-solid fa-brain"></i> Validate Idea
                   </button>
                  `;

            card.innerHTML = `
                <div class="idea-header">
                    <div>
                        <a href="submit-idea.html?edit=${idea.id}" class="idea-title" title="Edit idea details">
                            <h3>${escapeHtml(idea.title)}</h3>
                        </a>
                        <div class="idea-meta" style="margin-top: 0.5rem;">
                            <span class="badge badge-industry"><i class="fa-solid fa-tag"></i> ${escapeHtml(idea.industry)}</span>
                            <span class="badge badge-stage"><i class="fa-solid fa-bars-progress"></i> ${escapeHtml(idea.stage)}</span>
                            ${analysisBadge}
                        </div>
                    </div>
                    <div style="display: flex; gap: 0.5rem;">
                        <a href="submit-idea.html?edit=${idea.id}" class="btn-action-sm btn-edit" style="padding: 0.5rem;" title="Edit Idea">
                            <i class="fa-solid fa-pen-to-square"></i>
                        </a>
                        <button onclick="deleteIdea(${idea.id})" class="btn-action-sm btn-delete" style="padding: 0.5rem;" title="Delete Idea">
                            <i class="fa-solid fa-trash-can"></i>
                        </button>
                    </div>
                </div>
                <div class="idea-body">${escapeHtml(idea.description)}</div>
                <div class="idea-footer">
                    <span class="idea-date">Submitted on ${createdDate}</span>
                    <div class="idea-actions">${actionButtons}</div>
                </div>
            `;
            ideasListContainer.appendChild(card);
        });
    }

    // Expose actions to window context for onclick bindings
    window.runAnalysis = async (ideaId, button) => {
        const originalContent = button.innerHTML;
        button.disabled = true;
        button.innerHTML = `<i class="fa-solid fa-spinner fa-spin"></i> Validating...`;

        hideAlerts();

        try {
            const response = await fetch(`${API_BASE_URL}/ideas/${ideaId}/analyze`, {
                method: 'POST',
                headers: getAuthHeader()
            });

            if (response.ok) {
                showSuccess('Validation scan finished successfully! Redirecting to report...');
                setTimeout(() => {
                    window.location.href = `analysis.html?id=${ideaId}`;
                }, 1200);
            } else {
                const data = await response.json();
                showError(data.message || 'Validation analysis request failed.');
                button.disabled = false;
                button.innerHTML = originalContent;
            }
        } catch (error) {
            console.error('Validation request error:', error);
            showError('Unable to contact verification service. Verify backend is running.');
            button.disabled = false;
            button.innerHTML = originalContent;
        }
    };

    window.deleteIdea = async (ideaId) => {
        if (!confirm('Are you sure you want to permanently delete this startup idea? All related AI analysis reports will be deleted.')) {
            return;
        }

        hideAlerts();

        try {
            const response = await fetch(`${API_BASE_URL}/ideas/${ideaId}`, {
                method: 'DELETE',
                headers: getAuthHeader()
            });

            if (response.ok) {
                showSuccess('Startup idea deleted successfully.');
                fetchIdeas(); // Reload dashboard portfolio list
            } else {
                const data = await response.json();
                showError(data.message || 'Failed to delete idea.');
            }
        } catch (error) {
            console.error('Delete request error:', error);
            showError('Network error. Unable to perform deletion.');
        }
    };

    // Helper functions
    function showError(message) {
        alertError.innerHTML = `<i class="fa-solid fa-triangle-exclamation"></i> ${message}`;
        alertError.style.display = 'block';
    }

    function showSuccess(message) {
        alertSuccess.innerHTML = `<i class="fa-solid fa-circle-check"></i> ${message}`;
        alertSuccess.style.display = 'block';
    }

    function hideAlerts() {
        alertError.style.display = 'none';
        alertSuccess.style.display = 'none';
    }

    function debounce(func, delay) {
        let timer;
        return function (...args) {
            clearTimeout(timer);
            timer = setTimeout(() => func.apply(this, args), delay);
        };
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
