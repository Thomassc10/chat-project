package com.kines.server;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.kines.server.http.HttpLoginRequest;
import com.kines.server.http.HttpRegisterRequest;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

public class HttpRequests {

    public void startHttpServer() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        server.createContext("/login", exchange -> {
            addCorsHeaders(exchange);
            HttpLoginRequest.loginRequest(exchange);            
        });

        server.createContext("/register", exchange -> {
            addCorsHeaders(exchange);
            HttpRegisterRequest.registerRequest(exchange);
        });

        server.setExecutor(null);
        server.start();
        System.out.println("HTTP Login Server started on port 8080.");
    }

    public void addCorsHeaders(HttpExchange exchange) {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "https://kineschat.xyz");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "POST, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization");
    }
}
