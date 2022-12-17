package com.flash3388.flashlib.robot.net;

import com.flash3388.flashlib.net.robolink.InboundPacketsReceiver;
import com.flash3388.flashlib.net.robolink.PacketsSender;
import com.flash3388.flashlib.net.robolink.RemotesStorage;

public interface RoboLinkInterface {

    InboundPacketsReceiver getReceiver();
    PacketsSender getSender();
    RemotesStorage getStorage();
}
