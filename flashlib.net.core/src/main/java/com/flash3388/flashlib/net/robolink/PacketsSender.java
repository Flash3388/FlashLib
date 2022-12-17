package com.flash3388.flashlib.net.robolink;

public interface PacketsSender {

    void send(String remoteId, Packet packet);
    void broadcast(Packet packet);
}
