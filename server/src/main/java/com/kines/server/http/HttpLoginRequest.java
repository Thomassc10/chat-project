package com.kines.server.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.kines.server.Server;
import com.kines.server.packet.packets.LoginResponse;
import com.kines.server.user.User;
import com.kines.server.utils.SQLUtils;
import com.sun.net.httpserver.HttpExchange;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class HttpLoginRequest {

    private static Pattern emailRegex = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    private static Map<String, Long> cooldown = new ConcurrentHashMap<>();

    public static void loginRequest(HttpExchange exchange) throws IOException {
        if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        if (exchange.getRequestMethod().equalsIgnoreCase("POST")) {
            try {
                OutputStream os = exchange.getResponseBody();
                LoginResponse lr = new LoginResponse(false, "Wait a moment before doing this again.", null);
                String loginCooldown = Server.gson.toJson(lr);

                if (cooldown.containsKey(exchange.getRemoteAddress().toString())) {
                    if (cooldown.get(exchange.getRemoteAddress().toString()) > System.currentTimeMillis()) {
                        exchange.sendResponseHeaders(200, loginCooldown.getBytes("UTF-8").length);
                        os.write(loginCooldown.getBytes());
                        os.close();
                        return;                    
                    }
                    cooldown.remove(exchange.getRemoteAddress().toString());
                }
                
                InputStream is = exchange.getRequestBody();    
                String jsonBody = new String(is.readAllBytes(), StandardCharsets.UTF_8);

                JsonObject obj = JsonParser.parseString(jsonBody).getAsJsonObject();
                String username = obj.get("username").getAsString();
                String password = obj.get("password").getAsString();
                User user = null;

                Matcher matcher = emailRegex.matcher(username);
                if (matcher.matches()) {
                    user = SQLUtils.getUserByEmail(username);
                } else {
                    user = SQLUtils.getUserByUsername(username);
                }

                if (user == null) {
                    sendInvalidResponse(exchange, os);
                    return;
                }

                String storedHash = user.getPassword();

                BCrypt.Result result = BCrypt.verifyer().verify(password.toCharArray(), storedHash);

                if (!result.verified) {
                    sendInvalidResponse(exchange, os);
                    return;
                }

                String token = Server.tokenManager.createToken(username);
                String response = "{\"token\": \"" + token + "\"}";
                exchange.sendResponseHeaders(200, response.getBytes("UTF-8").length);
                    
                os.write(response.getBytes());
                os.close();
                return;
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                exchange.sendResponseHeaders(500, -1);
                exchange.close();
        } catch (Exception ignore) {}
            }
        exchange.sendResponseHeaders(401, -1);
    }

    private static void sendInvalidResponse(HttpExchange exchange, OutputStream os) throws UnsupportedEncodingException, IOException {
        LoginResponse loginResponse = new LoginResponse(false, "Invalid username or password.", null);
        String wrongInfo = Server.gson.toJson(loginResponse);
        
        cooldown.put(exchange.getRemoteAddress().toString(), System.currentTimeMillis() + 3000);
        exchange.sendResponseHeaders(200, wrongInfo.getBytes("UTF-8").length);
        os.write(wrongInfo.getBytes());
        os.close();
    }
}
