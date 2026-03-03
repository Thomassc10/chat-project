package com.kines.server.packet;

import com.google.gson.JsonObject;

public abstract class Packet {

    public abstract String getId();

    public abstract void read(JsonObject obj);
}
