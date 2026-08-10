import { appendMessage } from './main.js';
import { hideAuthScreen, showAuthError } from './auth.js';
import { renderChatList } from './main.js';

export function handleIncomingPacket(packet) {
    const handler = registry[packet.id];
    
    if (handler) {
        handler(packet);
    } else {
        console.warn("Unhandled packet type:", packet.id);
    }
}

const registry = {
    "message_packet": (packet) => {
        appendMessage(packet.sender, packet.content);
    },

    "login_response": (packet) => {
        if (packet.success) {
            hideAuthScreen(packet.username);
        } else {
            showAuthError(packet.reason);
        }
    },

    "register_response": (packet) => {
        if (packet.success) {
            hideAuthScreen(packet.username);
        } else {
            showAuthError(packet.reason);
        }
    }
};