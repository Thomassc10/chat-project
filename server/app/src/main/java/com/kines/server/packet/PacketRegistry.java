package com.kines.server.packet;

import java.util.HashMap;
import java.util.Map;

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
        
        if (clazz == null) return null;
        
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
}
