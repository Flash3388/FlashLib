package com.flash3388.flashlib.net.packets;

import com.notifier.Event;

public class NewPacketEvent implements Event {

    private final InboundPacket mPacket;

    public NewPacketEvent(InboundPacket packet) {
        mPacket = packet;
    }

    public InboundPacket getPacket() {
        return mPacket;
    }
}
