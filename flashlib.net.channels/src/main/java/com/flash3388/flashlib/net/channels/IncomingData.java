package com.flash3388.flashlib.net.channels;

import java.net.SocketAddress;

public class IncomingData {

    private final SocketAddress mSender;
    private final int mBytesReceived;

    public IncomingData(SocketAddress sender, int bytesReceived) {
        mSender = sender;
        mBytesReceived = bytesReceived;
    }

    public SocketAddress getSender() {
        return mSender;
    }

    public int getBytesReceived() {
        return mBytesReceived;
    }
}
