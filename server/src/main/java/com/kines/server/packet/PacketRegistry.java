package com.kines.server.packet;

import java.util.HashMap;
import java.util.Map;

import com.kines.server.packet.handlers.LoginHandler;
import com.kines.server.packet.handlers.MessageHandler;
import com.kines.server.packet.handlers.RegisterHandler;
import com.kines.server.packet.packets.LoginRequest;
import com.kines.server.packet.packets.LoginResponse;
import com.kines.server.packet.packets.MessagePacket;
import com.kines.server.packet.packets.RegisterRequest;
import com.kines.server.packet.packets.RegisterResponse;

public class PacketRegistry {

    public Map<String, Class<? extends Packet>> packets = new HashMap<>();
    public Map<String, PacketHandler<?>> handlers = new HashMap<>();
    
    private static PacketRegistry INSTANCE;
    
    public PacketRegistry() {
        INSTANCE = this;
    }

    public static PacketRegistry getInstance() {
        return INSTANCE;
    }

    public void register(String packetId, PacketHandler<?> handler, Class<? extends Packet> packet) {
        handlers.put(packetId, handler);
        packets.put(packetId, packet);
    }

    public Packet createPacketInstance(String id) {
        Class<? extends Packet> clazz = packets.get(id);
        
        if (clazz == null) {
            System.out.println("Received packet with unrecognized id: " + id);
            return null;
        }
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public PacketHandler<?> getHandler(String packetId) {
        return handlers.get(packetId);
    }

    public void registerPackets() {
        register("message_packet", new MessageHandler(), MessagePacket.class);
        register("login_request", new LoginHandler(), LoginRequest.class);
        register("login_response", null, LoginResponse.class);
        register("register_request", new RegisterHandler(), RegisterRequest.class);
        register("register_response", null, RegisterResponse.class);
    }
}
