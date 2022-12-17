package com.flash3388.flashlib.net.packets.io;

import com.flash3388.flashlib.net.robolink.Remote;
import com.flash3388.flashlib.time.Time;

public class PacketInfo {

    private final Remote mSender;
    private final Time mTimestamp;
    private final int mContentType;

    public PacketInfo(Remote sender, Time timestamp, int contentType) {
        mSender = sender;
        mTimestamp = timestamp;
        mContentType = contentType;
    }

    public int getContentType() {
        return mContentType;
    }

    public Time getTimestamp() {
        return mTimestamp;
    }

    public Remote getSender() {
        return mSender;
    }
}
