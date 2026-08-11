package com.kines.server.packet.packets;

import com.google.gson.JsonObject;
import com.kines.server.packet.Packet;

public class RegisterRequest extends Packet {

    private String id;
    private String email;
    private String username;
    private String password;

    public RegisterRequest(String email, String username, String password) {
        this.email = email;
        this.password = password;
        this.username = username;
        id = "register_request";
    }

    public RegisterRequest() {}

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void read(JsonObject obj) {
        username = obj.get("username").getAsString();
        password = obj.get("password").getAsString();
        email = obj.get("email").getAsString();
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

}
