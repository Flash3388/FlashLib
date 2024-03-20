package com.flash3388.flashlib.net.channels;

public class IncomingData {

    private final NetAddress mSender;
    private final int mBytesReceived;

    public IncomingData(NetAddress sender, int bytesReceived) {
        mSender = sender;
        mBytesReceived = bytesReceived;
    }

    public NetAddress getSender() {
        return mSender;
    }

    public int getBytesReceived() {
        return mBytesReceived;
    }
}
