const API_BASE = '/api';
let currentIdeas = [];
let currentIdeaId = null;

// Auth Helper
const getAuthHeaders = () => {
    return {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer ' + localStorage.getItem('token')
    };
};

const showMessage = (elementId, msg, isError = false) => {
    const el = document.getElementById(elementId);
    if (!el) return;
    el.innerText = msg;
    el.className = 'message-box ' + (isError ? 'error' : 'success');
    el.classList.remove('hidden');
    setTimeout(() => {
        el.classList.add('hidden');
    }, 5000);
};

// Login
const handleLogin = async (e) => {
    e.preventDefault();
    const username = e.target.username.value;
    const password = e.target.password.value;

    try {
        const res = await fetch(`${API_BASE}/auth/signin`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, password })
        });
        
        const data = await res.json();
        
        if (res.ok) {
            localStorage.setItem('token', data.token);
            localStorage.setItem('username', data.username);
            window.location.href = 'dashboard.html';
        } else {
            showMessage('messageBox', 'Invalid credentials', true);
        }
    } catch (err) {
        showMessage('messageBox', 'Network error. Try again.', true);
    }
};

// Register
const handleRegister = async (e) => {
    e.preventDefault();
    const username = e.target.regUsername.value;
    const email = e.target.regEmail.value;
    const password = e.target.regPassword.value;

    try {
        const res = await fetch(`${API_BASE}/auth/signup`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, email, password })
        });
        
        const data = await res.json();
        
        if (res.ok) {
            showMessage('messageBox', 'Registration successful! Redirecting to login...', false);
            setTimeout(() => {
                window.location.href = 'index.html';
            }, 2000);
        } else {
            showMessage('messageBox', data.message || 'Registration failed', true);
        }
    } catch (err) {
        showMessage('messageBox', 'Network error. Try again.', true);
    }
};

// Logout
const logout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('username');
    window.location.href = 'index.html';
};

// UI Navigation
const showSection = (sectionId) => {
    document.querySelectorAll('.content-section').forEach(el => el.classList.add('hidden'));
    document.getElementById(sectionId).classList.remove('hidden');
    
    document.querySelectorAll('.nav-menu li').forEach(el => el.classList.remove('active'));
    event.currentTarget.classList.add('active');

    if (sectionId === 'my-ideas') {
        fetchIdeas();
    }
};

// Fetch Ideas
const fetchIdeas = async () => {
    try {
        const res = await fetch(`${API_BASE}/ideas`, {
            headers: getAuthHeaders()
        });
        
        if (res.ok) {
            currentIdeas = await res.json();
            renderIdeas(currentIdeas);
        } else if (res.status === 401) {
            logout();
        }
    } catch (err) {
        console.error(err);
    }
};

const renderIdeas = (ideas) => {
    const grid = document.getElementById('ideasGrid');
    if (!grid) return;
    
    if (ideas.length === 0) {
        grid.innerHTML = '<div class="loader">No ideas found. Start by submitting a new one!</div>';
        return;
    }

    grid.innerHTML = '';
    ideas.forEach(idea => {
        const card = document.createElement('div');
        card.className = 'glass-card idea-card';
        card.innerHTML = `
            <h3>${idea.title}</h3>
            <p>${idea.problemSolved}</p>
            <div class="badge score-badge">Score: ${idea.viabilityScore}/100</div>
        `;
        card.onclick = () => openIdeaModal(idea);
        grid.appendChild(card);
    });
};

// Submit Idea
const handleIdeaSubmit = async (e) => {
    e.preventDefault();
    const dto = {
        title: e.target.ideaTitle.value,
        problemSolved: e.target.ideaProblem.value,
        targetAudience: e.target.ideaAudience.value,
        description: e.target.ideaDesc.value
    };

    try {
        const res = await fetch(`${API_BASE}/ideas`, {
            method: 'POST',
            headers: getAuthHeaders(),
            body: JSON.stringify(dto)
        });
        
        if (res.ok) {
            showMessage('submitMessage', 'Idea submitted for validation successfully!', false);
            e.target.reset();
            setTimeout(() => {
                document.getElementById('submitMessage').classList.add('hidden');
                document.querySelector('.nav-menu li').click(); // trigger My Ideas tab
            }, 2000);
        } else {
            showMessage('submitMessage', 'Failed to submit idea', true);
        }
    } catch (err) {
        showMessage('submitMessage', 'Network error', true);
    }
};

// Modal Logic
const openIdeaModal = (idea) => {
    currentIdeaId = idea.id;
    document.getElementById('modalTitle').innerText = idea.title;
    document.getElementById('modalScore').innerText = `Viability Score: ${idea.viabilityScore}/100`;
    document.getElementById('modalProblem').innerText = idea.problemSolved;
    document.getElementById('modalAudience').innerText = idea.targetAudience;
    document.getElementById('modalDesc').innerText = idea.description;
    document.getElementById('modalFeedback').innerText = idea.aiFeedback;
    
    document.getElementById('ideaModal').classList.remove('hidden');
    toggleEditMode(false); // Ensure it opens in view mode
};

const toggleEditMode = (isEditing) => {
    if (isEditing) {
        document.getElementById('ideaViewMode').classList.add('hidden');
        document.getElementById('ideaEditMode').classList.remove('hidden');
        document.getElementById('modalActionButtons').classList.add('hidden');
        
        // Populate form
        document.getElementById('editIdeaTitle').value = document.getElementById('modalTitle').innerText;
        document.getElementById('editIdeaProblem').value = document.getElementById('modalProblem').innerText;
        document.getElementById('editIdeaAudience').value = document.getElementById('modalAudience').innerText;
        document.getElementById('editIdeaDesc').value = document.getElementById('modalDesc').innerText;
    } else {
        document.getElementById('ideaViewMode').classList.remove('hidden');
        document.getElementById('ideaEditMode').classList.add('hidden');
        document.getElementById('modalActionButtons').classList.remove('hidden');
    }
};

const handleIdeaUpdate = async (e) => {
    e.preventDefault();
    const dto = {
        title: document.getElementById('editIdeaTitle').value,
        problemSolved: document.getElementById('editIdeaProblem').value,
        targetAudience: document.getElementById('editIdeaAudience').value,
        description: document.getElementById('editIdeaDesc').value
    };

    try {
        const res = await fetch(`${API_BASE}/ideas/${currentIdeaId}`, {
            method: 'PUT',
            headers: getAuthHeaders(),
            body: JSON.stringify(dto)
        });
        
        if (res.ok) {
            closeModal();
            fetchIdeas();
        }
    } catch (err) {
        console.error(err);
    }
};

// Event listener for edit form
if (document.getElementById('editIdeaForm')) {
    document.getElementById('editIdeaForm').addEventListener('submit', handleIdeaUpdate);
}

if (document.getElementById('modalEditBtn')) {
    document.getElementById('modalEditBtn').addEventListener('click', () => toggleEditMode(true));
}

const closeModal = () => {
    document.getElementById('ideaModal').classList.add('hidden');
    currentIdeaId = null;
};

// Delete Idea
if(document.getElementById('modalDeleteBtn')) {
    document.getElementById('modalDeleteBtn').addEventListener('click', async () => {
        if (!currentIdeaId || !confirm("Are you sure you want to delete this idea?")) return;
        
        try {
            const res = await fetch(`${API_BASE}/ideas/${currentIdeaId}`, {
                method: 'DELETE',
                headers: getAuthHeaders()
            });
            
            if (res.ok) {
                closeModal();
                fetchIdeas();
            }
        } catch (err) {
            console.error(err);
        }
    });
}
