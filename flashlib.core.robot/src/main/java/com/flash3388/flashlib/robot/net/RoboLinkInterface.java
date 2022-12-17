package com.flash3388.flashlib.robot.net;

import com.flash3388.flashlib.net.packets.InboundPacketsReceiver;
import com.flash3388.flashlib.net.packets.PacketsSender;
import com.flash3388.flashlib.net.robolink.RemotesStorage;

public interface RoboLinkInterface {

    InboundPacketsReceiver getReceiver();
    PacketsSender getSender();
    RemotesStorage getStorage();
}
