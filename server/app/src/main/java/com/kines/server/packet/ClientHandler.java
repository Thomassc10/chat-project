package com.kines.server.packet;

import org.java_websocket.WebSocket;

import com.google.gson.JsonObject;
import com.kines.server.Server;

public class ClientHandler {

    public void handlePacket(String id, JsonObject obj, WebSocket conn) {
        PacketRegistry registry = PacketRegistry.getInstance();
        Packet packet = registry.createPacketInstance(id);

        if (packet != null) {
            packet.read(obj);
            PacketHandler handler = registry.getHandler(id);
            handler.handle(packet, conn);
        }
    }

    public void sendPacket(WebSocket conn, Packet packet) {
        conn.send(Server.gson.toJson(packet));
    }
}
