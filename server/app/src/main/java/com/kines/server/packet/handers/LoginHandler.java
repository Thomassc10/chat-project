package com.kines.server.packet.handers;

import org.java_websocket.WebSocket;

import com.kines.server.Server;
import com.kines.server.packet.PacketHandler;
import com.kines.server.packet.packets.LoginRequest;
import com.kines.server.packet.packets.LoginResponse;

public class LoginHandler implements PacketHandler<LoginRequest> {

    @Override
    public void handle(LoginRequest packet, WebSocket conn) {
        
        Server.connectedUsers.put(conn, packet.getUsername());
        Server.clientHandler.sendPacket(conn, new LoginResponse(true, "Success"));
    }

}
