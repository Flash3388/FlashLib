package com.flash3388.flashlib.net.tcp;

import com.castle.time.exceptions.TimeoutException;
import com.castle.util.closeables.Closeables;
import com.flash3388.flashlib.net.ConnectedNetChannel;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class BlockingConnectedTcpChannel implements ConnectedNetChannel {

    private static final int READ_TIMEOUT = 100;

    private final SocketChannel mChannel;

    public BlockingConnectedTcpChannel(SocketChannel channel) throws IOException {
        if (!channel.isConnected()) {
            throw new IllegalArgumentException("expected connected channel");
        }

        mChannel = channel;
        mChannel.configureBlocking(true);
        mChannel.socket().setSoTimeout(READ_TIMEOUT);
    }

    public void write(ByteBuffer buffer) throws IOException {
        mChannel.write(buffer);
    }

    @Override
    public int read(ByteBuffer buffer) throws IOException, TimeoutException {
        try {
            return mChannel.read(buffer);
        } catch (SocketTimeoutException e) {
            throw new TimeoutException(e);
        }
    }

    @Override
    public void close() {
        Closeables.silentClose(mChannel);
    }
}
