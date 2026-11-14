// CLIENT-SIDE REGISTRATION MANAGER
document.addEventListener('DOMContentLoaded', () => {
    // 1. If user is already logged in, redirect them to home
    if (isAuthenticated()) {
        window.location.href = 'index.html';
        return;
    }

    const registerForm = document.getElementById('register-form');
    const alertError = document.getElementById('alert-error');
    const alertSuccess = document.getElementById('alert-success');

    registerForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        
        // Hide existing alerts
        alertError.style.display = 'none';
        alertSuccess.style.display = 'none';

        // Extract input values
        const fullName = document.getElementById('fullName').value.trim();
        const username = document.getElementById('username').value.trim();
        const email = document.getElementById('email').value.trim();
        const password = document.getElementById('password').value;

        // Basic Client-side password validation
        if (password.length < 6) {
            showError('Password must be at least 6 characters long');
            return;
        }

        const payload = {
            fullName,
            username,
            email,
            password
        };

        try {
            const response = await fetch(`${API_BASE_URL}/auth/register`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(payload)
            });

            const data = await response.json();

            if (response.ok) {
                // Success: Persist JWT token and metadata
                saveToken(data.token, data.username, data.email, data.role);
                
                showSuccess('Registration successful! Redirecting to homepage...');
                
                // Disable input controls during redirect transition
                registerForm.querySelectorAll('input, button').forEach(el => el.disabled = true);
                
                setTimeout(() => {
                    window.location.href = 'index.html';
                }, 1500);
            } else {
                // Failure: Parse structured exception response
                if (data.errors) {
                    // Extract validation field-level failures
                    const validationMessages = Object.values(data.errors).join('<br>');
                    showError(validationMessages);
                } else {
                    showError(data.message || 'Registration failed. Please check your credentials.');
                }
            }
        } catch (error) {
            console.error('Registration API Error:', error);
            showError('Network error or server is down. Please verify Spring Boot is running.');
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
