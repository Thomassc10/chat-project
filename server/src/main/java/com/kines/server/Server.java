package com.kines.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.java_websocket.WebSocket;
import org.java_websocket.drafts.Draft;
import org.java_websocket.exceptions.InvalidDataException;
import org.java_websocket.framing.CloseFrame;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.handshake.ServerHandshakeBuilder;
import org.java_websocket.server.WebSocketServer;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.kines.server.database.DatabaseManager;
import com.kines.server.packet.PacketRegistry;
import com.kines.server.utils.ClientUtils;
import com.kines.server.utils.TokenManager;
import com.zaxxer.hikari.HikariDataSource;

public class Server extends WebSocketServer {

    public static Gson gson = new Gson();
    public static TokenManager tokenManager;
    public static Map<String, WebSocket> connectedUsers = new ConcurrentHashMap<>();

    public Server(InetSocketAddress address) {
        super(address);
    }

    public static void main(String[] args) throws IOException {
        tokenManager = new TokenManager();
        DatabaseManager.initializePool();
        
        PacketRegistry registry = new PacketRegistry();
        registry.registerPackets();
        
        String host = "0.0.0.0";
        int port = 3407;
        String envPort = System.getenv("PORT");
        if (envPort != null && !envPort.isEmpty())
            port = Integer.parseInt(envPort);

        WebSocketServer server = new Server(new InetSocketAddress(host, port));
        server.start();
        
        HikariDataSource dataSource = DatabaseManager.getDataSource();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Received stop signal from Docker. Initiating graceful shutdown...");
            
            if (dataSource != null && !dataSource.isClosed()) {
                System.out.println("Closing HikariCP connection pool...");
                dataSource.close();
                System.out.println("Database pool closed safely.");
            }
            
            // maybe add code to stop server or send a warning to clients etc...
        }));

        HttpRequests requests = new HttpRequests();
        requests.startHttpServer();
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        System.out.println("Client connected: " + conn.getRemoteSocketAddress());
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        String username = conn.getAttachment();
        if (username != null) {
            connectedUsers.remove(username);
            System.out.println("Client disconnected: " + username);
        }
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        if (message.getBytes().length > 2048) {
            System.out.println("Packet too big! ._. " + message.getBytes().length);
            return;
        }
        JsonObject obj = null;
        try {
            obj = JsonParser.parseString(message).getAsJsonObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
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

    @Override
    public ServerHandshakeBuilder onWebsocketHandshakeReceivedAsServer(WebSocket conn, Draft draft, ClientHandshake request) throws InvalidDataException {
        String url = request.getResourceDescriptor();
        String token = tokenManager.extractToken(url);
        String userId = tokenManager.validateToken(token);
        
        if (userId == null) {
            System.out.println("Connection rejected: Invalid token. " + conn.getRemoteSocketAddress());
            throw new InvalidDataException(CloseFrame.POLICY_VALIDATION, "Unauthorized");
        }

        conn.setAttachment(userId);
        connectedUsers.put(userId, conn);

        return super.onWebsocketHandshakeReceivedAsServer(conn, draft, request);
    }
}
