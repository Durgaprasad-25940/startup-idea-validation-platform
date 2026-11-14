// CLIENT-SIDE STARTUP IDEA FORM MANAGER
document.addEventListener('DOMContentLoaded', () => {
    // 1. Guard route
    checkAuthAndRedirect();

    const currentUser = getCurrentUser();
    if (currentUser && currentUser.role === 'ADMIN') {
        const menuAdmin = document.getElementById('menu-admin');
        if (menuAdmin) menuAdmin.style.display = 'block';
    }

    // Parse URL query parameter
    const urlParams = new URLSearchParams(window.location.search);
    const editId = urlParams.get('edit');

    const form = document.getElementById('idea-form');
    const pageTitle = document.getElementById('page-title');
    const pageDesc = document.getElementById('page-desc');
    const btnSubmit = document.getElementById('btn-submit');
    const menuSubmitLink = document.getElementById('menu-submit-link');
    const alertError = document.getElementById('alert-error');
    const alertSuccess = document.getElementById('alert-success');

    // UI Input References
    const inputTitle = document.getElementById('idea-title');
    const inputIndustry = document.getElementById('idea-industry');
    const inputAudience = document.getElementById('idea-audience');
    const inputStage = document.getElementById('idea-stage');
    const inputDescription = document.getElementById('idea-description');

    // If editId is provided, fetch existing data and pre-fill form
    if (editId) {
        setupEditMode();
    }

    async function setupEditMode() {
        pageTitle.textContent = 'Edit Startup Idea';
        pageDesc.textContent = 'Modify details of your startup venture to update validations.';
        
        // Adjust active link visualization
        if (menuSubmitLink && menuSubmitLink.parentElement) {
            menuSubmitLink.parentElement.classList.remove('active');
        }
        const dashboardLink = document.querySelector('aside.sidebar .sidebar-menu li:nth-child(2)');
        if (dashboardLink) dashboardLink.classList.add('active');

        try {
            const response = await fetch(`${API_BASE_URL}/ideas/${editId}`, {
                method: 'GET',
                headers: getAuthHeader()
            });

            if (response.ok) {
                const data = await response.json();
                
                inputTitle.value = data.title || '';
                inputIndustry.value = data.industry || 'Tech';
                inputAudience.value = data.targetAudience || '';
                inputStage.value = data.stage || 'Ideation';
                inputDescription.value = data.description || '';
            } else if (response.status === 401 || response.status === 403) {
                showError('Session expired. Redirecting to login...');
                setTimeout(logout, 1500);
            } else {
                showError('Failed to fetch startup idea details for editing.');
                form.querySelectorAll('input, select, textarea, button').forEach(el => el.disabled = true);
            }
        } catch (error) {
            console.error('Fetch Edit Details Error:', error);
            showError('Unable to connect to backend server. Make sure Spring Boot is running.');
        }
    }

    form.addEventListener('submit', async (e) => {
        e.preventDefault();

        alertError.style.display = 'none';
        alertSuccess.style.display = 'none';

        const titleVal = inputTitle.value.trim();
        const descVal = inputDescription.value.trim();
        const industryVal = inputIndustry.value || 'General';
        const audienceVal = inputAudience.value.trim() || 'General Consumers';
        const stageVal = inputStage.value || 'Ideation';

        if (titleVal.length < 2) {
            showError('Startup Title must be at least 2 characters long.');
            inputTitle.focus();
            return;
        }

        if (descVal.length < 5) {
            showError('Business Description must be at least 5 characters long.');
            inputDescription.focus();
            return;
        }

        const payload = {
            title: titleVal,
            industry: industryVal,
            targetAudience: audienceVal,
            stage: stageVal,
            description: descVal
        };

        const targetUrl = editId 
            ? `${API_BASE_URL}/ideas/${editId}` 
            : `${API_BASE_URL}/ideas`;
            
        const method = editId ? 'PUT' : 'POST';

        btnSubmit.disabled = true;
        const originalContent = btnSubmit.innerHTML;
        btnSubmit.innerHTML = `<i class="fa-solid fa-spinner fa-spin"></i> Saving...`;

        try {
            const response = await fetch(targetUrl, {
                method: method,
                headers: getAuthHeader(),
                body: JSON.stringify(payload)
            });

            let data = null;
            try {
                data = await response.json();
            } catch (jsonErr) {
                // Non-JSON response
            }

            if (response.ok) {
                showSuccess(editId ? 'Startup idea updated successfully! Redirecting...' : 'Startup idea submitted successfully! Redirecting to dashboard...');
                
                // Disable form elements during redirect delay
                form.querySelectorAll('input, select, textarea, button').forEach(el => el.disabled = true);
                
                setTimeout(() => {
                    window.location.href = 'dashboard.html';
                }, 1200);
            } else {
                btnSubmit.disabled = false;
                btnSubmit.innerHTML = originalContent;

                if (response.status === 401 || response.status === 403) {
                    showError('Session expired or you are not logged in. Redirecting to login page...');
                    setTimeout(() => {
                        logout();
                    }, 1800);
                    return;
                }
                
                if (data && data.message) {
                    showError(data.message);
                } else if (data && data.errors) {
                    const validationMessages = Object.values(data.errors).join('<br>');
                    showError(validationMessages);
                } else {
                    showError(`Failed to save startup idea (Status: ${response.status}). Please check all required fields.`);
                }
            }
        } catch (error) {
            console.error('Save Idea API Error:', error);
            showError('Network error: Unable to connect to backend service. Please check your connection.');
            btnSubmit.disabled = false;
            btnSubmit.innerHTML = originalContent;
        }
    });

    function showError(message) {
        alertError.innerHTML = `<i class="fa-solid fa-triangle-exclamation"></i> ${message}`;
        alertError.style.display = 'block';
        alertError.scrollIntoView({ behavior: 'smooth', block: 'center' });
    }

    function showSuccess(message) {
        alertSuccess.innerHTML = `<i class="fa-solid fa-circle-check"></i> ${message}`;
        alertSuccess.style.display = 'block';
        alertSuccess.scrollIntoView({ behavior: 'smooth', block: 'center' });
    }
});
