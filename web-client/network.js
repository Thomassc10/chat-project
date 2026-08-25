import { handleIncomingPacket } from './packetHandlers.js';
import { showLoginScreen, showAuthError, hideAuthScreen } from './auth.js';

export let socket;

export async function performLogin(username, password) {
    try {
        const response = await fetch('https://kines-server.duckdns.org/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({username: username, password: password})
        });

        if (!response.ok) {
            document.getElementById('submitLoginBtn').disabled = false;
            return;
        }

        const data = await response.json();
        if (data.id != null && !data.success) {
            console.log(data.reason);
            showAuthError(data.reason);
            document.getElementById('submitLoginBtn').disabled = false;
            return;
        }

        const token = data.token;

        localStorage.setItem("chat-token", token);
        connectToWebsocket(token, username);
    } catch (error) {
        console.error("Network error during login: ", error);
    }
}

export async function performRegister(email, username, password) {
    try {
        const response = await fetch('https://kines-server.duckdns.org/register', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({email: email, username: username, password: password})
        });

        if (!response.ok) {
            document.getElementById('submitRegBtn').disabled = false;
            showAuthError(response.json.reason);
            return;
        }

        // ??
        localStorage.removeItem("chat-token");
        /*if (socket) {
            socket.close();
            socket = undefined;
        }*/
        showLoginScreen();
    } catch (error) {
        console.error("Network error during login: ", error);
    }
}

function connectToWebsocket(token, username) {
    socket = new WebSocket('wss://kines-server.duckdns.org/chat?token=' + token);
    socket.onopen = () => console.log("Connected to server.");
    hideAuthScreen(username);
    socket.onmessage = (event) => {
    try {
        const packet = JSON.parse(event.data);
        
        if (!packet.id) return console.error("Packet missing id:", packet);
        
        handleIncomingPacket(packet);
    } catch (error) {
        console.error("Failed to parse JSON:", event.data);
    }
    };
}

export function sendPacket(packetObject) {
    if (socket && socket.readyState === WebSocket.OPEN) {
        socket.send(JSON.stringify(packetObject));
    } else {
        console.error("Cannot send packet, WebSocket is not open.");
    }
}