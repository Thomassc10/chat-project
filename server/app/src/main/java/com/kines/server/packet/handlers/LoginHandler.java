package com.kines.server.packet.handlers;

import org.java_websocket.WebSocket;

import com.kines.server.Server;
import com.kines.server.packet.PacketHandler;
import com.kines.server.packet.packets.LoginRequest;
import com.kines.server.packet.packets.LoginResponse;
import com.kines.server.user.User;
import com.kines.server.utils.ClientUtils;
import com.kines.server.utils.SQLUtils;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class LoginHandler implements PacketHandler<LoginRequest> {

    @Override
    public void handle(LoginRequest packet, WebSocket conn) {
        String email = packet.getEmail();
        String password = packet.getPassword();

        User user = SQLUtils.getUserByEmail(email);
        
        if (user == null) {
            ClientUtils.sendPacket(conn, new LoginResponse(false, "Invalid username or password.", null));
            return;
        }

        String storedHash = user.getPassword();

        BCrypt.Result result = BCrypt.verifyer().verify(password.toCharArray(), storedHash);

        if (!result.verified) {
            ClientUtils.sendPacket(conn, new LoginResponse(false, "Invalid username or password.", null));
            return;
        }
        
        Server.connectedUsers.put(conn, user.getName());
        ClientUtils.sendPacket(conn, new LoginResponse(true, "Success", user.getName()));
        // TODO: send data from database to account (contacts, chat messages, etc)
        System.out.println("Client logged in successfully: " + email);
    }
}
