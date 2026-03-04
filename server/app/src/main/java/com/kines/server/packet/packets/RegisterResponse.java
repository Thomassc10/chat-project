package com.kines.server.packet.packets;

import com.google.gson.JsonObject;
import com.kines.server.packet.Packet;

public class RegisterResponse extends Packet {

    private String id;
    private boolean success;
    private String reason;
    private String username;

    public RegisterResponse(boolean success, String reason, String username) {
        this.reason = reason;
        this.success = success;
        this.username = username;
        id = "register_response";
    }

    public RegisterResponse() {}

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void read(JsonObject obj) {
        
    }

}
