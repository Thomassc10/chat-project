package com.kines.server.packet.handers;

import org.java_websocket.WebSocket;

import com.kines.server.Server;
import com.kines.server.packet.PacketHandler;
import com.kines.server.packet.packets.MessagePacket;

public class MessageHandler implements PacketHandler<MessagePacket> {

    @Override
    public void handle(MessagePacket packet, WebSocket conn) {
        if (Server.connectedUsers.containsValue(packet.getReceiver())) {
            // remove for loop
            for (WebSocket keySet : Server.connectedUsers.keySet()) {
                if (Server.connectedUsers.get(keySet).equals(packet.getReceiver()))
                    Server.clientHandler.sendPacket(keySet, new MessagePacket(packet.getContent(), packet.getSender(), packet.getReceiver()));
            }
        }
    }
}
