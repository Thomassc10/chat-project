import { sendPacket, socket } from './network.js';
import { initAuth } from './auth.js';

export const state = {
    username: null,
    chatHistory: new Map(),
    selectedChat: null,
    isDarkMode: true
};

const darkModeBtn = document.getElementById('darkModeBtn');
const addContactBtn = document.getElementById('addContactBtn');
const chatList = document.getElementById('chatList');
const chatHeader = document.getElementById('chatHeader');
const contactName = document.getElementById('contactName');
const messagesDisplay = document.getElementById('messagesDisplay');
const inputBar = document.getElementById('inputBar');
const messageInput = document.getElementById('messageInput');
const sendBtn = document.getElementById('sendBtn');

function init() {
    darkModeBtn.textContent = "LightMode";
    initAuth();
    setupEventListeners();
    renderChatList();
}

function sendMessage() {
    const text = messageInput.value.trim();
    if (text !== "" && state.selectedChat) {

        const packet = {
            id: "message_packet",
            sender: state.username,
            receiver: state.selectedChat,
            content: text
        }
        sendPacket(packet);

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

export function appendMessage(sender, content) {
    state.chatHistory.get(sender).push(`${sender}: ${content}`);
    renderMessages();
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
    
    messagesDisplay.scrollTop = messagesDisplay.scrollHeight;
}

init();