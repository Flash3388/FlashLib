package com.flash3388.flashlib.net.messaging.impl;

import com.castle.util.closeables.Closeables;
import com.castle.util.function.ThrowingFunction;

import java.io.Closeable;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

public class TcpClientChannel implements Closeable {

    private static final int CONNECTION_TIMEOUT = 500;

    private final SocketAddress mRemoteAddress;

    private Selector mConnectSelector;
    private SocketChannel mBaseChannel;
    private TcpSocketChannel mChannel;

    public TcpClientChannel(SocketAddress remoteAddress) {
        mRemoteAddress = remoteAddress;

        mConnectSelector = null;
        mBaseChannel = null;
        mChannel = null;
    }

    public synchronized void waitForConnection() throws IOException {
        //noinspection resource
        connectChannel();
    }

    public synchronized void write(ByteBuffer buffer) throws IOException {
        try {
            //noinspection resource
            TcpSocketChannel channel = connectChannel();
            channel.write(buffer);
        } catch (IOException e) {
            close();
            throw e;
        }
    }

    public synchronized <T> T read(ThrowingFunction<BufferedReader, T, IOException> func) throws IOException {
        try {
            //noinspection resource
            TcpSocketChannel channel = connectChannel();
            return func.apply(channel.reader());
        } catch (IOException e) {
            close();
            throw e;
        }
    }

    private synchronized TcpSocketChannel connectChannel() throws IOException {
        if (mChannel == null) {
            try {
                SocketChannel channel = openChannel();
                if (!channel.isConnected() && !channel.isConnectionPending()) {
                    channel.connect(mRemoteAddress);
                }

                //noinspection StatementWithEmptyBody
                while (mConnectSelector.select(CONNECTION_TIMEOUT) < 1);

                mChannel = new TcpSocketChannel(channel);
            } catch (IOException e) {
                close();
                throw e;
            }
        }

        return mChannel;
    }

    private synchronized SocketChannel openChannel() throws IOException {
        if (mBaseChannel == null) {
            try {
                mConnectSelector = Selector.open();

                mBaseChannel = SocketChannel.open();
                mBaseChannel.configureBlocking(false);

                mBaseChannel.register(mConnectSelector, SelectionKey.OP_CONNECT);
            } catch (IOException e) {
                close();
                throw e;
            }
        }

        return mBaseChannel;
    }


    @Override
    public synchronized void close() {
        if (mChannel != null) {
            Closeables.silentClose(mChannel);
            mChannel = null;
            mBaseChannel = null;
        } else if (mBaseChannel != null) {
            Closeables.silentClose(mBaseChannel);
            mChannel = null;
            mBaseChannel = null;
        }

        if (mConnectSelector != null) {
            Closeables.silentClose(mConnectSelector);
            mConnectSelector = null;
        }
    }
}
