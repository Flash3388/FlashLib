package com.flash3388.flashlib.net.udp;

import com.castle.time.exceptions.TimeoutException;
import com.castle.util.closeables.Closeables;

import java.io.Closeable;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;

public class UnconnectedUdpChannel implements Closeable {

    private static final int READ_TIMEOUT = 100;

    private final DatagramChannel mChannel;
    private final Selector mReadSelector;

    public UnconnectedUdpChannel(DatagramChannel channel) throws IOException {
        mChannel = channel;
        mReadSelector = Selector.open();

        mChannel.register(mReadSelector, SelectionKey.OP_READ);
    }

    public void writeTo(ByteBuffer buffer, SocketAddress address) throws IOException {
        mChannel.send(buffer, address);
    }

    public SocketAddress read(ByteBuffer buffer) throws IOException, TimeoutException {
        SocketAddress remote = mChannel.receive(buffer);
        while (remote == null) {
            int available = mReadSelector.select(READ_TIMEOUT);
            if (available < 1) {
                throw new TimeoutException();
            }

            remote = mChannel.receive(buffer);
        }

        return remote;
    }

    @Override
    public void close() {
        Closeables.silentClose(mChannel);
        Closeables.silentClose(mReadSelector);
    }
}
