package com.flash3388.flashlib.net.messaging.io;

import com.castle.util.closeables.Closeables;

import java.nio.channels.SocketChannel;

public class TcpSocketChannel extends TcpChannel {

    private final SocketChannel mChannel;

    public TcpSocketChannel(SocketChannel channel) {
        mChannel = channel;
    }

    @Override
    protected SocketChannel getChannel() {
        return mChannel;
    }

    @Override
    protected void closeChannel() {
        Closeables.silentClose(mChannel);
    }
}
