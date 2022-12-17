package com.flash3388.flashlib.net.packets;

public interface PacketsSender {

    void send(String remoteId, Packet packet);
    void broadcast(Packet packet);
}
