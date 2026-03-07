package com.kines.server.utils;

import org.java_websocket.WebSocket;

import com.google.gson.JsonObject;
import com.kines.server.Server;
import com.kines.server.packet.Packet;
import com.kines.server.packet.PacketHandler;
import com.kines.server.packet.PacketRegistry;

public class ClientUtils {

    public static void handlePacket(String id, JsonObject obj, WebSocket conn) {
        PacketRegistry registry = PacketRegistry.getInstance();
        Packet packet = registry.createPacketInstance(id);

        if (packet != null) {
            packet.read(obj);
            PacketHandler handler = registry.getHandler(id);
            handler.handle(packet, conn);
        }
    }

    public static void sendPacket(WebSocket conn, Packet packet) {
        conn.send(Server.gson.toJson(packet));
    }
}
