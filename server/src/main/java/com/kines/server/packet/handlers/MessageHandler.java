package com.kines.server.packet.handlers;

import org.java_websocket.WebSocket;
import org.java_websocket.exceptions.WebsocketNotConnectedException;

import com.kines.server.Server;
import com.kines.server.packet.PacketHandler;
import com.kines.server.packet.packets.MessagePacket;
import com.kines.server.utils.ClientUtils;

public class MessageHandler implements PacketHandler<MessagePacket> {

    @Override
    public void handle(MessagePacket packet, WebSocket conn) {
        if (!packet.getSender().equalsIgnoreCase(conn.getAttachment().toString())) {
            System.out.println("Sender and Attachment are not the same " + conn.getRemoteSocketAddress());
            return;
        }
        
        WebSocket receiver = Server.connectedUsers.get(packet.getReceiver().toLowerCase());

        if (receiver == null) {
            System.out.println("Couldn't find receiver's socket: " + packet.getReceiver());
            return;
        }

        try {
            receiver.sendPing();
        } catch (WebsocketNotConnectedException e) {
            System.out.println("Websocket not connected.");
            return;
        }
        
        ClientUtils.sendPacket(receiver, new MessagePacket(packet.getContent(), conn.getAttachment().toString(), packet.getReceiver()));
    }
}
