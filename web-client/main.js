// main.js
import { sendPacket, socket } from './network.js';

// --- State ---
export const state = {
    username: null,
    chatHistory: new Map(),
    selectedChat: null,
    isDarkMode: true
};

// --- DOM Elements ---
const loginOverlay = document.getElementById('loginOverlay');
const usernameInput = document.getElementById('usernameInput');
const loginBtn = document.getElementById('loginBtn');
const loginError = document.getElementById('loginError');
const darkModeBtn = document.getElementById('darkModeBtn');
const addContactBtn = document.getElementById('addContactBtn');
const chatList = document.getElementById('chatList');
const chatHeader = document.getElementById('chatHeader');
const contactName = document.getElementById('contactName');
const editContactBtn = document.getElementById('editContactBtn');
const messagesDisplay = document.getElementById('messagesDisplay');
const inputBar = document.getElementById('inputBar');
const messageInput = document.getElementById('messageInput');
const sendBtn = document.getElementById('sendBtn');

function init() {
    darkModeBtn.textContent = "LightMode";
    setupEventListeners();
    renderChatList();
}

// --- Event Listeners ---
loginBtn.addEventListener('click', () => {
    const name = usernameInput.value.trim();
    if (name) {
        // Create our LoginPacket and send it via the network module
        sendPacket({
            id: "login_request",
            username: name
        });
    }
});

function sendMessage() {
    const text = messageInput.value.trim();
    if (text !== "" && state.selectedChat) {

        const packet = {
            id: "message_packet",
            sender: state.username,
            receiver: state.selectedChat,
            content: text
        }
        socket.send(JSON.stringify(packet));

        state.chatHistory.get(state.selectedChat).push(`You: ${text}`);
        messageInput.value = "";
        renderMessages();
    }
}

function setupEventListeners() {
    darkModeBtn.addEventListener('click', toggleDarkMode);
    addContactBtn.addEventListener('click', addNewContact);
    //editContactBtn.addEventListener('click', editContact);
    sendBtn.addEventListener('click', sendMessage);
    
    messageInput.addEventListener('keypress', (e) => {
        if (e.key === 'Enter') sendMessage();
    });
}

// --- UI Export Functions (used by packetHandlers.js) ---
export function hideLoginScreen() {
    loginOverlay.style.display = 'none';
    state.username = usernameInput.value.trim();
}

export function showLoginError(reason) {
    loginError.textContent = reason;
    loginError.style.display = 'block';
}

export function appendMessage(sender, content) {
    if (state.selectedChat) {
        state.chatHistory.get(state.selectedChat).push(`${sender}: ${content}`);
        // call your renderMessages() function here
        renderMessages();
    }
}

function toggleDarkMode() {
    state.isDarkMode = !state.isDarkMode;
    document.body.classList.toggle('dark-mode', state.isDarkMode);
    darkModeBtn.textContent = state.isDarkMode ? "Light Mode" : "Dark Mode";
}

function addChat(chatName) {
    if (!state.chatHistory.has(chatName)) {
        state.chatHistory.set(chatName, []);
    }
}

function addNewContact() {
    const contactName = prompt("Enter contact name:");
    if (contactName && contactName.trim() !== "") {
        addChat(contactName.trim());
        renderChatList();
    }
}

function selectChat(chatName) {
    state.selectedChat = chatName;
    contactName.textContent = chatName;
    
    // Toggle UI visibility
    noChatSelected.style.display = 'none';
    chatHeader.style.display = 'flex';
    messagesDisplay.style.display = 'flex';
    inputBar.style.display = 'flex';
    
    renderChatList();
    renderMessages();
}

function renderChatList() {
    chatList.innerHTML = '';
    for (const chatName of state.chatHistory.keys()) {
        const li = document.createElement('li');
        li.textContent = chatName;
        if (chatName === state.selectedChat) {
            li.classList.add('active');
        }
        li.addEventListener('click', () => selectChat(chatName));
        chatList.appendChild(li);
    }
}

function renderMessages() {
    messagesDisplay.innerHTML = '';
    const messages = state.chatHistory.get(state.selectedChat) || [];
    
    messages.forEach(msg => {
        const div = document.createElement('div');
        div.className = 'message';
        div.textContent = msg;
        messagesDisplay.appendChild(div);
    });
    
    // Auto-scroll to bottom
    messagesDisplay.scrollTop = messagesDisplay.scrollHeight;
}

init();