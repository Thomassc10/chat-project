const socket = new WebSocket("ws://localhost:3407");

socket.onopen = function(event) {
    console.log("Connected to the Java Server!");
};

socket.onmessage = function(event) {
    console.log("Server says:", event.data);

    if (state.selectedChat) {
        state.chatHistory.get(state.selectedChat).push(event.data);
        renderMessages();
    }
};

socket.onerror = function(error) {
    console.error("WebSocket Error:", error);
}

// App State
const state = {
    chatHistory: new Map(), // Maps chatName to an array of message strings
    selectedChat: null,
    isDarkMode: true
};

// DOM Elements
const elements = {
    darkModeBtn: document.getElementById('darkModeBtn'),
    addContactBtn: document.getElementById('addContactBtn'),
    chatList: document.getElementById('chatList'),
    chatHeader: document.getElementById('chatHeader'),
    contactName: document.getElementById('contactName'),
    editContactBtn: document.getElementById('editContactBtn'),
    messagesDisplay: document.getElementById('messagesDisplay'),
    inputBar: document.getElementById('inputBar'),
    messageInput: document.getElementById('messageInput'),
    sendBtn: document.getElementById('sendBtn'),
    fileBtn: document.getElementById('fileBtn'),
    noChatSelected: document.getElementById('noChatSelected')
};

// Initialization
function init() {
    elements.darkModeBtn.textContent = "Light Mode";
    addChat("Thomas");
    renderChatList();
    setupEventListeners();
}

function setupEventListeners() {
    elements.darkModeBtn.addEventListener('click', toggleDarkMode);
    elements.addContactBtn.addEventListener('click', addNewContact);
    elements.editContactBtn.addEventListener('click', editContact);
    elements.sendBtn.addEventListener('click', sendMessage);
    elements.fileBtn.addEventListener('click', selectFile);
    
    // Send on Enter key
    elements.messageInput.addEventListener('keypress', (e) => {
        if (e.key === 'Enter') sendMessage();
    });
}

// Actions
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

function editContact() {
    if (state.selectedChat) {
        const newName = prompt("Edit contact name:", state.selectedChat);
        if (newName && newName.trim() !== "" && newName !== state.selectedChat) {
            const trimmedName = newName.trim();
            // Migrate history to new name
            const history = state.chatHistory.get(state.selectedChat);
            state.chatHistory.set(trimmedName, history);
            state.chatHistory.delete(state.selectedChat);
            
            state.selectedChat = trimmedName;
            elements.contactName.textContent = trimmedName;
            renderChatList();
        }
    }
}

function selectChat(chatName) {
    state.selectedChat = chatName;
    elements.contactName.textContent = chatName;
    
    // Toggle UI visibility
    elements.noChatSelected.style.display = 'none';
    elements.chatHeader.style.display = 'flex';
    elements.messagesDisplay.style.display = 'flex';
    elements.inputBar.style.display = 'flex';
    
    renderChatList();
    renderMessages();
}

function sendMessage() {
    const text = elements.messageInput.value.trim();
    if (text !== "" && state.selectedChat) {

        const packet = {
            id: "message_packet",
            sender: "Me",
            receiver: "Other",
            content: text
        }
        socket.send(JSON.stringify(packet));

        state.chatHistory.get(state.selectedChat).push(`You: ${text}`);
        elements.messageInput.value = "";
        renderMessages();
    }
}

function selectFile() {
    // In a real web app, you'd use <input type="file">. Using prompt to mock Swing's JFileChooser behavior.
    const fileName = prompt("Simulating JFileChooser. Enter a file name to send:");
    if (fileName && state.selectedChat) {
        state.chatHistory.get(state.selectedChat).push(`You: [File: ${fileName}]`);
        renderMessages();
    }
}

function toggleDarkMode() {
    state.isDarkMode = !state.isDarkMode;
    document.body.classList.toggle('dark-mode', state.isDarkMode);
    elements.darkModeBtn.textContent = state.isDarkMode ? "Light Mode" : "Dark Mode";
}

// Rendering
function renderChatList() {
    elements.chatList.innerHTML = '';
    for (const chatName of state.chatHistory.keys()) {
        const li = document.createElement('li');
        li.textContent = chatName;
        if (chatName === state.selectedChat) {
            li.classList.add('active');
        }
        li.addEventListener('click', () => selectChat(chatName));
        elements.chatList.appendChild(li);
    }
}

function renderMessages() {
    elements.messagesDisplay.innerHTML = '';
    const messages = state.chatHistory.get(state.selectedChat) || [];
    
    messages.forEach(msg => {
        const div = document.createElement('div');
        div.className = 'message';
        div.textContent = msg;
        elements.messagesDisplay.appendChild(div);
    });
    
    // Auto-scroll to bottom
    elements.messagesDisplay.scrollTop = elements.messagesDisplay.scrollHeight;
}

// Start app
init();