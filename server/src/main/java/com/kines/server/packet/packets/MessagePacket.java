package com.kines.server.packet.packets;

import com.google.gson.JsonObject;
import com.kines.server.packet.Packet;

public class MessagePacket extends Packet {

    private String id;
    private String sender;
    private String receiver;
    private String content;

    public MessagePacket(String content, String sender, String receiver) {
        this.content = content;
        this.sender = sender;
        this.receiver = receiver;
        id = "message_packet";
    }

    public MessagePacket() {}

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void read(JsonObject obj) {
        sender = obj.get("sender").getAsString();
        receiver = obj.get("receiver").getAsString();
        content = obj.get("content").getAsString();
    }

    public String getSender() {
        return sender;
    }

    public String getContent() {
        return content;
    }

    public String getReceiver() {
        return receiver;
    }
}
