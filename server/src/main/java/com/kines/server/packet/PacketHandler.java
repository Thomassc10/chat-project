package com.kines.server.packet;

import org.java_websocket.WebSocket;

public interface PacketHandler<T extends Packet> {

    void handle(T packet, WebSocket conn);
}
