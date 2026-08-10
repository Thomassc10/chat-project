package com.kines.server;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.kines.server.packet.PacketRegistry;
import com.kines.server.packet.handlers.LoginHandler;
import com.kines.server.packet.handlers.MessageHandler;
import com.kines.server.packet.handlers.RegisterHandler;
import com.kines.server.packet.packets.LoginRequest;
import com.kines.server.packet.packets.LoginResponse;
import com.kines.server.packet.packets.MessagePacket;
import com.kines.server.packet.packets.RegisterRequest;
import com.kines.server.packet.packets.RegisterResponse;
import com.kines.server.utils.ClientUtils;
import com.kines.server.utils.SQLUtils;

public class Server extends WebSocketServer {

    public static Gson gson = new Gson();

    public static Map<String, WebSocket> connectedUsers = new ConcurrentHashMap<>();

    public Server(InetSocketAddress address) {
        super(address);
    }

    public static void main(String[] args) {
        SQLUtils.createTable();
        
        // should probably move this somewhere else
        PacketRegistry registry = new PacketRegistry();
        registry.register("message_packet", new MessageHandler(), MessagePacket.class);
        registry.register("login_request", new LoginHandler(), LoginRequest.class);
        registry.register("login_response", null, LoginResponse.class);
        registry.register("register_request", new RegisterHandler(), RegisterRequest.class);
        registry.register("register_response", null, RegisterResponse.class);

        String host = "0.0.0.0";
        int port = 3407;
        String envPort = System.getenv("PORT");
        if (envPort != null && !envPort.isEmpty())
            port = Integer.parseInt(envPort);

        WebSocketServer server = new Server(new InetSocketAddress(host, port));
        server.start();
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        System.out.println("Client connected: " + conn.getRemoteSocketAddress());
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        System.out.println("Client disconnected: " + conn.getRemoteSocketAddress());
        // how to remove from map...
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        JsonObject obj = JsonParser.parseString(message).getAsJsonObject();
        
        if (!obj.has("id")) return;
        
        String id = obj.get("id").getAsString();
        ClientUtils.handlePacket(id, obj, conn);
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        System.err.println("An error occurred on connection " + (conn != null ? conn.getRemoteSocketAddress() : "null"));
        ex.printStackTrace();
    }

    @Override
    public void onStart() {
        System.out.println("WebSocket server started successfully on port: " + getPort());
    }
}
