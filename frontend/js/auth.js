// USER AUTHENTICATION UTILITY FUNCTIONS

/**
 * Persist authentication metadata inside the browser LocalStorage.
 */
function saveToken(token, username, email, role) {
    localStorage.setItem('jwtToken', token);
    localStorage.setItem('username', username);
    localStorage.setItem('email', email);
    localStorage.setItem('role', role);
}

/**
 * Remove credentials and logout the user, returning them to the login screen.
 */
function logout() {
    localStorage.removeItem('jwtToken');
    localStorage.removeItem('username');
    localStorage.removeItem('email');
    localStorage.removeItem('role');
    window.location.href = 'login.html';
}

/**
 * Checks whether a token is stored locally.
 */
function isAuthenticated() {
    return localStorage.getItem('jwtToken') !== null;
}

/**
 * Returns HTTP Request headers pre-populated with Bearer JWT details.
 */
function getAuthHeader() {
    const token = localStorage.getItem('jwtToken');
    if (token) {
        return {
            'Authorization': 'Bearer ' + token,
            'Content-Type': 'application/json'
        };
    }
    return {
        'Content-Type': 'application/json'
    };
}

/**
 * Reads user metadata from LocalStorage.
 */
function getCurrentUser() {
    if (!isAuthenticated()) return null;
    return {
        username: localStorage.getItem('username'),
        email: localStorage.getItem('email'),
        role: localStorage.getItem('role')
    };
}

/**
 * Guard method for protected screens. Redirects to login page if unauthenticated.
 */
function checkAuthAndRedirect() {
    if (!isAuthenticated()) {
        window.location.href = 'login.html';
    }
}
