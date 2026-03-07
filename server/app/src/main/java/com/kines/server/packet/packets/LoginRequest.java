package com.kines.server.packet.packets;

import com.google.gson.JsonObject;
import com.kines.server.packet.Packet;

public class LoginRequest extends Packet {

    private String id;
    private String email;
    private String password;

    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
        id = "login_request";
    }

    public LoginRequest() {}

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void read(JsonObject obj) {
        email = obj.get("email").getAsString();
        password = obj.get("password").getAsString();
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
