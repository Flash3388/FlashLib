package com.flash3388.flashlib.net.packets;

import com.flash3388.flashlib.net.robolink.Remote;
import com.flash3388.flashlib.time.Time;

public interface InboundPacket extends Packet {

    Remote getSender();
    Time getTimestamp();
}
