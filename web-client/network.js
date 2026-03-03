// network.js
import { handleIncomingPacket } from './packetHandlers.js';

// Get URL from your config.js
export const socket = new WebSocket("ws://localhost:3407");

socket.onopen = () => console.log("Connected to server.");

socket.onmessage = (event) => {
    try {
        const packet = JSON.parse(event.data);
        
        if (!packet.id) return console.error("Packet missing id:", packet);
        
        // Pass the packet to our router
        handleIncomingPacket(packet);
    } catch (error) {
        console.error("Failed to parse JSON:", event.data);
    }
};

// A helper function so other files can easily send JSON
export function sendPacket(packetObject) {
    if (socket.readyState === WebSocket.OPEN) {
        socket.send(JSON.stringify(packetObject));
    } else {
        console.error("Cannot send packet, WebSocket is not open.");
    }
}