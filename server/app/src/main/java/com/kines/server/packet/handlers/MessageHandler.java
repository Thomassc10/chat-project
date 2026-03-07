package com.kines.server.packet.handlers;

import org.java_websocket.WebSocket;

import com.kines.server.Server;
import com.kines.server.packet.PacketHandler;
import com.kines.server.packet.packets.MessagePacket;
import com.kines.server.utils.ClientUtils;

public class MessageHandler implements PacketHandler<MessagePacket> {

    @Override
    public void handle(MessagePacket packet, WebSocket conn) {
        if (Server.connectedUsers.containsValue(packet.getReceiver())) {
            // TODO: remove for loop (whole thing should be revamped tho)
            for (WebSocket keySet : Server.connectedUsers.keySet()) {
                if (Server.connectedUsers.get(keySet).equals(packet.getReceiver()))
                    ClientUtils.sendPacket(keySet, new MessagePacket(packet.getContent(), packet.getSender(), packet.getReceiver()));
            }
        }
    }
}
