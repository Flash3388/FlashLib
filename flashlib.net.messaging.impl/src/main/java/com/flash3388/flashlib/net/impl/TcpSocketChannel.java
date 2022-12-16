package com.flash3388.flashlib.net.impl;

import com.castle.util.closeables.Closeables;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

public class TcpSocketChannel implements Closeable, ReadableChannel {

    private static final int READ_TIMEOUT = 100;

    private final SocketChannel mChannel;
    private final int mIdentifier;
    private final Selector mReadSelector;

    public TcpSocketChannel(SocketChannel channel, int identifier) throws IOException {
        mChannel = channel;
        mIdentifier = identifier;
        mReadSelector = Selector.open();

        mChannel.register(mReadSelector, SelectionKey.OP_READ);
    }

    public TcpSocketChannel(SocketChannel channel) throws IOException {
        this(channel, -1);
    }

    public void write(ByteBuffer buffer) throws IOException {
        mChannel.write(buffer);
    }

    @Override
    public int getIdentifier() {
        return mIdentifier;
    }

    @Override
    public int read(ByteBuffer buffer) throws IOException {
        int read = mChannel.read(buffer);
        if (read < 0) {
            close();
            throw new ClosedChannelException();
        }

        while (read < 1) {
            mReadSelector.select(READ_TIMEOUT);
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
