package com.flash3388.flashlib.net.messaging.io;

import com.castle.util.closeables.Closeables;

import java.io.IOException;
import java.net.SocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.SocketChannel;

public class TcpClientChannel extends TcpChannel {

    private static final int SO_TIMEOUT = 500;

    private final SocketAddress mRemoteAddress;
    private SocketChannel mChannel;

    public TcpClientChannel(SocketAddress remoteAddress) {
        mRemoteAddress = remoteAddress;
        mChannel = null;
    }

    public synchronized boolean refreshConnection() {
        if (mChannel == null) {
            try {
                openChannel();
                return true;
            } catch (IOException e) {
                closeChannel();
            }
        } else if (!mChannel.isConnected()) {
            closeChannel();

            try {
                openChannel();
                return true;
            } catch (IOException e) {
                closeChannel();
            }
        }

        return false;
    }

    @Override
    protected SocketChannel getChannel() {
        refreshConnection();
        return mChannel;
    }

    @Override
    protected synchronized void closeChannel() {
        if (mChannel != null) {
            Closeables.silentClose(mChannel);
            mChannel = null;
        }
    }

    private synchronized void openChannel() throws IOException {
        mChannel = SocketChannel.open();
        mChannel.configureBlocking(true);
        mChannel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
        mChannel.socket().setSoTimeout(SO_TIMEOUT);

        if (!mChannel.connect(mRemoteAddress)) {
            throw new IOException("socket is in non-blocking mode despite being configured otherwise");
        }

        if (!mChannel.isConnected()) {
            throw new IOException("socket failed to connect");
        }
    }
}
