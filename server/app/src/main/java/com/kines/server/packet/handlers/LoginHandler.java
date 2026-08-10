package com.kines.server.packet.handlers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    private Pattern emailRegex = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    @Override
    public void handle(LoginRequest packet, WebSocket conn) {
        String email = packet.getEmail();
        String password = packet.getPassword();

        User user = null;

        Matcher matcher = emailRegex.matcher(email);
        if (matcher.matches()) {
            user = SQLUtils.getUserByEmail(email);
        } else {
            user = SQLUtils.getUserByUsername(email);
        }
        
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
        
        Server.connectedUsers.put(user.getName().toLowerCase(), conn);
        ClientUtils.sendPacket(conn, new LoginResponse(true, "Success", user.getName()));
        System.out.println("Client logged in successfully: " + email);
    }
}
