package com.flash3388.flashlib.net.udp;

import com.castle.time.exceptions.TimeoutException;
import com.castle.util.closeables.Closeables;
import com.flash3388.flashlib.net.ConnectedNetChannel;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;

public class ConnectedUdpChannel implements ConnectedNetChannel {

    private static final int READ_TIMEOUT = 100;
    private static final boolean BREAK_ON_READ_TIMEOUT = false;

    private final DatagramChannel mChannel;
    private final Selector mReadSelector;

    public ConnectedUdpChannel(DatagramChannel channel) throws IOException {
        if (!channel.isConnected()) {
            throw new IllegalArgumentException("expected connected channel");
        }

        mChannel = channel;
        mChannel.configureBlocking(false);
        mReadSelector = Selector.open();

        mChannel.register(mReadSelector, SelectionKey.OP_READ);
    }

    @Override
    public void write(ByteBuffer buffer) throws IOException {
        mChannel.send(buffer, mChannel.getRemoteAddress());
    }

    @Override
    public int read(ByteBuffer buffer) throws IOException, TimeoutException {
        SocketAddress remote = mChannel.receive(buffer);
        while (remote == null) {
            int available = mReadSelector.select(READ_TIMEOUT);
            if (available < 1) {
                if (BREAK_ON_READ_TIMEOUT) {
                    throw new TimeoutException();
                } else {
                    continue;
                }
            }

            remote = mChannel.receive(buffer);
        }

        return buffer.position();
    }

    @Override
    public void close() {
        Closeables.silentClose(mChannel);
        Closeables.silentClose(mReadSelector);
    }
}
