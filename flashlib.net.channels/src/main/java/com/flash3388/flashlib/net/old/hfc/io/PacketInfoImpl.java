package com.flash3388.flashlib.net.old.hfc.io;

import com.flash3388.flashlib.net.hfc.PacketInfo;
import com.flash3388.flashlib.net.Remote;
import com.flash3388.flashlib.time.Time;

public class PacketInfoImpl implements PacketInfo {

    private final Remote mSender;
    private final Time mTimestamp;

    public PacketInfoImpl(Remote sender, Time timestamp) {
        mSender = sender;
        mTimestamp = timestamp;
    }

    @Override
    public Time getTimestamp() {
        return mTimestamp;
    }

    @Override
    public Remote getSender() {
        return mSender;
    }
}
