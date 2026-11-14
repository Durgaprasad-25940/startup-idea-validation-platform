// CLIENT-SIDE LOGIN MANAGER
document.addEventListener('DOMContentLoaded', () => {
    // 1. If user is already logged in, redirect them to home
    if (isAuthenticated()) {
        window.location.href = 'index.html';
        return;
    }

    const loginForm = document.getElementById('login-form');
    const alertError = document.getElementById('alert-error');
    const alertSuccess = document.getElementById('alert-success');

    loginForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        
        // Hide existing alerts
        alertError.style.display = 'none';
        alertSuccess.style.display = 'none';

        // Extract input values
        const usernameOrEmail = document.getElementById('usernameOrEmail').value.trim();
        const password = document.getElementById('password').value;

        const payload = {
            usernameOrEmail,
            password
        };

        try {
            const response = await fetch(`${API_BASE_URL}/auth/login`, {
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
                
                showSuccess('Login successful! Welcome back.');
                
                // Disable input controls during redirect transition
                loginForm.querySelectorAll('input, button').forEach(el => el.disabled = true);
                
                setTimeout(() => {
                    window.location.href = 'index.html';
                }, 1500);
            } else {
                // Failure: Parse structured exception response
                showError(data.message || 'Login failed. Invalid username/email or password.');
            }
        } catch (error) {
            console.error('Login API Error:', error);
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
