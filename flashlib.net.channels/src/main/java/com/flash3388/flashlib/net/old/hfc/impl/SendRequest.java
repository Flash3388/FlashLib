package com.flash3388.flashlib.net.old.hfc.impl;

import com.flash3388.flashlib.net.hfc.Packet;

public class SendRequest {

    private final Packet mPacket;
    private final String mTargetId;

    public SendRequest(Packet packet, String targetId) {
        mPacket = packet;
        mTargetId = targetId;
    }

    public Packet getPacket() {
        return mPacket;
    }

    public String getTargetId() {
        return mTargetId;
    }

    public boolean isBroadcast() {
        return mTargetId == null;
    }
}
