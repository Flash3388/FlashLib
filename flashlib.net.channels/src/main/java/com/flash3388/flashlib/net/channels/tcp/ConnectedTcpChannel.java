package com.flash3388.flashlib.net.channels.tcp;

import com.flash3388.flashlib.net.channels.IncomingData;
import com.flash3388.flashlib.net.channels.NetChannel;
import com.flash3388.flashlib.net.channels.nio.ChannelListener;
import com.flash3388.flashlib.net.channels.nio.ChannelUpdater;
import com.flash3388.flashlib.net.channels.nio.UpdateRegistration;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

public class ConnectedTcpChannel implements NetChannel {

    private final SocketChannel mChannel;
    private final SocketAddress mRemoteAddress;

    public ConnectedTcpChannel(SocketChannel channel) throws IOException {
        mChannel = channel;
        mRemoteAddress = channel.getRemoteAddress();
    }

    @Override
    public UpdateRegistration register(ChannelUpdater updater, ChannelListener listener) throws IOException {
        return updater.register(mChannel, SelectionKey.OP_READ | SelectionKey.OP_WRITE, listener);
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
    }
}
