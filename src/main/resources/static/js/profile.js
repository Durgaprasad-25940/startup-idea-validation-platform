// CLIENT-SIDE PROFILE MANAGER
document.addEventListener('DOMContentLoaded', () => {
    // 1. Enforce user authentication. If not logged in, redirect to login.html
    checkAuthAndRedirect();

    const alertError = document.getElementById('alert-error');
    const alertSuccess = document.getElementById('alert-success');
    const profileUpdateForm = document.getElementById('profile-update-form');

    // UI elements to update
    const usernameEl = document.getElementById('profile-username');
    const emailEl = document.getElementById('profile-email');
    const roleEl = document.getElementById('profile-role');
    const createdEl = document.getElementById('profile-created');
    const fullNameInput = document.getElementById('update-fullName');
    const passwordInput = document.getElementById('update-password');

    // Fetch and display user profile details on load
    fetchProfile();

    async function fetchProfile() {
        try {
            const response = await fetch(`${API_BASE_URL}/users/profile`, {
                method: 'GET',
                headers: getAuthHeader()
            });

            if (response.ok) {
                const data = await response.json();
                
                // Populate profile metadata
                usernameEl.textContent = data.username;
                emailEl.textContent = data.email;
                roleEl.textContent = data.role;
                
                // Format the created timestamp beautifully
                if (data.createdAt) {
                    const date = new Date(data.createdAt);
                    createdEl.textContent = date.toLocaleDateString('en-US', {
                        year: 'numeric',
                        month: 'long',
                        day: 'numeric'
                    });
                }
                
                // Pre-fill form details
                fullNameInput.value = data.fullName;
            } else {
                if (response.status === 401 || response.status === 403) {
                    // Token expired or invalid, force logout
                    logout();
                } else {
                    showError('Failed to fetch user profile details.');
                }
            }
        } catch (error) {
            console.error('Fetch Profile Error:', error);
            showError('Unable to connect to backend server. Is it running?');
        }
    }

    // Submit profile updates
    profileUpdateForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        
        alertError.style.display = 'none';
        alertSuccess.style.display = 'none';

        const fullName = fullNameInput.value.trim();
        const password = passwordInput.value;

        const payload = {
            fullName
        };

        // Add password to payload only if user is setting a new password
        if (password.trim() !== '') {
            if (password.length < 6) {
                showError('New password must be at least 6 characters long.');
                return;
            }
            payload.password = password;
        }

        try {
            const response = await fetch(`${API_BASE_URL}/users/profile`, {
                method: 'PUT',
                headers: getAuthHeader(),
                body: JSON.stringify(payload)
            });

            const data = await response.json();

            if (response.ok) {
                showSuccess('Profile updated successfully!');
                
                // Re-fetch profile to display updated values
                fetchProfile();
                
                // Clear password input field
                passwordInput.value = '';
            } else {
                if (data.errors) {
                    const validationMessages = Object.values(data.errors).join('<br>');
                    showError(validationMessages);
                } else {
                    showError(data.message || 'Failed to update profile info.');
                }
            }
        } catch (error) {
            console.error('Update Profile Error:', error);
            showError('Network error. Unable to save profile changes.');
        }
    });

    function showError(message) {
        alertError.innerHTML = `<i class="fa-solid fa-triangle-exclamation"></i> ${message}`;
        alertError.style.display = 'block';
    }

    function showSuccess(message) {
        alertSuccess.innerHTML = `<i class="fa-solid fa-circle-check"></i> ${message}`;
        alertSuccess.style.display = 'block';
    }
});
