package com.kines.server.packet.handlers;

import org.java_websocket.WebSocket;

import com.kines.server.Server;
import com.kines.server.packet.PacketHandler;
import com.kines.server.packet.packets.RegisterRequest;
import com.kines.server.packet.packets.RegisterResponse;
import com.kines.server.utils.ClientUtils;
import com.kines.server.utils.SQLUtils;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class RegisterHandler implements PacketHandler<RegisterRequest> {

    @Override
    public void handle(RegisterRequest packet, WebSocket conn) {
        String email = packet.getEmail();
        String username = packet.getUsername();
        String password = packet.getPassword();

        if (SQLUtils.hasEmail(email)) {
            ClientUtils.sendPacket(conn, new RegisterResponse(false, "Email already being used.", null));
            return;
        }

        if (SQLUtils.hasUsername(username)) {
            ClientUtils.sendPacket(conn, new RegisterResponse(false, "Username already taken.", null));
            return;
        }

        String passwordHash = BCrypt.withDefaults().hashToString(12, password.toCharArray());

        SQLUtils.insertUser(email, username, passwordHash);
        System.out.println("Registered new user: " + email);
        
        Server.connectedUsers.put(packet.getUsername(), conn);
        ClientUtils.sendPacket(conn, new RegisterResponse(true, "Account created successfully.", username));
    }

}
