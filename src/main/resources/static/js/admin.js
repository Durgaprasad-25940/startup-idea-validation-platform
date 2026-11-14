// CLIENT-SIDE PLATFORM ADMINISTRATION CONTROLLER
document.addEventListener('DOMContentLoaded', () => {
    // 1. Guard route access
    checkAuthAndRedirect();

    const currentUser = getCurrentUser();
    if (!currentUser || currentUser.role !== 'ADMIN') {
        // Enforce role authorization
        window.location.href = 'dashboard.html';
        return;
    }

    const statUsers = document.getElementById('stat-total-users');
    const statIdeas = document.getElementById('stat-total-ideas');
    const statValidations = document.getElementById('stat-total-validations');
    const usersTableBody = document.getElementById('admin-users-tbody');
    const ideasTableBody = document.getElementById('admin-ideas-tbody');
    const alertError = document.getElementById('alert-error');
    const alertSuccess = document.getElementById('alert-success');

    // Run fetches
    loadAdminDashboard();

    async function loadAdminDashboard() {
        hideAlerts();
        try {
            // 1. Fetch platform statistics counts
            const statsRes = await fetch(`${API_BASE_URL}/admin/stats`, {
                method: 'GET',
                headers: getAuthHeader()
            });

            if (statsRes.ok) {
                const statsData = await statsRes.json();
                statUsers.textContent = statsData.totalUsers;
                statIdeas.textContent = statsData.totalIdeas;
                statValidations.textContent = statsData.totalAnalyzedIdeas;
            } else {
                showError('Failed to load platform statistics.');
            }

            // 2. Fetch User Directory list
            const usersRes = await fetch(`${API_BASE_URL}/admin/users`, {
                method: 'GET',
                headers: getAuthHeader()
            });

            if (usersRes.ok) {
                const usersData = await usersRes.json();
                renderUsers(usersData);
            } else {
                showError('Failed to load user directory.');
            }

            // 3. Fetch Global Idea Submissions list
            const ideasRes = await fetch(`${API_BASE_URL}/admin/ideas`, {
                method: 'GET',
                headers: getAuthHeader()
            });

            if (ideasRes.ok) {
                const ideasData = await ideasRes.json();
                renderGlobalIdeas(ideasData);
            } else {
                showError('Failed to load global startup submissions.');
            }

        } catch (error) {
            console.error('Admin API error:', error);
            showError('Unable to connect to administration API endpoints. Verify service status.');
        }
    }

    function renderUsers(users) {
        usersTableBody.innerHTML = '';
        if (users.length === 0) {
            usersTableBody.innerHTML = `<tr><td colspan="6" style="text-align: center; color: var(--text-muted);">No users registered on the platform.</td></tr>`;
            return;
        }

        users.forEach(user => {
            const tr = document.createElement('tr');
            
            const date = new Date(user.createdAt).toLocaleDateString('en-US', {
                year: 'numeric',
                month: 'short',
                day: 'numeric'
            });

            const roleBadge = user.role === 'ADMIN'
                ? `<span class="badge badge-admin"><i class="fa-solid fa-crown"></i> Admin</span>`
                : `<span class="badge badge-user"><i class="fa-solid fa-user"></i> User</span>`;

            tr.innerHTML = `
                <td>${user.id}</td>
                <td style="font-weight: 600; color: var(--text-primary);">${escapeHtml(user.fullName)}</td>
                <td>${escapeHtml(user.username)}</td>
                <td>${escapeHtml(user.email)}</td>
                <td>${roleBadge}</td>
                <td>${date}</td>
            `;
            usersTableBody.appendChild(tr);
        });
    }

    function renderGlobalIdeas(ideas) {
        ideasTableBody.innerHTML = '';
        if (ideas.length === 0) {
            ideasTableBody.innerHTML = `<tr><td colspan="6" style="text-align: center; color: var(--text-muted);">No startup ideas submitted globally.</td></tr>`;
            return;
        }

        ideas.forEach(idea => {
            const tr = document.createElement('tr');

            const statusBadge = idea.analyzed 
                ? `<span class="badge badge-analyzed"><i class="fa-solid fa-circle-check"></i> Analyzed</span>`
                : `<span class="badge badge-pending"><i class="fa-solid fa-circle-pause"></i> Pending</span>`;

            const viewLink = idea.analyzed
                ? `<a href="analysis.html?id=${idea.id}" class="btn-action-sm btn-view-analysis" style="padding: 0.4rem 0.75rem; font-size: 0.75rem; text-decoration: none;">View</a>`
                : `<span style="font-size: 0.75rem; color: var(--text-muted);">Unanalyzed</span>`;

            tr.innerHTML = `
                <td>${idea.id}</td>
                <td style="font-weight: 600; color: var(--text-primary);">${escapeHtml(idea.title)}</td>
                <td>${escapeHtml(idea.industry)}</td>
                <td>${escapeHtml(idea.stage)}</td>
                <td>${statusBadge}</td>
                <td>
                    <div style="display: flex; gap: 0.5rem; align-items: center;">
                        ${viewLink}
                        <button onclick="adminDeleteIdea(${idea.id})" class="btn-action-sm btn-delete" style="padding: 0.4rem; font-size: 0.75rem;" title="Moderate Submission (Delete)">
                            <i class="fa-solid fa-trash-can"></i>
                        </button>
                    </div>
                </td>
            `;
            ideasTableBody.appendChild(tr);
        });
    }

    window.adminDeleteIdea = async (ideaId) => {
        if (!confirm('ADMIN MODERATION ACTION:\nAre you sure you want to permanently delete this startup idea from the platform? This cannot be undone.')) {
            return;
        }

        hideAlerts();

        try {
            const response = await fetch(`${API_BASE_URL}/admin/ideas/${ideaId}`, {
                method: 'DELETE',
                headers: getAuthHeader()
            });

            if (response.ok) {
                showSuccess('Startup idea deleted successfully by Moderator.');
                loadAdminDashboard(); // Reload statistics and list tables
            } else {
                const data = await response.json();
                showError(data.message || 'Moderation deletion failed.');
            }
        } catch (error) {
            console.error('Admin delete request error:', error);
            showError('Network error. Unable to perform moderation deletion.');
        }
    };

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
