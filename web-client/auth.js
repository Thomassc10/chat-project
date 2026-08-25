import { state } from './main.js';
import { performLogin, performRegister } from './network.js';

const authOverlay = document.getElementById('authOverlay');
const authMenuView = document.getElementById('authMenuView');
const loginView = document.getElementById('loginView');
const registerView = document.getElementById('registerView');
const appContainer = document.querySelector('.app-container');
const submitLoginBtn = document.getElementById('submitLoginBtn');
const submitRegBtn = document.getElementById('submitRegBtn');

export function initAuth() {
    document.getElementById('showLoginBtn').addEventListener('click', () => switchAuthView(loginView));
    document.getElementById('showRegisterBtn').addEventListener('click', () => switchAuthView(registerView));

    document.getElementById('backToMenuFromLogin').addEventListener('click', () => switchAuthView(authMenuView));
    document.getElementById('backToMenuFromReg').addEventListener('click', () => switchAuthView(authMenuView));

    document.querySelectorAll('.toggle-password-btn').forEach(button => {
        button.addEventListener('click', function() {
            const input = this.previousElementSibling;
            if (input.type === 'password') {
                input.type = 'text';
                this.textContent = '🙈';
            } else {
                input.type = 'password';
                this.textContent = '👁️';
            }
        });
    });

    submitLoginBtn.addEventListener('click', handleLoginSubmit);
    submitRegBtn.addEventListener('click', handleRegisterSubmit);

    submitLoginBtn.addEventListener('keypress', e => {
        if (e.key === 'Enter') handleLoginSubmit();
    });
    submitRegBtn.addEventListener('keypress', e => {
        if (e.key === 'Enter') handleRegisterSubmit();
    });
}

function switchAuthView(viewToShow) {
    authMenuView.style.display = 'none';
    loginView.style.display = 'none';
    registerView.style.display = 'none';
    
    document.querySelectorAll('.error-text').forEach(el => el.style.display = 'none');
    document.querySelectorAll('.auth-box input').forEach(el => el.value = '');
    
    viewToShow.style.display = 'flex';
}

function handleLoginSubmit() {
    const email = document.getElementById('loginUsername').value.trim();
    const password = document.getElementById('loginPassword').value;
    const errorEl = document.getElementById('loginError');

    if (!email || !password) {
        errorEl.textContent = "Please fill in all fields.";
        errorEl.style.display = 'block';
        return;
    }

    submitLoginBtn.disabled = true;
    performLogin(email, password);
}

function handleRegisterSubmit() {
    const email = document.getElementById('regEmail').value.trim();
    const username = document.getElementById('regUsername').value.trim();
    const password = document.getElementById('regPassword').value;
    const confirm = document.getElementById('regPasswordConfirm').value;
    const errorEl = document.getElementById('regError');

    if (!email || !username || !password || !confirm) {
        errorEl.textContent = "Please fill in all fields.";
        errorEl.style.display = 'block';
        return;
    }

    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(email)) {
        errorEl.textContent = "Please enter a valid email address.";
        errorEl.style.display = 'block';
        return;
    }

    if (password !== confirm) {
        errorEl.textContent = "Passwords do not match.";
        errorEl.style.display = 'block';
        return;
    }

    submitRegBtn.disabled = true;
    performRegister(email, username, password);
}

export function hideAuthScreen(username) {
    authOverlay.style.display = 'none';
    appContainer.style.display = 'flex';
    state.username = username;
}

export function showLoginScreen() {
    appContainer.style.display = 'none';
    authOverlay.style.display = 'flex';
    switchAuthView(loginView);
    document.getElementById('loginUsername').focus();
}

export function showAuthError(reason) {
    if (loginView.style.display === 'flex') {
        const err = document.getElementById('loginError');
        err.textContent = reason; 
        err.style.display = 'block';
    } else if (registerView.style.display === 'flex') {
        const err = document.getElementById('regError');
        err.textContent = reason; 
        err.style.display = 'block';
    }
}