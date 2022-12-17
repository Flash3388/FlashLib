package com.flash3388.flashlib.net.tcp;

import com.castle.time.exceptions.TimeoutException;
import com.castle.util.closeables.Closeables;
import com.flash3388.flashlib.net.IdentifiedConnectedNetChannel;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

public class ConnectedTcpChannel implements IdentifiedConnectedNetChannel {

    private static final int READ_TIMEOUT = 100;

    private final SocketChannel mChannel;
    private final int mIdentifier;
    private final Selector mReadSelector;

    public ConnectedTcpChannel(SocketChannel channel, int identifier) throws IOException {
        if (!channel.isConnected()) {
            throw new IllegalArgumentException("expected connected channel");
        }

        mChannel = channel;
        mChannel.configureBlocking(false);
        mIdentifier = identifier;
        mReadSelector = Selector.open();

        mChannel.register(mReadSelector, SelectionKey.OP_READ);
    }

    public ConnectedTcpChannel(SocketChannel channel) throws IOException {
        this(channel, -1);
    }

    @Override
    public int getIdentifier() {
        return mIdentifier;
    }

    public void write(ByteBuffer buffer) throws IOException {
        mChannel.write(buffer);
    }

    @Override
    public int read(ByteBuffer buffer) throws IOException, TimeoutException {
        int read = mChannel.read(buffer);
        if (read < 0) {
            close();
            throw new ClosedChannelException();
        }

        while (read < 1) {
            int available = mReadSelector.select(READ_TIMEOUT);
            if (available < 1) {
                throw new TimeoutException();
            }

            read = mChannel.read(buffer);
        }

        return read;
    }

    @Override
    public void close() {
        Closeables.silentClose(mChannel);
        Closeables.silentClose(mReadSelector);
    }
}
