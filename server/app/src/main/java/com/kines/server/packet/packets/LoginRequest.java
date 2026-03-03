package com.kines.server.packet.packets;

import com.google.gson.JsonObject;
import com.kines.server.packet.Packet;

public class LoginRequest extends Packet {

    private String id;
    private String username;
    // password

    public LoginRequest(String username /* password */) {
        this.username = username;
        id = "login_request";
    }

    public LoginRequest() {}

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void read(JsonObject obj) {
        username = obj.get("username").getAsString();
    }

    public String getUsername() {
        return username;
    }
}
