package com.kines.server.packet.packets;

import com.google.gson.JsonObject;
import com.kines.server.packet.Packet;

public class LoginResponse extends Packet {

    private String id;
    private boolean success;
    private String reason;

    public LoginResponse(boolean success, String reason) {
        this.reason = reason;
        this.success = success;
        id = "login_response";
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void read(JsonObject obj) {
        
    }

    public String getReason() {
        return reason;
    }

    public boolean isSuccess() {
        return success;
    }

}
