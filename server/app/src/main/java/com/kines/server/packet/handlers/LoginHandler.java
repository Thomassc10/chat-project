package com.kines.server.packet.handlers;

import org.java_websocket.WebSocket;

import com.kines.server.Server;
import com.kines.server.packet.PacketHandler;
import com.kines.server.packet.packets.LoginRequest;
import com.kines.server.packet.packets.LoginResponse;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class LoginHandler implements PacketHandler<LoginRequest> {

    @Override
    public void handle(LoginRequest packet, WebSocket conn) {
        String username = packet.getUsername().trim();
        String password = packet.getPassword();

        if (!Server.userInfo.containsKey(username)) {
            Server.clientHandler.sendPacket(conn, new LoginResponse(false, "Invalid username or password."));
            return;
        }

        String storedHash = Server.userInfo.get(username);

        BCrypt.Result result = BCrypt.verifyer().verify(password.toCharArray(), storedHash);

        if (!result.verified) {
            Server.clientHandler.sendPacket(conn, new LoginResponse(false, "Invalid username or password."));
            return;
        }
        
        Server.connectedUsers.put(conn, packet.getUsername());
        Server.clientHandler.sendPacket(conn, new LoginResponse(true, "Success"));
        // send data from account
        System.out.println("Client logged in successfully: " + username);
    }
}
