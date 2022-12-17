package com.flash3388.flashlib.net.packets.io;

import com.flash3388.flashlib.net.packets.InboundPacket;
import com.flash3388.flashlib.net.robolink.Remote;
import com.flash3388.flashlib.time.Time;

public class InboundPacketImpl implements InboundPacket {

    private final Remote mSender;
    private final Time mTimestamp;
    private final int mContentType;
    private final byte[] mContent;

    public InboundPacketImpl(Remote sender, Time timestamp, int contentType, byte[] content) {
        mSender = sender;
        mTimestamp = timestamp;
        mContentType = contentType;
        mContent = content;
    }

    @Override
    public Remote getSender() {
        return mSender;
    }

    @Override
    public Time getTimestamp() {
        return mTimestamp;
    }

    @Override
    public int getContentType() {
        return mContentType;
    }

    @Override
    public byte[] getContent() {
        return mContent;
    }
}
