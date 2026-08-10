package com.kines.server.packet.handlers;

import org.java_websocket.WebSocket;

import com.kines.server.Server;
import com.kines.server.packet.PacketHandler;
import com.kines.server.packet.packets.MessagePacket;
import com.kines.server.utils.ClientUtils;

public class MessageHandler implements PacketHandler<MessagePacket> {

    @Override
    public void handle(MessagePacket packet, WebSocket conn) {
        WebSocket sender = Server.connectedUsers.get(packet.getSender());

        if (!sender.equals(conn)) {
            System.out.println("[WARNING] Packet received from sender does not match with actual sender. Packet's ip: " + conn.getRemoteSocketAddress() + "; Actual sender's ip: " + sender.getRemoteSocketAddress());
            return;
        }

        WebSocket receiver = Server.connectedUsers.get(packet.getReceiver().toLowerCase());

        if (receiver == null) {
            System.out.println("Couldn't find receiver's socket: " + packet.getReceiver());
            return;
        }

        ClientUtils.sendPacket(receiver, new MessagePacket(packet.getContent(), packet.getSender(), packet.getReceiver()));
    }
}
