package com.kines.server.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.kines.server.Server;
import com.kines.server.packet.packets.RegisterResponse;
import com.kines.server.utils.SQLUtils;
import com.sun.net.httpserver.HttpExchange;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class HttpRegisterRequest {

    private static Map<String, Long> cooldown = new ConcurrentHashMap<>();

    public static void registerRequest(HttpExchange exchange) throws IOException {
        if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }
            
            if (exchange.getRequestMethod().equalsIgnoreCase("POST")) {
                OutputStream os = exchange.getResponseBody();
                RegisterResponse rr = new RegisterResponse(false, "Wait a moment before doing this again.", null);
                String registerCooldown = Server.gson.toJson(rr);

                if (cooldown.containsKey(exchange.getRemoteAddress().toString())) {
                    if (cooldown.get(exchange.getRemoteAddress().toString()) > System.currentTimeMillis()) {
                        exchange.sendResponseHeaders(200, registerCooldown.getBytes("UTF-8").length);
                        os.write(registerCooldown.getBytes());
                        os.close();
                        return;                    
                    }
                    cooldown.remove(exchange.getRemoteAddress().toString());
                }

                InputStream is = exchange.getRequestBody();
                String jsonBody = new String(is.readAllBytes(), StandardCharsets.UTF_8);

                JsonObject obj = JsonParser.parseString(jsonBody).getAsJsonObject();
                String email = obj.get("email").getAsString();
                String username = obj.get("username").getAsString();
                String password = obj.get("password").getAsString();


                if (SQLUtils.hasEmail(email)) {
                    RegisterResponse registerResponseEmail = new RegisterResponse(false, "E-mail already in use.", null);
                    String toJsonEmail = Server.gson.toJson(registerResponseEmail);
                    sendInvalidResponse(exchange, os, toJsonEmail);
                    return;
                }

                if (SQLUtils.hasUsername(username)) {
                    RegisterResponse registerResponseUsername = new RegisterResponse(false, "Username already taken.", null);
                    String toJsonUsername = Server.gson.toJson(registerResponseUsername);
                    sendInvalidResponse(exchange, os, toJsonUsername);
                    return;
                }

                String passwordHash = BCrypt.withDefaults().hashToString(12, password.toCharArray());

                SQLUtils.insertUser(email, username, passwordHash);

                String response = "{\"Success: true\"}";
                exchange.sendResponseHeaders(200, response.getBytes("UTF-8").length);
                os.write(response.getBytes());
                os.close();
            }
            exchange.sendResponseHeaders(401, -1);
    }

    private static void sendInvalidResponse(HttpExchange exchange, OutputStream os, String response) throws UnsupportedEncodingException, IOException {
        cooldown.put(exchange.getRemoteAddress().toString(), System.currentTimeMillis() + 3000);
        
        exchange.sendResponseHeaders(200, response.getBytes("UTF-8").length);
        os.write(response.getBytes());
        os.close();
    }
}
