import { sendPacket } from './network.js';
import { state } from './main.js';

const authOverlay = document.getElementById('authOverlay');
const authMenuView = document.getElementById('authMenuView');
const loginView = document.getElementById('loginView');
const registerView = document.getElementById('registerView');

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

    document.getElementById('submitLoginBtn').addEventListener('click', handleLoginSubmit);
    document.getElementById('submitRegBtn').addEventListener('click', handleRegisterSubmit);
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
    const username = document.getElementById('loginUsername').value.trim();
    const password = document.getElementById('loginPassword').value;
    const errorEl = document.getElementById('loginError');

    if (!username || !password) {
        errorEl.textContent = "Please fill in all fields.";
        errorEl.style.display = 'block';
        return;
    }

    sendPacket({ id: "login_request", username, password });
}

function handleRegisterSubmit() {
    const username = document.getElementById('regUsername').value.trim();
    const password = document.getElementById('regPassword').value;
    const confirm = document.getElementById('regPasswordConfirm').value;
    const errorEl = document.getElementById('regError');

    if (!username || !password || !confirm) {
        errorEl.textContent = "Please fill in all fields.";
        errorEl.style.display = 'block';
        return;
    }

    if (password !== confirm) {
        errorEl.textContent = "Passwords do not match.";
        errorEl.style.display = 'block';
        return;
    }

    sendPacket({ id: "register_request", username, password });
}

export function hideAuthScreen(username) {
    authOverlay.style.display = 'none';
    state.username = username;
}

export function showAuthError(reason) {
    if (loginView.style.display === 'flex') {
        const err = document.getElementById('loginError');
        err.textContent = "Username and/or password incorrect"; 
        err.style.display = 'block';
    } else if (registerView.style.display === 'flex') {
        const err = document.getElementById('regError');
        err.textContent = reason; 
        err.style.display = 'block';
    }
}