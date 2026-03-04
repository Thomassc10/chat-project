package com.kines.server.packet.handlers;

import org.java_websocket.WebSocket;

import com.kines.server.Server;
import com.kines.server.packet.PacketHandler;
import com.kines.server.packet.packets.RegisterRequest;
import com.kines.server.packet.packets.RegisterResponse;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class RegisterHandler implements PacketHandler<RegisterRequest> {

    @Override
    public void handle(RegisterRequest packet, WebSocket conn) {
        String username = packet.getUsername().trim();
        String password = packet.getPassword();

        if (Server.userInfo.containsKey(username)) {
            Server.clientHandler.sendPacket(conn, new RegisterResponse(false, "Username alredy taken.", null));
            return;
        }

        String passwordHash = BCrypt.withDefaults().hashToString(12, password.toCharArray());

        Server.userInfo.put(username, passwordHash);
        System.out.println("Registered new user: " + username);
        
        Server.connectedUsers.put(conn, packet.getUsername());
        Server.clientHandler.sendPacket(conn, new RegisterResponse(true, "Account created successfully.", username));
    }

}
