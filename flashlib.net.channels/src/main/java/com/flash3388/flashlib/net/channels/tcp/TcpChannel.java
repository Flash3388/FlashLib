package com.flash3388.flashlib.net.channels.tcp;

import com.castle.util.closeables.Closeables;
import com.flash3388.flashlib.net.channels.ConnectableNetChannel;
import com.flash3388.flashlib.net.channels.IncomingData;
import com.flash3388.flashlib.net.channels.nio.ChannelListener;
import com.flash3388.flashlib.net.channels.nio.ChannelUpdater;
import com.flash3388.flashlib.net.channels.nio.UpdateRegistration;
import org.slf4j.Logger;

import java.io.IOException;
import java.net.SocketAddress;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public class TcpChannel implements ConnectableNetChannel {

    private final Logger mLogger;

    private final SocketChannel mChannel;
    private SocketAddress mRemoteAddress;

    public TcpChannel(SocketAddress bindAddress, Logger logger) throws IOException {
        mLogger = logger;

        mChannel = openChannel(bindAddress);
    }

    @Override
    public UpdateRegistration register(ChannelUpdater updater, ChannelListener listener) throws IOException {
        return updater.register(mChannel, SelectionKey.OP_CONNECT | SelectionKey.OP_READ | SelectionKey.OP_WRITE, listener);
    }

    @Override
    public boolean startConnection(SocketAddress remote) throws IOException {
        return mChannel.connect(remote);
    }

    @Override
    public void finishConnection() throws IOException {
        if (!mChannel.finishConnect()) {
            throw new IOException("channel not connected");
        }

        mRemoteAddress = mChannel.getRemoteAddress();
    }

    @Override
    public IncomingData read(ByteBuffer buffer) throws IOException {
        int received = mChannel.read(buffer);
        if (received < 0) {
            throw new ClosedChannelException();
        }
        if (received < 1) {
            return new IncomingData(null, 0);
        }

        return new IncomingData(mRemoteAddress, received);
    }

    @Override
    public void write(ByteBuffer buffer) throws IOException {
        mChannel.write(buffer);
    }

    @Override
    public void close() throws IOException {
        mChannel.close();
        mRemoteAddress = null;
    }

    private SocketChannel openChannel(SocketAddress bindAddress) throws IOException {
        SocketChannel channel = null;
        try {
            mLogger.debug("Opening new TCP CLIENT socket in non-blocking mode");

            channel = SocketChannel.open();
            channel.setOption(StandardSocketOptions.SO_REUSEADDR, true);
            channel.configureBlocking(false);

            if (bindAddress != null) {
                mLogger.debug("Binding TCP Client to {}", bindAddress);
                channel.bind(bindAddress);
            }
        } catch (IOException e) {
            if (channel != null) {
                Closeables.silentClose(channel);
            }

            throw e;
        }

        return channel;
    }
}
