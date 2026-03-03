// packetHandlers.js
import { hideLoginScreen, showLoginError, appendMessage } from './main.js';

export function handleIncomingPacket(packet) {
    const handler = registry[packet.id];
    
    if (handler) {
        handler(packet);
    } else {
        console.warn("Unhandled packet type:", packet.id);
    }
}

const registry = {
    /*
    "login_request": (packet) => {
        if (packet.success) {
            hideLoginScreen();
        } else {
            showLoginError(packet.reason);
        }
    },
    */
    
    "message_packet": (packet) => {
        appendMessage(packet.sender, packet.content);
    },

    "login_response": (packet) => {
        if (packet.success) {
            hideLoginScreen();
        } else {
            showLoginError(packet.reason);
        }
    }
};